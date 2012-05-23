package cc.denkbares.testing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;

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
 * @author jochenreutelshofer
 * @created 04.05.2012
 */
public class TestExecutor {

	@SuppressWarnings("rawtypes")
	private final TestObjectProvider testObjectProvider;
	private final List<Pair<String, List<String>>> tests;

	/**
	 * Creates a TestExecutor with the given task list and TestObjectProvider.
	 */
	public TestExecutor(@SuppressWarnings("rawtypes") TestObjectProvider provider, List<Pair<String, List<String>>> testAndItsParameters) {
		this.testObjectProvider = provider;
		this.tests = testAndItsParameters;
	}

	@SuppressWarnings("unchecked")
	private static <T> T cast(Object testObject, Class<T> testObjectClass) {
		// first check null, because Class.isInstance differs from
		// "instanceof"-operator for null objects
		if (testObject == null) return null;

		// check the type of the test-object
		if (!testObjectClass.isInstance(testObject)) {
			throw new ClassCastException();
		}
		// and securely cast
		return (T) testObject;
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
	public BuildResultSet runtTests(int buildNumber) {
		long buildStartTime = System.currentTimeMillis();
		BuildResultSet build = new BuildResultSet(buildNumber);

		for (Pair<String, List<String>> testAndItsParameters : tests) {

			String testName = testAndItsParameters.getA();
			List<String> parameters = testAndItsParameters.getB();

			String[] array = parameters.toArray(new String[] {});
			Set<TestResult> testResults = this.runTest(testName, array[0],
						array);
			if (testResults != null) {
				for (TestResult testResult : testResults) {
					build.addTestResult(testResult);
				}
			}
		}

		build.setBuildDuration(System.currentTimeMillis() - buildStartTime);
		return build;
	}

	/**
	 * Retrieves and runs the test with the given name.
	 * 
	 * 
	 * @created 22.05.2012
	 * @param <T>
	 * @param t The test to be executed.
	 * @param testObjectID Identifier for the test-object
	 * @param args The parameters for the test execution
	 * @return
	 */
	public Set<TestResult> runTest(String testName, final String testObjectID, final String[] args) {
		Test<?> t = findTest(testName);
		if (t == null) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Test not found: '" + testName
							+ "'");
			Set<TestResult> result = new HashSet<TestResult>();
			result.add(new TestResultImpl(new Message(Message.Type.ERROR,
					"Test could not be found on the system."), testName, Utils.concat(args)));
			return result;
		}
		else {

			return runTest(t, testObjectID, args);
		}
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
	public <T> Set<TestResult> runTest(final Test<T> t, final String testObjectID, final String[] args) {

		ArgsCheckResult argsCheckResult = t.checkArgs(args);
		if (argsCheckResult.getType().equals(ArgsCheckResult.Type.ERROR)) {
			Set<TestResult> set = new HashSet<TestResult>();
			String message = argsCheckResult.getType().toString();
			if (argsCheckResult.getMessage() != null) {
				message += ": " + argsCheckResult.getMessage();
			}
			set.add(new TestResultImpl(new Message(Message.Type.ERROR, "Invalid arguments: "
					+ message), t.getClass().getSimpleName(), Utils.concat(args)));
			return set;
		}

		ExecutorService executor = Executors.newSingleThreadExecutor();

		Callable<Set<TestResult>> c = new Callable<Set<TestResult>>() {

			@Override
			public Set<TestResult> call() throws Exception {
				Set<TestResult> set = new HashSet<TestResult>();
				@SuppressWarnings("unchecked")
				List<?> testObjects = testObjectProvider.getTestObject(t.getTestObjectClass(),
						testObjectID);
				if (testObjects.size() == 0) {
					set.add(new TestResultImpl(new Message(Message.Type.ERROR,
							"No test-object found. (Check TestObjectProviders)"),
							t.getClass().getSimpleName(),
							Utils.concat(args)));

				}

				for (Object testObject : testObjects) {
					set.add(new TestResultImpl(t.execute(cast(testObject, t.getTestObjectClass()),
							args), t.getClass().getSimpleName(), Utils.concat(args)));
				}
				return set;
			}

		};
		Future<Set<TestResult>> future = executor.submit(c);

		Set<TestResult> result = null;
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

	/**
	 * Searches within the plugged tests for a test with a specific name.
	 * Returns null if the test is not found.
	 * 
	 * @created 04.05.2012
	 * @param testName
	 * @return
	 */
	private Test<?> findTest(String testName) {
		Extension[] extensions = PluginManager.getInstance().getExtensions(Test.PLUGIN_ID,
				Test.EXTENSION_POINT_ID);
		for (Extension extension : extensions) {
			if (extension.getNewInstance() instanceof Test) {
				Test<?> t = (Test<?>) extension.getSingleton();
				if (t.getClass().getSimpleName().equals(testName)) {
					return t;
				}
			}
			else {
				Logger.getLogger(getClass().getName()).warning(
						"extension of class '" + extension.getClass().getName() +
								"' is not of the expected type " + Test.class.getName());
			}
		}
		return null;
	}
}
