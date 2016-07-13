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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import com.denkbares.strings.Strings;
import de.d3web.testcase.model.CheckTemplate;
import de.d3web.testcase.model.DefaultTestCase;
import de.d3web.testcase.model.DescribedTestCase;
import de.d3web.testcase.model.FindingTemplate;
import de.d3web.testcase.model.TemplateTestCase;
import de.d3web.testcase.model.TestCase;
import de.d3web.testcase.record.SessionRecordWrapper;

/**
 * Reads and writes {@link DefaultTestCase}s.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 28.10.15
 */
public class DefaultTestCaseHandler implements FragmentHandler<TestCase> {

	protected static final String TEST_CASE = "TestCase";
	protected static final String TEST_CASE_ENTRY = "TestCaseEntry";
	protected static final String FINDINGS = "Findings";
	protected static final String CHECKS = "Checks";
	protected static final String DEFAULT = "Default";
	protected static final String START_DATE = "startDate";
	protected static final String DATE = "date";
	protected static final String DESCRIPTION = "description";

	@Override
	public Object read(Element element, Persistence<TestCase> persistence) throws IOException {
		DefaultTestCase testCase = new DefaultTestCase();
		if (persistence instanceof TestCasePersistence) {
			((TestCasePersistence) persistence).setTestCase(testCase);
		}

		testCase.setStartDate(readStartDate(element));

		String description = readDescription(element);
		if (!Strings.isBlank(description)) {
			testCase.setDescription(description);
		}

		for (Element entryElement : XMLUtil.getChildren(element, TEST_CASE_ENTRY)) {
			Date date = readDate(entryElement);

			// make sure the entry appears in the chronology, even if it is empty otherwise
			testCase.addFinding(date);

			String comment = readDescription(entryElement);
			if (!Strings.isBlank(comment)) {
				testCase.addDescription(date, comment);
			}

			testCase.addFinding(date, readFindings(persistence, entryElement));
			testCase.addCheck(date, readChecks(persistence, entryElement));
		}

		return testCase;
	}

	protected CheckTemplate[] readChecks(Persistence<TestCase> persistence, Element entryElement) throws IOException {
		List<CheckTemplate> checkTemplates = new ArrayList<>();
		for (Element checksElement : XMLUtil.getChildren(entryElement, CHECKS)) {
			for (Element checkElement : XMLUtil.getChildren(checksElement)) {
				CheckTemplate check = (CheckTemplate) persistence.readFragment(checkElement);
				checkTemplates.add(check);
			}
		}
		return checkTemplates.toArray(new CheckTemplate[checkTemplates.size()]);
	}

	protected FindingTemplate[] readFindings(Persistence<TestCase> persistence, Element entryElement) throws IOException {
		List<FindingTemplate> findings = new ArrayList<>();
		for (Element findingsElement : XMLUtil.getChildren(entryElement, FINDINGS)) {
			for (Element findingElement : XMLUtil.getChildren(findingsElement)) {
				findings.add((FindingTemplate) persistence.readFragment(findingElement));
			}
		}
		return findings.toArray(new FindingTemplate[findings.size()]);
	}

	protected Date readDate(Element entryElement) throws IOException {
		String dateString = entryElement.getAttribute(DATE);
		return readDate(dateString);
	}

	protected Date readDate(String dateString) throws IOException {
		Date date;
		try {
			date = Strings.readDate(dateString);
		}
		catch (ParseException e) {
			throw new IOException(e);
		}
		return date;
	}

	protected String readDescription(Element element) {
		return element.getAttribute(DESCRIPTION);
	}

	protected Date readStartDate(Element element) throws IOException {
		String startDateString = element.getAttribute(START_DATE);
		return readDate(startDateString);
	}

	@Override
	public Element write(Object object, Persistence<TestCase> persistence) throws IOException {
		return writeTestCase((TestCase) object, persistence);
	}

	protected Element writeTestCase(TestCase testCase, Persistence<TestCase> persistence) throws IOException {
		Element testCaseElement = createTestCaseElement(testCase, persistence, DEFAULT);
		if (testCase instanceof DescribedTestCase) {
			writeTestCaseDescription((DescribedTestCase) testCase, testCaseElement);
		}

		for (Date date : testCase.chronology()) {
			writeEntry(persistence, testCase, date, testCaseElement);
		}

		return testCaseElement;
	}

	protected void writeEntry(Persistence<TestCase> persistence, TestCase testCase, Date date, Element testCaseElement) throws IOException {
		TemplateTestCase templateTestCase = (TemplateTestCase) testCase;
		Element entryElement = createEntryElement(testCaseElement);
		writeEntryDate(entryElement, date);
		if (testCase instanceof DescribedTestCase) {
			DescribedTestCase describedTestCase = (DescribedTestCase) testCase;
			writeEntryDescription(describedTestCase.getDescription(date), entryElement);
		}
		writeFindings(persistence, templateTestCase.getFindingTemplates(date), entryElement);
		writeChecks(persistence, templateTestCase.getCheckTemplates(date), entryElement);
	}

	protected void writeEntryDescription(String description, Element entryElement) {
		if (description != null) {
			entryElement.setAttribute(DESCRIPTION, description);
		}
	}

	protected Element createEntryElement(Element testCaseElement) {
		Element entryElement = testCaseElement.getOwnerDocument().createElement(TEST_CASE_ENTRY);
		testCaseElement.appendChild(entryElement);
		return entryElement;
	}

	protected Element createTestCaseElement(TestCase testCase, Persistence<TestCase> persistence, String type) {
		Document document = persistence.getDocument();
		Element testCaseElement = document.createElement(TEST_CASE);
		testCaseElement.setAttribute(XMLUtil.TYPE, type);
		testCaseElement.setAttribute(START_DATE, Strings.writeDate(testCase.getStartDate()));
		return testCaseElement;
	}

	protected void writeTestCaseDescription(DescribedTestCase testCase, Element testCaseElement) {
		String testCaseDescription = testCase.getDescription();
		if (testCaseDescription != null) {
			testCaseElement.setAttribute(DESCRIPTION, testCaseDescription);
		}
	}

	protected void writeEntryDate(Element entryElement, Date date) {
		entryElement.setAttribute(DATE, Strings.writeDate(date));
	}

	protected void writeChecks(Persistence<TestCase> persistence, Collection<CheckTemplate> checkTemplates, Element entryElement) throws IOException {
		Document document = entryElement.getOwnerDocument();
		if (!checkTemplates.isEmpty()) {
			Element checksElement = document.createElement(CHECKS);
			entryElement.appendChild(checksElement);
			for (CheckTemplate checkTemplate : checkTemplates) {
				checksElement.appendChild(persistence.writeFragment(checkTemplate));
			}
		}
	}

	protected void writeFindings(Persistence<TestCase> persistence, Collection<FindingTemplate> findingTemplates, Element entryElement) throws IOException {
		Document document = entryElement.getOwnerDocument();
		if (!findingTemplates.isEmpty()) {
			Element findingsElement = document.createElement(FINDINGS);
			entryElement.appendChild(findingsElement);
			for (FindingTemplate findingTemplate : findingTemplates) {
				findingsElement.appendChild(persistence.writeFragment(findingTemplate));
			}
		}
	}

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, TEST_CASE, DEFAULT);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof DefaultTestCase || object instanceof SessionRecordWrapper;
	}
}
