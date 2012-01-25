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
import java.util.HashSet;
import java.util.Set;

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
	 * Applies all Findings to the {@link Session}
	 * 
	 * @created 24.01.2012
	 * @param session Session on which the Findings should be applied
	 * @param findings Findings to apply
	 */
	public static void applyFindings(Session session, Collection<Finding> findings) {
		Blackboard blackboard = session.getBlackboard();
		for (Finding f : findings) {
			Fact fact = FactFactory.createUserEnteredFact(f.getTerminologyObject(), f.getValue());
			if (f.getValue() instanceof Indication) {
				blackboard.addInterviewFact(fact);
			}
			else {
				blackboard.addValueFact(fact);
			}
		}
	}

	/**
	 * Returns all questions used in Findings
	 * 
	 * @created 24.01.2012
	 * @param testCase {@link TestCase} to examine
	 * @return Collection of questions used in the specified {@link TestCase}
	 */
	public static Collection<Question> getUsedQuestions(TestCase testCase) {
		Set<Question> questions = new HashSet<Question>();
		for (Date date : testCase.chronology()) {
			for (Finding f : testCase.getFindings(date)) {
				if (f.getTerminologyObject() instanceof Question) {
					questions.add((Question) f.getTerminologyObject());
				}
			}
		}
		return questions;
	}

}
