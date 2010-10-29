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
import java.util.NoSuchElementException;

import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;

/**
 * Represents a Property. Properties can only be created by extending the
 * Extensionpoint "Property"
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 06.10.2010
 */
public class Property<T> {

	private static final String EXTENSIONPOINT_ID = "Property";
	private static final Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
	static {
		parseProperties();
	}

	public enum Autosave {
		basic, mminfo, none
	}

	private final Autosave autosave;
	private final String name;
	private final boolean multilingual;
	private final String storedClassName;
	private Class<T> storedClass = null;

	private Property(Autosave autosave, String name, boolean multilingual, String className) {
		this.autosave = autosave;
		this.name = name;
		this.multilingual = multilingual;
		this.storedClassName = className;
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

	/**
	 * This method initializes the stored class instance. This initialization is
	 * delayed (not node in the constructor), due there might be a cyclic
	 * reference between the stored class and the property that the stored class
	 * will instantiate as a static reference (the storage class will contain an
	 * entry like
	 * <code>Property THIS_PROPERTY = Property.getProperty("thisPropertyName");</code>
	 * ). In this case, the <code>Property.getProperty()</code> will be called
	 * while initializing the storedClass and not been able to find the
	 * property.
	 * 
	 * @created 28.10.2010
	 */
	@SuppressWarnings("unchecked")
	private void initStoredClass() {
		if (this.storedClass != null) return;
		try {
			this.storedClass = (Class<T>) Class.forName(this.storedClassName);
		}
		catch (ClassNotFoundException e) {
			throw new NoClassDefFoundError(
					this.storedClassName + " not found for property " + this.name);
		}
	}

	/**
	 * Returns the class of all data to be stored in this property.
	 * 
	 * @created 28.10.2010
	 * @return the class of the data stored in this property
	 */
	public Class<T> getStoredClass() {
		if (this.storedClass == null) {
			throw new IllegalStateException(
						"property " + name + " not initialized");
		}
		return storedClass;
	}

	/**
	 * Returns the Property with the specified name.
	 * 
	 * This method will not generate Properties lazy, it only returns Properties
	 * defined in a plugin. The class specified has to be identical with the
	 * plugin definition, otherwise an ClassCastException is thrown.
	 * 
	 * @created 07.10.2010
	 * @param name specified name of the {@link Property}
	 * @return {@link Property} if a property with this name is defined, null
	 *         otherwise
	 * 
	 * @throws NoSuchElementException if there is no property defined with that
	 *         name
	 * @throws ClassCastException if the specified class instance is not
	 *         identical with the class specified in the plugin definition
	 */
	@SuppressWarnings("unchecked")
	public static <StoreageType> Property<StoreageType> getProperty(String name, Class<StoreageType> theStoredClass) {
		Property<?> property = getUntypedProperty(name);
		if (!property.storedClassName.equals(theStoredClass.getName())) {
			throw new ClassCastException(
					"specified class not compatible to plugin's property declaration");
		}
		// this cast is save due to the test before
		// thus we can accept the @SuppressWarnings("unchecked")
		Property<StoreageType> typedProperty = (Property<StoreageType>) property;
		typedProperty.initStoredClass();
		return typedProperty;
	}

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
	 * 
	 * @throws NoSuchElementException if there is no property defined with that
	 *         name
	 */
	@SuppressWarnings("unchecked")
	public static Property<Object> getUntypedProperty(String name) {
		Property<?> property = properties.get(name);
		if (property == null) {
			throw new NoSuchElementException("unknown property " + name);
		}
		property.initStoredClass();
		return (Property<Object>) property;
	}

	/**
	 * Dynamically casts the specified value to the type of the stored value.
	 * 
	 * 
	 * @created 28.10.2010
	 * @param o
	 * @return
	 * @throws ClassCastException
	 */
	public T castToStoredValue(Object o) throws ClassCastException {
		return getStoredClass().cast(o);
	}

	private static void parseProperties() {
		Extension[] extensions = PluginManager.getInstance().getExtensions(
					"d3web-Kernel-ExtensionPoints", EXTENSIONPOINT_ID);
		for (Extension e : extensions) {
			String pname = e.getName();
			Autosave pautosave = Autosave.valueOf(e.getParameter("autosave"));
			boolean pmultilingual = Boolean.parseBoolean(e.getParameter("multilingual"));
			String storedClassName = e.getParameter("instanceof");
			Property<?> p = new Property<Object>(
					pautosave, pname, pmultilingual, storedClassName);
			properties.put(pname, p);
		}
	}

	/**
	 * Returns a Collection of all plugged Properties
	 * 
	 * @created 07.10.2010
	 * @return Collection of all plugged Properties
	 */
	public static Collection<Property<?>> getAllProperties() {
		return properties.values();
	}

}
