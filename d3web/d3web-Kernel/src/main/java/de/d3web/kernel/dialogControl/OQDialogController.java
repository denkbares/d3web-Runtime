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

package de.d3web.kernel.dialogControl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.dialogControl.exceptions.InvalidNextQASetRequestException;
import de.d3web.kernel.dialogControl.exceptions.InvalidPreviousQASetRequestException;
import de.d3web.kernel.dialogControl.exceptions.InvalidQASetRequestException;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.RuleAction;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.psMethods.PSMethod;
import de.d3web.kernel.psMethods.PSMethodInit;
import de.d3web.kernel.psMethods.userSelected.PSMethodUserSelected;

/**
 * one-question-controller class for the dialog; based on MQDialogController
 * asks one question at a time
 * @see MQDialogController
 * @author Georg Buscher
 * @author Norman Brümmer
 */
public class OQDialogController implements DialogController {
	
	private List history = null;
	private int historyCursor = -1;
	
	private MQDialogController mqdc;
	private XPSCase theCase;


		public OQDialogController(XPSCase theCase) {
		mqdc = new MQDialogController(theCase);
		history = new LinkedList();
		this.theCase = theCase;
		theCase.setQASetManager(this);
		moveToNewestQASet();
	}
	
	public MQDialogController getMQDialogController() {
		return mqdc;
	}
	
	public QASet getCurrentQASet() throws InvalidQASetRequestException {
		if (historyCursor < 0) {
			throw new InvalidPreviousQASetRequestException();
		} else if (historyCursor >= history.size()) {
			throw new InvalidNextQASetRequestException();
		} else {
			return (QASet) history.get(historyCursor);
		}
	}
	
	/**
	 * @return the internal history. 
	 */
	public List getHistory() {
		return history;
	}
	
	/**
	 * hasPreviousQASet method comment.
	 */
	public boolean hasPreviousQASet() {
		int historyC = historyCursor;
		boolean result = findPreviousUnblockedOnHistory() != null;
		historyCursor = historyC;
		return result;
	}
	
	/**
	 * @return true if there is a next question on history or an unanswered question
	 */
	public boolean hasNextQASet() {
		int historyC = historyCursor;
		boolean result = (findNextUnblockedOnHistory() != null) || (hasNewestQASet());
		historyCursor = historyC;
		return result;
	}
	
	/**
	 * checks if there is any unanswered question (looking forwards)
	 */
	public boolean hasNewestQASet() {
		return mqdc.hasNextQASet();
	}
	
	
	public boolean isValidForDC(QASet qaSet) {
		return mqdc.isValidForDC(qaSet);
	}
	
	public QASet moveToNextRemainingQASet() {
		return mqdc.moveToNextRemainingQASet();
	}
	
	public List getProcessedContainers() {
		return mqdc.getProcessedContainers();
	}
	
	public List getQASetQueue() {
		return mqdc.getQASetQueue();
	}

	/**
	 * moves to the next unanswered QASet
	 * @return the QASet the controller has moved to
	 */
	public QASet moveToNewestQASet() {
		QContainer qc = (QContainer) mqdc.moveToNewestQASet();
		if (qc != null) {
			Iterator questionIter =
				mqdc.getAllQuestionsToRender(qc)
					.iterator();
			while (questionIter.hasNext()) {
				Question q = (Question) ((List) questionIter.next()).get(0);
				if (! q.isDone(theCase)) {
					if ((history.isEmpty()) || (!history.get(history.size()-1).equals(q))) {
						history.add(q);
					}
					historyCursor = history.size()-1;
					return q;
				}
			}
		}
		historyCursor = history.size();
		return null;
	}
	
	/**	
	 * moves to the next QASet on history.<br>
	 * ATTENTION: moveToNextQASet and moveToPreviousQASet may not work correctly, if
	 * follow-questions are indicated.
	 * @return the QASet the controller moved to
	 */
	public QASet moveToNextQASet() {
		QASet next = findNextUnblockedOnHistory();
		if (next != null) {
			return next;
		} else {
			return moveToNewestQASet();
		}
	}
	
	/**	
	 * moves to the previous QASet on history.<br>
	 * ATTENTION: moveToNextQASet and moveToPreviousQASet may not work correctly, if
	 * follow-questions are indicated.
	 * @return the QASet the controller moved to
	 */
	public QASet moveToPreviousQASet() {
		QASet q = findPreviousUnblockedOnHistory();
		if (q != null) {
			mqdc.addUserIndicationQASet(q);
			mqdc.moveToNewestQASet();
		}
		return q;
	}
	
	public QASet moveToQASet(QASet searchQ) {
		return mqdc.moveToQASet(searchQ);
	}
	
	public QASet moveToQuestion(QASet searchQ) {
		return mqdc.moveToQASet(searchQ);
	}
	
	
	private synchronized Question findNextUnblockedOnHistory() {
		if (history.size() > 0) {
			while (historyCursor < history.size() - 1) {
				historyCursor++;
				Question q = checkUnblocked();
				if (q != null) {
					return q;
				}
			}
			historyCursor = history.size();
		}
		return null;
	}
	
	private Question findPreviousUnblockedOnHistory() {
		if (history.size() > 0) {
			while (historyCursor > 0) {
				historyCursor--;
				Question q = checkUnblocked();
				if (q != null) {
					return q;
				}
			}
			historyCursor = -1;
		}
		return null;
	}
	
	/**
	 * @return a question, if it is valid.
	 */
	private Question checkUnblocked() {
		Question tempQ = (Question) history.get(historyCursor);

		List proList = getProReasonsOfParent(theCase, tempQ);

		theCase.trace("pro reasons: " + tempQ.getProReasons(theCase));
		theCase.trace("determined recursively:" + proList);

		if (tempQ.isValid(theCase)) {
			// question valid but answered (of course).
			// check if there are proper pro-reasons:
			//  - ActionNextQASet
			//  - InitQASets
			//  - User-Activated

			Iterator proIter = proList.iterator();
			while (proIter.hasNext()) {
				QASet.Reason pro = (QASet.Reason) proIter.next();
				if (isQASetRule(pro)
					|| PSMethodInit.class.equals(pro.getProblemSolverContext())
					|| PSMethodUserSelected.class.equals(pro.getProblemSolverContext())) {
					return tempQ;
				}
			}
		}
		return null;
	}
	
	private List getProReasonsOfParent(XPSCase theCase, Question q) {
		List proReasons = new LinkedList();
		Iterator iter = q.getParents().iterator();
		while (iter.hasNext()) {
			QASet qaSet = (QASet) iter.next();
			proReasons.addAll(qaSet.getProReasons(theCase));
		}
		return proReasons;
	}
	
	/**
	 * @return true, if the specified rule contains the action ActionNextQASet
	 */
	private boolean isQASetRule(QASet.Reason reason) {
		if (reason.getRule() != null) {
			RuleAction action = reason.getRule().getAction();
			return (action instanceof de.d3web.kernel.psMethods.nextQASet.ActionNextQASet);
		} else
			return false;
	}
	
	public void propagate(NamedObject no, RuleComplex rule, PSMethod psm) {
		mqdc.propagate(no, rule, psm);
	}

	public MQDialogController getMQDialogcontroller() {
		return mqdc;
	}

	public OQDialogController getOQDialogcontroller() {
		return this;
	}

}
