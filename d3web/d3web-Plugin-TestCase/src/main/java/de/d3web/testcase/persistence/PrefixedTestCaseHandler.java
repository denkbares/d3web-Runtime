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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.testcase.model.TestCase;
import de.d3web.testcase.prefix.PrefixedTestCase;

/**
 * Handler allowing to read and write {@link PrefixedTestCase}s.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 04.11.15
 */
public class PrefixedTestCaseHandler implements FragmentHandler<TestCase> {

	private static final String TEST_CASE = "TestCase";

	private static final String PREFIXED = "Prefixed";

	@Override
	public Object read(Element element, Persistence<TestCase> persistence) throws IOException {

		List<Element> children = XMLUtil.getChildren(element, TEST_CASE);

		if (children.size() != 2) {
			throw new IOException("Unexpected amount of test cases found for prefixed test case xml. " +
					"Expected 2 but was " + children.size());
		}

		TestCase prefix = (TestCase) persistence.readFragment(children.get(0));
		TestCase testCase = (TestCase) persistence.readFragment(children.get(1));

		PrefixedTestCase prefixedTestCase = new PrefixedTestCase(prefix, testCase);
		if (persistence instanceof TestCasePersistence) {
			((TestCasePersistence) persistence).setTestCase(prefixedTestCase);
		}
		return prefixedTestCase;
	}

	@Override
	public Element write(Object object, Persistence<TestCase> persistence) throws IOException {
		PrefixedTestCase prefixedTestCase = (PrefixedTestCase) object;
		Document document = persistence.getDocument();

		Element prefixTestCaseElement = document.createElement(TEST_CASE);
		prefixTestCaseElement.setAttribute(XMLUtil.TYPE, PREFIXED);

		prefixTestCaseElement.appendChild(persistence.writeFragment(prefixedTestCase.getPrefix()));
		prefixTestCaseElement.appendChild(persistence.writeFragment(prefixedTestCase.getTestCase()));

		return prefixTestCaseElement;
	}

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, TEST_CASE, PREFIXED);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof PrefixedTestCase;
	}
}
