package de.d3web.persistence.xml.loader;

import de.d3web.kernel.supportknowledge.Property;

/**
 * Realizes a static representation of properties for a KnowledgeBase
 * This is needed because the properties must be set after parsing the knowledge base source.<br>
 * Creation date: (04.10.2001 18:51:53)
 * @author Norman Brümmer
 */
public class PropertyObject {

	private Property property = null;
	private Object value = null;

	/**
	 * Creates a new PropertyObject
	 */
	public PropertyObject() {
		super();
	}

	/**
	 * @return the encapsulated PropertyDescriptor
	 */
	public Property getProperty() {
		return property;
	}

	/**
	 * @return the encapsulated Property-value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * specifies the PropertyDescriptor to encapsulate
	 */
	public void setProperty(Property newProperty) {
		property = newProperty;
	}

	/**
	 * specifies the value of the encapsulated PropertyDescriptor
	 */
	public void setValue(Object newValue) {
		value = newValue;
	}
}