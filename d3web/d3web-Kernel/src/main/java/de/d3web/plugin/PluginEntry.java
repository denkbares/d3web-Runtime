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
 * An Entry for PluginConfig.
 * Each entry contains
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class PluginEntry {

	private Plugin plugin;
	private boolean necessary;
	private boolean autodetect;
	
	public PluginEntry(Plugin plugin, boolean necessary,
			boolean autodetect) {
		super();
		this.plugin = plugin;
		this.necessary = necessary;
		this.autodetect = autodetect;
	}
	
	/**
	 * @return the Extension, which is configured in this entry 
	 */
	public Plugin getPlugin() {
		return plugin;
	}
	
	/**
	 * @return true, if the Extension is necessary for the kb, false otherwise
	 */
	public boolean isNecessary() {
		return necessary;
	}
	
	/**
	 * @return true, if the necessity of the Extension is detected automatically
	 *	false, if it is set by the user
	 */
	public boolean isAutodetect() {
		return autodetect;
	}
	
	/**
	 * Returns the Autodetect singleton of this plugin
	 * @return Autodetect
	 */
	public Autodetect getAutodetect() {
		Autodetect auto = null;
		for (Extension e: PluginManager.getInstance().getExtensions("d3web-Kernel-ExtensionPoints", Autodetect.EXTENSIONPOINT_ID)) {
			if (e.getPluginID().equals(plugin.getPluginID())) {
				auto=(Autodetect) e.getSingleton();
				break;
			}
		}
		return auto;
	}

	public void setNecessary(boolean necessary) {
		this.necessary = necessary;
	}

	public void setAutodetect(boolean autodetect) {
		this.autodetect = autodetect;
	}
}
