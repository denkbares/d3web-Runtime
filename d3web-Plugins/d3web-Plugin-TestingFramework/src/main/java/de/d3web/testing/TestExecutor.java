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

import de.d3web.collections.DefaultMultiMap;
import de.d3web.collections.MultiMap;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.testing.Message.Type;
import de.d3web.utils.Log;

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
 * A TestExecutor executes a set of tests. It can use multiple threads to execute the testing tasks
 * in parallel. It uses updates a ProgressListener on finished tasks.
 *
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 04.05.2012
 */
public class TestExecutor {

	/**
	 * Here the number of (parllel) threads are configured that will be used to execute the testing
	 * tasks.
	 */
	private static final int NUMBER_OF_PARALLEL_THREADS = 2;

	private final Collection<TestObjectProvider> objectProviders;
	private final List<TestSpecification<?>> specifications;
	private final ProgressListener progressListener;
	private final BuildResult build;
	private final HashMap<TestSpecification, TestResult> testResults = new HashMap<>();
	private String currentMessage;
	private final List<String> currentlyRunningTests = Collections.synchronizedList(new LinkedList<String>());
	private float finishedTests;
	private float overallTestsCount;
	private final ExecutorService executor;
	private Thread executorThread;

	/**
	 * Returns the current build or null if the build has been terminated.
	 *
	 * @return the current build
	 * @created 14.08.2012
	 */
	public synchronized BuildResult getBuildResult() {
		if (executorThread == null || executorThread.isInterrupted() || !isShutdown()) return null;
		return build;
	}

	/**
	 * Creates a TestExecutor with the given task list and TestObjectProvider.
	 */
	public TestExecutor(Collection<TestObjectProvider> providers, List<TestSpecification<?>> specifications, ProgressListener listener) {
		this.objectProviders = providers;
		this.specifications = specifications;
		this.progressListener = listener;
		this.executor = Executors.newFixedThreadPool(NUMBER_OF_PARALLEL_THREADS);
		this.build = new BuildResult();
	}

	/**
	 * Runs the tests given by the task list using the provided TestObjectProvider. A BuildResultSet
	 * with the given build-number is created.
	 *
	 * @created 22.05.2012
	 */
	public void run() {
		executorThread = Thread.currentThread();
		long buildStartTime = System.currentTimeMillis();

		// checks the given tests of validity and only
		// returns the valid ones
		Collection<TestSpecification<?>> validSpecifications = getValidSpecifications();

		// creates and returns a CallableTest for each Test and TestObject
		Map<TestSpecification, Collection<CallableTest>> callableTests =
				getCallableTestsMap(validSpecifications);
		try {
			initProgressFields(callableTests);
			executeTests(callableTests);
		}
		finally {
			shutdown();
			updateTestResults(callableTests);
			build.setBuildDuration(System.currentTimeMillis() - buildStartTime);
		}
	}

	private void updateTestResults(Map<TestSpecification, Collection<CallableTest>> callableTests) {
		MultiMap<TestResult, TestResult> groups =
				new DefaultMultiMap<>();
		TestResult currentGroup = null;
		for (TestSpecification<?> specification : callableTests.keySet()) {
			Collection<CallableTest> ct = callableTests.get(specification);
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
			Type type = BuildResult.getOverallResult(groups.getValues(group));
			group.setSummary(new Message(type));
		}
	}

	private void initProgressFields(Map<TestSpecification, Collection<CallableTest>> callableTests) {
		overallTestsCount = getNumberOfTests(callableTests);
		finishedTests = 0f;
		currentMessage = "Initializing...";
	}

