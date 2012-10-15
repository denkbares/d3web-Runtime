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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;

/**
 * Utility class for plugins of the type Test.
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 30.05.2012
 */
public class TestManager {

	/**
	 * Searches within the plugged tests for a test with a specific name.
	 * Returns null if the test is not found.
	 * 
	 * @created 04.05.2012
	 * @param testName
	 * @return
	 */
	public static Test<?> findTest(String testName) {
		Extension[] extensions = PluginManager.getInstance().getExtensions(Test.PLUGIN_ID,
				Test.EXTENSION_POINT_ID);
		for (Extension extension : extensions) {
			Object singleton = extension.getSingleton();
			if (singleton instanceof Test) {
				if (extension.getName().equals(testName)) {
					return (Test<?>) singleton;
				}
			}
			else {
				Logger.getLogger(TestManager.class.getName()).warning(
						"extension of class '" + extension.getClass().getName() +
								"' is not of the expected type " + Test.class.getName());
			}
		}
		return null;
	}

	/**
	 * Returns the name of the specified test as declared in the plugin
	 * declaration. The method returns null if the test cannot be found. This
	 * may only occur if the specified test instance is not the singleton
	 * specified through an extension declaration and if it if also not "equals"
	 * to such a singleton.
	 * 
	 * @created 15.09.2012
	 * @param test the test to get the name for
	 * @return the name of the test as specified in the extension
	 */
	public static String getTestName(Test<?> test) {
		Extension[] extensions = PluginManager.getInstance().getExtensions(Test.PLUGIN_ID,
				Test.EXTENSION_POINT_ID);
		for (Extension extension : extensions) {
			Object singleton = extension.getSingleton();
			if (singleton.equals(test)) return extension.getName();
		}
		return null;
	}

	/**
	 * Searches and returns all plugged tests.
	 * 
	 * @created 31.07.2012
	 * @return
	 */
	public static List<Test<?>> findAllTests() {
		List<Test<?>> result = new ArrayList<Test<?>>();
		Extension[] extensions = PluginManager.getInstance().getExtensions(Test.PLUGIN_ID,
				Test.EXTENSION_POINT_ID);
		for (Extension extension : extensions) {
			Object singleton = extension.getSingleton();
			if (singleton instanceof Test) {
				result.add((Test<?>) singleton);
			}
			else {
				Logger.getLogger(TestManager.class.getName()).warning(
						"extension of class '" + extension.getClass().getName() +
								"' is not of the expected type " + Test.class.getName());
			}
		}
		return result;
	}

	/**
	 * Searches and returns the name of all plugged tests.
	 * 
	 * @created 07.08.2012
	 * @return the test names
	 */
	public static List<String> findAllTestNames() {
		List<String> result = new LinkedList<String>();
		Extension[] extensions = PluginManager.getInstance().getExtensions(Test.PLUGIN_ID,
				Test.EXTENSION_POINT_ID);
		for (Extension extension : extensions) {
			result.add(extension.getName());
		}
		return result;
	}

}
