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

package de.d3web.core.knowledge.terminology;
import java.util.List;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.Rule;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.D3WebCase;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.blackboard.CaseQASet;
import de.d3web.indication.ActionNextQASet;
import de.d3web.indication.inference.PSMethodUserSelected;
/**
 * This is a container to store Questions or Sets of Questions.
 * <BR>Composite Design Pattern:
 * <LI> Component : QASet
 * <LI> QContainer : Composite
 * <LI> Question : (abstract) Leaf
 * <BR>
 * The QASets are activated (deactivated) by the methods activate (deactivate).
 * @author joba, Christian Betz, norman
 * @see QContainer
 * @see Question
 */
public abstract class QASet extends NamedObject implements InterviewObject {
	
	private static final long serialVersionUID = -6129285010227602284L;

	public static class Reason {
		private Rule rule;
		private Class<? extends PSMethod> psm;
		public Reason(Rule myRule) {
			super();
			rule = myRule;
			psm = myRule.getProblemsolverContext();
		}
		public Reason(Class<? extends PSMethod> problemSolverContext) {
			super();
			rule = null;
			psm = problemSolverContext;
		}
		public Reason(Rule myRule, Class<? extends PSMethod> problemSolverContext) {
			super();
			rule = myRule;
			psm = problemSolverContext;
		}

		public boolean equals(Object o) {
			if (!(o instanceof Reason)) {
				return false;
			}
			return (((Reason) o).psm == psm) && (((Reason) o).rule == rule);
		}
		public Rule getRule() {
			return rule;
		}
		public Class<? extends PSMethod> getProblemSolverContext() {
			return psm;
		}
		
		public String toString() {
			return "reason " + psm + "	| " + rule;
		}
	}

	public QASet(String id) {
	    super(id);
	}

	/**
	  * Activates a question. That means it will be added to the list of questions to ask
	  * if it was not asked yet
	  * @param rule rule that has activated the question
	  * @param theCase current case
	  */
	public void activate(XPSCase theCase, Rule rule, Class<? extends PSMethod> psm) {
		CaseQASet caseQA =
			((de.d3web.core.session.blackboard.CaseQASet) theCase
				.getCaseObject(this));
		Reason source = getReason(rule, psm);
		Boolean external = (Boolean) getProperties().getProperty(Property.EXTERNAL);
		if(external != null && external) {
			((D3WebCase) theCase).addQASet(this, rule, psm);
		}
		if (!isDone(theCase)) {
			theCase.trace("in activate./!isDone(theCase)");
			if (caseQA.getContraReasons().contains(source)) {
				removeContraReason(source, theCase);
				theCase.trace(
					"Question "
						+ getId()
						+ "wurde eine contra reason entzogen.");
			} else {
				addProReason(source, theCase);
				theCase.trace(
					"Question "
						+ getId()
						+ " wurde eine pro reason hinzugefügt:"
						+ source);
				if (!caseQA.hasContraReason()) {
					theCase.trace("... und aktiviert");
					((D3WebCase) theCase).addQASet(this, rule, psm);
//					if (PSMethodUserSelected
//						.class
//						.equals(source.getProblemSolverContext())) {
//						//theCase.getQASetManager().addUserIndicationQASet(this);
//						theCase.getQASetManager().propagate(
//							this,
//							null,
//							theCase.getPSMethodInstance(
//								PSMethodUserSelected.class));
//					} else if (
//						!theCase.getQASetManager().getQASetQueue().contains(
//							this)) {
//						((D3WebCase) theCase).addQASet(this, rule, psm);
//						theCase.trace(
//							"... und auf die 'zuFragen'-Liste gestellt!");
//					}
				}
			}
		} else if (
			(source.getRule() != null)
				&& (source.getRule().getAction() instanceof ActionNextQASet)) {
			if (!caseQA.getProReasons().contains(source))
				caseQA.addProReason(source);
		}
	}

	public abstract void addContraReason(Reason source, XPSCase theCase);

	public abstract void addProReason(Reason source, XPSCase theCase);

	/**
	   * removes "source" from the list of pro reasons, if the question has been activated from a pro reason
	   * Otherwise "source" will be added to contra reason list
	   */
	public void deactivate(XPSCase theCase, Rule rule, Class<? extends PSMethod> psm) {
		CaseQASet caseQA =
			((de.d3web.core.session.blackboard.CaseQASet) theCase
				.getCaseObject(this));
		Reason source = getReason(rule, psm);
		if (caseQA.getProReasons().contains(source)) {
			removeProReason(source, theCase);
			theCase.trace(
				"Question " + getId() + " wurde eine pro reason entzogen.");
		} else {
			addContraReason(source, theCase);
			theCase.trace(
				"Question "
					+ getId()
					+ " wurde eine contra reason hinzugefügt.");
		}

		if (!caseQA.hasProReason()) {
			theCase.trace("Question " + getId() + " wird deaktiviert");
			//((D3WebCase)theCase).removeQuestion(this);
		} else {
			theCase.trace(
				"Question "
					+ getId()
					+ " sollte deaktiviert werden, hat aber noch andere Begründungen.");
		}

	}

	public List<Reason> getContraReasons(XPSCase theCase) {
		return ((CaseQASet) theCase.getCaseObject(this)).getContraReasons();
	}

	public List<Reason> getProReasons(XPSCase theCase) {
		return ((CaseQASet) theCase.getCaseObject(this)).getProReasons();
	}

	private Reason getReason(Rule rule, Class<? extends PSMethod> psm) {
		return new Reason(rule, psm);
	}

	public abstract boolean hasValue(XPSCase theCase);

	public abstract boolean isDone(XPSCase theCase);

	public abstract boolean isDone(XPSCase theCase, boolean respectFollowQuestions);


	/**
	 * A QASet can be valid only, if it hasn't any contra-reasons or if it is user-selected.
	 * It is valid, if it has proreasons or, in case of a question, if it has a 
	 * QContainer-parent, which is valid.
	 */
	public boolean isValid(XPSCase theCase) {
		CaseQASet caseQASet = (CaseQASet) theCase.getCaseObject(this);
		List<Reason> pros = getProReasons(theCase);
		if (!caseQASet.hasContraReason()
            || pros.contains(new QASet.Reason(null, PSMethodUserSelected.class))) {
			if (!pros.isEmpty())
				return true;
            
			if (this instanceof Question)
				for (TerminologyObject parent : getParents())
					if (parent instanceof QContainer) {
						QContainer qcon = (QContainer) parent;
						if (qcon.isValid(theCase)) {
							return true;
						}
					}
		}
		return false;
	}

	public abstract void removeContraReason(Reason source, XPSCase theCase);

	public abstract void removeProReason(Reason source, XPSCase theCase);

}