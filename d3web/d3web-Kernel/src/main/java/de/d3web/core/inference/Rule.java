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

package de.d3web.core.inference;
import java.util.Iterator;
import java.util.List;

import de.d3web.abstraction.ActionQuestionSetter;
import de.d3web.core.inference.condition.AbstractCondition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.session.CaseObjectSource;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.blackboard.CaseRuleComplex;
import de.d3web.core.session.blackboard.XPSCaseObject;
import de.d3web.core.terminology.IDObject;
import de.d3web.core.terminology.NamedObject;
import de.d3web.scoring.inference.PSMethodHeuristic;
/**
 * Abstract super class for all rules. <BR>
 * It stores the condition, the check routine and if it has fired or not.
 * The action of a rule is specified by the extensions of RuleComplex.
 * Additionally it is possible to store an exception, when this rule must not fire. 
 * @author Michael Wolber, joba
 */
public class Rule
	extends IDObject
	implements KnowledgeSlice, CaseObjectSource {

	private static final long serialVersionUID = 1648330152712439470L;

	/**
	 * Flag indicates, if the rule is activated.
	 */
	private boolean active = true;
	
	/**
	 * contains a comment for this rule, extract later into e.g. properties
	 */
	private String comment;
	
	/**
	  * A condition which must be true, if rule should fire (obligatory).
	  */
	protected AbstractCondition condition;

	/**
	  * A condition that must be false or undefined, if 
	  * rule should fire (optional).
	 */
	protected AbstractCondition exception;

	/**
	  * A condition which contains CondDState(diagnosis, ESTABLISHED)
	  * to formulate a context of established diagnoses in which the 
	  * rule is able to fire. If specified diagnoses are not established,
	  * then rule must not fire.
	 */
	protected AbstractCondition diagnosisContext;

	private Class problemsolverContext;

	/**
	  * The specified action the rule activates, if <it>condition</it> is true
	  *	,<it>exception</it> is false/undefined and <it>diagnosisContext</it> is 
	  *	true/undefined.
	 */
	private RuleAction ruleAction;

	/**
	  * Creates a new rule. The following properties have to be 
	  * setted by hand:
	  * <LI> condition 
	  * <LI> exception (optional)
	  * @see IDObject
	  */
	public Rule(String id) {
		super(id);
	}
	
	/**
	  * Checks if the rule is able to fire in context of the values of 
	  * the specified case.
	  * In detail it evaluates the condition, which must be true and
	  * <OL>
	  * <LI>  if available - checks the exception not to be true.
	  * <LI>  if available - checks the diagnosis context to be true
	  * </OL>
	  */
	public boolean canFire(XPSCase theCase) throws UnknownAnswerException {
		try {
			/* if an exception is available and it is true, then do not fire! */
			if ((getException() != null)
				&& (getException().eval(theCase) == true)) {
				return false;
			}

		} catch (NoAnswerException ex) {
			/* The exception could not be testet --> just go on and treat it like there is no exception */
			// D3WebCase.trace("Exception contained unknown term ");
		} catch (UnknownAnswerException uex) {
			/* The exception could not be testet --> just go on and treat it like there is no exception */
		}

		try {
			/* if a diagnosis context is available and it is false, then do not fire! */
			if ((getContext() != null)
				&& (getContext().eval(theCase) == false)) {
				return false;
			}
		} catch (NoAnswerException e) {
			return false;
		}

		try {
			return getCondition().eval(theCase);
		} catch (NoAnswerException ex) {
			/* the condition could not be tested
			--> return false
			*/
			// D3WebCase.trace("Condition contained unknown term ");
			return false;
		}
	}

	/**
	  * Buawa: This method checks a lot:<BR>
	  * is this rule able to fire? (check condition)
	  * If it is able to fire the rule executes the action part.<BR>
	  *
	  * Different from rules which fire to diagnosis, this check-method does not need to check whether the
	  * rule already has fired - since the value is not accumulative.
	  * It is instead an error to do so - since the action of the rule might evaluate to another value when the
	  * source values change.
	  * 
	  * If it has fired but is now not able to fire (cause the
	  * conditions are now not fulfilled) the action's backtrack mechanism
	  * is executed.
	  * 
	  * If the rule has already fired, can fire AND the depending Action is a ActionQuestionSetter whose
	  * elementary values have changed (e.g. terminals in a formula) it will be undone and fired again,
	  * so that the e.g. depending formula will be recalculated.
	  */
	public void check(XPSCase theCase) {
		// should we execute the rule action ???
		boolean EXECUTE_ACTION = false;
		// should we undo the rule action ???
		boolean UNDO_ACTION = false;

		try {
			boolean hasFired = hasFired(theCase);
			boolean canFire = canFire(theCase);

			// ... do nothing, if not active
			if(!active) {
				/*
				if(hasFired) {
					undo(theCase);
				}*/
				return;
			}

			if (!hasFired && canFire)
				EXECUTE_ACTION = true;

			if (hasFired && !canFire)
				UNDO_ACTION = true;

			// if the action is a question setter action, changes in depending values (e.g. elements of a formula)
			// will be noticed and stored in the boolean "isQuestionSetterActionWithChangedValues"
			boolean isQuestionSetterActionWithChangedValues =
				getAction().hasChangedValue(theCase);
			// if this is a multipleFire-rule that has fired AND can fire again AND any depending value has
			// changed, its action will be undone and executed again.
			// This change fixes the "fire-undo-fire-bug" (when a question gets the same
			// value several times (see MQDialogController)) and some problems with the "cycle-check".
			if (hasFired
				&& canFire
				&& isQuestionSetterActionWithChangedValues) {
				UNDO_ACTION = true;
				EXECUTE_ACTION = true;
			}

		} catch (UnknownAnswerException ex) {
			if (hasFired(theCase))
				UNDO_ACTION = true;
		}

		if (UNDO_ACTION) {
			undo(theCase);
		}
		if (EXECUTE_ACTION) {
			doIt(theCase);
		}
	}

	@Override
	public XPSCaseObject createCaseObject(XPSCase session) {
		return new CaseRuleComplex(this);
	}

	/**
	  * Executes the action of the rule.
	  */
	public void doIt(XPSCase theCase) {
		setFired(true, theCase);
		//theCase.trace("  <<RULE FIRE>> " + getId());

		// d3web.debug
		notifyListeners(theCase, this);

		if (getAction() != null) {
			getAction().doIt(theCase);
		}

	}

	public RuleAction getAction() {
		return ruleAction;
	}

	/**
	 * @return the condtion which must be true, so that the rule can fire.
	 */
	public AbstractCondition getCondition() {
		return condition;
	}

	/**
	 * @return the specified <it>diagnosis context</it>. If not defined
	 * this method returns null. <BR>
	 * A diagnosis context is a condition which contains CondDState(diagnosis, ESTABLISHED)
	 * to formulate a context of established diagnoses in which the 
	 * rule is able to fire. If specified diagnoses are not established,
	 * then rule must not fire.
	 */
	public AbstractCondition getContext() {
		return diagnosisContext;
	}

	/**
	 * @return the exception when this rule must not fire.
	 */
	public AbstractCondition getException() {
		return exception;
	}

	public Class<? extends PSMethod> getProblemsolverContext() {
		if ((problemsolverContext == null) && (getAction() != null))
			return getAction().getProblemsolverContext();
		else
			/* 
			 * joba: this else-brach should be deleted, when 
			 * rule-action-refactoring is finished 
			 */
			return problemsolverContext;
	}

	/**
	  * Simply checks if the rule has already been fired in
	  * context of the specified user case.
	  */
	public boolean hasFired(XPSCase theCase) {
		return ((CaseRuleComplex) theCase.getCaseObject(this)).hasFired();
	}

	/**
	 * Checks if the rule has been fired
	 * (like hasFired()).
	 */
	public boolean isUsed(XPSCase theCase) {
		return hasFired(theCase);
	}

	public void setAction(RuleAction theRuleAction) {
		updateActionReferences(ruleAction, theRuleAction);
		ruleAction = theRuleAction;
	}

	/**
	 * Remove entries of the old action from the 
	 * named objects participating in the old
	 * rule action, and insert rule into 
	 * the new action objects.
	 * <BR>
	 * If the rule action changes, we also have to 
	 * change the references for the condition 
	 * entries (since the knowledge map key changes 
	 * for them as well) and
	 * diagnosisContext, rule exceptions.
	 * */
	protected void updateActionReferences(
		RuleAction oldAction,
		RuleAction newAction) {
		if ((oldAction != null)
			&& (oldAction.getTerminalObjects() != null)
			&& (oldAction.getProblemsolverContext() != null)) {
			removeFrom(
				this,
				oldAction.getTerminalObjects(),
				oldAction.getProblemsolverContext(),
				MethodKind.BACKWARD);
		}
		if ((newAction != null)
			&& (newAction.getTerminalObjects() != null)
			&& (newAction.getProblemsolverContext() != null)) {
			insertInto(
				this,
				newAction.getTerminalObjects(),
				newAction.getProblemsolverContext(),
				MethodKind.BACKWARD);

		}
		updateConditionTerminals(oldAction, newAction, getCondition());
		updateConditionTerminals(oldAction, newAction, getException());
		updateConditionTerminals(oldAction, newAction, getContext());
	}

	/**
	 * Remove terminal objects of specified condition from the old action
	 * and insert them into the specified new action.
	 * */
	protected void updateConditionTerminals(
		RuleAction oldAction,
		RuleAction newAction,
		AbstractCondition condi) {
		if (condi != null) {
			if (oldAction != null) {
				removeFrom(
					this,
					condi.getTerminalObjects(),
					oldAction.getProblemsolverContext(),
					MethodKind.FORWARD);
			}
			if (newAction != null) {
				insertInto(
					this,
					condi.getTerminalObjects(),
					newAction.getProblemsolverContext(),
					MethodKind.FORWARD);
			}
		}
	}

	/**
	 * Removes the specified rule from the knowledge map of the specified objects.
	 * @param namedObjects list of named objects, in which the rule should be removed
	 * @param psContext key for the specified knowledge map
	 * @param kind key for the specified knowledge map
	 * */
	private static void removeFrom(
		Rule r,
		List namedObjects,
		Class psContext,
		MethodKind kind) {
		if (namedObjects != null) {
			Iterator iter = namedObjects.iterator();
			while (iter.hasNext()) {
				NamedObject element = (NamedObject) iter.next();
				element.removeKnowledge(psContext, r, kind);
			}
		}
	}

	/**
	 * Adds the specified rule from the knowledge map of the specified objects.
	 * @param namedObjects list of named objects, in which the rule should be added
	 * @param psContext key for the specified knowledge map
	 * @param kind key for the specified knowledge map
	 * */
	private static void insertInto(
		Rule r,
		List namedObjects,
		Class psContext,
		MethodKind kind) {
		if (namedObjects != null) {
			Iterator iter = namedObjects.iterator();
			while (iter.hasNext()) {
				NamedObject element = (NamedObject) iter.next();
				if (element != null) {
					element.addKnowledge(psContext, r, kind);
				}
			}
		}

	}

	/**
	 * Sets the condtion which must be true, so that the rule can fire.
	 * This Method also inserts this instance as knowledge (backward/forward)
	 * into the involved objects contained in the condition.
	 */
	public void setCondition(
		de.d3web.core.inference.condition.AbstractCondition newCondition) {

		/* check, if there are already some conditions */
		if ((getCondition() != null) && (getAction() != null))
			removeFrom(
				this,
				getCondition().getTerminalObjects(),
				getAction().getProblemsolverContext(),
				MethodKind.FORWARD);
		//removeRuleFromObjects(getCondition().getTerminalObjects());

		condition = newCondition;
		if ((getCondition() != null) && (getAction() != null))
			insertInto(
				this,
				getCondition().getTerminalObjects(),
				getAction().getProblemsolverContext(),
				MethodKind.FORWARD);
		//insertRuleIntoObjects(getCondition().getTerminalObjects());
	}

	/**
	 * Sets the specified <it>diagnosis context</it>. <BR>
	 * Diagnosis context is a  condition which contains CondDState(diagnosis, ESTABLISHED)
	 * to formulate a context of established diagnoses in which the 
	 * rule is able to fire. If specified diagnoses are not established,
	 * then rule must not fire. For checking the state of the diagnosis, the
	 * heuristic problem solver is used.
	 */
	public void setContext(AbstractCondition newDiagnosisContext) {

		/* check, if there are already some conditions */
		if (getContext() != null)
			removeRuleFromObjects(
				getContext().getTerminalObjects(),
				PSMethodHeuristic.class);

		diagnosisContext = newDiagnosisContext;
		if (getContext() != null)
			insertRuleIntoObjects(
				getContext().getTerminalObjects(),
				PSMethodHeuristic.class);

	}

	private void insertRuleIntoObjects(List objects, Class context) {
		if (objects != null) {
			Iterator i = objects.iterator();
			while (i.hasNext()) {
				((NamedObject) i.next()).addKnowledge(
					context,
					this,
					MethodKind.FORWARD);
			}
		}
	}

	private void insertRuleIntoObjects(List objects) {
		insertRuleIntoObjects(objects, getProblemsolverContext());
	}
	private void removeRuleFromObjects(List objects, Class context) {
		if (objects != null) {
			Iterator i = objects.iterator();
			while (i.hasNext()) {
				((NamedObject) i.next()).removeKnowledge(
					context,
					this,
					MethodKind.FORWARD);

			}
		}
	}

	private void removeRuleFromObjects(List objects) {
		removeRuleFromObjects(objects, getProblemsolverContext());
	}

	/**
	 * Sets exception when this rule must not fire.
	 */
	public void setException(
		de.d3web.core.inference.condition.AbstractCondition newException) {
		/* check, if there are already some conditions */
		if (getException() != null)
			removeRuleFromObjects(getException().getTerminalObjects());

		exception = newException;
		if (getException() != null)
			insertRuleIntoObjects(getException().getTerminalObjects());
	}

	/**
	  * Sets the state of the rule, if it has fired or not
	  * in context of the specified userCase.
	  */
	protected void setFired(boolean newFired, XPSCase theCase) {
		((CaseRuleComplex) theCase.getCaseObject(this)).setFired(newFired);
	}

	public void setProblemsolverContext(java.lang.Class problemsolverContext) {
		this.problemsolverContext = problemsolverContext;
	}

	/**
	  * Executes the backtracking mechanism of the
	  * rule's action and sets the rule state to "not fired".
	  */
	public void undo(XPSCase theCase) {
		setFired(false, theCase);
		//theCase.trace("  <<RULE UNDO>> " + getId());

		// d3web.debug
		notifyListeners(theCase, this);

		if (getAction() != null) {
			getAction().undo(theCase);
		}
	}

	/**
	  * Gives a description of the rule and the state of the
	  * rule (fired or not).
	  */
	public String verbalize(XPSCase theCase) {
		return toString() + "\n fired: " + hasFired(theCase);
	}
    
    public boolean equals(Object o) {
        if (o == null)
            return false;
        else if (this == o)
            return true;
        if (o instanceof Rule) {
            Rule r = (Rule)o;
            boolean eq = super.equals(r);
            if (eq == false)
                return false;
            eq = eq && equalConditions(getCondition(), r.getCondition());
            eq = eq && equalConditions(getException(), r.getException());
            eq = eq && equalConditions(getContext(), r.getContext());
            eq = eq && equalActions(getAction(), r.getAction());
            return eq;
        }
        return false;

    }
    
    public int hashCode() {
        int hash = super.hashCode();
        if (getAction() != null) 
            hash += getAction().hashCode();
        if (getCondition() != null)
            hash += getCondition().hashCode();
        if (getException() != null) 
            hash += getException().hashCode();
        if (getContext() != null)
            hash += getContext().hashCode();
        return hash;
    }
    
    private static boolean equalActions(RuleAction a1, RuleAction a2) {
        if (a1 != null && a2 != null)
            return a1.equals(a2);
        else if (a1 == null && a2 == null)
            return true;
        else 
            return false;
    }

    private static boolean equalConditions(AbstractCondition c1, AbstractCondition c2) {
        if (c1 != null && c2 != null)
            return c1.equals(c2);
        else if (c1 == null && c2 == null)
            return true;
        else 
            return false;
    }

	public void remove() {
		setContext(null);
		setException(null);
		setCondition(null);
		setAction(null);
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}