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
package de.d3web.core.knowledge.terminology.info;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;

/**
 * Represents a Property. Properties can only be created by extending the
 * Extensionpoint "Property"
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 06.10.2010
 */
public class Property {

	private static final String EXTENSIONPOINT_ID = "Property";

	public enum Autosave {
		basic, mminfo, none
	}

	private Autosave autosave;
	private String name;
	private boolean multilingual;

	private Property(Autosave autosave, String name, boolean multilingual) {
		super();
		this.autosave = autosave;
		this.name = name;
		this.multilingual = multilingual;
	}

	public boolean hasState(Autosave autosave) {
		return this.autosave.equals(autosave);
	}

	public String getName() {
		return name;
	}

	public boolean isMultilingual() {
		return multilingual;
	}

	private static Map<String, Property> properties = null;

	/**
	 * Returns the Property with the specified name.
	 * 
	 * This method will not generate Properties lazy, it only returns Properties
	 * defined in a Plugin.
	 * 
	 * @created 07.10.2010
	 * @param name specified name of the {@link Property}
	 * @return {@link Property} if a property with this name is defined, null
	 *         otherwise
	 */
	public static Property getProperty(String name) {
		parseProperties();
		Property property = properties.get(name);
		return property;
	}

	private static void parseProperties() {
		if (properties == null) {
			properties = new HashMap<String, Property>();
			for (Extension e : PluginManager.getInstance().getExtensions(
					"d3web-Kernel-ExtensionPoints", EXTENSIONPOINT_ID)) {
				String pname = e.getName();
				Autosave pautosave = Autosave.valueOf(e.getParameter("autosave"));
				boolean pmultilingual = Boolean.parseBoolean(e.getParameter("multilingual"));
				Property p = new Property(pautosave, pname, pmultilingual);
				properties.put(pname, p);
			}
		}
	}

	/**
	 * Returns a Collection of all plugged Properties
	 * 
	 * @created 07.10.2010
	 * @return Collection of all plugged Properties
	 */
	public static Collection<Property> getAllProperties() {
		parseProperties();
		return properties.values();
	}

}
