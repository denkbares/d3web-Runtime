package de.d3web.testcase.model;

import java.util.Locale;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.Session;
import de.d3web.utils.EqualsUtils;

/**
 * Checks whether a certain property has the expected value.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 14.06.16
 */
public class PropertyCheck<T> implements Check {

	private final @NotNull NamedObject namedObject;
	private final Property<T> property;
	private final Locale locale;
	private final T propertyValue;

	/**
	 * Creates a new check template for properties. <tt>Locale</tt> and <tt>propertyValue</tt> are allowed to be
	 * <tt>null</tt>.
	 * In case of multilingual properties (see {@link Property#isMultilingual()}), if the locale is null,
	 * the version of the property without locale ist tested. If the property is not multilingual and a locale is
	 * given, an {@link IllegalArgumentException} is thrown.
	 * If the given propertyValue is null, this check will make sure, that the given property is also null during
	 * execution.
	 *
	 * @param namedObject   the object containing the property
	 * @param property      the property we want to test
	 * @param locale        the locale for which we want to test the property (in case of multilingual), can be
	 *                      null
	 * @param propertyValue the value of the property we want to check, can be null to check non-existence of
	 *                      property
	 */
	public PropertyCheck(@NotNull NamedObject namedObject, @NotNull Property<T> property, @Nullable Locale locale, @Nullable T propertyValue) {
		Objects.requireNonNull(namedObject);
		Objects.requireNonNull(property);
		if (locale != null && !property.isMultilingual()) {
			throw new IllegalArgumentException("Property " + property.getName() + " is not multilingual, so locale is expected to be null.");
		}
		this.namedObject = namedObject;
		this.property = property;
		this.locale = locale;
		this.propertyValue = propertyValue;
	}

	/**
	 * Creates a new check template for non-multilingual properties. <tt>PropertyValue</tt> is allowed to be
	 * <tt>null</tt>.
	 * If the given propertyValue is null, the check will make sure, that the given property is also null
	 * (meaning not defined/set) during execution.
	 *
	 * @param namedObject   the object containing the property
	 * @param property      the property we want to test
	 * @param propertyValue the value of the property we want to check in its string representation, can be null to
	 *                      check non-existence of property
	 */
	public PropertyCheck(TerminologyObject namedObject, Property<T> property, T propertyValue) {
		this(namedObject, property, null, propertyValue);
	}

	@Override
	public boolean check(Session session) {
		T currentPropertyValue = namedObject.getInfoStore().getValue(property, locale);
		return EqualsUtils.equals(currentPropertyValue, propertyValue);
	}

	@Override
	public String getCondition() {
		return "Property " + property.getName() +
				(property.isMultilingual() ? "(locale: " + (locale == null ? "none" : locale.toLanguageTag()) + ")" : "") +
				" of object '" + namedObject.getName() + "' is '" + (propertyValue == null ? "NULL" : propertyValue
				.toString()) + "'";
	}

}
