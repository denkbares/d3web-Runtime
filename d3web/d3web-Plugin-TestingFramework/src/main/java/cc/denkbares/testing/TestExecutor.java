package cc.denkbares.testing;

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
public class TestExecutor<T> {

	private final TestObjectProvider testObjectProvider;

	/**
	 * 
	 */
	public TestExecutor(TestObjectProvider provider) {
		this.testObjectProvider = provider;
	}

	public TestResult runTest(String testName, final String testObjectID, final String[] args) {

		final Test<T> t = findTest(testName);
		if (t == null) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Test not found: '" + testName + "'");
		}

		ExecutorService executor = Executors.newSingleThreadExecutor();

		Callable<TestResult> c = new Callable<TestResult>() {

			@Override
			public TestResult call() throws Exception {
				T testObject = testObjectProvider.getTestObject(t.getTestObjectClass(),
						testObjectID);
				// TODO: return result if no TestObject Found
				return t.execute(testObject,
							args);
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
		}

		return result;
	}

	/**
	 * 
	 * @created 04.05.2012
	 * @param testName
	 * @return
	 */
	private Test<T> findTest(String testName) {
		Extension[] extensions = PluginManager.getInstance().getExtensions(Test.PLUGIN_ID,
				Test.EXTENSION_POINT_ID);
		for (Extension extension : extensions) {
			if (extension instanceof Test) {
				@SuppressWarnings("unchecked")
				Test<T> t = (Test<T>) extension;
				if (t.getClass().getName().equals(testName)) {
					return t;
				}
				else {
					Logger.getLogger(getClass().getName()).warning(
							"extension of class '" + extension.getClass().getName() +
									"' is not of the extected type " + Test.class.getName());
				}
			}
		}
		return null;
	}
}
