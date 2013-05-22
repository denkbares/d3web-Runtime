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

import de.d3web.core.extensions.KernelExtensionPoints;

/**
 * An Entry for PluginConfig. Each entry contains
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class PluginEntry {

	private final Plugin plugin;
	private boolean required;
	private boolean autodetect;
	private Autodetect autodetectInstance = null;

	public PluginEntry(Plugin plugin, boolean required, boolean autodetect) {
		this.plugin = plugin;
		this.required = required;
		this.autodetect = autodetect;

		// search for autodetect instance
		for (Extension e : PluginManager.getInstance().getExtensions(
				KernelExtensionPoints.PLUGIN_ID, KernelExtensionPoints.EXTENSIONPOINT_AUTODETECT)) {
			if (e.getPluginID().equals(plugin.getPluginID())) {
				autodetectInstance = (Autodetect) e.getSingleton();
				break;
			}
		}
	}

	/**
	 * Creates a new Plugin entry for a specific plugin with default initialized
	 * values for {@link #required} and {@link #autodetect}.
	 */
	public PluginEntry(Plugin plugin) {
		this(plugin, false, true);
	}

	/**
	 * @return the Extension, which is configured in this entry
	 */
	public Plugin getPlugin() {
		return plugin;
	}

	/**
	 * @return true, if the Extension is required to load the knowledge base,
	 *         false otherwise.
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Returns true, if the necessity of the Extension should be detected
	 * automatically, false otherwise. If the specific plugin has no autodetect
	 * capabilities, this method always returns false, regardless what value is
	 * set by the {@link #setAutodetect(boolean)} method.
	 * 
	 */
	public boolean isAutodetect() {
		return autodetect && autodetectInstance != null;
	}

	/**
	 * Returns the Autodetect singleton of this plugin. This is the instance to
	 * detect if the plugin is required to load a specific knowledge base. If
	 * the plugin has no autodetect capabilities, null is returned.
	 * 
	 * @return Autodetect the instance to detect if the plugin is required to
	 *         load a specific knowledge base
	 * @see Autodetect
	 */
	public Autodetect getAutodetect() {
		return autodetectInstance;
	}

	/**
	 * Sets the internal flag to signal id the plugin is required for loading
	 * the knowledge base. This flag will be ignored if {@link #autodetect} is
	 * <code>true</code> and the plugin has autodetect capabilities.
	 * 
	 * @created 22.04.2011
	 * @param required if the plugin is required for loading the knowledge base
	 * @see #getAutodetect()
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	public void setAutodetect(boolean autodetect) {
		this.autodetect = autodetect;
	}

	@Override
	public String toString() {
		return plugin.getPluginID();
	}
}
