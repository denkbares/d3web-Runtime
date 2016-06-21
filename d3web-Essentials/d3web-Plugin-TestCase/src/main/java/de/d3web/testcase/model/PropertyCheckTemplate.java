package de.d3web.testcase.model;

import java.util.Locale;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.strings.Identifier;

/**
 * Template for checking whether a certain property has the expected value.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 14.06.16
 */
public class PropertyCheckTemplate<T> implements CheckTemplate {

	private final @NotNull Identifier objectIdentifier;
	private final @NotNull Property<T> property;
	private final @Nullable Locale locale;
	private final @Nullable T propertyValue;

	/**
	 * Creates a new check template for properties. <tt>Locale</tt> and <tt>propertyValue</tt> are allowed to be
	 * <tt>null</tt>.
	 * In case of multilingual properties (see {@link Property#isMultilingual()}), if the locale is null,
	 * the version of the property without locale ist tested. If the property is not multilingual and a locale is
	 * given, an {@link IllegalArgumentException} is thrown.
	 * If the given propertyValue is null, this template's check will make sure, that the given property is also null
	 * during execution.
	 *
	 * @param objectIdentifier the name of the object containing the property
	 * @param property         the property we want to test
	 * @param locale           the locale for which we want to test the property (in case of multilingual), can be null
	 * @param propertyValue    the value of the property we want to check in its string representation, can be null to
	 *                         check non-existence of property
	 */
	public PropertyCheckTemplate(@NotNull Identifier objectIdentifier, @NotNull Property<T> property, @Nullable Locale locale, @Nullable T propertyValue) {
		Objects.requireNonNull(objectIdentifier);
		Objects.requireNonNull(property);
		if (locale != null && !property.isMultilingual()) {
			throw new IllegalArgumentException("Property " + property.getName() + " is not multilingual, so locale is expected to be null.");
		}
		this.objectIdentifier = objectIdentifier;
		this.property = property;
		this.locale = locale;
		this.propertyValue = propertyValue;
	}

	/**
	 * Creates a new check template for non-multilingual properties. <tt>PropertyValue</tt> is allowed to be
	 * <tt>null</tt>.
	 * If the given propertyValue is null, this template's check will make sure, that the given property is also null
	 * (meaning not defined/set) during execution.
	 *
	 * @param objectIdentifier the name of the object containing the property
	 * @param property         the property we want to test
	 * @param propertyValue    the value of the property we want to check in its string representation, can be null to
	 *                         check non-existence of property
	 */
	public PropertyCheckTemplate(Identifier objectIdentifier, Property<T> property, T propertyValue) {
		this(objectIdentifier, property, null, propertyValue);
	}

	public Identifier getObjectIdentifier() {
		return objectIdentifier;
	}

	public Property<T> getProperty() {
		return property;
	}

	public Locale getLocale() {
		return locale;
	}

	public T getPropertyValue() {
		return propertyValue;
	}

	@Override
	public Check toCheck(KnowledgeBase knowledgeBase) throws TransformationException {
		String[] pathElements = objectIdentifier.getPathElements();
		NamedObject object = knowledgeBase.getManager().search(pathElements[0]);
		if (pathElements.length == 2) {
			if (object instanceof QuestionChoice) {
				object = KnowledgeBaseUtils.findChoice((QuestionChoice) object, pathElements[1]);
			}
			else {
				throw new TransformationException("For object identifiers with two path elements, " +
						"the first element is expected to point at a QuestionChoice, but was " +
						object.getClass().getSimpleName());
			}
		}
		if (pathElements.length > 2) {
			throw new TransformationException("To many path elements in object identifier, " +
					"expecting 1 or 2 elements, but was " + pathElements.length);
		}
		if (object == null && pathElements.length == 1 && pathElements[0].equals(knowledgeBase.getId())) {
			object = knowledgeBase;
		}
		if (object == null) {
			throw new TransformationException("No knowledge base object found for object identifier " +
					objectIdentifier.toExternalForm());
		}
		return new PropertyCheck<>(object, this.property, locale, propertyValue);
	}
}
