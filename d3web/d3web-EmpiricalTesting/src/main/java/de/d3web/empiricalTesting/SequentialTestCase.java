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

package de.d3web.empiricalTesting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import de.d3web.caseGeneration.HeuristicScoreRatingStrategy;
import de.d3web.caseGeneration.RatingStrategy;
import de.d3web.caseGeneration.StateRatingStrategy;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Answer;
import de.d3web.core.knowledge.terminology.Diagnosis;
import de.d3web.core.knowledge.terminology.DiagnosisState;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.session.CaseFactory;
import de.d3web.core.session.XPSCase;
import de.d3web.xcl.XCLModel;
import de.d3web.xcl.inference.PSMethodXCL;

public class SequentialTestCase {

	private String name = "";
	private List<RatedTestCase> ratedTestCases;

	/**
	 * Default Constructor
	 */
	public SequentialTestCase() {
		ratedTestCases = new ArrayList<RatedTestCase>();
	}

	/**
	 * Adds RatedTestCase to this SequentialTestCase.
	 * @param ratedTestCase The RatedTestCase which will be added
	 * @return true if the RatedTestCase was added to this SequntialTestCase
	 */
	public boolean add(RatedTestCase ratedTestCase) {
		return ratedTestCases.add(ratedTestCase);
	}

	/**
	 * Inverses the rating comparator of all RatedSolutions
	 * in all RatedTestCases of this SequentialTestCase.
	 */
	public void inverseSortSolutions() {
		for (RatedTestCase ratedTestCase : ratedTestCases) {
			ratedTestCase.inverseSortSolutions();
		}
	}

	/**
	 * Here, the name is copied and new instances of the contained test cases
	 * are created. The objects within the test cases are not created again but
	 * taken from the original one.
	 * 
	 * @return a flat copy of the instance
	 */
	public SequentialTestCase flatClone() {
		SequentialTestCase newSTC = new SequentialTestCase();
		newSTC.setName(this.getName());
		for (RatedTestCase rtc : ratedTestCases) {
			newSTC.add(rtc.flatClone());
		}
		return newSTC;
	}

	/**
	 * Shows String Representation of this SequentialTestCase
	 * 
	 * name: ratedTestCase, RatedTestCase, ...
	 */
	@Override
	public String toString() {
		StringBuffer buffy = new StringBuffer(getName() + ": ");
		for (RatedTestCase rtc : ratedTestCases) {
			buffy.append(rtc.toString() + ", ");
		}
		buffy.replace(buffy.length() - 2, buffy.length(), ""); // remove last
																// ", "
		return buffy.toString();
	}

	/**
	 * Returns the name of this SequentialTestCase.
	 * @return name of this SequentialTestCase
	 */
	public synchronized String getName() {
		return name;
	}

	/**
	 * Sets the name of this SequentialTestCase.
	 * @param name desired name of this SequentialTestCase
	 */
	public synchronized void setName(String name) {
		this.name = name;
	}

	
	/**
	 * Finds the derived Solutions for this SequentialTestCase.
	 * @param kb the underlying KnowledgeBase
	 * @param psMethodContext the problemsolver which is used to 
	 * 						  get the derived solutions
	 */
	@SuppressWarnings("unchecked")
	public void deriveSolutions(KnowledgeBase kb, Class psMethodContext) {
		RatingStrategy ratingStrategy = new StateRatingStrategy();
		XPSCase thecase = CaseFactory.createXPSCase(kb);
		
		for (RatedTestCase rtc : ratedTestCases) {
						
			// Answer and Question setting in Case
			for (Finding f : rtc.getFindings()) {
				setValues(thecase, f);
			}
			
			// Check used Rating (StateRating or ScoreRating) in ExpectedSolutions	
			for (RatedSolution rs : rtc.getExpectedSolutions()) {
				Rating r = rs.getRating();
				if (!(r instanceof StateRating)) {
					ratingStrategy = new HeuristicScoreRatingStrategy();
					break;
				}
			}

			// Derive Solutions
			Collection<KnowledgeSlice> slices = thecase.getKnowledgeBase().getAllKnowledgeSlicesFor(PSMethodXCL.class);
			
			if (slices.size() != 0) {	
				deriveXCLSolutions(thecase, rtc, slices);
			} else { 
				deriveSolutionsForPSMethod(thecase, rtc, psMethodContext, ratingStrategy);
			}			

			// Mark this RatedTestCase as successfully derived
			rtc.setDerivedSolutionsAreUpToDate(true);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HHmm");
			rtc.setTestingDate(df.format(new Date()));
		}
	}

	/**
	 * Sets the Answer of the Finding to all Questions which have
	 * the same name as the Question of the Finding.
	 * @param thecase the current XPSCase
	 * @param f the current processed Finding
	 */
	private void setValues(XPSCase thecase, Finding f) {
		
		List<Question> questionsToAnswer = getAmbiguousQuestions(thecase, f);
		
		for (Question q : questionsToAnswer) {
			List answers = new ArrayList();
			
			// Necessary for QuestionMC, otherwise only one answer can be given
			if (q instanceof QuestionMC) {
				answers.addAll(((QuestionMC) q).getValue(thecase));
			}
			
			addAnswers(q, f.getAnswer(), answers);
			thecase.setValue((Question) q, answers.toArray());
		}
		
	}

