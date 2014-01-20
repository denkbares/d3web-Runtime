package de.d3web.testing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
 * A TestExecutor executes a set of tests. It can use multiple threads to
 * execute the testing tasks in parallel. It uses updates a ProgressListener on
 * finished tasks.
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 04.05.2012
 */
public class TestExecutor {

	/**
	 * Here the number of (parllel) threads are configured that will be used to
	 * execute the testing tasks.
	 */
	private static final int NUMBER_OF_PARALLEL_THREADS = 2;

	private final Collection<TestObjectProvider> objectProviders;
	private final List<TestSpecification<?>> specifications;
	private final ProgressListener progressListener;
	private final BuildResult build;
	private String currentMessage;
	private final List<String> currentlyRunningTests = Collections.synchronizedList(new LinkedList<String>());
	private float finishedTests;
	private float overallTestsCount;
	private final ExecutorService executor;
	private Thread executorThread;

	/**
	 * Returns the current build or null if the build has been terminated.
	 * 
	 * @created 14.08.2012
	 * @return the current build
	 */
	public BuildResult getBuildResult() {
		if (executorThread == null || executorThread.isInterrupted() || isRunning()) return null;
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
	 * Runs the tests given by the task list using the provided
	 * TestObjectProvider. A BuildResultSet with the given build-number is
	 * created.
	 * 
	 * @created 22.05.2012
	 * @param buildNumber Build number for this build.
	 * @return
	 */
	public void run() {
		executorThread = Thread.currentThread();
		long buildStartTime = System.currentTimeMillis();

		try {
			// checks the given tests of validity and only
			// returns the valid ones
			Collection<TestSpecification<?>> validSpecifications = getValidSpecifications();

			// creates and returns a CallableTest for each Test and TestObject
			Map<TestSpecification<?>, Collection<CallableTest<?>>> callableTests = getCallableTestsMap(validSpecifications);

			initProgressFields(callableTests);

			executeTests(callableTests);
		}
		finally {
			shutdown(buildStartTime);
		}
	}

	private void initProgressFields(Map<TestSpecification<?>, Collection<CallableTest<?>>> callableTests) {
		overallTestsCount = getNumberOfTests(callableTests);
		finishedTests = 0f;
		currentMessage = "Initializing...";
	}

	private float getNumberOfTests(Map<TestSpecification<?>, Collection<CallableTest<?>>> callablesMap) {
		int count = 0;
		for (Collection<CallableTest<?>> callablesOfTest : callablesMap.values()) {
			count += callablesOfTest.size();
		}
		return count;
	}

	private <T> Collection<TestObjectContainer<T>> getTestObjects(TestSpecification<T> specification) {

		Collection<TestObjectContainer<T>> allTestObjects = new LinkedList<TestObjectContainer<T>>();
		Test<T> test = specification.getTest();
		String testObjectID = specification.getTestObject();

		for (TestObjectProvider testObjectProvider : objectProviders) {

			List<TestObjectContainer<T>> testObjects = testObjectProvider.getTestObjects(
					test.getTestObjectClass(), testObjectID);

			allTestObjects.addAll(testObjects);
		}

		return allTestObjects;
	}

	private Collection<TestSpecification<?>> getValidSpecifications() {
		Collection<TestSpecification<?>> validTests = new ArrayList<TestSpecification<?>>();
		prepareTests: for (TestSpecification<?> specification : specifications) {

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

	private Map<TestSpecification<?>, Collection<CallableTest<?>>> getCallableTestsMap(Collection<TestSpecification<?>> validSpecifications) {

		Map<TestSpecification<?>, Collection<CallableTest<?>>> callableTests = new LinkedHashMap<TestSpecification<?>, Collection<CallableTest<?>>>();

		for (TestSpecification<?> specification : validSpecifications) {
			String[] testArgs = specification.getArguments();
			String testName = specification.getTestName();
			// create result
			TestResult testResult = new TestResult(testName, testArgs);
			build.addTestResult(testResult);
			Collection<CallableTest<?>> futuresForCallableTest = getCallableTests(specification,
					testResult);
			callableTests.put(specification, futuresForCallableTest);
		}
		return callableTests;
	}

	private <T> Collection<CallableTest<?>> getCallableTests(
			final TestSpecification<T> specification,
			final TestResult testResult) {

		Collection<CallableTest<?>> result = new HashSet<CallableTest<?>>();
		boolean noTestObjects = true;

		for (final TestObjectContainer<T> testObjectContainer : getTestObjects(specification)) {
			noTestObjects = false;
			String testObjectName = testObjectContainer.getTestObjectName();
			T testObject = testObjectContainer.getTestObject();
			CallableTest<T> callableTest = new CallableTest<T>(specification, testObjectName,
					testObject, testResult);
			result.add(callableTest);
		}

		if (noTestObjects) {
			testResult.addUnexpectedMessage("", new Message(Message.Type.ERROR,
					"No test-object found."));
		}
		return result;
	}

	private void executeTests(Map<TestSpecification<?>, Collection<CallableTest<?>>> callablesMap) {
		// finally run execute on callable tests
		outerLoop: for (TestSpecification<?> specification : callablesMap.keySet()) {
			Collection<CallableTest<?>> callableTests = callablesMap.get(specification);
			for (CallableTest<?> callableTest : callableTests) {
				try {
					executor.execute(new FutureTestTask(callableTest));
				}
				catch (RejectedExecutionException e) {
					// it is possible that the executor is shut down during or
					// before adding the tests to the executor... we just catch
					// it
				}

				try {
					Utils.checkInterrupt();
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

	private void shutdown(long buildStartTime) {
		try {
			executor.shutdown();
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		}
		catch (InterruptedException e) {
			setTerminateStatus();
		}
		finally {
			build.setBuildDuration(System.currentTimeMillis() - buildStartTime);
		}
	}

	public boolean isRunning() {
		return !executor.isShutdown();
	}

	/**
	 * Terminates/aborts all running tests as fast as possible. This method does
	 * not wait until the tests really have been terminated, use
	 * {@link TestExecutor#awaitTermination()} for this purpose.
	 * 
	 * @created 21.09.2012
	 */
	public void terminate() {
		// aborted, so discard build
		setTerminateStatus();
		// System.out.println("Terminating executor");
		executor.shutdownNow();
		if (executorThread != null) executorThread.interrupt();
	}

	/**
	 * Blocks/waits until all running tests are done, tests are not aborted or
	 * shutdown by calling this method. Given timeout defines the maximum waited
	 * time until the method returns.
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
		}
	}

	/**
	 * Blocks/waits until all running tests are done, tests are not aborted by
	 * calling this method.
	 * 
	 * @created 17.12.2013
	 */
	public void awaitTermination() {
		awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
	}

	private static <T> T cast(Object object, Class<T> objectClass) {
		// first check null, because Class.isInstance differs from
		// "instanceof"-operator for null objects
		if (object == null) return null;

		// check the type of the test-object
		if (!objectClass.isInstance(object)) {
			throw new ClassCastException();
		}
		// and securely cast
		return objectClass.cast(object);
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
		if (message != null) result.addUnexpectedMessage(testObjectName, message);
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
		private final T testObject;
		private final Test<T> test;
		private final TestResult testResult;
		private final String[] args;
		private final String testname;
		private final String[][] ignores;

		public CallableTest(TestSpecification<T> specification, String testObjectName, T testObject, TestResult testresult) {
			this.test = specification.getTest();
			this.testObject = testObject;
			this.testObjectName = testObjectName;
			this.testResult = testresult;
			this.args = specification.getArguments();
			this.ignores = specification.getIgnores();
			this.testname = specification.getTestName();
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
			return testname + ": " + testObjectName;
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
				Message message = test.execute(cast(testObject, test.getTestObjectClass()), args,
						ignores);
				if (message.getType().equals(Message.Type.SUCCESS)) {
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
			catch (Exception e) {
				String message = "Unexpected error in test " + testname + ", during testing "
						+ testObjectName;
				testResult.addUnexpectedMessage(testObjectName,
						new Message(Message.Type.ERROR,
								message + ": " + e));
				Log.severe(message, e);
				return null;
			}
		}
	}

}