	private float getNumberOfTests(Map<TestSpecification, Collection<CallableTest>> callablesMap) {
		int count = 0;
		for (Collection<CallableTest> callablesOfTest : callablesMap.values()) {
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

	@SuppressWarnings("unchecked")
	private Map<TestSpecification, Collection<CallableTest>> getCallableTestsMap(Collection<TestSpecification<?>> validSpecifications) {
		// Some weird non-generic stuff here to satisfy both Eclipse and Intellij compilers
		Map callableTests = new LinkedHashMap();
		for (TestSpecification specification : validSpecifications) {
			Collection futuresForCallableTest = getCallableTests((TestSpecification<Object>) specification);
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
			CallableTest<T> callableTest = new CallableTest<>(specification, testObjectName,
					testObject, testResult);
			result.add(callableTest);
		}

		// if no test can be applied, we assume test to be erroneous
		if (noTestObjects) {
			testResult.setSummary(new Message(Message.Type.ERROR,
					"No test objects available for this test."));
		}
		return result;
	}

	private void executeTests(Map<TestSpecification, Collection<CallableTest>> callablesMap) {
		// finally run execute on callable tests
		outerLoop:
		for (TestSpecification<?> specification : callablesMap.keySet()) {
			Collection<CallableTest> callableTests = callablesMap.get(specification);
			specification.prepareExecution();
			for (CallableTest callableTest : callableTests) {
				try {
					executor.execute(new FutureTestTask(callableTest));
				}
				catch (RejectedExecutionException e) {
					// it is possible that the executor is shut down during or
					// before adding the tests to the executor... we just catch
					// it
					Log.warning("Rejected execution of " + callableTest.testObjectName);
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
	 * Terminates/aborts all running tests as fast as possible. This method does not wait until the
	 * tests really have been terminated, use {@link TestExecutor#awaitTermination()} for this
	 * purpose.
	 *
	 * @created 21.09.2012
	 */
	public void shutDownNow() {
		// aborted, so discard build
		setTerminateStatus();
		// System.out.println("Terminating executor");
		executor.shutdownNow();
		if (executorThread != null) executorThread.interrupt();
	}

	/**
	 * Blocks/waits until all running tests are done, tests are not aborted or shutdown by calling
	 * this method. Given timeout defines the maximum waited time until the method returns.
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
						"invalid argument " + arguments[i] + ": " + checkResult.getMessage(i));
				break;
			}
		}
		if (arguments.length == 0 && checkResult.hasError(0)) {
			message = new Message(Type.ERROR, checkResult.getMessage(0));
		}

		TestResult result = new TestResult(test.getName(), checkResult.getArguments());
		if (message != null) {
			result.addUnexpectedMessage(testObjectName, message);
			result.setSummary(new Message(Type.ERROR));
		}
		return result;
	}

	private synchronized void updateProgressListener() {
		progressListener.updateProgress(finishedTests / overallTestsCount, currentMessage);
	}

	class FutureTestTask extends FutureTask<Void> {

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

		public CallableTest(TestSpecification<T> specification, String testObjectName, T testObject, TestResult testresult) {
			this.specification = specification;
			this.testObject = testObject;
			this.testObjectName = testObjectName;
			this.testResult = testresult;
		}

		public void testStarted() {
			currentlyRunningTests.add(getMessage());
			currentMessage = getMessage();
			updateProgressListener();
		}

		public void testFinished() {
			currentlyRunningTests.remove(getMessage());
			String tempMessage = null;
			try {
				tempMessage = currentlyRunningTests.get(0);
			}
			catch (IndexOutOfBoundsException e) {
				// since another thread could remove the last element between
				// checking for not empty and actually retrieving it, we just
				// try to retrieve it and catch a possible exception
			}
			if (tempMessage != null) currentMessage = tempMessage;
			finishedTests += getTaskVolume();
			updateProgressListener();
		}

		public float getTaskVolume() {
			// maybe this can be derived somehow more precisely some time
			return 1;
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
				if (message.getType() == Type.SUCCESS) {
					testResult.addExpectedMessage(testObjectName, message);
				}
				else {
					testResult.addUnexpectedMessage(testObjectName, message);
				}
				return null;
			}
			catch (InterruptedException e) {
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
