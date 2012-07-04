package de.d3web.testing;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.d3web.core.io.progress.CombinedProgressListener;
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
	private final CombinedProgressListener progressListener;

	/**
	 * Creates a TestExecutor with the given task list and TestObjectProvider.
	 */
	public TestExecutor(Collection<TestObjectProvider> providers, List<ExecutableTest> testAndItsParameters, CombinedProgressListener listener) {
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
	public BuildResult runtTests(int buildNumber) {
		long buildStartTime = System.currentTimeMillis();
		BuildResult build = new BuildResult(buildNumber);

		Map<ExecutableTest, Map<TestObjectProvider, List<?>>> allTestsAndTestobjects = new HashMap<ExecutableTest, Map<TestObjectProvider, List<?>>>();

		int testObjectsTotal = 0;
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
				testObjectsTotal += countTestObjects(testObjectsForTest);
			}
		}

		// now the total count of tests to be executed is clear and can be told
		// to the progress listener
		progressListener.setTotalSize(testObjectsTotal);

		for (ExecutableTest test : allTestsAndTestobjects.keySet()) {
			// next major step for combined progress listener
			// here do a normalization of the task weights assuming that all
			// major steps have equal weight
			progressListener.next((long) ((float) testObjectsTotal / allTestsAndTestobjects.size()));

			String[] array = test.getArguments();
			String[] testArgs = Arrays.copyOfRange(array, 1,
					array.length); // strip the first argument since it always
									// is the test object name
			TestResult testResult = executeTests(test.getTest(), testArgs,
					allTestsAndTestobjects.get(test));

			if (testResult == null) {
				testResult = new TestResult(
						test.getTest().getClass().getSimpleName(),
						test.getArguments());
				testResult.addMessage(test.getArguments()[0], new Message(
						Message.Type.ERROR,
						"Unexpected error while executing test."));
			}
			build.addTestResult(testResult);
		}

		build.setBuildDuration(System.currentTimeMillis() - buildStartTime);
		return build;
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

	private <T> TestResult executeTests(final Test<T> t, final String[] args, final Map<TestObjectProvider, List<?>> allTestObjects) {
		final String testName = t.getClass().getSimpleName();
		ExecutorService executor = Executors.newSingleThreadExecutor();

		Callable<TestResult> c = new Callable<TestResult>() {

			@Override
			public TestResult call() throws Exception {
				TestResult result = new TestResult(testName, args);

				int totalCountOfTestobjects = countTestObjects(allTestObjects);

				// finally run the tests
				int currentIndex = 0;
				for (TestObjectProvider testObjectProvider : testObjectProviders) {
					for (Object testObject : allTestObjects.get(testObjectProvider)) {

						if (testObject != null) {
							Message message = t.execute(cast(testObject, t.getTestObjectClass()),
									args);
							result.addMessage(testObjectProvider.getTestObjectName(testObject),
									message);
						}
						else {
							result.addMessage(testName, new Message(Message.Type.ERROR,
									"Test-object was null. (Check TestObjectProviders)"));
						}
						currentIndex++;
						// update progress listener
						progressListener.updateProgress(
								((float) (currentIndex)) / totalCountOfTestobjects,
								t.getClass().getSimpleName() + ": "
										+ testObjectProvider.getTestObjectName(testObject));

					}

				}

				if (totalCountOfTestobjects == 0) {
					result.addMessage(testName, new Message(Message.Type.ERROR,
							"No test-object found. (Check TestObjectProviders)"));

				}
				return result;
			}

		};
		Future<TestResult> future = executor.submit(c);

		TestResult result = null;
		try {
			result = future.get();
		}
		catch (InterruptedException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Test execution was interrupted: '" + t.toString() + "'");
		}
		catch (ExecutionException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Exception in Test execution: '" + t.toString() + "'");
			e.printStackTrace();
		}

		return result;
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
