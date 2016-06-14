package de.d3web.testcase.model;

import java.util.Locale;
import java.util.Objects;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.Session;

/**
 * Checks whether a certain property has the expected value.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 14.06.16
 */
public class PropertyCheck<T> implements Check {

	private final TerminologyObject terminologyObject;
	private final Property<T> property;
	private Locale locale;
	private final T propertyValue;

	public PropertyCheck(TerminologyObject terminologyObject, Property<T> property, Locale locale, T propertyValue) {
		Objects.requireNonNull(terminologyObject);
		Objects.requireNonNull(property);
		Objects.requireNonNull(propertyValue);
		if (locale != null && !property.isMultilingual()) {
			throw new IllegalArgumentException("Property " + property.getName() + " is not multilingual, so locale is expected to be null.");
		}
		this.terminologyObject = terminologyObject;
		this.property = property;
		this.locale = locale;
		this.propertyValue = propertyValue;
	}

	public PropertyCheck(TerminologyObject terminologyObject, Property<T> property, T propertyValue) {
		this(terminologyObject, property, null, propertyValue);
	}

	@Override
	public boolean check(Session session) {
		T value = terminologyObject.getInfoStore().getValue(property, locale);
		return value.equals(propertyValue);
	}

	@Override
	public String getCondition() {
		return "Property " + property.getName() +
				(property.isMultilingual() ? "(locale: " + (locale == null ? "none" : locale.toString()) + ")" : "") +
				" of object '" + terminologyObject.getName() + "' is '" + propertyValue.toString() + "'";
	}

}
