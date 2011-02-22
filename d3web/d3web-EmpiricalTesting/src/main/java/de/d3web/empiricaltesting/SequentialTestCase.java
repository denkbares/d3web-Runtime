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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.d3web.casegeneration.HeuristicScoreRatingStrategy;
import de.d3web.casegeneration.RatingStrategy;
import de.d3web.casegeneration.StateRatingStrategy;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.interviewmanager.NextUnansweredQuestionFormStrategy;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;

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
	 */
	public void deriveSolutions(KnowledgeBase kb) {
		RatingStrategy ratingStrategy = new StateRatingStrategy();
		Session session = SessionFactory.createSession(kb);
		session.getInterview().setFormStrategy(new NextUnansweredQuestionFormStrategy());

		for (RatedTestCase rtc : ratedTestCases) {
			// Answer and Question setting in Case
			Collection<Finding> findings = preprocessFindings(rtc.getFindings());
			for (Finding f : findings) {
				Fact fact = FactFactory.createUserEnteredFact(f.getQuestion(), f.getValue());
				session.getBlackboard().addValueFact(fact);
			}

			// Check used Rating in ExpectedSolutions
			for (RatedSolution rs : rtc.getExpectedSolutions()) {
				Rating r = rs.getRating();
				if (!(r instanceof StateRating)) {
					ratingStrategy = new HeuristicScoreRatingStrategy();
					break;
				}
			}

			// Derive Solutions
			for (Solution solution : session.getKnowledgeBase().getManager().getSolutions()) {
				Rating rating = ratingStrategy.getRatingFor(solution, session);
				if (rating != null && rating.isProblemSolvingRelevant()) {
					RatedSolution ratedSolution = new RatedSolution(solution, rating);
					rtc.addDerived(ratedSolution);
				}
			}

			// Mark this RatedTestCase as successfully derived
			rtc.setDerivedSolutionsAreUpToDate(true);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HHmm");
			rtc.setTestingDate(df.format(new Date()));
		}
	}

	/**
	 * The specified findings are searched for MC questions and combined, when
	 * one MC question has more than one finding in the given list. The merged
	 * findings are returned; all other findings are returned as given.
	 * 
	 * @created 22.02.2011
	 * @param findings the specified findings
	 * @return the specified findings, but with MC questions merged
	 */
	private Collection<Finding> preprocessFindings(List<Finding> findings) {
		Collection<Finding> mergedFindings = new ArrayList<Finding>(findings.size());
		Collection<QuestionMC> mcquestions = getMCQuestionsIn(findings);
		// combine the mc findings and add the combined to merged
		for (QuestionMC questionMC : mcquestions) {
			Finding mf = mergeFinding(questionMC, findings);
			if (mf != null) mergedFindings.add(mf);
		}
		// add the remaining findings
		for (Finding finding : findings) {
			if (!mcquestions.contains(finding.getQuestion())) {
				mergedFindings.add(finding);
			}
		}
		return mergedFindings;
	}

	/**
	 * Merge all findings into one, that contain the specified question.
	 */
	private Finding mergeFinding(QuestionMC questionMC, List<Finding> findings) {
		Collection<ChoiceID> choiceIDs = new HashSet<ChoiceID>();

		for (Finding finding : findings) {
			if (questionMC.equals(finding.getQuestion())) {
				Value v = finding.getValue();
				if (v instanceof ChoiceValue) {
					choiceIDs.add(((ChoiceValue) v).getChoiceID());
				}
				else if (v instanceof MultipleChoiceValue) {
					choiceIDs.addAll(((MultipleChoiceValue) v).getChoiceIDs());
				}
				else {
					throw new IllegalArgumentException("Choice value expected!");
				}
			}
		}
		if (choiceIDs.isEmpty()) return null;
		else return new Finding(questionMC, new MultipleChoiceValue(choiceIDs));
	}

	private Collection<QuestionMC> getMCQuestionsIn(List<Finding> findings) {
		Set<QuestionMC> questions = new HashSet<QuestionMC>();
		for (Finding finding : findings) {
			if (finding.getQuestion() instanceof QuestionMC) {
				questions.add((QuestionMC) finding.getQuestion());
			}
		}
		return questions;
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
