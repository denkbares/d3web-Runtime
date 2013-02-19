/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.testcase.TestCaseUtils;
import de.d3web.testcase.model.Check;
import de.d3web.testcase.model.TestCase;
import de.d3web.testing.AbstractTest;
import de.d3web.testing.Message;
import de.d3web.testing.Message.Type;
import de.d3web.testing.TestObjectContainer;
import de.d3web.testing.TestObjectProvider;
import de.d3web.testing.TestObjectProviderManager;
import de.d3web.testing.TestParameter;
import de.d3web.testing.Utils;

/**
 * A simple test to execute test cases.
 * 
 * 
 * @author Jochen Reutelshöfer, Albrecht Striffler (denkbares GmbH)
 * @created 29.06.2012
 */
public class TestCaseTest extends AbstractTest<TestCase> {

	private static final long year = TimeUnit.DAYS.toMillis(365);
	private static final String SEARCH_STRING_DESCRIPTION = "Specifies the knowledge base with which the test case is to be tested.";

	public TestCaseTest() {
		this.addParameter("KnowledgeBase", TestParameter.Type.Regex, TestParameter.Mode.Mandatory,
				SEARCH_STRING_DESCRIPTION);
	}

	@Override
	public Message execute(TestCase testCase, String[] args, String[]... ignores) throws InterruptedException {
		Collection<KnowledgeBase> kbs = getKnowledgeBases(args);
		if (kbs.size() == 0) {
			return new Message(Type.FAILURE, "No Knowledge base found!");
		}

		// we have to check each test against (potentially) multiple KBs
		// that makes message generate a bit more complicated
		List<String> inconsistentKBs = new ArrayList<String>();
		List<String> failedKBs = new ArrayList<String>();
		List<String> passedKBs = new ArrayList<String>();

		for (KnowledgeBase kb : kbs) {

			if (!testCase.check(kb).isEmpty()) {
				inconsistentKBs.add(kb.getId());
				continue;
			}

			Session session = SessionFactory.createSession(kb, testCase.getStartDate());
			boolean failed = false;
			for (Date date : testCase.chronology()) {
				TestCaseUtils.applyFindings(session, testCase, date);
				for (Check check : testCase.getChecks(date, session.getKnowledgeBase())) {
					String time = "(time ";
					if (date.getTime() < year) {
						time += date.getTime() + "ms";
					}
					else {
						time += date;
					}
					time += ")";
					// check if session was terminated due to detected session
					if (!check.check(session)) {
						String messageText = "Check '" + check.getCondition().trim() +
								"' " + time + " failed.";
						failedKBs.add("KB " + kb.getId() + " failed: " + messageText);
						failed = true;
					}

					Utils.checkInterrupt();
				}

			}
			if (!failed) {
				passedKBs.add(kb.getId());
			}
		}

		if (inconsistentKBs.size() > 0 || failedKBs.size() > 0) {
			String message = renderFailureMessage(inconsistentKBs, failedKBs, passedKBs);
			return new Message(Type.FAILURE, message);
		}

		return new Message(Type.SUCCESS);
	}

	private String renderFailureMessage(List<String> inconsistentKBs, List<String> failedKBs, List<String> passedKBs) {
		String message = "";
		if (inconsistentKBs.size() > 0) {
			message += "Knowledge base(s) inconsistent with test: ";
			for (String inconsistentKB : inconsistentKBs) {
				// enumerate inconsistent KBs in one line
				message += " " + inconsistentKB + ";";
			}
			// add line break afterwards
			if (message.endsWith(";")) {
				message = message.substring(0, message.length() - 1) + "\n";
			}
		}
		// enumerate failed KBs one per line
		for (String failedMessage : failedKBs) {
			message += failedMessage + "\n";
		}
		if (passedKBs.size() > 0) {
			message += "Knowledge base(s) passed test: ";
			for (String passedKB : passedKBs) {
				// enumerate inconsistent KBs in one line
				message += " " + passedKB + ";";
			}
			// add line break afterwards
			if (message.endsWith(";")) {
				message = message.substring(0, message.length() - 1) + "\n";
			}
		}
		return message;
	}

	private Collection<KnowledgeBase> getKnowledgeBases(String[] args) {
		if (args.length == 0) return null;
		Collection<KnowledgeBase> result = new ArrayList<KnowledgeBase>();
		for (TestObjectProvider testObjectProvider : TestObjectProviderManager.getTestObjectProviders()) {
			List<TestObjectContainer<KnowledgeBase>> testObjects = testObjectProvider.getTestObjects(
					KnowledgeBase.class, args[0]);
			if (!testObjects.isEmpty()) {
				for (TestObjectContainer<KnowledgeBase> testObjectContainer : testObjects) {
					result.add(testObjectContainer.getTestObject());
				}
			}
		}
		return result;
	}

	@Override
	public Class<TestCase> getTestObjectClass() {
		return TestCase.class;
	}

	@Override
	public String getDescription() {
		return "This test executes test cases. It compares the expected findings defined in the test cases with the findings actually derived by the knowledge base.";
	}

}
