/*
 * Copyright (C) 2011 denkbares GmbH
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import de.d3web.core.extensions.KernelExtensionPoints;
import de.d3web.core.knowledge.InfoStore;
import com.denkbares.plugin.Extension;
import com.denkbares.plugin.PluginManager;
import com.denkbares.strings.Strings;
import com.denkbares.utils.Log;

/**
 * Represents a Property. Properties can only be created by extending the
 * Extensionpoint "Property"
 *
 * For more information about the xml markup, see plugin.xml of
 * d3web-Plugin-ExtensionPoints
 *
 * @author Markus Friedrich (denkbares GmbH)
 * @created 06.10.2010
 */
public final class Property<T> {

	private static final Map<String, Property<?>> properties = new HashMap<>();
	private static final Map<String, Property<?>> synonyms = new HashMap<>();
	static {
		parseProperties();
	}

	public enum Autosave {
		basic, mminfo, none
	}

	private final Autosave autosave;
	private final String name;
	private final String description;
	private final boolean multilingual;
	private final String storedClassName;
	private final String defaultValueString;
	// and some transient fields for later on-demand initialization
	// (to prevent loading class T, see getProperty for details and reason why)
	private Class<T> storedClass = null;
	private T defaultValue = null;
	private final Extension extension;

	private Property(Extension extension, Autosave autosave, String name, String description, boolean multilingual, String className, String defaultValueString) {
		this.extension = extension;
		this.autosave = autosave;
		this.name = name;
		this.description = description;
		this.multilingual = multilingual;
		this.storedClassName = className;
		this.defaultValueString = defaultValueString;
	}

	public boolean hasState(Autosave autosave) {
		return this.autosave == autosave;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
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

		// initialize only once
		if (this.storedClass != null) return;

		// first initialize the class provided by the plugins class name
		try {
			ClassLoader classLoader = extension.getPlugin().getClassLoader();
			this.storedClass = (Class<T>) classLoader.loadClass(this.storedClassName);
		}
		catch (ClassNotFoundException e) {
			throw new NoClassDefFoundError(
					this.storedClassName + " not found for property " + this.name);
		}

		// check for having a default value in place to be initialized
		if (this.defaultValueString != null && this.defaultValue == null) {
			// on runtime exceptions we do not catch them, cause we
			// have a wrong plugin definition then!
			try {
				this.defaultValue = parseValue(this.defaultValueString);
			}
			catch (IllegalArgumentException | NoSuchMethodException e) {
				Log.severe("cannot parse/initialize property default value: " +
								this.name + " = " + this.defaultValueString, e);
				// do noting here, leave defaultValue on null
			}
		}
	}

	/**
	 * Returns the DefaultValue of this property. The default value will be
	 * returned by the {@link InfoStore#getValue(Property)} and
	 * {@link InfoStore#getValue(Property, java.util.Locale)} methods when no
	 * explicit value has been specified.
	 * <p>
	 * The default value may be specified by either the plugin.xml using the
	 * "default" attribute when extending the "property" extension point. In
	 * addition a default value can also be set using the
	 * {@link Property#setDefaultValue(T)} method.
	 * <p>
	 * When specifying the default value in the plugin.xml, the stored class
	 * must define the prerequisites defined in {@link #parseValue(String)}.
	 *
	 * @created 31.10.2010
	 * @return the default value of this property
	 * @see #setDefaultValue(T)
	 * @see #parseValue(String)
	 */
	public T getDefaultValue() {
		return this.defaultValue;
	}

