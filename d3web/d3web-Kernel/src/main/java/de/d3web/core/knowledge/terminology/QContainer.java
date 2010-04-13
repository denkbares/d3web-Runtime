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

import java.util.LinkedList;
import java.util.logging.Logger;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.Rule;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Value;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.CaseQASet;
import de.d3web.core.session.blackboard.CaseQContainer;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.indication.inference.PSMethodParentQASet;
import de.d3web.indication.inference.PSMethodUserSelected;

/**
 * Storage for QASets (Questions or QContainers again) Part of the Composite
 * design pattern (see QASet for further description)
 * 
 * @author joba, Christian Betz, norman
 * @see QASet
 */
public class QContainer extends QASet {

	private Integer priority;

	public QContainer(String id) {
		super(id);
		setChildren(new LinkedList<NamedObject>());
	}

	/**
	 * adds a contra reason to the case object of this QContainer
	 */
	@Override
	public void addContraReason(Reason source, Session theCase) {
		((CaseQASet) theCase.getCaseObject(this)).addContraReason(source);
		if ((theCase.getPSMethods().contains(PSMethodParentQASet
				.getInstance()))
				&& (!PSMethodUserSelected.class.equals(source
				.getProblemSolverContext()))) {
			delegateContraReason(theCase);
		}
	}

	/**
	 * Adds contrareasons to all children-QContainers.
	 * 
	 * @param theCase
	 */
	private void delegateContraReason(Session theCase) {
		for (TerminologyObject qaSet: getChildren()) {
			if (qaSet instanceof QContainer) {
				((QContainer) qaSet).addContraReason(new QASet.Reason(
						PSMethodParentQASet.class), theCase);
			}
		}
	}

	/**
	 * @see QASet
	 */
	@Override
	public void activate(Session theCase, Rule rule, Class<? extends PSMethod> psm) {
		super.activate(theCase, rule, psm);
	}

	/**
	 * @see QASet
	 */
	@Override
	public void deactivate(Session theCase, Rule rule, Class<? extends PSMethod> psm) {
		super.deactivate(theCase, rule, psm);
	}

	/**
	 * addProReason method comment.
	 */
	@Override
	public void addProReason(Reason source, Session theCase) {
		((CaseQASet) theCase.getCaseObject(this)).addProReason(source);
		if ((theCase.getPSMethods().contains(PSMethodParentQASet
				.getInstance()))
				&& (!PSMethodUserSelected.class.equals(source
				.getProblemSolverContext()))) {
			delegateProReason(theCase);
		}
	}

	/**
	 * Adds proreasons to all children-QContainers.
	 * 
	 * @param theCase
	 */
	private void delegateProReason(Session theCase) {
		for (TerminologyObject qaSet: getChildren()) {
			if (qaSet instanceof QContainer) {
				((QContainer) qaSet).addProReason(new QASet.Reason(
						PSMethodParentQASet.class), theCase);
			}
		}
	}

	/**
	 * Compares the priority with that of another QContainer.
	 * <table>
	 * <tr>
	 * <td>Returns</td>
	 * <td align=right>1</td>
	 * <td>, if <code>this</code> has higher priority,</td>
	 * </tr>
	 * <tr>
	 * <td></td>
	 * <td align=right>0</td>
	 * <td>, if <code>this</code> and <code>anotherQContainer</code> have the
	 * same (or none) priority</td>
	 * <tr>
	 * <td></td>
	 * <td align=right>- 1</td>
	 * <td>, if <code>this</code> has lower priority</td>
	 * </table>
	 * 
	 * @param QContainer
	 *            anotherQContainer
	 * @return int
	 */
	public int comparePriority(QContainer anotherQContainer) {
		Integer acPriority = anotherQContainer.getPriority();
		if (acPriority == null) {
			return ((getPriority() == null) ? 0 : 1);
		}
		// acPriority != null
		if (getPriority() == null) {
			return -1;
		}
		// both priorities are non-null Integer objects
		return getPriority().compareTo(acPriority);
	}

	public SessionObject createCaseObject(Session session) {
		return new CaseQContainer(this);
	}

	/**
	 * Gets the QContainers priority. This is a non-negative Integer value
	 * specifying the order of the QContainers to be brought up by a dialogue
	 * component. Thus priority is not an absolute number but relative to all
	 * the other QContainers priorities.
	 * 
	 * @return java.lang.Integer
	 */
	public Integer getPriority() {
		return priority;
	}

