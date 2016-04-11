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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;

/**
 * Utility class for plugins of the type TestObjectProvider.
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 30.05.2012
 */
public class TestObjectProviderManager {

	public static final Set<TestObjectProvider> registeredProviders = new HashSet<TestObjectProvider>();

	/**
	 * Can be used for tests to add special {@link TestObjectProvider}s without
	 * adding it as an extension.
	 * 
	 * @created 14.09.2012
	 */
	public static void registerTestObjectProvider(TestObjectProvider provider) {
		registeredProviders.add(provider);
	}

	public static List<TestObjectProvider> getTestObjectProviders() {
		Extension[] extensions = PluginManager.getInstance().getExtensions(Test.PLUGIN_ID,
				TestObjectProvider.EXTENSION_POINT_ID);
		List<TestObjectProvider> pluggedProviders = new ArrayList<TestObjectProvider>(
				registeredProviders);
		for (Extension extension : extensions) {
			if (extension.getNewInstance() instanceof TestObjectProvider) {
				TestObjectProvider t = (TestObjectProvider) extension.getSingleton();
				pluggedProviders.add(t);
			}
		}
		return pluggedProviders;
	}

	public static void clearRegisteredTestObjectProviders() {
		registeredProviders.clear();
	}
}
