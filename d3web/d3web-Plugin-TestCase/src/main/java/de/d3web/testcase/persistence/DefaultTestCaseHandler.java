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
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.strings.Strings;
import de.d3web.testcase.model.CheckTemplate;
import de.d3web.testcase.model.DefaultTestCase;
import de.d3web.testcase.model.FindingTemplate;
import de.d3web.testcase.model.TestCase;

/**
 * Reads and writes {@link TestCase}s.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 28.10.15
 */
public class DefaultTestCaseHandler implements FragmentHandler<TestCase> {

	private static final String TEST_CASE = "TestCase";
	private static final String TEST_CASE_ENTRY = "TestCaseEntry";
	private static final String FINDINGS = "Findings";
	private static final String CHECKS = "Checks";
	private static final String DEFAULT = "Default";
	private static final String START_DATE = "startDate";
	private static final String DATE = "date";
	private static final String COMMENT = "date";

	@Override
	public Object read(Element element, Persistence<TestCase> persistence) throws IOException {
		String startDateString = element.getAttribute(START_DATE);
		Date startDate;
		try {
			startDate = Strings.readDate(startDateString);
		}
		catch (ParseException e) {
			throw new IOException("Unable to parse start date of test case: " + startDateString);
		}
		DefaultTestCase testCase = new DefaultTestCase();
		testCase.setStartDate(startDate);
		if (persistence instanceof TestCasePersistence) {
			((TestCasePersistence) persistence).setTestCase(testCase);
		}

		for (Element entryElement : XMLUtil.getChildren(element, TEST_CASE_ENTRY)) {
			String dateString = entryElement.getAttribute(DATE);
			Date date;
			try {
				date = Strings.readDate(dateString);
			}
			catch (ParseException e) {
				throw new IOException(e);
			}

			String comment = entryElement.getAttribute(COMMENT);
			if (!Strings.isBlank(comment)) {
				testCase.addComment(date, comment);
			}
			for (Element findingsElement : XMLUtil.getChildren(entryElement, FINDINGS)) {
				for (Element findingElement : XMLUtil.getChildren(findingsElement)) {
					FindingTemplate finding = (FindingTemplate) persistence.readFragment(findingElement);
					testCase.addFinding(date, finding);
				}
			}
			for (Element checksElement : XMLUtil.getChildren(entryElement, CHECKS)) {
				for (Element checkElement : XMLUtil.getChildren(checksElement)) {
					CheckTemplate check = (CheckTemplate) persistence.readFragment(checkElement);
					testCase.addCheck(date, check);
				}
			}
		}

		return testCase;
	}

	@Override
	public Element write(Object object, Persistence<TestCase> persistence) throws IOException {
		DefaultTestCase testCase = (DefaultTestCase) object;

		Element testCaseElement = persistence.getDocument().createElement(TEST_CASE);
		testCaseElement.setAttribute(XMLUtil.TYPE, DEFAULT);
		testCaseElement.setAttribute(START_DATE, Strings.writeDate(testCase.getStartDate()));

		for (Date date : testCase.chronology()) {
			Collection<FindingTemplate> findingTemplates = testCase.getFindingTemplates(date);
			Collection<CheckTemplate> checkTemplates = testCase.getCheckTemplates(date);
			if (findingTemplates.isEmpty() && checkTemplates.isEmpty()) continue;

			Element entryElement = persistence.getDocument().createElement(TEST_CASE_ENTRY);
			testCaseElement.appendChild(entryElement);
			entryElement.setAttribute(DATE, Strings.writeDate(date));
			String comment = testCase.getComment(date);
			if (comment != null) {
				entryElement.setAttribute(COMMENT, comment);
			}

			if (!findingTemplates.isEmpty()) {
				Element findingsElement = persistence.getDocument().createElement(FINDINGS);
				entryElement.appendChild(findingsElement);
				for (FindingTemplate findingTemplate : findingTemplates) {
					findingsElement.appendChild(persistence.writeFragment(findingTemplate));
				}
			}
			if (!checkTemplates.isEmpty()) {
				Element checksElement = persistence.getDocument().createElement(CHECKS);
				entryElement.appendChild(checksElement);
				for (CheckTemplate checkTemplate : checkTemplates) {
					checksElement.appendChild(persistence.writeFragment(checkTemplate));
				}
			}
		}

		return testCaseElement;
	}

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, TEST_CASE, DEFAULT);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof DefaultTestCase;
	}
}
