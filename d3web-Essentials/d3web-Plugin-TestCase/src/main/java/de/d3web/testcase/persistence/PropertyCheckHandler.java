/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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

package de.d3web.testcase.persistence;

import java.io.IOException;
import java.util.Locale;

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.strings.Identifier;
import de.d3web.testcase.model.PropertyCheckTemplate;
import de.d3web.testcase.model.TestCase;

/**
 * FragmentHandler to read and write PropertyChecks.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 30.10.15
 */
public class PropertyCheckHandler implements FragmentHandler<TestCase> {

	private static final String CHECK = "Check";
	private static final String TYPE = "Property";
	private static final String OBJECT_IDENTIFIER = "objectName";
	private static final String PROPERTY = "property";
	private static final String LOCALE = "locale";
	private static final String HAVE_VALUE = "haveValue";

	@Override
	public Object read(Element element, Persistence<TestCase> persistence) throws IOException {
		Identifier objectIdentifier = Identifier.fromExternalForm(element.getAttribute(OBJECT_IDENTIFIER));
		Property<?> property = Property.getUntypedProperty(element.getAttribute(PROPERTY));
		String propertyValue = null;
		if (!element.hasAttribute(HAVE_VALUE) || !"false".equals(element.getAttribute(HAVE_VALUE))) {
			propertyValue = element.getTextContent();
		}
		Locale locale = null;
		if (element.hasAttribute(LOCALE)) {
			String localeString = element.getAttribute(LOCALE);
			locale = Locale.forLanguageTag(localeString);
		}
		return new PropertyCheckTemplate<>(objectIdentifier, property, locale, propertyValue);
	}

	@Override
	public Element write(Object object, Persistence<TestCase> persistence) throws IOException {
		PropertyCheckTemplate checkTemplate = (PropertyCheckTemplate) object;
		Element element = persistence.getDocument().createElement(CHECK);
		element.setAttribute(XMLUtil.TYPE, TYPE);
		element.setAttribute(OBJECT_IDENTIFIER, checkTemplate.getObjectIdentifier().toExternalForm());
		element.setAttribute(PROPERTY, checkTemplate.getProperty().getName());
		String propertyValue = checkTemplate.getPropertyValue();
		if (propertyValue == null) {
			element.setAttribute(HAVE_VALUE, "false");
		}
		else {
			element.setTextContent(propertyValue);
		}
		Locale locale = checkTemplate.getLocale();
		if (locale != null) {
			element.setAttribute(LOCALE, locale.toLanguageTag());
		}
		return element;
	}

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, CHECK, TYPE);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof PropertyCheckTemplate;
	}

	public static void main(String[] args) {
		System.out.println(Locale.GERMANY.toLanguageTag());
	}
}
