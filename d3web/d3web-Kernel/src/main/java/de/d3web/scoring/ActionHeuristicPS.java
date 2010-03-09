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

package de.d3web.scoring;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleAction;
import de.d3web.core.knowledge.terminology.Diagnosis;
import de.d3web.core.session.D3WebCase;
import de.d3web.core.session.XPSCase;
import de.d3web.scoring.inference.PSMethodHeuristic;

/**
 * Action to add scores to diagnoses 
 * (heuristic problemsolver)
 * Creation date: (19.06.2001 17:18:31)
 * @author Joachim Baumeister
 */
public class ActionHeuristicPS extends RuleAction {
	private Diagnosis diagnosis;
	private Score score;

	public String toString() {
		String diagnosisId = null;
		if(getDiagnosis() != null) {
			diagnosisId = getDiagnosis().getId();
		}
		return "<RuleAction type=\"Heuristic\">\n"
			+ "  ["
			+ diagnosisId
			+ ": "
			+ getScore()
			+ "]"
			+ "\n</RuleAction>";
	}

	/**
	 * @return all objects participating on the action.<BR>
	 */
	public List getTerminalObjects() {
		List terminals = new ArrayList(1);
		if (getDiagnosis() != null) {
			terminals.add(getDiagnosis());
		}
		return terminals;
	}

	/**
	 * Creates a new ActionHeuristicPS for the given corresponding Rule
	 * Creation date: (19.06.2001 17:41:53)
	 */
	public ActionHeuristicPS() {
		super();
	}

	/**
	 * Executes the included action.
	 */
	public void doIt(XPSCase theCase) {

		DiagnosisScore resultDS =
			getDiagnosis().getScore(theCase, getProblemsolverContext()).add(
				getScore());

		((D3WebCase) theCase).setValue(
			getDiagnosis(),
			new Object[] { resultDS },
			getProblemsolverContext());
	}

	/**
	 * @return the Diagnosis this Action can add scores to
	 */
	public Diagnosis getDiagnosis() {
		return diagnosis;
	}

	/**
	 * @return PSMethosHeuristic.class
	 */
	public Class getProblemsolverContext() {
		return PSMethodHeuristic.class;
	}

	/**
	 * @return the Score that can be added to the corresponding Diagnosis
	 */
	public Score getScore() {
		return score;
	}

	/**
	 * Inserts the corresponding rule as Knowledge to the given Diagnosis
	 */
	private void insertRuleIntoDiagnosis(Diagnosis diagnosis) {
		if (diagnosis != null) {
			diagnosis.addKnowledge(
				getProblemsolverContext(),
				getCorrespondingRule(),
				MethodKind.BACKWARD);
		}
	}

	/**
	 * Removes the corresponding rule from the given Diagnosis
	 */
	private void removeRuleFromOldDiagnosis(Diagnosis diagnosis) {
		if (diagnosis != null) {
			diagnosis.removeKnowledge(
				getProblemsolverContext(),
				getCorrespondingRule(),
				MethodKind.BACKWARD);
		}
	}

	/**
	 * sets the given Diagnosis and resets the corresponding rule as Knowledge slice 
	 */
	public void setDiagnosis(Diagnosis theDiagnosis) {
		removeRuleFromOldDiagnosis(diagnosis);
		diagnosis = theDiagnosis;
		insertRuleIntoDiagnosis(diagnosis);
	}

	/**
	  * Sets the score to be added to the specified diagnosis,
	  * if the rule can fire.
	  */
	public void setScore(Score score) {
		this.score = score;
	}

	/**
	 * Tries to undo the included action.
	 */
	public void undo(XPSCase theCase) {
		DiagnosisScore resultDS = null;
		if (getScore().equals(Score.N7)) {
			Iterator iter =
				((Collection) getDiagnosis()
					.getKnowledge(PSMethodHeuristic.class, MethodKind.BACKWARD))
					.iterator();
			resultDS =
				new DiagnosisScore(getDiagnosis().getAprioriProbability());
			while (iter.hasNext()) {
				Rule rule = (Rule) iter.next();
				if (rule.isUsed(theCase)) {
					resultDS =
						resultDS.add(
							((ActionHeuristicPS) rule.getAction()).getScore());
				}
			}
		} else {
			resultDS =
				getDiagnosis().getScore(
					theCase,
					getProblemsolverContext()).subtract(
					getScore());
		}
		((D3WebCase) theCase).setValue(
			getDiagnosis(),
			new Object[] { resultDS },
			getProblemsolverContext());
	}

	public RuleAction copy() {
		ActionHeuristicPS a = new ActionHeuristicPS();
		a.setRule(getCorrespondingRule());
		a.setDiagnosis(getDiagnosis());
		a.setScore(getScore());
		return a;
	}
	
	public int hashCode() {
		int hash = 0;
		if(getDiagnosis() != null) {
			hash += getDiagnosis().hashCode();
		}
		if(getScore() != null) {
			hash += getScore().hashCode();
		}
		return hash;
	}
	
	public boolean equals(Object o) {
		if (o==this) 
			return true;
		if (o instanceof ActionHeuristicPS) {
			ActionHeuristicPS a = (ActionHeuristicPS)o;
			return isSame(a.getDiagnosis(), getDiagnosis()) && isSame(a.getScore(), getScore());
		}
		else
			return false;
	}
	
	private boolean isSame(Object obj1, Object obj2) {
		if(obj1 == null && obj2 == null) return true;
		if(obj1 != null && obj2 != null) return obj1.equals(obj2);
		return false;
	}
}