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

import org.w3c.dom.Element;

import de.d3web.core.inference.PSConfig;
import de.d3web.plugin.Autodetect;

/**
 * DummyPSConfig to save the configuration of a PSMethod, when it is not
 * accessible
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class DummyPSConfig extends PSConfig {

	private final Element element;

	public Element getElement() {
		return element;
	}

	public DummyPSConfig(PSState psState, String extensionID, String pluginID, Element e) {
		// priority is set to 10, it is not used anyway (e is loaded and saved,
		// which includes a priority)
		super(psState, null, null, extensionID, pluginID, 10);
		this.element = e;
	}

	@Override
	public Autodetect getAutodetect() {
		return kb -> {
			// PSMethod cannot be added, so autodetect always fails
			return false;
		};
	}

}
