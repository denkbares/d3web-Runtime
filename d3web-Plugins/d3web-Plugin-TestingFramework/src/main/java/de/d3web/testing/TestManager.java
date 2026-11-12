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
package de.d3web.testing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.denkbares.plugin.Extension;
import com.denkbares.plugin.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for plugins of the type Test.
 *
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 30.05.2012
 */
public class TestManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestManager.class);

	/**
	 * Searches within the plugged tests for a test with a specific name. Returns null if the test is not found.
	 *
	 * @param testName the test to be searched
	 * @return the test singleton
	 * @created 04.05.2012
	 */
	public static Test<?> findTest(String testName) {
		Test<?> test = findTestByParameter(testName, "name");
		// we didn't find a test with the name, lets try deprecated names
		// (which might not by unique, we just use the first...)
		if (test == null) test = findTestByParameter(testName, "alternativeName");
		return test;
	}

	private static Test<?> findTestByParameter(String testName, String parameterName) {
		Extension[] extensions = PluginManager.getInstance().getExtensions(Test.PLUGIN_ID, Test.EXTENSION_POINT_ID);
		for (Extension extension : extensions) {
			// check the declaration first, so only the requested test gets instantiated
			List<String> parameters = extension.getParameters(parameterName);
			if (parameters == null || !parameters.contains(testName)) continue;
			Object test = getSingleton(extension);
			if (test instanceof Test) {
				return (Test<?>) test;
			}
			if (test != null) {
				LOGGER.warn("extension of class '" + test.getClass().getName() +
						"' is not of the expected type " + Test.class.getName());
			}
		}
		return null;
	}

	/**
	 * Returns the singleton of the specified test extension, or null if it cannot be instantiated, e.g. because its
	 * class is not on the classpath. Skipping it prevents a single broken declaration from breaking the lookup of all
	 * other tests.
	 */
	private static Object getSingleton(Extension extension) {
		try {
			return extension.getSingleton();
		}
		catch (LinkageError e) {
			LOGGER.warn("Skipping test extension '{}' of plugin '{}', it cannot be instantiated",
					extension.getID(), extension.getPluginID(), e);
			return null;
		}
	}

	/**
	 * Returns the name of the specified test as declared in the plugin declaration. The method returns null if the test
	 * cannot be found. This may only occur if the specified test instance is not the singleton specified through an
	 * extension declaration and if it if also not "equals" to such a singleton.
	 *
	 * @param test the test to get the name for
	 * @return the name of the test as specified in the extension
	 * @created 15.09.2012
	 */
	public static String getTestName(Test<?> test) {
		Extension[] extensions = PluginManager.getInstance().getExtensions(Test.PLUGIN_ID, Test.EXTENSION_POINT_ID);
		for (Extension extension : extensions) {
			Object singleton = getSingleton(extension);
			if (singleton != null && singleton.equals(test)) return extension.getName();
		}
		return null;
	}

	/**
	 * Searches and returns all test plugged into the current installation.
	 *
	 * @return the plugged test extensions
	 * @created 31.07.2012
	 */
	public static List<Test<?>> findAllTests() {
		List<Test<?>> result = new ArrayList<>();
		Extension[] extensions = PluginManager.getInstance().getExtensions(Test.PLUGIN_ID, Test.EXTENSION_POINT_ID);
		for (Extension extension : extensions) {
			Object singleton = getSingleton(extension);
			if (singleton instanceof Test) {
				result.add((Test<?>) singleton);
			}
			else if (singleton != null) {
				LOGGER.warn("extension of class '" + extension.getClass().getName() +
						"' is not of the expected type " + Test.class.getName());
			}
		}
		return result;
	}

	/**
	 * Searches and returns the name of all plugged tests.
	 *
	 * @return the test names
	 * @created 07.08.2012
	 */
	public static List<String> findAllTestNames() {
		List<String> result = new ArrayList<>();
		Extension[] extensions = PluginManager.getInstance().getExtensions(Test.PLUGIN_ID, Test.EXTENSION_POINT_ID);
		for (Extension extension : extensions) {
			result.add(extension.getName());
		}
		Collections.sort(result);
		return result;
	}
}
