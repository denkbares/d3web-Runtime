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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.FragmentManager;
import de.d3web.core.io.Persistence;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.testcase.model.TestCase;

/**
 * Persistence for {@link TestCase}s.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 27.10.15
 */
public class TestCasePersistence implements Persistence<TestCase> {


	public static final String EXTENDED_PLUGIN_ID = "d3web-TestCase";
	public static final String EXTENDED_POINT_FRAGMENT = "FragmentHandler";

	private final Document document;
	private final FragmentManager<TestCase> testCaseFragmentManager = new FragmentManager<>();
	private TestCase testCase;

	public TestCasePersistence() throws IOException {
		this(XMLUtil.createEmptyDocument());
	}

	public TestCasePersistence(Document document) throws IOException {
		this.document = document;
		testCaseFragmentManager.init(EXTENDED_PLUGIN_ID, EXTENDED_POINT_FRAGMENT);
	}

	@Override
	public TestCase getArtifact() {
		return testCase;
	}

	public void setTestCase(TestCase testCase) {
		this.testCase = testCase;
	}

	@Override
	public Document getDocument() {
		return document;
	}

	@Override
	public Element writeFragment(Object object) throws IOException {
		return getTestCaseFragmentManager().writeFragment(object, this);
	}

	private FragmentManager<TestCase> getTestCaseFragmentManager() {
		return testCaseFragmentManager;
	}

	@Override
	public Object readFragment(Element element) throws IOException {
		return getTestCaseFragmentManager().readFragment(element, this);
	}


}
