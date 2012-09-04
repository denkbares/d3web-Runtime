package de.d3web.testing;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
	private final int buildNumber;
	private BuildResult build;
	private float progress = 0;
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
	public TestExecutor(Collection<TestObjectProvider> providers, List<ExecutableTest> testAndItsParameters, ProgressListener listener, int buildNumber) {
		this.testObjectProviders = providers;
		this.tests = testAndItsParameters;
		this.progressListener = listener;
		this.buildNumber = buildNumber;
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

	private synchronized void taskFinished(float volume, String message) {
		currentlyProcessedTaskVolune += volume;
		this.progressListener.updateProgress(currentlyProcessedTaskVolune / overallTasks, message);
	}

	class MyFutureTask extends FutureTask<Void> {

		private final TestExecutor resultHandler;
		private final Callable<Void> myCallable;

		public MyFutureTask(Callable<Void> callable, TestExecutor resultHandler) {
			super(callable);
			this.resultHandler = resultHandler;
			myCallable = callable;
		}

		@Override
		protected void done() {
			// update progress listener as task has been finished
			if (this.myCallable instanceof CallableTest) {
					resultHandler.taskFinished(((CallableTest<?>) myCallable).getTaskVolume(),
							((CallableTest<?>) myCallable).getMessage());
			}
		}
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
		build = new BuildResult(buildNumber);
		progress = 0f;
		executor = Executors.newFixedThreadPool(2);

		overallTasks = 0;

		Map<ExecutableTest, Map<TestObjectProvider, List<?>>> allTestsAndTestobjects = new HashMap<ExecutableTest, Map<TestObjectProvider, List<?>>>();

		for (ExecutableTest testAndItsParameters : tests) {

			String[] array = testAndItsParameters.getArguments();

			String[] testArgs = Arrays.copyOfRange(array, 1,
					array.length);
			String testObjectID = array[0];
			TestResult checkArgs = checkArgs(testAndItsParameters.getTest(), testObjectID,
					testArgs);

			if (checkArgs != null && checkArgs.getType().equals(Type.ERROR)) {
				build.addTestResult(checkArgs);
			}
			else {
				Map<TestObjectProvider, List<?>> testObjectsForTest = this.collectTestObjects(
						testAndItsParameters.getTest(), testObjectID,
						testArgs
						);


				allTestsAndTestobjects.put(testAndItsParameters, testObjectsForTest);
			}
		}

		Map<ExecutableTest, Collection<CallableTest<?>>> futures = new HashMap<ExecutableTest, Collection<CallableTest<?>>>();

		// now the total count of tests to be executed is clear and can be told
		// to the progress listener

		for (ExecutableTest test : allTestsAndTestobjects.keySet()) {
			String[] array = test.getArguments();
			// strip the first argument since it always is the test object name
			String[] testArgs = Arrays.copyOfRange(array, 1, array.length);
			String testName = test.getTestName();
			TestResult testResult = new TestResult(testName, testArgs);
			build.addTestResult(testResult);
			// try { // testing purposes only!!
			// Thread.sleep(10000);
			// }
			// catch (InterruptedException e1) {
			// // TODO Auto-generated catch block
			// e1.printStackTrace();
			// }
			try {
				Collection<CallableTest<?>> futuresForExecutableTest = executeTests(testName,
						test.getTest(),
						testArgs, allTestsAndTestobjects.get(test), testResult);
				futures.put(test, futuresForExecutableTest);
				overallTasks += futuresForExecutableTest.size();
			}
			catch (InterruptedException e) {
				progressListener.updateProgress(1f, "aborted, please wait...");
				executor.shutdownNow();
				// build is discarded, method call terminated
				build = null;
				return;
			}

		}


		// finally run execute on callable tests
		Set<ExecutableTest> keySet = futures.keySet();
		for (ExecutableTest executableTest : keySet) {
			Collection<CallableTest<?>> tasks = futures.get(executableTest);
			for (CallableTest<?> callableTest : tasks) {
				executor.execute(new MyFutureTask(callableTest, this));

				try {
					Utils.checkInterrupt();
				}
				catch (InterruptedException e) {
					progressListener.updateProgress(1f, "aborted, please wait...");
					executor.shutdownNow();
					// build is discarded, method call terminated
					build = null;
					return;
				}

			}
		}
		


		// wait until finished
		try {
			executor.shutdown();
			while (!executor.awaitTermination(1, TimeUnit.SECONDS))
				;
			build.setBuildDuration(System.currentTimeMillis() - buildStartTime);
		}
		catch (InterruptedException e) {
			progressListener.updateProgress(1f, "aborted, please wait...");
		}

		if (terminated) {
			// aborted, so discard build
			build = null;
		}
	}

	public void terminate() {
		// System.out.println("Terminating executor");
		executor.shutdownNow();
		executorThread.interrupt();
		terminated = true;
	}

	/**
	 * Runs the given test with the given parameters on a test-object specified
	 * by the string testObjectID.
	 * 
	 * @created 22.05.2012
	 * @param <T>
	 * @param t The test to be executed.
	 * @param testObjectID Identifier for the test-object
	 * @param args The parameters for the test execution
	 * @return
	 */
	public <T> Map<TestObjectProvider, List<?>> collectTestObjects(final Test<T> t, final String testObjectID, final String[] args) {

		final Map<TestObjectProvider, List<?>> allTestObjects = new HashMap<TestObjectProvider, List<?>>();

		// retrieve all test objects
		for (TestObjectProvider testObjectProvider : testObjectProviders) {

			List<? extends Object> testObjects = testObjectProvider.getTestObjects(
					t.getTestObjectClass(),
					testObjectID);
			allTestObjects.put(testObjectProvider, testObjects);

		}

		return allTestObjects;
	}

	private <T> TestResult checkArgs(final Test<T> t, final String testObjectID, final String[] args) {

		ArgsCheckResult argsCheckResult = t.checkArgs(args);
		if (argsCheckResult.hasError()) {
			TestResult result = new TestResult(t.getClass().getSimpleName(),
					args);
			String[] arguments = argsCheckResult.getArguments();
			for (int i = 0; i < arguments.length; i++) {
				if (argsCheckResult.hasError(i)) {
					renderMessage(testObjectID, args, argsCheckResult, result, i, "Error");
				}
				if (argsCheckResult.hasWarning(i)) {
					renderMessage(testObjectID, args, argsCheckResult, result, i, "Warning");
				}
			}
			if (arguments.length == 0 && argsCheckResult.hasError(0)) {
				renderMessage(testObjectID, args, argsCheckResult, result, 0, "Error");
			}

			return result;
		}
		return null;
	}

	private static int countTestObjects(Map<TestObjectProvider, List<?>> allTestObjects) {
		int count = 0;
		Set<TestObjectProvider> keySet = allTestObjects.keySet();
		for (TestObjectProvider p : keySet) {
			List<?> list = allTestObjects.get(p);
			count += list.size();
		}
		return count;
	}

	private <T> Collection<CallableTest<?>> executeTests(final String testName, final Test<T> t, final String[] args, final Map<TestObjectProvider, List<?>> allTestObjects, final TestResult testResult) throws InterruptedException {

		int totalCountOfTestobjects = countTestObjects(allTestObjects);

		if (totalCountOfTestobjects == 0) {
			final Message message = new Message(Message.Type.ERROR, "No test-object found.");
			testResult.addMessage("", message);

			progress += 1f / tests.size();
			progressListener.updateProgress(progress, testName);
		}

		Collection<CallableTest<?>> result = new HashSet<CallableTest<?>>();

		// finally run the tests
		for (final TestObjectProvider testObjectProvider : testObjectProviders) {
			for (final Object testObject : allTestObjects.get(testObjectProvider)) {
				String testObjectName = testObjectProvider.getTestObjectName(testObject);
				CallableTest<T> c = new CallableTest<T>(testObjectName, testObject, t, testName,
						testResult, args);
				result.add(c);
				// try {
				// Future<Void> future = executor.submit(c);
				// }
				// catch (RejectedExecutionException e) {
				// // happens when Executor is shut down
				// // System.out.println("RejectedExecutionException");
				// }
			}
		}
		return result;
	}

	class CallableTest<T> implements Callable<Void> {

		private final String testObjectName;
		private final Object testObject;
		private final Test<T> t;
		private final TestResult testResult;
		private final String[] args;
		private final String testname;

		public CallableTest(String testObjectName, Object testObject, Test<T> t, String testname, TestResult testresult, String[] args) {
			this.t = t;
			this.testObject = testObject;
			this.testObjectName = testObjectName;
			this.testResult = testresult;
			this.args = args;
			this.testname = testname;
		}

		public float getTaskVolume() {
			// maybe this can be derived somehow more precisely some time
			return 1;
		}

		public String getMessage() {
			return testname + ": "
					+ testObjectName;
		}

		@Override
		public Void call() throws Exception {

			// for testing only
			// Thread.sleep(500);

			try {
				if (testObject == null) {
					testResult.addMessage(testObjectName, new Message(
							Message.Type.ERROR, "Test-object was null."));
					return null;
				}
				Message message = t.execute(cast(testObject, t.getTestObjectClass()), args);
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
			// finally {
			// // update progress listener
			// progress += progessIncrement;
			// progressListener.updateProgress(progress, t + ": "
			// + testObjectName);
			//
			// }
		}

	}

	private void renderMessage(final String testObjectID, final String[] args, ArgsCheckResult argsCheckResult, TestResult result, int i, String type) {
		if (argsCheckResult.getMessage(i) != null) {
			String arg = "none";
			if (i < args.length) {
				arg = args[i];
			}
			String message = type + ": " + argsCheckResult.getMessage(i);
			result.addMessage(testObjectID, new Message(Message.Type.ERROR,
					"Invalid argument: "
							+ arg + " (" + message + ")"));
		}
	}

}