	/**
	 * Adds all Answers of the Questions which have the same name
	 * as the answer of the finding to the list of answers to be set.
	 * @param q 
	 * @param answer
	 * @param answers
	 */
	private void addAnswers(Question q, Answer answer, List answers) {
		if (q instanceof QuestionChoice) {
			for (Answer a : ((QuestionChoice) q).getAllAlternatives())
				if (a.getText().equals(answer.getText()))
					answers.add(a);
		} else {
			answers.add(answer);
		}
	}

	/**
	 * Returns a List of Questions with the same Name.
	 * @param thecase the current XPS Case
	 * @param f the currently processed Finding
	 * @return
	 */
	private List<Question> getAmbiguousQuestions(XPSCase thecase, Finding f) {
		List<Question> ambiguousQuestions = new ArrayList<Question>();
		for (Question q : thecase.getKnowledgeBase().getQuestions()) {
			if (q.getText().equals(f.getQuestion().getText()))
				ambiguousQuestions.add(q);
		}
		return ambiguousQuestions;
	}

	private void deriveSolutionsForPSMethod(XPSCase thecase, RatedTestCase rtc,
			Class<? extends PSMethod> psMethodContext, RatingStrategy ratingStrategy) {
		
		for (Diagnosis solution : thecase.getDiagnoses()) {
			Rating rating = ratingStrategy.getRatingFor(solution, thecase);
			if (rating.isProblemSolvingRelevant()) {
				RatedSolution ratedSolution = new RatedSolution(solution, rating);
				rtc.addDerived(ratedSolution);
			}
		}
		
//		for (Diagnosis dia : thecase.getDiagnoses()) {
//			
//			DiagnosisState state = dia.getState(thecase, psMethodContext);
//			// Only suggested and established diagnoses are taken into account
//			if (!state.equals(DiagnosisState.UNCLEAR)
//					&& !state.equals(DiagnosisState.EXCLUDED)) {
//				if (!useStateRatings) { // use ScoreRating
//					DiagnosisScore sco = dia.getScore(thecase, psMethodContext);
//					RatedSolution rs = new RatedSolution(dia, new ScoreRating(
//							sco.getScore()));
//					rtc.addDerived(rs);	
//				} else { // use StateRating
//					RatedSolution rs = new RatedSolution(dia, new StateRating(state));
//					rtc.addDerived(rs);
//				}
//			}
//		}
		
	}

	private void deriveXCLSolutions(XPSCase thecase, RatedTestCase rtc, Collection<KnowledgeSlice> slices) {
		for (KnowledgeSlice slice : slices) {
			if (slice instanceof XCLModel) {
				Diagnosis d = ((XCLModel) slice).getSolution();
				DiagnosisState s = ((XCLModel) slice).getState(thecase);
				if (!s.equals(DiagnosisState.UNCLEAR) && !s.equals(DiagnosisState.EXCLUDED)) {
					RatedSolution rs = new RatedSolution(d, new StateRating(s));
					rtc.addDerived(rs);
				}
			}
		}
		
	}

	
	public List<Answer> getAnswerForQuestionNum(KnowledgeBase kb, String questionname) {
		XPSCase thecase = CaseFactory.createXPSCase(kb);
		
		for (RatedTestCase rtc : ratedTestCases) {
			// Answer and Question setting in Case
			for (Finding f : rtc.getFindings()) {
				Object q = f.getQuestion();
				List answers = new ArrayList();
				
				// Necessary for QuestionMC, otherwise only one answer can be given
				if (q instanceof QuestionMC) {
					answers.addAll(((QuestionMC) q).getValue(thecase));
				}
				
				answers.add(f.getAnswer());
				thecase.setValue((Question) q, answers.toArray());
			}		
		}
		
		List<? extends Question> answeredQuestions = thecase.getAnsweredQuestions();
		for (Question question : answeredQuestions) {
			if (question.getText().equals(questionname))
				return question.getValue(thecase);
		}
		
		return null;
	}
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((ratedTestCases == null) ? 0 : ratedTestCases.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SequentialTestCase))
			return false;
		SequentialTestCase other = (SequentialTestCase) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (ratedTestCases == null) {
			if (other.ratedTestCases != null)
				return false;
		} else if (!ratedTestCases.equals(other.ratedTestCases))
			return false;
		return true;
	}

	/**
	 * Returns the SequentialTestCase's RatedTestCases
	 * @return List of RatedTestCases
	 */
	public List<RatedTestCase> getCases() {
		return ratedTestCases;
	}

	/**
	 * Tests if this SequentialTestCase contains the same
	 * RatedTestCase as another SequentialTestCase 
	 * @param obj Other SequentialTestCase
	 * @return true, if RatedTestCases are equal
	 * 		   false, if RatedTestCases aren't equal
	 */
	public boolean testTo(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SequentialTestCase))
			return false;
		SequentialTestCase other = (SequentialTestCase) obj;
		if (ratedTestCases == null) {
			if (other.ratedTestCases != null)
				return false;
		} else if (!ratedTestCases.containsAll(other.ratedTestCases))
			return false;
		return true;
	}

}