	/**
	 * Containers don´t have value
	 * 
	 * @return false
	 */
	@Override
	public boolean hasValue(Session theCase) {
		return false;
	}

	@Override
	public boolean isDone(Session theCase) {
		// Falls auch nur ein einziges Children nicht abgearbeitet ist, ist auch
		// die ganze FK nicht abgearbeitet.
		for (TerminologyObject to: getChildren()) {
			QASet qaset = (QASet) to;
			if (!qaset.isDone(theCase)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isDone(Session theCase, boolean respectValidFollowQuestions) {

		// Falls auch nur ein einziges (valides) Children nicht abgearbeitet
		// ist, ist auch die ganze FK nicht abgearbeitet.
		for (TerminologyObject to: getChildren()) {
			if (!((QASet) to).isDone(theCase,
					respectValidFollowQuestions)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void removeContraReason(Reason source, Session theCase) {
		((CaseQASet) theCase.getCaseObject(this)).removeContraReason(source);
		if ((theCase.getPSMethods().contains(PSMethodParentQASet
				.getInstance()))
				&& (getContraReasons(theCase).isEmpty())) {
			delegateRemoveContraReason(theCase);
		}
	}

	@Override
	public void removeProReason(Reason source, Session theCase) {
		((CaseQASet) theCase.getCaseObject(this)).removeProReason(source);
		if ((theCase.getPSMethods().contains(PSMethodParentQASet
				.getInstance()))
				&& (getProReasons(theCase).isEmpty())) {
			delegateRemoveProReason(theCase);
		}
	}

	/**
	 * Removes the PSMethodParentQASet-contrareason of all children-qcontainers,
	 * which do not have any parent which is contraindicated
	 * 
	 * @param theCase
	 */
	private void delegateRemoveContraReason(Session theCase) {
		for (TerminologyObject to: getChildren()) {
			if (to instanceof QContainer) {
				QContainer qaSet = (QContainer) to;
				boolean parentContraIndicated = false;
				for (TerminologyObject parentTo: qaSet.getParents()) {
					if (!parentContraIndicated) {
						QASet parent = (QASet) parentTo;
						if (!parent.getContraReasons(theCase).isEmpty()) {
							parentContraIndicated = true;
						}
					}
				}
				if (!parentContraIndicated) {
					qaSet.removeContraReason(new QASet.Reason(
							PSMethodParentQASet.class), theCase);
				}
			}
		}
	}

	/**
	 * Removes the PSMethodParentQASet-proreason of all children-qcontainers,
	 * which do not have any parent which is indicated
	 * 
	 * @param theCase
	 */
	private void delegateRemoveProReason(Session theCase) {
		for (TerminologyObject to: getChildren()) {
			QASet qaSet = (QASet) to;
			if (qaSet instanceof QContainer) {
				boolean parentIndicated = false;
				for (TerminologyObject to2: qaSet.getParents()) {
					if (!parentIndicated) {
						QASet parent = (QASet) to2;
						if (!parent.getProReasons(theCase).isEmpty()) {
							parentIndicated = true;
						}
					}
				}
				if (!parentIndicated) {
					qaSet.removeProReason(new QASet.Reason(
							PSMethodParentQASet.class), theCase);
				}
			}
		}
	}

	/**
	 * Sets the knowledgebase, to which this objects belongs to and adds this
	 * object to the knowledge base (reverse link).
	 * 
	 * @param newKnowledgeBase
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 */
	@Override
	public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
		super.setKnowledgeBase(knowledgeBase);
		// maybe somebody should remove this object from the old
		// knowledge base if available
		getKnowledgeBase().add(this);
	}

	/**
	 * Sets the QContainers priority to the given non-negative int value.
	 * Specifying the order of the QContainers (in special dialogue situations)
	 * with this property is optional. Any QContainer without an assigned
	 * priority value gets a positive infinite default value and will thus be
	 * asked latest by those dialog components respecting the priority value.
	 * 
	 * @param newPriority
	 *            java.lang.Integer
	 */
	public void setPriority(Integer priority) {
		/*
		 * if (priority.intValue() < 0) { throw new
		 * ValueNotAcceptedException("Negative Priority"); }
		 */
		this.priority = priority;
	}

	/**
	 * Sets the list of QASets contained in the QContainer to the specified
	 * values. The Session has no meaning in this case.
	 */
	@Override
	public void setValue(Session theCase, Value value) {
		Logger.getLogger(this.getClass().getName()).warning(
				"deedless QContainer.setValue was called");
	}
}
