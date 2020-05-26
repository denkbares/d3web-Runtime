package de.d3web.testing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import com.denkbares.collections.DefaultMultiMap;
import com.denkbares.collections.MultiMap;
import com.denkbares.progress.ParallelProgress;
import com.denkbares.progress.ProgressListener;
import com.denkbares.utils.Log;
import de.d3web.testing.Message.Type;

import static java.util.stream.Collectors.toList;

/*
 * Copyright (C) 2012 denkbares GmbH
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

/**
 * A TestExecutor executes a set of tests. It can use multiple threads to execute the testing tasks in parallel. It uses
 * updates a ProgressListener on finished tasks.
 *
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 04.05.2012
 */
public class TestExecutor {

	/**
	 * Here the number of (parllel) threads are configured that will be used to execute the testing tasks.
	 */
	private static final int DEFAULT_NUMBER_OF_PARALLEL_THREADS = 2;

	private final Collection<TestObjectProvider> objectProviders;
	private final List<TestSpecification<?>> specifications;
	private final ProgressListener progressListener;
	private final BuildResult build;
	private final HashMap<TestSpecification<?>, TestResult> testResults = new HashMap<>();
	private final List<String> currentlyRunningTests = Collections.synchronizedList(new LinkedList<>());
	private final ExecutorService executor;
	private volatile boolean aborted;

	/**
	 * Returns the current build or null if the build has been terminated.
	 *
	 * @return the current build
	 * @created 14.08.2012
	 */
	public synchronized BuildResult getBuildResult() {
		if (!isShutdown()) return null;
		return build;
	}

	public TestExecutor(Collection<TestObjectProvider> providers, List<TestSpecification<?>> specifications, ProgressListener listener) {
		this(providers, specifications, listener, DEFAULT_NUMBER_OF_PARALLEL_THREADS);
	}

	public TestExecutor(Collection<TestObjectProvider> providers, List<TestSpecification<?>> specifications, ProgressListener listener, int numberOfThreads) {
		this.objectProviders = providers;
		this.specifications = specifications;
		this.progressListener = listener;
		this.executor = Executors.newFixedThreadPool(numberOfThreads);
		this.build = new BuildResult();
	}

	/**
	 * Runs the tests given by the task list using the provided TestObjectProvider. A BuildResultSet with the given
	 * build-number is created.
	 *
	 * @created 22.05.2012
	 */
	public void run() {
		long buildStartTime = System.currentTimeMillis();

		// checks the given tests of validity and only
		// returns the valid ones
		Collection<TestSpecification<?>> validSpecifications = getValidSpecifications();

		// creates and returns a CallableTest for each Test and TestObject
		Map<TestSpecification<?>, Collection<CallableTest<?>>> callableTests =
				getCallableTestsMap(validSpecifications);
		List<FutureTestTask> testTasks = null;
		try {
			initProgress(callableTests);
			executeTests(callableTests);
		}
		finally {
			shutdown();
			updateTestResults(callableTests);
			build.setBuildDuration(System.currentTimeMillis() - buildStartTime);
		}
	}

	private void updateTestResults(Map<TestSpecification<?>, Collection<CallableTest<?>>> callableTests) {
		MultiMap<TestResult, TestResult> groups = new DefaultMultiMap<>();
		TestResult currentGroup = null;
		for (TestSpecification<?> specification : callableTests.keySet()) {
			Collection<CallableTest<?>> ct = callableTests.get(specification);
			if (this.aborted) {
				for (CallableTest<?> callableTest : ct) {
					if (!callableTest.started) {
						callableTest.testResult.addUnexpectedMessage(callableTest.testObjectName,
								new Message(Type.SKIPPED, "Test was aborted"));
					}
				}
			}
			if (specification.getTest() instanceof TestGroup) {
				// if this is a group, remember the group, but do nothing,
				// because we will update the groups later on
				currentGroup = createTestResult(specification);
			}
			else {
				if (ct.isEmpty()) continue;
				TestResult testResult = ct.iterator().next().testResult;
				// update test and add to group (if there is any)
				specification.getTest().updateSummary(specification, testResult);
				if (currentGroup != null) groups.put(currentGroup, testResult);
			}
		}
		// finally update the groups
		for (TestResult group : groups.keySet()) {
			Message summarizedResult = TestingUtils.getSummarizedResult(
					groups.getValues(group).toArray(new TestResult[0]));
			group.setSummary(summarizedResult);
		}
	}

