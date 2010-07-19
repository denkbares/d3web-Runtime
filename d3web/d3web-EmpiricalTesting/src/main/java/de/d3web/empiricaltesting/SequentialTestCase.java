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

package de.d3web.empiricaltesting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import de.d3web.casegeneration.HeuristicScoreRatingStrategy;
import de.d3web.casegeneration.RatingStrategy;
import de.d3web.casegeneration.StateRatingStrategy;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.interviewmanager.NextUnansweredQuestionFormStrategy;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.xcl.XCLModel;
import de.d3web.xcl.inference.PSMethodXCL;

public class SequentialTestCase {

	private String name = "";
	private final List<RatedTestCase> ratedTestCases;

	/**
	 * Default Constructor
	 */
	public SequentialTestCase() {
		ratedTestCases = new ArrayList<RatedTestCase>();
	}

	/**
	 * Adds RatedTestCase to this SequentialTestCase.
	 * 
	 * @param ratedTestCase The RatedTestCase which will be added
	 * @return true if the RatedTestCase was added to this SequntialTestCase
	 */
	public boolean add(RatedTestCase ratedTestCase) {
		return ratedTestCases.add(ratedTestCase);
	}

	/**
	 * Inverses the rating comparator of all RatedSolutions in all
	 * RatedTestCases of this SequentialTestCase.
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
	 * 
	 * @return name of this SequentialTestCase
	 */
	public synchronized String getName() {
		return name;
	}

	/**
	 * Sets the name of this SequentialTestCase.
	 * 
	 * @param name desired name of this SequentialTestCase
	 */
	public synchronized void setName(String name) {
		this.name = name;
	}

	/**
	 * Finds the derived solutions for this SequentialTestCase.
	 * 
	 * @param kb the underlying KnowledgeBase
	 * @param psMethodContext the problem solver which is used to get the
	 *        derived solutions
	 */
	@SuppressWarnings("unchecked")
	public void deriveSolutions(KnowledgeBase kb, Class psMethodContext) {
		RatingStrategy ratingStrategy = new StateRatingStrategy();
		Session session = SessionFactory.createSession(kb);
		session.getInterview().setFormStrategy(new NextUnansweredQuestionFormStrategy());

		for (RatedTestCase rtc : ratedTestCases) {
			// Answer and Question setting in Case
			for (Finding f : rtc.getFindings()) {
				session.getBlackboard().addValueFact(
						FactFactory.createFact(f.getQuestion(), f.getValue(),
						PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));
			}

			// Check used Rating (StateRating or ScoreRating) in
			// ExpectedSolutions
			for (RatedSolution rs : rtc.getExpectedSolutions()) {
				Rating r = rs.getRating();
				if (!(r instanceof StateRating)) {
					ratingStrategy = new HeuristicScoreRatingStrategy();
					break;
				}
			}

			// Derive Solutions
			Collection<KnowledgeSlice> slices = session.getKnowledgeBase().getAllKnowledgeSlicesFor(
					PSMethodXCL.class);
			if (slices.size() != 0) {
				deriveXCLSolutions(session, rtc, slices);
			}
			else {
				deriveSolutionsForPSMethod(session, rtc, psMethodContext, ratingStrategy);
			}

			// Mark this RatedTestCase as successfully derived
			rtc.setDerivedSolutionsAreUpToDate(true);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HHmm");
			rtc.setTestingDate(df.format(new Date()));
		}
	}

	private void deriveSolutionsForPSMethod(Session session, RatedTestCase rtc,
			Class<? extends PSMethod> psMethodContext, RatingStrategy ratingStrategy) {

		for (Solution solution : session.getKnowledgeBase().getSolutions()) {
			Rating rating = ratingStrategy.getRatingFor(solution, session);
			if (rating.isProblemSolvingRelevant()) {
				RatedSolution ratedSolution = new RatedSolution(solution, rating);
				rtc.addDerived(ratedSolution);
			}
		}

		// for (Diagnosis dia : session.getDiagnoses()) {
		//
		// DiagnosisState state = dia.getState(session, psMethodContext);
		// // Only suggested and established diagnoses are taken into account
		// if (!state.equals(DiagnosisState.UNCLEAR)
		// && !state.equals(DiagnosisState.EXCLUDED)) {
		// if (!useStateRatings) { // use ScoreRating
		// DiagnosisScore sco = dia.getScore(session, psMethodContext);
		// RatedSolution rs = new RatedSolution(dia, new ScoreRating(
		// sco.getScore()));
		// rtc.addDerived(rs);
		// } else { // use StateRating
		// RatedSolution rs = new RatedSolution(dia, new StateRating(state));
		// rtc.addDerived(rs);
		// }
		// }
		// }

	}

	private void deriveXCLSolutions(Session session, RatedTestCase rtc, Collection<KnowledgeSlice> slices) {
		for (KnowledgeSlice slice : slices) {
			if (slice instanceof XCLModel) {
				Solution solution = ((XCLModel) slice).getSolution();
				de.d3web.core.knowledge.terminology.Rating s = ((XCLModel) slice).getState(session);
				if (!s.hasState(State.UNCLEAR)
						&& !s.hasState(State.EXCLUDED)) {
					RatedSolution rs = new RatedSolution(solution, new StateRating(s));
					rtc.addDerived(rs);
				}
			}
		}

	}

	// public List<Answer> getAnswerForQuestionNum(KnowledgeBase kb, String
	// questionname) {
	// Session session = CaseFactory.createSession(kb);
	//
	// for (RatedTestCase rtc : ratedTestCases) {
	// // Answer and Question setting in Case
	// for (Finding f : rtc.getFindings()) {
	// Object q = f.getQuestion();
	// List answers = new ArrayList();
	//
	// // Necessary for QuestionMC, otherwise only one answer can be given
	// if (q instanceof QuestionMC) {
	// answers.addAll(((QuestionMC) q).getValue(session));
	// }
	//
	// answers.add(f.getAnswer());
	// session.setValue((Question) q, answers.toArray());
	// }
	// }
	//
	// List<? extends Question> answeredQuestions =
	// session.getAnsweredQuestions();
	// for (Question question : answeredQuestions) {
	// if (question.getText().equals(questionname))
	// return question.getValue(session);
	// }
	//
	// return null;
	// }

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
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof SequentialTestCase)) return false;
		SequentialTestCase other = (SequentialTestCase) obj;
		if (name == null) {
			if (other.name != null) return false;
		}
		else if (!name.equals(other.name)) return false;
		if (ratedTestCases == null) {
			if (other.ratedTestCases != null) return false;
		}
		else if (!ratedTestCases.equals(other.ratedTestCases)) return false;
		return true;
	}

	/**
	 * Returns the SequentialTestCase's RatedTestCases
	 * 
	 * @return List of RatedTestCases
	 */
	public List<RatedTestCase> getCases() {
		return ratedTestCases;
	}

	/**
	 * Tests if this SequentialTestCase contains the same RatedTestCase as
	 * another SequentialTestCase
	 * 
	 * @param obj Other SequentialTestCase
	 * @return true, if RatedTestCases are equal false, if RatedTestCases aren't
	 *         equal
	 */
	public boolean testTo(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof SequentialTestCase)) return false;
		SequentialTestCase other = (SequentialTestCase) obj;
		if (ratedTestCases == null) {
			if (other.ratedTestCases != null) return false;
		}
		else if (!ratedTestCases.containsAll(other.ratedTestCases)) return false;
		return true;
	}

}
