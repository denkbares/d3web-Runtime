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

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.java.plugin.JpfException;
import org.java.plugin.ObjectFactory;
import org.java.plugin.PluginManager.PluginLocation;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.Identity;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.standard.StandardPluginLocation;

import de.d3web.plugin.util.PluginCollectionComparatorByPriority;

/**
 * An implementation of the PluginManager for the Java Plugin Framework (JPF)
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class JPFPluginManager extends PluginManager {

	private final org.java.plugin.PluginManager manager;
	
	private HashMap<org.java.plugin.registry.Extension, Extension> cachedExtension = new HashMap<org.java.plugin.registry.Extension, Extension>();

	/**
	 * Contains the registered Plugins. The field will be initialized lazy by
	 * the {@link #getPlugins()} method.
	 */
	private Plugin[] plugins = null;

	private JPFPluginManager(File[] pluginFiles) throws JpfException {
		this.manager = ObjectFactory.newInstance().createManager();

		List<PluginLocation> locations = new ArrayList<PluginLocation>();
		for (File pluginFile : pluginFiles) {
			try {
				PluginLocation location = StandardPluginLocation.create(pluginFile);
				if (location != null) {
					locations.add(location);
				}
				else {
					Logger.getLogger("PluginManager").warning(
							"File '" + pluginFile
							+ "' is not a plugin. It will be ignored.");
				}
			}
			catch (MalformedURLException e) {
				Logger.getLogger("PluginManager").severe(
						"error initializing plugin '" + pluginFile + "': " + e);
			}
		}
		Map<String, Identity> map = manager.publishPlugins(locations.toArray(new PluginLocation[locations.size()]));
		//activate all plugins
		for (Identity i: map.values()) {
			manager.activatePlugin(i.getId());
		}
	}

	/**
	 * This method initializes the JPFPluginmanager as PluginManager (which can
	 * be accessed via PluginManager.getInstance()) with the directory of the
	 * plugins as a String.
	 * <p>
	 * If the manager could not be initialized with the specified directory (for
	 * any reason), an IllegalArgumentException is thrown.
	 * 
	 * @param directory
	 *            directory of the plugins
	 * @throws IllegalArgumentException
	 *             the directory could not be used for initialization
	 */
	public static void init(String directory) {
		if (instance!=null) {
			Logger.getLogger("PluginManager").warning(
					"PluginManager already initialised.");
			return;
		}
		File pluginsDir = new File(directory);
		File[] listFiles = pluginsDir.listFiles();
		init(listFiles);
	}

	/**
	 * This method initializes the JPFPluginmanager as PluginManager (which can
	 * be accessed via PluginManager.getInstance()) with an array of plugin
	 * files (any mixture of jars, zips or folders)
	 * <p>
	 * If the manager could not be initialized with the specified directory (for
	 * any reason), an IllegalArgumentException is thrown.
	 * 
	 * @param pluginFiles
	 *            list of plugin files
	 * @throws IllegalArgumentException
	 *             the files could not be used for initialization
	 */
	public static void init(File[] pluginFiles) {
		if (pluginFiles == null) {
//			throw new IllegalArgumentException("invalid plugin files");
			Logger.getLogger("PluginManager").severe(
					"invalid plugin files");
			return;
		}
		try {
			instance = new JPFPluginManager(pluginFiles);
		}
		catch (JpfException e) {
			Logger.getLogger("PluginManager").severe(
					"internal error while initializing plugin manager: " + e);
			throw new IllegalArgumentException(
					"internal error while initializing plugin manager", e);
		}
	}

	@Override
	public Extension[] getExtensions(String extendetPointID, String extendetPluginID) {
		List<Extension> result = new ArrayList<Extension>();
		ExtensionPoint toolExtPoint = manager.getRegistry().getExtensionPoint(
				extendetPointID, extendetPluginID);
		Collection<org.java.plugin.registry.Extension> connectedExtensions = toolExtPoint
				.getConnectedExtensions();
		for (org.java.plugin.registry.Extension e : connectedExtensions) {
			Extension extension = cachedExtension.get(e);
			if (extension==null) {
				extension = new JPFExtension(e, manager);
				cachedExtension.put(e, extension);
			}
			result.add(extension);
		}
		Extension[] ret = result.toArray(new Extension[result.size()]);
		Arrays.sort(ret, new PluginCollectionComparatorByPriority());
		return ret;
	}

	@Override
	public Extension getExtension(String extendetPluginID,
			String extendetPointID, String extensionID) {
		Extension[] extensions = getExtensions(extendetPointID, extendetPluginID);
		for (Extension e : extensions) {
			if (e.getID().equals(extensionID)) return e;
		}
		return null;
	}

	@Override
	public Plugin[] getPlugins() {
		// initialize plugins lazy
		if (this.plugins == null) {
			Collection<Plugin> result = new LinkedList<Plugin>();
			Collection<PluginDescriptor> descriptors =
					this.manager.getRegistry().getPluginDescriptors();
			for (PluginDescriptor descriptor : descriptors) {
				result.add(new JPFPlugin(descriptor));
			}
			this.plugins = result.toArray(new Plugin[result.size()]);
		}
		return this.plugins;
	}
}