	private void initProgress(Map<TestSpecification<?>, Collection<CallableTest<?>>> callableTestsBySpecification) {
		List<CallableTest<?>> callableTests = callableTestsBySpecification.values()
				.stream()
				.flatMap(Collection::stream)
				.collect(toList());

		// collect all the complexities from all tests
		float[] complexities = new float[callableTests.size()];
		for (int i = 0; i < callableTests.size(); i++) {
			CallableTest<?> callableTest = callableTests.get(i);
			Test<?> test = callableTest.specification.getTest();
			complexities[i] = test instanceof ProgressingTest ?
					((ProgressingTest) test).getComputationalComplexity() : ProgressingTest.DEFAULT_COMPUTATION_COMPLEXITY;
		}

		// create ParallelProgressListener and set the listener of the sub tasks accordingly
		ParallelProgress parallelProgress = new ParallelProgress(this.progressListener, complexities);
		for (int i = 0; i < callableTests.size(); i++) {
			callableTests.get(i).setProgressListener(parallelProgress.getSubTaskProgressListener(i));
		}
		// set starting message
		parallelProgress.updateProgress(0, "Initializing...");
	}

	private float getNumberOfTests(Map<TestSpecification<?>, Collection<CallableTest<?>>> callablesMap) {
		int count = 0;
		for (Collection<CallableTest<?>> callablesOfTest : callablesMap.values()) {
			count += callablesOfTest.size();
		}
		return count;
	}

	private <T> Collection<TestObjectContainer<T>> getTestObjects(TestSpecification<T> specification) {

		Collection<TestObjectContainer<T>> allTestObjects = new LinkedList<>();
		Test<T> test = specification.getTest();
		Class<T> objectClass = test.getTestObjectClass();
		if (objectClass == null) return Collections.emptyList();

		String testObjectID = specification.getTestObject();
		for (TestObjectProvider testObjectProvider : objectProviders) {
			allTestObjects.addAll(testObjectProvider.getTestObjects(objectClass, testObjectID));
		}
		return allTestObjects;
	}

