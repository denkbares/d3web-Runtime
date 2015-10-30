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
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.testcase.model.TestCase;

/**
 * Reads and writes {@link ConditionPersistenceCheckTemplate}s for {@link TestCase}s.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 30.10.15
 */
public class ConditionPersistenceCheckHandler implements FragmentHandler<TestCase> {

	private static final String CHECK = "Check";
	private static final String CONDITION = "Condition";

	@Override
	public Object read(Element element, Persistence<TestCase> persistence) throws IOException {
		List<Element> conditionElements = XMLUtil.getChildren(element, CONDITION);
		String xmlString = XMLUtil.getElementAsString(conditionElements.get(0));
		return new ConditionPersistenceCheckTemplate(xmlString);
	}

	@Override
	public Element write(Object object, Persistence<TestCase> persistence) throws IOException {
		ConditionPersistenceCheckTemplate checkTemplate = (ConditionPersistenceCheckTemplate) object;
		Element checkElement = persistence.getDocument().createElement(CHECK);
		checkElement.setAttribute(XMLUtil.TYPE, CONDITION);
		try {
			Element conditionElement = checkTemplate.getDocument().getDocumentElement();
			// we deep-copy the element from the template in our new document
			checkElement.appendChild(checkElement.getOwnerDocument().importNode(conditionElement, true));
		}
		catch (ParserConfigurationException | SAXException e) {
			throw new IOException(e);
		}
		return checkElement;
	}

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, CHECK, CONDITION);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof ConditionPersistenceCheckTemplate;
	}
}
