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

package de.d3web.empiricaltesting;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.strings.Strings;
import de.d3web.testcase.model.CheckTemplate;
import de.d3web.testcase.model.DefaultTestCase;
import de.d3web.testcase.model.FindingTemplate;
import de.d3web.testcase.model.TestCase;
import de.d3web.testcase.persistence.TestCasePersistence;
import de.d3web.utils.Log;

/**
 * Reads legacy xml persistence for {@link SequentialTestCase}s, but creates {@link DefaultTestCase}s. Writing is not
 * done here, because there are other handlers for {@link DefaultTestCase}s.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 30.10.15
 */
public class SequentialTestCaseHandler implements FragmentHandler<TestCase> {

	private static final String SEQUENTIAL_TEST_CASES_OLD = "SeqTestCaseRepository"; // compatibility
	private static final String SEQUENTIAL_TEST_CASE_OLD = "STestCase"; // compatibility
	private static final String SEQUENTIAL_TEST_CASES = "SequentialTestCaseRepository";
	private static final String SEQUENTIAL_TEST_CASE = "SequentialTestCase";
	private static final String NAME = "Name";
	private static final String RATED_TEST_CASE = "RatedTestCase";
	public static final String EXPECTED_FINDINGS = "ExpectedFindings";
	private static final String SOLUTIONS = "Solutions";
	private static final String FINDINGS = "Findings";
	private static final String START_DATE = "startDate";
	private static final String TIMESTAMP = "Time";

	@Override
	public Object read(Element element, Persistence<TestCase> persistence) throws IOException {

		Element stcElement;
		String tagName = element.getTagName();
		if (tagName.equals(SEQUENTIAL_TEST_CASES_OLD) || tagName.equals(SEQUENTIAL_TEST_CASES)) {
			List<Element> children = XMLUtil.getChildren(element, SEQUENTIAL_TEST_CASE_OLD, SEQUENTIAL_TEST_CASE);
			stcElement = children.get(0);
			if (children.size() > 1) {
				Log.severe("Unable to read multiple SequentialTestCases in on repository in compatibility mode. " +
						"Only the first SequestionTestCase will be loaded.");
			}
		}
		else {
			stcElement = element;
		}
		DefaultTestCase testCase = new DefaultTestCase();

		String startDateString = stcElement.getAttribute(START_DATE);
		Date startDate;
		if (!Strings.isBlank(startDateString)) {
			try {
				startDate = Strings.readDate(startDateString);
			}
			catch (ParseException e) {
				throw new IOException("Unable to parse start date of test case: " + startDateString);
			}
			testCase.setStartDate(startDate);
		}

		String name = stcElement.getAttribute(NAME);
		if (!Strings.isBlank(name)) {
			testCase.setDescription(name);
		}

		if (persistence instanceof TestCasePersistence) {
			((TestCasePersistence) persistence).setTestCase(testCase);
		}

		for (Element rtcElement : XMLUtil.getChildren(stcElement, RATED_TEST_CASE)) {
			Date date;
			String time = rtcElement.getAttribute(TIMESTAMP);
			if (Strings.isBlank(time)) {
				Collection<Date> chronology = testCase.chronology();
				if (!chronology.isEmpty()) {
					List<Date> chronologyList = new ArrayList<>(chronology);
					date = new Date(chronologyList.get(chronologyList.size() - 1).getTime() + 1);
				}
				else {
					date = testCase.getStartDate();
				}
			}
			else {
				try {
					date = Strings.readDate(time);
				}
				catch (ParseException e) {
					throw new IOException(e);
				}

			}
			String rtcName = rtcElement.getAttribute(NAME);
			if (!Strings.isBlank(rtcName)) {
				testCase.addDescription(date, rtcName);
			}

			for (Element entryChildElements : XMLUtil.getChildren(rtcElement, FINDINGS)) {
				for (Element findingOrCheckElement : XMLUtil.getChildren(entryChildElements)) {
					FindingTemplate findingTemplate = (FindingTemplate) persistence.readFragment(findingOrCheckElement);
					testCase.addFinding(date, findingTemplate);
				}
			}

			for (Element entryChildElements : XMLUtil.getChildren(rtcElement, EXPECTED_FINDINGS, SOLUTIONS)) {
				for (Element findingOrCheckElement : XMLUtil.getChildren(entryChildElements)) {
					CheckTemplate checkTemplate = (CheckTemplate) persistence.readFragment(findingOrCheckElement);
					testCase.addCheck(date, checkTemplate);
				}
			}
		}

		return testCase;
	}

	@Override
	public Element write(Object object, Persistence<TestCase> persistence) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean canRead(Element element) {
		String tagName = element.getTagName();
		return Arrays.asList(
				SEQUENTIAL_TEST_CASES_OLD,
				SEQUENTIAL_TEST_CASE_OLD,
				SEQUENTIAL_TEST_CASES,
				SEQUENTIAL_TEST_CASE).contains(tagName);
	}

	@Override
	public boolean canWrite(Object object) {
		// not supposed to write... we create a {@link DefaultTestCase} that can be written by other handlers
		return false;
	}
}
