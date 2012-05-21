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
 * 
 * @author jochenreutelshofer
 * @created 04.05.2012
 */
public class TestExecutor {

	private final TestObjectProvider testObjectProvider;

	/**
	 * 
	 */
	public TestExecutor(TestObjectProvider provider) {
		this.testObjectProvider = provider;
	}

	@SuppressWarnings("unchecked")
	private static <T> T cast(Object testObject, Class<T> testObjectClass) {
		// first check null, because Class.isInstance differs from
		// "instanceof"-operator for null objects
		if (testObject == null) return null;

		// check the type of the section
		if (!testObjectClass.isInstance(testObject)) {
			throw new ClassCastException();
		}
		// and securely cast
		return (T) testObject;
	}

	public Set<TestResult> runTest(String testName, final String testObjectID, final String[] args) {
		Test<?> t = findTest(testName);
		if (t == null) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Test not found: '" + testName
							+ "'");
			Set<TestResult> result = new HashSet<TestResult>();
			result.add(new TestResultImpl(new Message(Message.Type.ERROR,
					"Test could not be found on the system."), testName, args.toString()));
			return result;
		}
		else {

			return runTest(t, testObjectID, args);
		}
	}

	public <T> Set<TestResult> runTest(final Test<T> t, final String testObjectID, final String[] args) {

		ArgsCheckResult argsCheckResult = t.checkArgs(args);
		if (argsCheckResult.getType().equals(ArgsCheckResult.Type.ERROR)) {
			Set<TestResult> set = new HashSet<TestResult>();
			set.add(new TestResultImpl(new Message(Message.Type.ERROR, "Invalid arguments: "
					+ argsCheckResult.toString()), t.toString(), args.toString()));
		}

		ExecutorService executor = Executors.newSingleThreadExecutor();

		Callable<Set<TestResult>> c = new Callable<Set<TestResult>>() {

			@Override
			public Set<TestResult> call() throws Exception {
				List<?> testObjects = testObjectProvider.getTestObject(t.getTestObjectClass(),
						testObjectID);
				// TODO: return result if no TestObject Found

				Set<TestResult> set = new HashSet<TestResult>();
				for (Object testObject : testObjects) {
					set.add(new TestResultImpl(t.execute(cast(testObject, t.getTestObjectClass()),
							args), t.toString(), args.toString()));
				}
				return set;
			}

		};
		Future<Set<TestResult>> future = executor.submit(c);

		Set<TestResult> result = null;
		try {
			result = future.get();
			for (TestResult testResult : result) {
				testResult.setConfiguration(args.toString());
			}
		}
		catch (InterruptedException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Test execution was interrupted: '" + t.toString() + "'");
		}
		catch (ExecutionException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Exception in Test execution: '" + t.toString() + "'");
		}

		return result;
	}

	/**
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