	@SuppressWarnings("UnnecessaryLabelOnContinueStatement")
	private Collection<TestSpecification<?>> getValidSpecifications() {
		Collection<TestSpecification<?>> validTests = new ArrayList<>();
		prepareTests:
		for (TestSpecification<?> specification : specifications) {

			String[] testArgs = specification.getArguments();
			String testObjectID = specification.getTestObject();
			Test<?> test = specification.getTest();

			// check arguments and create error if erroneous
			ArgsCheckResult argsCheckResult = test.checkArgs(testArgs);
			if (argsCheckResult.hasError()) {
				TestResult testResult = toTestResult(test, testObjectID, argsCheckResult);
				build.addTestResult(testResult);
				continue prepareTests;
			}

			// check ignores and create error if erroneous
			for (String[] ignoreArgs : specification.getIgnores()) {
				ArgsCheckResult ignoreCheckResult = test.checkIgnore(ignoreArgs);
				if (ignoreCheckResult.hasError()) {
					TestResult testResult = toTestResult(test, testObjectID, ignoreCheckResult);
					build.addTestResult(testResult);
					continue prepareTests;
				}
			}
			validTests.add(specification);
		}
		return validTests;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map<TestSpecification<?>, Collection<CallableTest<?>>> getCallableTestsMap(Collection<TestSpecification<?>> validSpecifications) {
		// Some weird non-generic stuff here to satisfy both Eclipse and Intellij compilers
		Map<TestSpecification<?>, Collection<CallableTest<?>>> callableTests = new LinkedHashMap<>();
		for (TestSpecification<?> specification : validSpecifications) {
			Collection futuresForCallableTest = getCallableTests(specification);
			callableTests.put(specification, futuresForCallableTest);
		}
		return callableTests;
	}

	private <T> Collection<CallableTest<T>> getCallableTests(TestSpecification<T> specification) {
		// prepare test objects
		Collection<TestObjectContainer<T>> testObjects = getTestObjects(specification);

		// create (empty) result for test execution
		TestResult testResult = createTestResult(specification);
		build.addTestResult(testResult);

		// create callable tests to be executed
		return getCallableTests(specification, testObjects, testResult);
	}

	private <T> TestResult createTestResult(TestSpecification<T> specification) {
		TestResult testResult = testResults.get(specification);
		if (testResult == null) {
			String[] testArgs = specification.getArguments();
			String testName = specification.getTestName();
			String[] config = new String[testArgs.length + 1];
			System.arraycopy(testArgs, 0, config, 1, testArgs.length);
			config[0] = specification.getTestObject();
			testResult = new TestResult(testName, config);
			testResults.put(specification, testResult);
		}
		return testResult;
	}

	private <T> Collection<CallableTest<T>> getCallableTests(
			final TestSpecification<T> specification,
			Collection<TestObjectContainer<T>> testObjects, final TestResult testResult) {

		if (specification.getTest() instanceof TestGroup) {
			return Collections.emptyList();
		}

		Collection<CallableTest<T>> result = new HashSet<>();
		boolean noTestObjects = true;

		for (final TestObjectContainer<T> testObjectContainer : testObjects) {
			noTestObjects = false;
			String testObjectName = testObjectContainer.getTestObjectName();
			T testObject = testObjectContainer.getTestObject();
			CallableTest<T> callableTest = new CallableTest<>(specification, testObjectName, testObject, testResult);
			result.add(callableTest);
		}

		// if no test can be applied, we assume test to be skipped
		if (noTestObjects) {
			testResult.setSummary(new Message(Type.SKIPPED, "No test objects available for this test."));
		}
		return result;
	}

	private void executeTests(Map<TestSpecification<?>, Collection<CallableTest<?>>> callablesMap) {
		// finally run execute on callable tests
		outerLoop:
		for (TestSpecification<?> specification : callablesMap.keySet()) {
			Collection<CallableTest<?>> callableTests = callablesMap.get(specification);
			specification.prepareExecution();
			for (CallableTest<?> callableTest : callableTests) {
				try {
					executor.execute(new FutureTestTask(callableTest));
				}
				catch (RejectedExecutionException e) {
					// it is possible that the executor is shut down during or
					// before adding the tests to the executor... we just catch it
					Log.fine("Rejected execution of " + callableTest.specification.getTestName() + ": " + callableTest.testObjectName);
				}

				try {
					TestingUtils.checkInterrupt();
				}
				catch (InterruptedException e) {
					setTerminateStatus();
					executor.shutdownNow();
					// build is discarded, method call terminated
					break outerLoop;
				}
			}
		}
	}

	private void setTerminateStatus() {
		aborted = true;
		progressListener.updateProgress(1f, "Aborted, please wait...");
	}

	private void shutdown() {
		try {
			executor.shutdown();
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		}
		catch (InterruptedException e) {
			setTerminateStatus();
		}
	}

	public boolean isShutdown() {
		return executor.isShutdown();
	}

	/**
	 * Terminates/aborts all running tests as fast as possible. This method does not wait until the tests really have
	 * been terminated, use {@link TestExecutor#awaitTermination()} for this purpose.
	 *
	 * @created 21.09.2012
	 */
	public void shutDownNow() {
		// aborted, so discard build
		setTerminateStatus();
		// System.out.println("Terminating executor");
		executor.shutdownNow();
	}

	/**
	 * Blocks/waits until all running tests are done, tests are not aborted or shutdown by calling this method. Given
	 * timeout defines the maximum waited time until the method returns.
	 *
	 * @created 17.12.2013
	 */
	public void awaitTermination(long timeout, TimeUnit unit) {
		// No shutdown needed, because shutdown is called at
		// the end of method run.
		try {
			executor.awaitTermination(timeout, unit);
		}
		catch (InterruptedException e) {
			// do nothing here
		}
	}

	/**
	 * Blocks/waits until all running tests are done, tests are not aborted by calling this method.
	 *
	 * @created 17.12.2013
	 */
	public void awaitTermination() {
		awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
	}

	private <T> TestResult toTestResult(Test<T> test, String testObjectName, ArgsCheckResult checkResult) {
		Message message = null;
		String[] arguments = checkResult.getArguments();
		for (int i = 0; i < arguments.length; i++) {
			if (checkResult.hasError(i)) {
				message = new Message(Type.ERROR,
						"Invalid argument " + arguments[i] + ": " + checkResult.getMessage(i));
				break;
			}
		}
		if (arguments.length == 0 && checkResult.hasError(0)) {
			message = new Message(Type.ERROR, checkResult.getMessage(0));
		}

		TestResult result = new TestResult(test.getName(), checkResult.getArguments());
		if (message != null) {
			result.addUnexpectedMessage(testObjectName, message);
			result.setSummary(new Message(Type.ERROR, message.getText()));
		}
		return result;
	}

	static class FutureTestTask extends FutureTask<Void> {

		private final CallableTest<?> callable;

		public FutureTestTask(CallableTest<?> callable) {
			super(callable);
			this.callable = callable;
		}

		@Override
		protected void done() {
			// update progress listener as task has been finished
			callable.testFinished();
		}
	}

	class CallableTest<T> implements Callable<Void> {

		private final String testObjectName;
		private final TestSpecification<T> specification;
		private final T testObject;
		private final TestResult testResult;
		private ProgressListener progressListener = null;
		private boolean started = false;

		public CallableTest(TestSpecification<T> specification, String testObjectName, T testObject, TestResult testresult) {
			this.specification = specification;
			this.testObject = testObject;
			this.testObjectName = testObjectName;
			this.testResult = testresult;
		}

		public void setProgressListener(ProgressListener progressListener) {
			this.progressListener = progressListener;
			Test<T> test = specification.getTest();
			if (test instanceof ProgressingTest) {
				((ProgressingTest) test).setProgressListener(progressListener);
			}
		}

		public void testStarted() {
			started = true;
			currentlyRunningTests.add(getMessage());
			progressListener.updateProgress(0, getMessage());
		}

		public void testFinished() {
			currentlyRunningTests.remove(getMessage());
			progressListener.updateProgress(1f, null);
		}

		public String getMessage() {
			return specification.getTestName() + ": " + testObjectName;
		}

		@Override
		public Void call() throws Exception {

			testStarted();

			try {
				if (testObject == null) {
					testResult.addUnexpectedMessage(testObjectName, new Message(
							Message.Type.ERROR, "Test-object was null."));
					return null;
				}
				Test<T> test = specification.getTest();
				Message message = test.execute(specification, testObject);
				if (TestExecutor.this.aborted) {
					testResult.addUnexpectedMessage(testObjectName, new Message(Type.SKIPPED, "Test was aborted"));
				}
				else {
					if (message.getType() == Type.SUCCESS) {
						testResult.addExpectedMessage(testObjectName, message);
					}
					else {
						testResult.addUnexpectedMessage(testObjectName, message);
					}
				}
				return null;
			}
			catch (InterruptedException e) {
				testResult.addUnexpectedMessage(testObjectName, new Message(Type.SKIPPED, "Test was aborted"));
				throw e;
			}
			catch (Throwable e) { // NOSONAR
				// must catch throwable here to also handle unexpected errors
				// such as StackOverflow, memory issues or invalid plugins (linkage) errors
				String message = "Unexpected error in test " +
						specification.getTestName() + ", " +
						"during testing '" + testObjectName + "'";
				testResult.addUnexpectedMessage(testObjectName,
						new Message(Message.Type.ERROR, message + ": " + e));
				Log.severe(message, e);
				return null;
			}
		}
	}
}
