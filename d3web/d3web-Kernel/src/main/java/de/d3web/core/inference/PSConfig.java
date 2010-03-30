/*
 * Copyright (C) 2010 denkbares GmbH
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
package de.d3web.core.inference;

import de.d3web.plugin.Autodetect;

/**
 * Saves the configuration of one problemsolver instance
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class PSConfig implements Comparable<PSConfig> {

	private static final long serialVersionUID = -8392435221734086888L;

	public enum PSState {
		active,
		deactivated,
		autodetect,
	}

	private PSState psState;
	private PSMethod psMethod;
	private Autodetect autodetect;
	private String extensionID;
	private String pluginID;
	private double priority;

	public double getPriority() {
		return priority;
	}

	public PSConfig(PSState psState, PSMethod psMethod, String extensionID, String pluginID, double priority) {
		this(psState, psMethod, null, extensionID, pluginID, priority);
	}

	public PSConfig(PSState psState, PSMethod psMethod, Autodetect autodetect, String extensionID, String pluginID, double priority) {
		this.psState = psState;
		this.psMethod = psMethod;
		this.autodetect = autodetect;
		this.extensionID = extensionID;
		this.pluginID = pluginID;
		this.priority = priority;
	}

	public PSState getPsState() {
		return psState;
	}

	public PSMethod getPsMethod() {
		return psMethod;
	}

	public Autodetect getAutodetect() {
		return autodetect;
	}

	public String getExtensionID() {
		return extensionID;
	}

	public String getPluginID() {
		return pluginID;
	}

	@Override
	public int compareTo(PSConfig o) {
		return Double.compare(priority, o.getPriority());
	}
}
