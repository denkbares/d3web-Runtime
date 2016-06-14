package de.d3web.testcase.model;

import java.util.Locale;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.info.Property;

/**
 * Template for checking whether a certain property has the expected value.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 14.06.16
 */
public class PropertyCheckTemplate<T> implements CheckTemplate {

	private final String objectName;
	private final Property<T> property;
	private final Locale locale;
	private final String propertyStringRepresentation;

	public PropertyCheckTemplate(@NotNull String objectName, @NotNull Property<T> property, @Nullable Locale locale, @NotNull String propertyStringRepresentation) {
		Objects.requireNonNull(objectName);
		Objects.requireNonNull(property);
		Objects.requireNonNull(propertyStringRepresentation);
		if (locale != null && !property.isMultilingual()) {
			throw new IllegalArgumentException("Property " + property.getName() + " is not multilingual, so locale is expected to be null.");
		}
		this.objectName = objectName;
		this.property = property;
		this.locale = locale;
		this.propertyStringRepresentation = propertyStringRepresentation;
	}

	public PropertyCheckTemplate(String objectName, Property<T> property, String propertyStringRepresentation) {
		this(objectName, property, null, propertyStringRepresentation);
	}

	public String getObjectName() {
		return objectName;
	}

	public Property<T> getProperty() {
		return property;
	}

	public Locale getLocale() {
		return locale;
	}

	public String getPropertyStringRepresentation() {
		return propertyStringRepresentation;
	}

	@Override
	public Check toCheck(KnowledgeBase knowledgeBase) throws TransformationException {
		T propertyValue;
		try {
			propertyValue = this.property.parseValue(propertyStringRepresentation);
		}
		catch (Exception e) {
			throw new TransformationException("Unable to create PropertyCheck, string '" + propertyStringRepresentation
					+ "' could not be parsed for Property " + property.getName(), e);
		}
		return new PropertyCheck<>(knowledgeBase.getManager().search(objectName), this.property, locale, propertyValue);
	}
}
