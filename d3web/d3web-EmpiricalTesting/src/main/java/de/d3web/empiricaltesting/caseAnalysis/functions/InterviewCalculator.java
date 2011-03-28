/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.empiricaltesting.caseAnalysis.functions;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.interviewmanager.NextUnansweredQuestionFormStrategy;
import de.d3web.empiricaltesting.Finding;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.caseAnalysis.RTCDiff;

public class InterviewCalculator extends PrecisionRecallCalculator {

	private final Session session;

	public InterviewCalculator(KnowledgeBase kb) {
		this.session = SessionFactory.createSession(kb);
		this.session.getInterview().setFormStrategy(new NextUnansweredQuestionFormStrategy());
	}

	// -------Rated Precision--------

	/**
	 * Calculates the Interview-Precision of a RatedTestCase.
	 * 
	 * @param rtc The RatedTestCase necessary for the calculation
	 * @return Interview-Precision of RatedTestCase
	 */
	@Override
	public double prec(RTCDiff rtcDiff) {
		double numerator = similarity(rtcDiff.getCase());
		numerator /= rtcDiff.getCase().getFindings().size();
		return numerator;
	}

	// -------Rated Recall--------

	/**
	 * Calculates the Interview-Recall of a RatedTestCase.
	 * 
	 * @param rtcDiff The RatedTestCase necessary for the calculation
	 * @return Interview-Recall of RatedTestCase
	 */
	@Override
	public double rec(RTCDiff rtcDiff) {
		double numerator = similarity(rtcDiff.getCase());
		numerator /= rtcDiff.getCase().getFindings().size();
		return numerator;
	}

	// -------Similarity Helper--------

	/**
	 * Calculates an overall similarity of expected an actually asked questions.
	 * This method is necessary for the precision and recall calculation.
	 * 
	 * @param rtc The RatedTestCase necessary for the calculation
	 * @return Total similarity of expected and actually asked questions
	 */
	private double similarity(RatedTestCase rtc) {

		double sum = 0;

		for (Finding f : rtc.getFindings()) {

			Question expected = f.getQuestion();
			Question asked = getNextQuestion();

			// Compare expected question with asked question
			if (expected.equals(asked)) {
				sum += 1;
			}

			// Set answer of current question in XPS-Case
			setCaseValue(f.getQuestion(), f.getValue());
		}

		return sum;
	}

	/**
	 * Sets question and answer values in the underlying XPS-Case
	 * 
	 * @param q Question which will be set.
	 * @param a Answer which will be set.
	 */
	private void setCaseValue(Question q, Value v) {
		Fact fact = FactFactory.createUserEnteredFact(q, v);
		session.getBlackboard().addValueFact(fact);
	}

	/**
	 * Returns the question which will be asked by the dialog next.
	 * 
	 * @return Question which will be asked next.
	 */
	private Question getNextQuestion() {
		return (Question) session.getInterview().nextForm().getInterviewObject();
	}

}
