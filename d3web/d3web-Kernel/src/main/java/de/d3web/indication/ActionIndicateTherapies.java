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

package de.d3web.indication;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.PSAction;
import de.d3web.core.knowledge.terminology.Diagnosis;
import de.d3web.core.session.XPSCase;
import de.d3web.indication.inference.PSMethodTherapyIndication;

/**
 * Action to add scores to diagnoses (heuristic problemsolver) Creation date:
 * (19.06.2001 17:18:31)
 * 
 * @author Joachim Baumeister
 */
public class ActionIndicateTherapies extends PSAction {
	
	private static final long serialVersionUID = -1883766652217433924L;
	private List<Diagnosis> therapies;

	public String toString() {
		return "<RuleAction type=\"IndicateTherapies\">\n"
			+ "  ["
			+ getTherapies()
			+ ": "
			+ "]"
			+ "\n</RuleAction>";
	}

	/**
	 * @return all objects participating on the action. <BR>
	 */
	public List<Diagnosis> getTerminalObjects() {
		// This relies on a plain list of therapies.
		return getTherapies();
	}

	/**
	 * Creates a new ActionHeuristicPS for the given corresponding Rule
	 * Creation date: (19.06.2001 17:41:53)
	 */
	public ActionIndicateTherapies() {
		super();
	}

	/**
	 * Executes the included action.
	 */
	@Override
	public void doIt(XPSCase theCase, Rule rule) {
		//
		//		DiagnosisScore resultDS =
		//			getDiagnosis().getScore(theCase, getProblemsolverContext()).add(
		//				getScore());
		//
		//		((D3WebCase) theCase).setValue(
		//			getDiagnosis(),
		//			new Object[] { resultDS },
		//			getProblemsolverContext());
		//
		//[FIXME] IMPORTANT: This action does nothing at the moment.
		//
	}

	/**
	 * @return PSMethosHeuristic.class
	 */
	public Class<? extends PSMethod> getProblemsolverContext() {
		return PSMethodTherapyIndication.class;
	}

	/**
	 * Tries to undo the included action.
	 */
	@Override
	public void undo(XPSCase theCase, Rule rule) {
		//		DiagnosisScore resultDS = null;
		//		if (getScore().equals(Score.N7)) {
		//			Iterator iter =
		//				((Collection) getDiagnosis()
		//					.getKnowledge(PSMethodTherapyIndication.class, MethodKind.BACKWARD))
		//					.iterator();
		//			resultDS =
		//				new DiagnosisScore(getDiagnosis().getAprioriProbability());
		//			while (iter.hasNext()) {
		//				RuleComplex rule = (RuleComplex) iter.next();
		//				if (rule.isUsed(theCase)) {
		//					resultDS =
		//						resultDS.add(
		//							((ActionIndicateTherapies) rule.getAction()).getScore());
		//				}
		//			}
		//		} else {
		//			resultDS =
		//				getDiagnosis().getScore(
		//					theCase,
		//					getProblemsolverContext()).subtract(
		//					getScore());
		//		}
		//		((D3WebCase) theCase).setValue(
		//			getDiagnosis(),
		//			new Object[] { resultDS },
		//			getProblemsolverContext());

		//
		//[FIXME] IMPORTANT: This action does nothing at the moment.
		//

	}
	/**
	 * @return Returns the therapies.
	 */
	public List<Diagnosis> getTherapies() {
		return therapies;
	}

	/**
	 * @param therapies
	 *            The therapies to set.
	 */
	public void setTherapies(List<Diagnosis> newTherapies) {
		therapies = newTherapies;
	}
	
	public PSAction copy() {
		ActionIndicateTherapies a = new ActionIndicateTherapies();
		a.setTherapies(new LinkedList<Diagnosis>(getTherapies()));
		return a;
	}
	
	public int hashCode() {
		if (getTherapies() != null) 
			return (getTherapies().hashCode());
		else return 0;
	}
	
	public boolean equals(Object o) {
		if (o==this) 
			return true;
		if (o instanceof ActionIndicateTherapies) {
			ActionIndicateTherapies a = (ActionIndicateTherapies)o;
			return isSame(a.getTherapies(), getTherapies());
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