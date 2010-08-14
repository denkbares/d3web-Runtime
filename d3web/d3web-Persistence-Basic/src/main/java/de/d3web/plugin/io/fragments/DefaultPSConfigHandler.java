/*
 * Copyright (C) 2010 denkbares GmbH
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
package de.d3web.plugin.io.fragments;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.inference.PSConfig;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PSConfig.PSState;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.plugin.Autodetect;
import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;

/**
 * Default FragementHandler for PSConfigs Writes/Reades all PSConfigs, must have
 * the lowest priority
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class DefaultPSConfigHandler implements FragmentHandler {

	protected static final String EXTENSION_ID = "extensionID";
	protected static final String PS_ENTRY = "psEntry";

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals(PS_ENTRY);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof PSConfig;
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		String extensionID = element.getAttribute(EXTENSION_ID);
		String pluginID = element.getAttribute("pluginID");
		PluginManager pluginManager = PluginManager.getInstance();
		PSState psState = PSConfig.PSState.valueOf(element.getAttribute("state"));
		Extension extension = null;
		try {
			extension = pluginManager.getExtension("d3web-Kernel-ExtensionPoints",
					PSMethod.EXTENSIONPOINT_ID, pluginID, extensionID);
		}
		catch (IllegalArgumentException e) {
			if (psState.equals(PSConfig.PSState.active)) {
				throw new IOException("Problemsolver " + extensionID + " not found");
			}
			else {
				return new DummyPSConfig(psState, extensionID, pluginID, element);
			}
		}
		if (psState.equals(PSConfig.PSState.active) && extension == null) {
			throw new IOException("Problemsolver " + extensionID + " not found");
		}
		else if (extension == null) {
			return new DummyPSConfig(psState, extensionID, pluginID, element);
		}
		Autodetect auto = null;
		for (Extension e : pluginManager.getExtensions("d3web-Kernel-ExtensionPoints",
				Autodetect.EXTENSIONPOINT_ID)) {
			if (e.getPluginID().equals(pluginID)) {
				auto = (Autodetect) e.getSingleton();
				break;
			}
		}
		PSMethod psMethod = (PSMethod) extension.getNewInstance();
		// Getting the priority
		// setting the priority to the priority of the extension
		double priority = extension.getPriority();
		// if the priority is set in the xml file, this priority will be used
		String priorityString = element.getAttribute("priority");
		if (priorityString.length() > 0) {
			priority = Double.parseDouble(priorityString);
		}
		return new PSConfig(psState, psMethod, auto, extensionID, pluginID, priority);
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		if (object instanceof DummyPSConfig) {
			DummyPSConfig dummy = (DummyPSConfig) object;
			return dummy.getElement();
		}
		PSConfig psConfig = (PSConfig) object;
		Element element = doc.createElement(PS_ENTRY);
		element.setAttribute(EXTENSION_ID, psConfig.getExtensionID());
		element.setAttribute("pluginID", psConfig.getPluginID());
		element.setAttribute("state", psConfig.getPsState().name());
		element.setAttribute("priority", "" + psConfig.getPriority());
		return element;
	}

}
