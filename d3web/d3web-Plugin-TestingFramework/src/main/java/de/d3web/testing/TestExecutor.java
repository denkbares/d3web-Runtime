package de.d3web.testing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import de.d3web.core.io.progress.ProgressListener;
import de.d3web.testing.Message.Type;

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
 * A TestExecutor executes a set of tests.
 * 
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 04.05.2012
 */
public class TestExecutor {

	private final Collection<TestObjectProvider> testObjectProviders;
	private final List<ExecutableTest> tests;
	private final ProgressListener progressListener;
	private BuildResult build;
	private float progress = 0;
	private String message = "Initializing...";
	private final List<String> currentlyRunning = Collections.synchronizedList(new LinkedList<String>());
	private float currentlyProcessedTaskVolune = 0;
	private float overallTasks;
	private ExecutorService executor;
	private Thread executorThread;
	private boolean terminated = false;

	/**
	 * Returns the current build or null if the build has been terminated.
	 * 
	 * @created 14.08.2012
	 * @return the current build
	 */
	public BuildResult getBuildResult() {
		if (Thread.interrupted()) return null;
		return build;
	}

	/**
	 * Creates a TestExecutor with the given task list and TestObjectProvider.
	 */
	public TestExecutor(Collection<TestObjectProvider> providers, List<ExecutableTest> testAndItsParameters, ProgressListener listener) {
		this.testObjectProviders = providers;
		this.tests = testAndItsParameters;
		this.progressListener = listener;
	}

	private static <T> T cast(Object testObject, Class<T> testObjectClass) {
		// first check null, because Class.isInstance differs from
		// "instanceof"-operator for null objects
		if (testObject == null) return null;

		// check the type of the test-object
		if (!testObjectClass.isInstance(testObject)) {
			throw new ClassCastException();
		}
		// and securely cast
		return testObjectClass.cast(testObject);
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
		build = new BuildResult();
		progress = 0f;
		executor = Executors.newFixedThreadPool(2);

		overallTasks = 0;

		Map<ExecutableTest, Map<TestObjectProvider, List<TestObjectContainer<?>>>> allTestsAndTestobjects =
				new HashMap<ExecutableTest, Map<TestObjectProvider, List<TestObjectContainer<?>>>>();

		checkAndAddTests(allTestsAndTestobjects);

		Map<ExecutableTest, Collection<CallableTest<?>>> futures = getFutures(allTestsAndTestobjects);

		executeTests(futures);

		shutdown(buildStartTime);
	}

	private void checkAndAddTests(Map<ExecutableTest, Map<TestObjectProvider, List<TestObjectContainer<?>>>> allTestsAndTestobjects) {
		prepareTests: for (ExecutableTest executableTest : tests) {

			String[] testArgs = executableTest.getArguments();
			String testObjectID = executableTest.getTestObject();
			Test<?> test = executableTest.getTest();

			// check arguments and create error if erroneous
			ArgsCheckResult argsCheckResult = test.checkArgs(testArgs);
			if (argsCheckResult.hasError()) {
				TestResult testResult = toTestResult(test, testObjectID, argsCheckResult);
				build.addTestResult(testResult);
				continue prepareTests;
			}

			// check ignores and create error if erroneous
			for (String[] ignoreArgs : executableTest.getIgnores()) {
				ArgsCheckResult ignoreCheckResult = test.checkIgnore(ignoreArgs);
				if (ignoreCheckResult.hasError()) {
					TestResult testResult = toTestResult(test, testObjectID, ignoreCheckResult);
					build.addTestResult(testResult);
					continue prepareTests;
				}
			}

			// only execute test if argument checks haven't created an error
			allTestsAndTestobjects.put(executableTest, collectTestObjects(test, testObjectID));
		}
	}

	private Map<ExecutableTest, Collection<CallableTest<?>>> getFutures(Map<ExecutableTest, Map<TestObjectProvider, List<TestObjectContainer<?>>>> allTestsAndTestobjects) {
		Map<ExecutableTest, Collection<CallableTest<?>>> futures = new HashMap<ExecutableTest, Collection<CallableTest<?>>>();

		for (ExecutableTest test : allTestsAndTestobjects.keySet()) {
			String[] testArgs = test.getArguments();
			String testName = test.getTestName();
			// create result
			TestResult testResult = new TestResult(testName, testArgs);
			build.addTestResult(testResult);
			Collection<CallableTest<?>> futuresForCallableTest = getCallableTests(testName,
					test.getTest(), testArgs, allTestsAndTestobjects.get(test), testResult);
			futures.put(test, futuresForCallableTest);
			overallTasks += futuresForCallableTest.size();
		}
		return futures;
	}

	private <T> Collection<CallableTest<?>> getCallableTests(
			final String testName,
			final Test<T> test,
			final String[] args,
			final Map<TestObjectProvider, List<TestObjectContainer<?>>> allTestObjects,
			final TestResult testResult) {

		Collection<CallableTest<?>> result = new HashSet<CallableTest<?>>();
		boolean noTestObjects = true;
		for (final TestObjectProvider testObjectProvider : testObjectProviders) {
			for (final TestObjectContainer<?> testObjectContainer : allTestObjects.get(testObjectProvider)) {
				noTestObjects = false;
				String testObjectName = testObjectContainer.getTestObjectName();
				Object testObject = testObjectContainer.getTestObject();
				CallableTest<T> callableTest = new CallableTest<T>(testObjectName, testObject,
						test, testName,
						testResult, args);
				result.add(callableTest);
			}
		}
		if (noTestObjects) {
			testResult.addMessage("", new Message(Message.Type.ERROR, "No test-object found."));
		}
		return result;
	}

