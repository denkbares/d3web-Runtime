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
		this.addParameter("KnowledgeBase", TestParameter.Type.String, TestParameter.Mode.Mandatory,
				SEARCH_STRING_DESCRIPTION);
	}

	@Override
	public Message execute(TestCase testCase, String[] args, String[]... ignores) throws InterruptedException {
		KnowledgeBase kb = getKnowledgeBase(args);
		if (kb == null) {
			return new Message(Type.FAILURE, "Knowledge base not found!");
		}
		if (!testCase.check(kb).isEmpty()) {
			return new Message(Type.FAILURE, "Test is not consistent!");
		}

		Session session = SessionFactory.createSession(kb, testCase.getStartDate());
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
				if (!check.check(session)) {
					String messageText = "Check '" + check.getCondition().trim() +
							"' " + time + " failed.";

					return new Message(Type.FAILURE, messageText);
				}
				Utils.checkInterrupt();
			}
		}

		return new Message(Type.SUCCESS);
	}

	private KnowledgeBase getKnowledgeBase(String[] args) {
		if (args.length == 0) return null;
		for (TestObjectProvider testObjectProvider : TestObjectProviderManager.getTestObjectProviders()) {
			List<TestObjectContainer<KnowledgeBase>> testObjects = testObjectProvider.getTestObjects(
					KnowledgeBase.class, args[0]);
			if (!testObjects.isEmpty()) {
				return testObjects.get(0).getTestObject();
			}
		}
		return null;
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
