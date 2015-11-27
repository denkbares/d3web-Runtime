/*
 * Copyright (C) 2009 denkbares GmbH
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

package de.d3web.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.java.plugin.JpfException;
import org.java.plugin.ObjectFactory;
import org.java.plugin.PluginManager.PluginLocation;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.Identity;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.standard.StandardPluginLocation;

import de.d3web.plugin.util.PluginCollectionComparatorByPriority;
import de.d3web.utils.Log;

/**
 * An implementation of the PluginManager for the Java Plugin Framework (JPF)
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public final class JPFPluginManager extends PluginManager {

	private final org.java.plugin.PluginManager manager;

	private final Map<org.java.plugin.registry.Extension, Extension> cachedExtension = new HashMap<org.java.plugin.registry.Extension, Extension>();

	/**
	 * Contains the registered Plugins. The field will be initialized lazy by
	 * the {@link #getPlugins()} method.
	 */
	private Plugin[] plugins = null;

	private JPFPluginManager(File[] pluginFiles) throws JpfException {
		this.manager = ObjectFactory.newInstance().createManager();

		List<PluginLocation> locations = new ArrayList<PluginLocation>();
		for (File pluginFile : pluginFiles) {
			String pluginName = pluginFile.getName();

			try {
				PluginLocation location = StandardPluginLocation.create(pluginFile);
				if (location != null) {
					locations.add(location);
				}
				else {
					Log.warning("File '" + pluginFile
							+ "' is not a plugin. It will be ignored.");
				}
			}
			catch (MalformedURLException e) {
				Log.severe("error initializing plugin '" + pluginFile + "': " + e);
			}
		}
		Map<String, Identity> map = manager.publishPlugins(locations.toArray(new PluginLocation[locations.size()]));
		// activate all plugins
		for (Identity i : map.values()) {
			String id = i.getId();
			manager.activatePlugin(id);
			Log.info("Plugin '" + id + "' installed and activated.");
		}
	}

	/**
	 * Checks whether the given pluginName matches any of the given pluginFilterPatterns. If no patterns are given, we
	 * always return true.
	 *
	 * @param pluginName           the name of the plugin to be checked
	 * @param pluginFilterPatterns a set of regex pattern to either accept or decline a plugin name
	 */
	public static boolean isPlugin(String pluginName, String... pluginFilterPatterns) {
		if (pluginFilterPatterns == null || pluginFilterPatterns.length == 0) return true;
		boolean matches = false;
		for (String additionalPluginPattern : pluginFilterPatterns) {
			if (pluginName.matches(additionalPluginPattern)) {
				matches = true;
				break;
			}
		}
		return matches;
	}

	/**
	 * This method initializes the JPFPluginmanager as PluginManager (which can
	 * be accessed via PluginManager.getInstance()) with the directory of the
	 * plugins as a String.
	 * <p>
	 * If the manager could not be initialized with the specified directory (for
	 * any reason), an IllegalArgumentException is thrown.
	 *
	 * @param directory           directory of the plugins
	 * @param pluginFilterPattern specifies patterns to filter plugins to be loaded by the plugin manager. If no
	 *                            patterns are given, we try to load every file.
	 * @throws IllegalArgumentException the directory could not be used for
	 *                                  initialization
	 */
	public static void init(String directory, String... pluginFilterPattern) {
		if (instance != null) {
			Log.warning("PluginManager already initialised.");
			return;
		}
		File pluginsDir = new File(directory);
		File[] pluginFiles = pluginsDir.listFiles();
		if (pluginFiles != null) {
			pluginFiles = Arrays.stream(pluginFiles)
					.filter(file -> isPlugin(file.getName(), pluginFilterPattern))
					.toArray(File[]::new);
		}
		init(pluginFiles);
	}

	/**
	 * This method initializes the JPFPluginmanager as PluginManager (which can
	 * be accessed via PluginManager.getInstance()) with an array of plugin
	 * files (any mixture of jars, zips or folders)
	 * <p>
	 * If the manager could not be initialized with the specified directory (for
	 * any reason), an IllegalArgumentException is thrown.
	 *
	 * @param pluginFiles list of plugin files
	 * @throws IllegalArgumentException the files could not be used for
	 *                                  initialization
	 */
	public static void init(File[] pluginFiles) { // NOSONAR false-positive
		// warning
		if (pluginFiles == null) {
			// throw new IllegalArgumentException("invalid plugin files");
			Log.severe("invalid plugin files");
			return;
		}
		try {
			instance = new JPFPluginManager(pluginFiles);
		}
		catch (JpfException e) {
			Log.severe("internal error while initializing plugin manager: " + e);
			throw new IllegalArgumentException(
					"internal error while initializing plugin manager", e);
		}
	}

	@Override
	public synchronized Extension[] getExtensions(String extendedPluginID, String extendedPointID) {
		List<Extension> result = new ArrayList<Extension>();
		ExtensionPoint toolExtPoint = manager.getRegistry().getExtensionPoint(
				extendedPluginID, extendedPointID);
		Collection<org.java.plugin.registry.Extension> connectedExtensions = toolExtPoint
				.getConnectedExtensions();
		for (org.java.plugin.registry.Extension e : connectedExtensions) {
			Extension extension = cachedExtension.get(e);
			if (extension == null) {
				extension = new JPFExtension(e, manager);
				cachedExtension.put(e, extension);
			}
			result.add(extension);
		}
		return toSortedAndFilteredArray(result);
	}

	public Extension[] toSortedAndFilteredArray(List<Extension> result) {
		Collections.sort(result, new PluginCollectionComparatorByPriority());
		Set<String> ids = new HashSet<>();
		// lets filter duplicate IDs... higher priority wins.
		List<Extension> filtered = new ArrayList<>(result.size());
		for (Extension extension : result) {
			if (ids.add(extension.getID())) {
				filtered.add(extension);
			}
		}
		return filtered.toArray(new Extension[filtered.size()]);
	}

	@Override
	public synchronized Extension[] getExtensions() {
		List<Extension> result = new ArrayList<>();
		Collection<PluginDescriptor> pluginDescriptors = manager.getRegistry().getPluginDescriptors();
		for (PluginDescriptor pluginDescriptor : pluginDescriptors) {
			for (org.java.plugin.registry.Extension e : pluginDescriptor.getExtensions()) {
				Extension extension = cachedExtension.get(e);
				if (extension == null) {
					extension = new JPFExtension(e, manager);
					cachedExtension.put(e, extension);
				}
				result.add(extension);
			}
		}
		return toSortedAndFilteredArray(result);
	}

	@Override
	public Extension getExtension(String extendetPluginID,
								  String extendetPointID, String pluginID, String extensionID) {
		Extension[] extensions = getExtensions(extendetPluginID, extendetPointID);
		for (Extension e : extensions) {
			if (e.getID().equals(extensionID) && e.getPluginID().equals(pluginID)) return e;
		}
		return null;
	}

	@Override
	public synchronized Plugin[] getPlugins() {
		// initialize plugins lazy
		if (this.plugins == null) {
			Collection<Plugin> result = new LinkedList<Plugin>();
			Collection<PluginDescriptor> descriptors =
					this.manager.getRegistry().getPluginDescriptors();
			for (PluginDescriptor descriptor : descriptors) {
				result.add(new JPFPlugin(manager, descriptor));
			}
			this.plugins = result.toArray(new Plugin[result.size()]);
		}
		return this.plugins;
	}

	@Override
	public Plugin getPlugin(String id) {
		for (Plugin p : getPlugins()) {
			if (p.getPluginID().equals(id)) {
				return p;
			}
		}
		return null;
	}
}
