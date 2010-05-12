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

package de.d3web.core.knowledge.terminology;

import java.util.LinkedList;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.Rule;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.CaseQASet;
import de.d3web.core.session.blackboard.CaseQContainer;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.indication.inference.PSMethodParentQASet;
import de.d3web.indication.inference.PSMethodUserSelected;

/**
 * This class stores {@link Question} instances or (recursively) other
 * {@link QContainer} instances. Typically, this class is used to represent a
 * questionnaire that is jointly presented in a problem-solving session.
 * 
 * @author joba, norman
 * @see QASet
 */
public class QContainer extends QASet {

	private Integer priority;

	/**
	 * Creates a new instance with the specified unique identifier.
	 * 
	 * @param id the unique identifier
	 */
	public QContainer(String id) {
		super(id);
		setChildren(new LinkedList<NamedObject>());
	}

	/**
	 * Adds a new contra-reason to this instance. Contra-reasons are used during
	 * the dialog to omit some questionnaires.
	 * 
	 * @param source the source of the reason
	 * @param session the session for which the contra-reason is valid
	 */
	@Override
	public void addContraReason(Reason source, Session session) {
		((CaseQASet) session.getCaseObject(this)).addContraReason(source);
		if ((session.getPSMethods().contains(PSMethodParentQASet
				.getInstance()))
				&& (!PSMethodUserSelected.class.equals(source
				.getProblemSolverContext()))) {
			delegateContraReason(session);
		}
	}

	/**
	 * Propagates contra-reasons to all children-QContainers.
	 */
	private void delegateContraReason(Session theCase) {
		for (TerminologyObject qaSet : getChildren()) {
			if (qaSet instanceof QContainer) {
				((QContainer) qaSet).addContraReason(new QASet.Reason(
						PSMethodParentQASet.class), theCase);
			}
		}
	}

	@Override
	public void activate(Session theCase, Rule rule, Class<? extends PSMethod> psm) {
		super.activate(theCase, rule, psm);
	}

	@Override
	public void deactivate(Session theCase, Rule rule, Class<? extends PSMethod> psm) {
		super.deactivate(theCase, rule, psm);
	}

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
	 * Propagates the pro-reasons to all children QContainers.
	 */
	private void delegateProReason(Session theCase) {
		for (TerminologyObject qaSet : getChildren()) {
			if (qaSet instanceof QContainer) {
				((QContainer) qaSet).addProReason(new QASet.Reason(
						PSMethodParentQASet.class), theCase);
			}
		}
	}

	/**
	 * Compares the priority with the priority of another {@link QContainer}
	 * instance.
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
	 * @param QContainer anotherQContainer
	 * @return int the result of the comparison
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

	/**
	 * Creates and returns a new fylweight object of this instance, that is
	 * corresponding to the specified {@link Session} instance.
	 * 
	 * @param session the corresponding session instance
	 * @return a created fylweight representation of this instance
	 */
	public SessionObject createCaseObject(Session session) {
		return new CaseQContainer(this);
	}

	/**
	 * <b>Deprecated:</b> not used anymore. <br>
	 * Returns the {@link QContainer}s priority. This is a non-negative
	 * {@link Integer} value specifying the order of the QContainers to be
	 * brought-up by a dialog component. Thus, priority is not an absolute
	 * number, but relative to all the other QContainers priorities.
	 * 
	 * @return java.lang.Integer
	 */
	@Deprecated
	public Integer getPriority() {
		return priority;
	}

	@Override
	public boolean isDone(Session session) {
		// recursively check, whether all children of this instance are "done"
		for (TerminologyObject to : getChildren()) {
			QASet qaset = (QASet) to;
			if (!qaset.isDone(session)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isDone(Session session, boolean respectValidFollowQuestions) {
		// recursively check, whether all children of this instance are "done"
		for (TerminologyObject to : getChildren()) {
			if (!((QASet) to).isDone(session,
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
	 * Removes the PSMethodParentQASet contra-reason of all
	 * children-qcontainers, which do not have any parent which is
	 * contra-indicated.
	 */
	private void delegateRemoveContraReason(Session theCase) {
		for (TerminologyObject to : getChildren()) {
			if (to instanceof QContainer) {
				QContainer qaSet = (QContainer) to;
				boolean parentContraIndicated = false;
				for (TerminologyObject parentTo : qaSet.getParents()) {
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
	 * Removes the PSMethodParentQASet pro-reason of all children-qcontainers,
	 * which do not have any parent which is indicated.
	 */
	private void delegateRemoveProReason(Session theCase) {
		for (TerminologyObject to : getChildren()) {
			QASet qaSet = (QASet) to;
			if (qaSet instanceof QContainer) {
				boolean parentIndicated = false;
				for (TerminologyObject to2 : qaSet.getParents()) {
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
	 * Defines the relation to the specified {@link KnowledgeBase} instance, to
	 * which this objects belongs to.
	 * 
	 * @param knowledgeBase the specified {@link KnowledgeBase} instance.
	 */
	@Override
	public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
		super.setKnowledgeBase(knowledgeBase);
		// maybe somebody should remove this object from the old
		// knowledge base if available
		getKnowledgeBase().add(this);
	}

	/**
	 * <b>Deprecated:</b> not used anymore. <br>
	 * Sets the priority of this instance to the specified non-negative
	 * {@link Integer} value. Specifying the order of the QContainers (in
	 * special dialog situations) with this property is optional. Any
	 * {@link QContainer} without a defined priority value receives a positive
	 * infinite default value and will thus be asked latest by those dialog
	 * components respecting the priority value.
	 * 
	 * @param priority the priority value of this instance
	 */
	@Deprecated
	public void setPriority(Integer priority) {
		/*
		 * if (priority.intValue() < 0) { throw new
		 * ValueNotAcceptedException("Negative Priority"); }
		 */
		this.priority = priority;
	}
}
