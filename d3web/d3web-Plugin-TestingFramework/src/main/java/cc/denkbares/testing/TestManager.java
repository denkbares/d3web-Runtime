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
package cc.denkbares.testing;

import java.util.logging.Logger;

import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;

/**
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
			if (extension.getNewInstance() instanceof Test) {
				Test<?> t = (Test<?>) extension.getSingleton();
				if (t.getClass().getSimpleName().equals(testName)) {
					return t;
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

}
