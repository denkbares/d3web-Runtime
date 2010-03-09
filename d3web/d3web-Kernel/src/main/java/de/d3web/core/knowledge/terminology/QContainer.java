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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import de.d3web.core.inference.Rule;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.blackboard.CaseQASet;
import de.d3web.core.session.blackboard.CaseQContainer;
import de.d3web.core.session.blackboard.XPSCaseObject;
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

    /**
     * Creates a new QContainer by setting an empty LinkedList as the children
     * of this instance. <BR>
     * The following properties have to be setted by hand:
     * <LI> id (IBObject)
     * <LI> text (NamedObject)
     * <LI> knowledgeBase (NamedObject)
     * <LI> parents (QASet)
     * <LI> children
     * <LI> priority
     * 
     * @see QASet
     * @see Question
     * @see IDObject
     * @see NamedObject
     */
    public QContainer() {
	super();
	setChildren(new LinkedList());
    }

    public QContainer(String id) {
	super(id);
	setChildren(new LinkedList());
    }

    /**
     * adds a contra reason to the case object of this QContainer
     */
    public void addContraReason(Reason source, XPSCase theCase) {
	((CaseQASet) theCase.getCaseObject(this)).addContraReason(source);
	if ((theCase.getUsedPSMethods().contains(PSMethodParentQASet
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
    private void delegateContraReason(XPSCase theCase) {
	Iterator childrenIter = this.getChildren().iterator();
	while (childrenIter.hasNext()) {
	    QASet qaSet = (QASet) childrenIter.next();
	    if (qaSet instanceof QContainer) {
		((QContainer) qaSet).addContraReason(new QASet.Reason(
			PSMethodParentQASet.class), theCase);
	    }
	}
    }

    /**
     * @see QASet
     */
    public void activate(XPSCase theCase, Rule rule, Class psm) {
	super.activate(theCase, rule, psm);
	notifyListeners(theCase, this);
    }

    /**
     * @see QASet
     */
    public void deactivate(XPSCase theCase, Rule rule, Class psm) {
	super.deactivate(theCase, rule, psm);
	notifyListeners(theCase, this);
    }

    /**
     * addProReason method comment.
     */
    public void addProReason(Reason source, XPSCase theCase) {
	((CaseQASet) theCase.getCaseObject(this)).addProReason(source);
	if ((theCase.getUsedPSMethods().contains(PSMethodParentQASet
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
    private void delegateProReason(XPSCase theCase) {
	Iterator childrenIter = this.getChildren().iterator();
	while (childrenIter.hasNext()) {
	    QASet qaSet = (QASet) childrenIter.next();
	    if (qaSet instanceof QContainer) {
		((QContainer) qaSet).addProReason(new QASet.Reason(
			PSMethodParentQASet.class), theCase);
	    }
	}
    }

    /**
     * Compares the priority with that of another QContainer. <table>
     * <tr>
     * <td>Returns</td>
     * <td align=right>1</td>
     * <td>, if <code>this</code> has higher priority, </td>
     * </tr>
     * <tr>
     * <td> </td>
     * <td align=right>0</td>
     * <td>, if <code>this</code> and <code>anotherQContainer</code> have
     * the same (or none) priority</td>
     * <tr>
     * <td> </td>
     * <td align=right>- 1</td>
     * <td>, if <code>this</code> has lower priority</td>
     * </table>
     * 
     * @param QContainer
     *                anotherQContainer
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

    public XPSCaseObject createCaseObject(XPSCase session) {
	return new CaseQContainer(this);
    }

    /**
     * Expands a single QContainer to a list of children, if it is contained in
     * this list. The expansion objects will be at the same place as the
     * original object. returns true iff expansion changed the list!
     */
    /*
     * This is an old version: Expansion of the first element takes place, until
     * first element of expansion list is a Question <br/> <b>Example:</b><br/>
     * List: (Q1, Q2, Q3), Sons of Q2: (Q4, Q5, Q6), Sons of Q4: (Q7 Q8)<br/>
     * Result of expanding Q2: (Q1 Q7 Q8 Q5 Q6 Q3)
     */
    public boolean expand(List onList, XPSCase theCase) {
	// Fehlerfall abfangen: falls this nicht auf aufListe steht!!!

	if (!onList.contains(this)) {
	    return false;
	}
	int index = onList.indexOf(this);
	onList.remove(this);

	// Expand a single level only

	Vector expandVec = new Vector();
	Iterator iter = getChildren().iterator();
	while (iter.hasNext()) {
	    QASet fg = (QASet) iter.next();
	    if (!fg.isDone(theCase)) {
		expandVec.add(fg);
	    }
	}

	/*
	 * if (expandVec.size() > 0) { // the expandVec might be empty -> thus
	 * having no first element! QASet first = null; first = (QASet)
	 * expandVec.firstElement();
	 *  // vorsicht bei frisch expandierten sachen, die schon auf der Liste
	 * stehen // (was vor oder hinter dor!!! }
	 */
	// Abbruch: expand von Question bricht die Rekursion ab!
	onList.addAll(index, expandVec);
	return true;
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
    public boolean hasValue(XPSCase theCase) {
	return false;
    }

    public boolean isDone(XPSCase theCase) {

	// Falls auch nur ein einziges Children nicht abgearbeitet ist, ist auch
	// die ganze FK nicht abgearbeitet.
	Iterator iter = getChildren().iterator();
	while (iter.hasNext()) {
	    QASet qaset = (QASet) iter.next();
	    if (!qaset.isDone(theCase)) {
		theCase.trace("isDone von " + getId() + " liefert false!");
		return false;
	    }
	}
	theCase.trace("isDone von " + getId() + " liefert true!");
	return true;
    }

    public boolean isDone(XPSCase theCase, boolean respectValidFollowQuestions) {

	// Falls auch nur ein einziges (valides) Children nicht abgearbeitet
	// ist, ist auch die ganze FK nicht abgearbeitet.
	Iterator iter = getChildren().iterator();
	while (iter.hasNext()) {
	    if (!((QASet) iter.next()).isDone(theCase,
		    respectValidFollowQuestions)) {
		theCase.trace("isDone von " + getId() + " liefert false!");
		return false;
	    }
	}
	theCase.trace("isDone von " + getId() + " liefert true!");
	return true;
    }

    public void removeContraReason(Reason source, XPSCase theCase) {
	((CaseQASet) theCase.getCaseObject(this)).removeContraReason(source);
	if ((theCase.getUsedPSMethods().contains(PSMethodParentQASet
		.getInstance()))
		&& (getContraReasons(theCase).isEmpty())) {
	    delegateRemoveContraReason(theCase);
	}
    }

    public void removeProReason(Reason source, XPSCase theCase) {
	((CaseQASet) theCase.getCaseObject(this)).removeProReason(source);
	if ((theCase.getUsedPSMethods().contains(PSMethodParentQASet
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
    private void delegateRemoveContraReason(XPSCase theCase) {
	Iterator childrenIter = this.getChildren().iterator();
	while (childrenIter.hasNext()) {
	    QASet qaSet = (QASet) childrenIter.next();
	    if (qaSet instanceof QContainer) {
		boolean parentContraIndicated = false;
		Iterator parentIter = qaSet.getParents().iterator();
		while ((parentIter.hasNext()) && (!parentContraIndicated)) {
		    QASet parent = (QASet) parentIter.next();
		    if (!parent.getContraReasons(theCase).isEmpty()) {
			parentContraIndicated = true;
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
    private void delegateRemoveProReason(XPSCase theCase) {
	Iterator childrenIter = this.getChildren().iterator();
	while (childrenIter.hasNext()) {
	    QASet qaSet = (QASet) childrenIter.next();
	    if (qaSet instanceof QContainer) {
		boolean parentIndicated = false;
		Iterator parentIter = qaSet.getParents().iterator();
		while ((parentIter.hasNext()) && (!parentIndicated)) {
		    QASet parent = (QASet) parentIter.next();
		    if (!parent.getProReasons(theCase).isEmpty()) {
			parentIndicated = true;
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
     *                de.d3web.kernel.domainModel.KnowledgeBase
     */
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
     *                java.lang.Integer
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
     * values. The XPSCase has no meaning in this case.
     */
    public void setValue(XPSCase theCase, Object[] values) {
	Logger.getLogger(this.getClass().getName()).warning(
		"deedless QContainer.setValue was called");
    }
}