	/**
	 * Sets the default value of this property. The default value will be
	 * returned by the {@link InfoStore#getValue(Property)} and
	 * {@link InfoStore#getValue(Property, java.util.Locale)} methods when no
	 * explicit value has been specified.
	 * <p>
	 * Instead of using this method, the default value may also be specified in
	 * the plugin.xml using the "default" attribute when extending the
	 * "property" extension point.
	 *
	 * @created 31.10.2010
	 * @param defaultValue
	 */
	public void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Returns the class of all data to be stored in this property.
	 *
	 * @created 28.10.2010
	 * @return the class of the data stored in this property
	 */
	public Class<T> getStoredClass() {
		initStoredClass();
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
	public static <StoreageType> Property<StoreageType> getProperty(String name, Class<StoreageType> theStoredClass) throws NoSuchElementException, ClassCastException {
		Property<?> property = getUntypedProperty(name);
		if (!property.storedClassName.equals(theStoredClass.getName())) {
			throw new ClassCastException(
					"specified class not compatible to plugin's property declaration");
		}
		// this cast is save due to the test before
		// thus we can accept the @SuppressWarnings("unchecked")
		return (Property<StoreageType>) property;
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
	public static Property<Object> getUntypedProperty(String name) throws NoSuchElementException {
		String key = name.toLowerCase();
		Property<?> property = properties.get(key);
		if (property == null) property = synonyms.get(key);
		if (property == null) {
			throw new NoSuchElementException("unknown property " + name);
		}
		property.initStoredClass();
		return (Property<Object>) property;
	}

	/**
	 * Dynamically casts the specified value to the type of the stored value.
	 *
	 * @created 28.10.2010
	 * @param o
	 * @return
	 * @throws ClassCastException
	 */
	public T castToStoredValue(Object o) throws ClassCastException {
		return getStoredClass().cast(o);
	}

	/**
	 * Parses a property from a string representation.
	 * <p>
	 * For parsing this method uses the static "valueOf"-method of the stored
	 * class T. Therefore class T must have a method with the exact signature
	 * <code>public static T valueOf(String)</code>. (Only the return value may
	 * also be a more specific subclass of T). Please note that the java basic
	 * types support that method, as well as all java enumerations.
	 * <p>
	 * If there is no public method suitable for parsing a
	 * {@link NoSuchMethodException} is thrown. If there is any unchecked
	 * exception during parsing, the exception is also thrown by this method. If
	 * there is any checked exception during parsing, an
	 * IllegalArgumentException is thrown, containing the original exception as
	 * its cause.
	 *
	 *
	 * @created 31.10.2010
	 * @param string the string to be parsed
	 * @return the resulting value
	 * @throws NoSuchMethodException there is no appropriate parse method found
	 * @throws IllegalArgumentException if any parse exception occurred
	 */
	public T parseValue(String string) throws NoSuchMethodException, IllegalArgumentException {
		// use string directly if T == String
		// because String only supports valueOf(Object)
		// instead of valueOf(String)
		Class<T> clazz = getStoredClass();
		if (String.class.isAssignableFrom(clazz)) return castToStoredValue(string);
		// lets parse enums case insensitive
		if (Enum.class.isAssignableFrom(clazz)) {
			Enum value = Strings.parseEnum(string, (Class) clazz);
			if (value == null) {
				throw new IllegalArgumentException("'" + string + "' is not a valid value for property " + getName());
			}
			return castToStoredValue(value);
		}

		// find method
		try {
			Method method = clazz.getMethod("valueOf", String.class);
			return castToStoredValue(method.invoke(null, string));
		}
		catch (SecurityException e) {
			throw new NoSuchMethodException("method 'valueOf' seems to be non-public");
		}
		catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			// re-throw runtime exceptions,
			// such as NumberFormatException from Double.valueOf(..)
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			}
			throw new IllegalArgumentException(
					"parameter '" + string + "' causes an exception", cause);
		}
		catch (IllegalAccessException e) {
			// must not happen, cause we do not use a constructor method
			throw new IllegalStateException();
		}
	}

	/**
	 * Returns if this property have the ability to parse String representations
	 * into a value of this property.
	 *
	 * @created 02.11.2010
	 * @return if this property can be parsed from a String
	 */
	public boolean canParseValue() {
		// use string directly if T == String
		// because String only supports valueOf(Object)
		// instead of valueOf(String)
		Class<T> clazz = getStoredClass();
		if (String.class.isAssignableFrom(clazz)) return true;
		if (Enum.class.isAssignableFrom(clazz)) return true;
		// find method and check for public access
		try {
			Method method = clazz.getMethod("valueOf", String.class);
			return Modifier.isPublic(method.getModifiers());
		}
		catch (SecurityException e) {
			return false;
		}
		catch (NoSuchMethodException e) {
			return false;
		}
	}

	private static void parseProperties() {
		Extension[] extensions = PluginManager.getInstance().getExtensions(
				KernelExtensionPoints.PLUGIN_ID, KernelExtensionPoints.EXTENSIONPOINT_PROPERTY);
		for (Extension extension : extensions) {
			String pname = extension.getName();
			Autosave pautosave = Autosave.valueOf(extension.getParameter("autosave"));
			boolean pmultilingual = Boolean.parseBoolean(extension.getParameter("multilingual"));
			String storedClassName = extension.getParameter("instanceof");
			String defaultValueString = extension.getParameter("default");
			String description = extension.getParameter("description");
			Property<?> property = new Property<>(extension,
					pautosave, pname, description, pmultilingual,
					storedClassName, defaultValueString);
			properties.put(pname.toLowerCase(), property);

			// also add deprecated names for this property
			// to provide backward compatibility
			List<String> depecatedNames = extension.getParameters("deprecated");
			for (String depecatedName : depecatedNames) {
				addSynonymProperty(depecatedName, property);
			}
		}
	}

	/**
	 * Adds an existing property under a synonym name to the properties manager.
	 * After this call there will be no difference when accessing the property
	 * by its original name or its synonym name. This method may be used to
	 * provide backward compatibility when renaming properties.
	 *
	 * @created 20.08.2012
	 * @param otherName the synonym name of the property (usually the old /
	 *        original name of the property)
	 * @param property the property to add the synonym for
	 */
	private static void addSynonymProperty(String otherName, Property<?> property) {
		synonyms.put(otherName.toLowerCase(), property);
	}

	public String getDescription() {
		return this.description;
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
