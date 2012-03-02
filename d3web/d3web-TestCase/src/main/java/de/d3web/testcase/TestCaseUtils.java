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
package de.d3web.testcase;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.testcase.model.Finding;
import de.d3web.testcase.model.TestCase;

/**
 * Provides basic static functions
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 24.01.2012
 */
public class TestCaseUtils {

	/**
	 * Applies the findings of the specified {@link TestCase} at the specified
	 * {@link Date} to the {@link Session}
	 * 
	 * @created 24.01.2012
	 * @param session Session on which the Findings should be applied
	 * @param testCase specified TestCase
	 * @param date specified Date
	 */
	public static void applyFindings(Session session, TestCase testCase, Date date) {
		Blackboard blackboard = session.getBlackboard();
		session.getPropagationManager().openPropagation(date.getTime());
		for (Finding f : testCase.getFindings(date)) {
			Fact fact = FactFactory.createUserEnteredFact(f.getTerminologyObject(), f.getValue());
			if (f.getValue() instanceof Indication) {
				blackboard.addInterviewFact(fact);
			}
			else {
				blackboard.addValueFact(fact);
			}
		}
		session.getPropagationManager().commitPropagation();
	}

	/**
	 * Returns all questions used in Findings
	 * 
	 * @created 24.01.2012
	 * @param testCase {@link TestCase} to examine
	 * @return Collection of questions used in the specified {@link TestCase}
	 */
	public static Collection<Question> getUsedQuestions(TestCase testCase) {
		Collection<Question> questions = new LinkedList<Question>();
		for (Date date : testCase.chronology()) {
			for (Finding f : testCase.getFindings(date)) {
				if (f.getTerminologyObject() instanceof Question) {
					Question question = (Question) f.getTerminologyObject();
					if (!questions.contains(question)) {
						questions.add(question);
					}
				}
			}
		}
		return questions;
	}

}
