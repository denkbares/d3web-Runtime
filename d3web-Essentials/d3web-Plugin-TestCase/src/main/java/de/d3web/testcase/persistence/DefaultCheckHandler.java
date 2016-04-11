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

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.testcase.model.DefaultCheckTemplate;
import de.d3web.testcase.model.TestCase;

/**
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 30.10.15
 */
public class DefaultCheckHandler implements FragmentHandler<TestCase> {

	private static final String CHECK = "Check";
	private static final String DEFAULT = "Default";
	private static final String OBJECT_NAME = "objectName";
	private static final String VALUE = "value";

	@Override
	public Object read(Element element, Persistence<TestCase> persistence) throws IOException {
		String objectName = element.getAttribute(OBJECT_NAME);
		String value = element.getAttribute(VALUE);
		return new DefaultCheckTemplate(objectName, value);
	}

	@Override
	public Element write(Object object, Persistence<TestCase> persistence) throws IOException {
		DefaultCheckTemplate checkTemplate = (DefaultCheckTemplate) object;
		Element element = persistence.getDocument().createElement(CHECK);
		element.setAttribute(XMLUtil.TYPE, DEFAULT);
		element.setAttribute(OBJECT_NAME, checkTemplate.getObjectName());
		element.setAttribute(VALUE, checkTemplate.getValue());
		return element;
	}

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, CHECK, DEFAULT);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof DefaultCheckTemplate;
	}
}