	private void executeTests(Map<ExecutableTest, Collection<CallableTest<?>>> futures) {
		// finally run execute on callable tests
		Set<ExecutableTest> keySet = futures.keySet();
		outerLoop: for (ExecutableTest executableTest : keySet) {
			Collection<CallableTest<?>> tasks = futures.get(executableTest);
			for (CallableTest<?> callableTest : tasks) {
				executor.execute(new FutureTestTask(callableTest));

				try {
					Utils.checkInterrupt();
				}
				catch (InterruptedException e) {
					progressListener.updateProgress(1f, "Aborted, please wait...");
					executor.shutdownNow();
					// build is discarded, method call terminated
					build = null;
					break outerLoop;
				}
			}
		}
	}

	private void shutdown(long buildStartTime) {
		try {
			executor.shutdown();
			while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
				build.setBuildDuration(System.currentTimeMillis() - buildStartTime);
			}
		}
		catch (InterruptedException e) {
			progressListener.updateProgress(1f, "Aborted, please wait...");
		}
		finally {
			if (terminated) {
				build = null;
			}
		}
	}

	/**
	 * Terminates all running tests as fast as possible. Returns at the latest
	 * after 5 seconds, even if the termination is not quite complete.
	 * 
	 * @created 21.09.2012
	 */
	public void terminate() {
		// aborted, so discard build
		terminated = true;
		// System.out.println("Terminating executor");
		executor.shutdownNow();
		executorThread.interrupt();
		try {
			executor.awaitTermination(5, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) {
		}
	}

	/**
	 * Runs the given test with the given parameters on a test-object specified
	 * by the string testObjectID.
	 * 
	 * @created 22.05.2012
	 * @param <T>
	 * @param test The test to be executed.
	 * @param testObjectID Identifier for the test-object
	 * @return
	 */
	private <T> Map<TestObjectProvider, List<TestObjectContainer<?>>> collectTestObjects(Test<T> test, String testObjectID) {

		final Map<TestObjectProvider, List<TestObjectContainer<?>>> allTestObjects = new HashMap<TestObjectProvider, List<TestObjectContainer<?>>>();

		// retrieve all test objects
		for (TestObjectProvider testObjectProvider : testObjectProviders) {

			List<TestObjectContainer<T>> testObjects = testObjectProvider.getTestObjects(
					test.getTestObjectClass(), testObjectID);
			List<TestObjectContainer<?>> genericTestObjects = castTestObjects(testObjects);
			allTestObjects.put(testObjectProvider, genericTestObjects);

		}

		return allTestObjects;
	}

	private <T> List<TestObjectContainer<?>> castTestObjects(List<TestObjectContainer<T>> testObjects) {
		// generics fail here, so we need to cast
		List<TestObjectContainer<?>> genericTestObjects = new ArrayList<TestObjectContainer<?>>(
				testObjects.size());
		for (TestObjectContainer<?> testObjectContainer : testObjects) {
			genericTestObjects.add(new TestObjectContainer<Object>(
					testObjectContainer.getTestObjectName(),
					testObjectContainer.getTestObject()));
		}
		return genericTestObjects;
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
		if (message != null) result.addMessage(testObjectName, message);
		return result;
	}

	private synchronized void updateProgressListener() {
		progress = currentlyProcessedTaskVolune / overallTasks;
		progressListener.updateProgress(progress, message);
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
		private final Object testObject;
		private final Test<T> test;
		private final TestResult testResult;
		private final String[] args;
		private final String testname;

		public CallableTest(String testObjectName, Object testObject, Test<T> test, String testname, TestResult testresult, String[] args) {
			this.test = test;
			this.testObject = testObject;
			this.testObjectName = testObjectName;
			this.testResult = testresult;
			this.args = args;
			this.testname = testname;
		}

		public void testStarted() {
			currentlyRunning.add(getMessage());
			message = getMessage();
			updateProgressListener();
		}

		public void testFinished() {
			currentlyRunning.remove(getMessage());
			String tempMessage = null;
			try {
				tempMessage = currentlyRunning.get(0);
			}
			catch (IndexOutOfBoundsException e) {
				// since another thread could remove the last element between
				// checking for not empty and actually retrieving it, we just
				// try to retrieve it and catch a possible exception
			}
			if (tempMessage != null) message = tempMessage;
			currentlyProcessedTaskVolune += getTaskVolume();
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
					testResult.addMessage(testObjectName, new Message(
							Message.Type.ERROR, "Test-object was null."));
					return null;
				}
				Message message = test.execute(cast(testObject, test.getTestObjectClass()), args);
				testResult.addMessage(testObjectName,
						message);
				return null;
			}
			catch (InterruptedException e) {
				throw e;
			}
			catch (Throwable e) {
				testResult.addMessage(testObjectName,
						new Message(Message.Type.ERROR,
								"Unexpected error in test " + testname + ", during testing "
										+ testObjectName +
										": " + e));
				return null;
			}
		}
	}

}
