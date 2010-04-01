/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.empiricalTesting.joba.testcases;

import java.util.ArrayList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Answer;
import de.d3web.core.knowledge.terminology.Diagnosis;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.session.CaseFactory;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.interviewmanager.MQDialogController;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.empiricalTesting.Finding;
import de.d3web.empiricalTesting.RatedSolution;
import de.d3web.empiricalTesting.RatedTestCase;
import de.d3web.empiricalTesting.ScoreRating;
import de.d3web.empiricalTesting.SequentialTestCase;
import de.d3web.scoring.inference.PSMethodHeuristic;

/**
 * @deprecated Please use InterviewBot
 */
public class DDBot2 {

	private static final double SCORE_THRESHOLD = 10;
	List<SequentialTestCase> storedCases = new ArrayList<SequentialTestCase>();
	private int counter;
	private String caseNamePraefix = "STC";
	private int rtcCounter = 0;

	public DDBot2() {
		counter = 0;
	}

	public void traverse(KnowledgeBase knowledge, List<Finding> initFindings)
			throws Exception {
		XPSCase theCase = createCase(knowledge, initFindings);

		RatedTestCase ratedTestCase = new RatedTestCase();
		ratedTestCase.addFindings(initFindings);
		ratedTestCase.addExpected(toRatedSolutions(theCase));
		ratedTestCase.inverseSortSolutions();
		ratedTestCase.setName("RTC" + rtcCounter);
		rtcCounter++;

		SequentialTestCase stc = new SequentialTestCase();
		stc.setName(getCaseNamePraefix());
		stc.add(ratedTestCase);

		traverse(stc, getNextQuestion(theCase), knowledge);
	}

	private void traverse(SequentialTestCase theSeqCase,
			QuestionChoice currentQuestion, KnowledgeBase knowledge)
			throws Exception {
		if (currentQuestion == null) {
			store(theSeqCase);
			return;
		}

		// Iterate over all possible answers of the next question
		List<Finding> flattendFindings = flattenFindings(theSeqCase);
		List<AnswerChoice> nextAnswers = currentQuestion.getAllAlternatives();
		for (AnswerChoice nextAnswer : nextAnswers) {
			XPSCase theCase = createCase(knowledge, flattendFindings);
			setCaseValue(theCase, currentQuestion, nextAnswer);

			RatedTestCase ratedCase = createRatedTestCase(currentQuestion,
					nextAnswer, theCase);
			SequentialTestCase newSequentialCase = theSeqCase.flatClone();

			newSequentialCase.add(ratedCase);

			// GET NEXT QUESTION nextQ
			traverse(newSequentialCase, getNextQuestion(theCase), knowledge);
		}

	}

	// **** THE PRIVATE METHODS ******//

	private void store(SequentialTestCase theSeqCase) {
		theSeqCase.setName(getCaseNamePraefix() + counter);
		storedCases.add(theSeqCase);
		counter++;

	}

	private XPSCase createCase(KnowledgeBase knowledge, List<Finding> findings)
			throws Exception {
		XPSCase theCase = CaseFactory.createXPSCase(knowledge,
				MQDialogController.class);

		for (Finding finding : findings) {
			setCaseValue(theCase, finding.getQuestion(), finding.getAnswer());
		}

		return theCase;
	}

	/**
	 * @return Extracts all findings from the sequentialized test case as they
	 *         were entered in the original order.
	 */
	private List<Finding> flattenFindings(SequentialTestCase theSeqCase) {
		List<Finding> findings = new ArrayList<Finding>();
		for (RatedTestCase c : theSeqCase.getCases()) {
			findings.addAll(c.getFindings());
		}
		return findings;
	}

	private List<RatedSolution> toRatedSolutions(XPSCase theCase) {
		List<RatedSolution> ratedSolutions = new ArrayList<RatedSolution>();
		for (Diagnosis diagnosis : theCase.getDiagnoses()) {
			double score = diagnosis.getScore(theCase, PSMethodHeuristic.class)
					.getScore();
			if (score >= SCORE_THRESHOLD) {
				ScoreRating rating = new ScoreRating(score);
				RatedSolution ratedSolution = new RatedSolution(diagnosis,
						rating);
				ratedSolutions.add(ratedSolution);
			}
		}
		return ratedSolutions;
	}

	private RatedTestCase createRatedTestCase(QuestionChoice currentQuestion,
			AnswerChoice nextAnswer, XPSCase theCase) {
		RatedTestCase ratedCase = new RatedTestCase();
		ratedCase.add(new Finding(currentQuestion, nextAnswer));
		ratedCase.addExpected(toRatedSolutions(theCase));
		ratedCase.inverseSortSolutions();
		ratedCase.setName("RTC" + rtcCounter);
		rtcCounter++;
		return ratedCase;
	}

	private QuestionChoice getNextQuestion(XPSCase theCase) throws Exception {
		MQDialogController controller = (MQDialogController) theCase
				.getQASetManager();
		QASet next = controller.moveToNextRemainingQASet();
		if (next != null && next instanceof QuestionChoice) {
			return (QuestionChoice) next;
		} else if (next != null) {
			List<Question> validQuestions = controller
					.getAllValidQuestionsOf((QContainer) next);
			return (QuestionChoice) validQuestions.get(0);
		} else {
			return null;
		}
	}

	public void setCaseValue(XPSCase theCase, Question q, Answer a)
			throws Exception {
		theCase.setValue(q, new Object[] { a });

	}

	public void setCaseNamePraefix(String caseNamePraefix) {
		this.caseNamePraefix = caseNamePraefix;

	}

	public synchronized String getCaseNamePraefix() {
		return caseNamePraefix;
	}

	public void setCaseCounter(int counter) {
		this.counter = counter;
	}
}
