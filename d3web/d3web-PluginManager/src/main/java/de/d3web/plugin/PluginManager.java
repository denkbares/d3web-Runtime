/*
 * Copyright (C) 2009 denkbares GmbH
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.plugin;

/**
 * Via this abstract class a Pluginmanager can be accessed. The pluginmanager
 * can be used to access the extensions
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public abstract class PluginManager {
	protected static PluginManager instance;

	/**
	 * This method is used to get an array of all Extensions, resgistered in this plugin manager.
	 * 
	 * @return array of all registered Extensions
	 */
	public abstract Extension[] getExtensions();

	/**
	 * This method is used to get an array of Extensions, which extend the
	 * extensionPoint represented by the extendedPointID in the Plugin
	 * represented by the extendedPluginID
	 * 
	 * @param extendedPluginID
	 *            ID of the Plugin of the extended ExtensionPoint
	 * @param extendedPointID
	 *            ID of the extended ExtensionPoint
	 * @return array of Extensions specified with the parameters
	 */
	public abstract Extension[] getExtensions(String extendedPluginID, String extendedPointID);

	/**
	 * This method is used to get an extension with the given parameters.
	 * 
	 * @param extendedPluginID
	 *            ID of the Plugin of the extended ExtensionPoint
	 * @param extendedPointID
	 *            ID of the extended ExtensionPoint
	 * @param extensionID
	 *            ID of the Extension
	 * @param extensionID 
	 * @return Extension specified with the parameters
	 */
	public abstract Extension getExtension(String extendetPluginID, String extendetPointID, String pluginID, String extensionID);

	/**
	 * Returns the list of all installed Plugins available to this manager.
	 * 
	 * @return the available Plugins
	 */
	public abstract Plugin[] getPlugins();

	/**
	 * Returns the plugin with the specified id
	 * 
	 * @param id
	 *            of the plugin
	 * @return the plugin with the speciefied id, if no plugin with the id
	 *         exists, null will be returned
	 */
	public abstract Plugin getPlugin(String id);

	/**
	 * The current instance of the PluginManager can be accessed with this
	 * method
	 * 
	 * @return Instance of the actual PluginManager
	 */
	public static PluginManager getInstance() {
		if (instance != null) {
			return instance;
		}
		else {
			throw new IllegalStateException(
					"Plugin Manager not initialized during application startup.");
		}
	}

}
