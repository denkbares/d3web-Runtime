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

package de.d3web.core.inference;

import java.util.List;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.session.CaseObjectSource;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.CaseRuleComplex;
import de.d3web.core.session.blackboard.SessionObject;

/**
 * Abstract super class for all rules. <BR>
 * It stores the condition, the check routine and if it has fired or not. The
 * action of a rule is specified by the extensions of RuleComplex. Additionally
 * it is possible to store an exception, when this rule must not fire.
 * 
 * @author Michael Wolber, joba
 */
public class Rule implements CaseObjectSource {

	private final String id;

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
	private Condition condition;

	/**
	 * A condition that must be false or undefined, if rule should fire
	 * (optional).
	 */
	private Condition exception;

	/**
	 * A condition which contains CondDState(diagnosis, ESTABLISHED) to
	 * formulate a context of established diagnoses in which the rule is able to
	 * fire. If specified diagnoses are not established, then rule must not
	 * fire.
	 */
	private Condition diagnosisContext;

	private Class<? extends PSMethod> problemsolverContext;

	/**
	 * The specified action the rule activates, if <it>condition</it> is true
	 * ,<it>exception</it> is false/undefined and <it>diagnosisContext</it> is
	 * true/undefined.
	 */
	private PSAction ruleAction;

	public Rule(String id, Class<? extends PSMethod> context) {
		this.id = id;
		this.problemsolverContext = context;
	}

	public String getId() {
		return id;
	}

	/**
	 * Checks if the rule is able to fire in context of the values of the
	 * specified case. In detail it evaluates the condition, which must be true
	 * and
	 * <OL>
	 * <LI>if available - checks the exception not to be true.
	 * <LI>if available - checks the diagnosis context to be true
	 * </OL>
	 */
	public boolean canFire(Session session) throws UnknownAnswerException {
		try {
			/* if an exception is available and it is true, then do not fire! */
			if ((getException() != null) && getException().eval(session)) {
				return false;
			}

		}
		catch (NoAnswerException ex) {
			/*
			 * The exception could not be testet --> just go on and treat it
			 * like there is no exception
			 */
			// D3WebCase.trace("Exception contained unknown term ");
		}
		catch (UnknownAnswerException uex) {
			/*
			 * The exception could not be testet --> just go on and treat it
			 * like there is no exception
			 */
		}

		try {
			/*
			 * if a diagnosis context is available and it is false, then do not
			 * fire!
			 */
			if ((getContext() != null) && !getContext().eval(session)) {
				return false;
			}
		}
		catch (NoAnswerException e) {
			return false;
		}

		try {
			return getCondition().eval(session);
		}
		catch (NoAnswerException ex) {
			/*
			 * the condition could not be tested --> return false
			 */
			// D3WebCase.trace("Condition contained unknown term ");
			return false;
		}
	}

	/**
	 * Buawa: This method checks a lot:<BR>
	 * is this rule able to fire? (check condition) If it is able to fire the
	 * rule executes the action part.<BR>
	 * 
	 * Different from rules which fire to diagnosis, this check-method does not
	 * need to check whether the rule already has fired - since the value is not
	 * accumulative. It is instead an error to do so - since the action of the
	 * rule might evaluate to another value when the source values change.
	 * 
	 * If it has fired but is now not able to fire (cause the conditions are now
	 * not fulfilled) the action's backtrack mechanism is executed.
	 * 
	 * If the rule has already fired, can fire AND the depending Action is a
	 * ActionQuestionSetter whose elementary values have changed (e.g. terminals
	 * in a formula) it will be undone and fired again, so that the e.g.
	 * depending formula will be recalculated.
	 */
	public void check(Session session) {
		// should we execute the rule action ???
		boolean executeRuleAction = false;
		// should we undo the rule action ???
		boolean undoRuleAction = false;

		try {
			boolean hasFired = hasFired(session);
			boolean canFire = canFire(session);

			// ... do nothing, if not active
			if (!active) {
				/*
				 * if(hasFired) { undo(session); }
				 */
				return;
			}

			if (!hasFired && canFire) {
				executeRuleAction = true;
			}

			if (hasFired && !canFire) {
				undoRuleAction = true;
			}

			// if the action is a question setter action, changes in depending
			// values (e.g. elements of a formula)
			// will be noticed and stored in the boolean
			// "isQuestionSetterActionWithChangedValues"
			boolean isQuestionSetterActionWithChangedValues =
					getAction().hasChangedValue(session);
			// if this is a multipleFire-rule that has fired AND can fire again
			// AND any depending value has
			// changed, its action will be undone and executed again.
			// This change fixes the "fire-undo-fire-bug" (when a question gets
			// the same
			// value several times (see MQDialogController)) and some problems
			// with the "cycle-check".
			if (hasFired
					&& canFire
					&& isQuestionSetterActionWithChangedValues) {
				undoRuleAction = true;
				executeRuleAction = true;
			}

		}
		catch (UnknownAnswerException ex) {
			if (hasFired(session)) {
				undoRuleAction = true;
			}
		}

		if (undoRuleAction) {
			undo(session);
		}
		if (executeRuleAction) {
			doIt(session);
		}
	}

	@Override
	public SessionObject createCaseObject(Session session) {
		return new CaseRuleComplex(this);
	}

	/**
	 * Executes the action of the rule.
	 */
	public void doIt(Session session) {
		setFired(true, session);
		// session.trace("  <<RULE FIRE>> " + getId());
		if (getAction() != null) {
			getAction().doIt(session, this, session.getPSMethodInstance(getProblemsolverContext()));
		}

	}

	public PSAction getAction() {
		return ruleAction;
	}

	/**
	 * @return the condtion which must be true, so that the rule can fire.
	 */
	public Condition getCondition() {
		return condition;
	}

	/**
	 * @return the specified <it>diagnosis context</it>. If not defined this
	 *         method returns null. <BR>
	 *         A diagnosis context is a condition which contains
	 *         CondDState(diagnosis, ESTABLISHED) to formulate a context of
	 *         established diagnoses in which the rule is able to fire. If
	 *         specified diagnoses are not established, then rule must not fire.
	 */
	public Condition getContext() {
		return diagnosisContext;
	}

	/**
	 * @return the exception when this rule must not fire.
	 */
	public Condition getException() {
		return exception;
	}

	public Class<? extends PSMethod> getProblemsolverContext() {
		return problemsolverContext;
	}

	/**
	 * Simply checks if the rule has already been fired in context of the
	 * specified user case.
	 */
	public boolean hasFired(Session session) {
		return ((CaseRuleComplex) session.getCaseObject(this)).hasFired();
	}

	/**
	 * Checks if the rule has been fired (like hasFired()).
	 */
	public boolean isUsed(Session session) {
		return hasFired(session);
	}

	public void setAction(PSAction theRuleAction) {
		updateActionReferences(ruleAction, theRuleAction);
		ruleAction = theRuleAction;
	}

	/**
	 * Remove entries of the old action from the named objects participating in
	 * the old rule action, and insert rule into the new action objects. <BR>
	 * If the rule action changes, we also have to change the references for the
	 * condition entries (since the knowledge map key changes for them as well)
	 * and diagnosisContext, rule exceptions.
	 * */
	protected void updateActionReferences(
			PSAction oldAction,
			PSAction newAction) {
		if ((oldAction != null)
				&& (oldAction.getBackwardObjects() != null)) {
			removeFrom(
					this,
					oldAction.getBackwardObjects(),
					getProblemsolverContext(),
					MethodKind.BACKWARD);
		}
		if ((oldAction != null)
				&& (oldAction.getForwardObjects() != null)) {
			removeFrom(
					this,
					oldAction.getForwardObjects(),
					getProblemsolverContext(),
					MethodKind.FORWARD);
		}
		if ((newAction != null)
				&& (newAction.getBackwardObjects() != null)) {
			insertInto(
					this,
					newAction.getBackwardObjects(),
					getProblemsolverContext(),
					MethodKind.BACKWARD);
		}
		if ((newAction != null)
				&& (newAction.getForwardObjects() != null)) {
			insertInto(
					this,
					newAction.getForwardObjects(),
					getProblemsolverContext(),
					MethodKind.FORWARD);
		}
		updateConditionTerminals(oldAction, newAction, getCondition());
		updateConditionTerminals(oldAction, newAction, getException());
		updateConditionTerminals(oldAction, newAction, getContext());
	}

	/**
	 * Remove terminal objects of specified condition from the old action and
	 * insert them into the specified new action.
	 * */
	protected void updateConditionTerminals(
			PSAction oldAction,
			PSAction newAction,
			Condition condi) {
		if (condi != null) {
			if (oldAction != null) {
				removeFrom(
						this,
						condi.getTerminalObjects(),
						getProblemsolverContext(),
						MethodKind.FORWARD);
			}
			if (newAction != null) {
				insertInto(
						this,
						condi.getTerminalObjects(),
						getProblemsolverContext(),
						MethodKind.FORWARD);
			}
		}
	}

	/**
	 * Removes the specified rule from the knowledge map of the specified
	 * objects.
	 * 
	 * @param namedObjects list of named objects, in which the rule should be
	 *        removed
	 * @param psContext key for the specified knowledge map
	 * @param kind key for the specified knowledge map
	 * */
	public static void removeFrom(
			Rule r,
			List<? extends TerminologyObject> namedObjects,
			Class<? extends PSMethod> psContext,
			MethodKind kind) {
		if (namedObjects != null) {
			for (TerminologyObject nob : namedObjects) {
				removeFrom(r, psContext, kind, nob);
			}
		}
	}

	/**
	 * Removes the specified rule from the knowledge of the specified object
	 * 
	 * @param r specified rule
	 * @param psContext Problemsolver
	 * @param kind Methodkind
	 * @param nob specified Object
	 */
	public static void removeFrom(Rule r, Class<? extends PSMethod> psContext, MethodKind kind, TerminologyObject nob) {
		if (nob != null) {
			KnowledgeSlice knowledge = ((NamedObject) nob).getKnowledge(psContext, kind);
			if (knowledge != null) {
				RuleSet rs = (RuleSet) knowledge;
				rs.removeRule(r);
				if (rs.isEmpty()) {
					((NamedObject) nob).removeKnowledge(psContext, rs, kind);
				}
			}
		}
	}

	/**
	 * Adds the specified rule to the knowledge map of the specified objects.
	 * 
	 * @param namedObjects list of named objects, in which the rule should be
	 *        added
	 * @param psContext key for the specified knowledge map
	 * @param kind key for the specified knowledge map
	 * */
	public static void insertInto(
			Rule r,
			List<? extends TerminologyObject> namedObjects,
			Class<? extends PSMethod> psContext,
			MethodKind kind) {
		if (namedObjects != null) {
			for (TerminologyObject nob : namedObjects) {
				insertInto(r, psContext, kind, nob);
			}
		}
	}

	/**
	 * Adds the specified rule to the knowledge map of the specified objects.
	 * 
	 * @param r specified rule
	 * @param psContext Problemsolver
	 * @param kind Methodkind
	 * @param nob specified Object
	 */
	public static void insertInto(Rule r, Class<? extends PSMethod> psContext, MethodKind kind, TerminologyObject nob) {
		if (nob != null) {
			KnowledgeSlice knowledge = ((NamedObject) nob).getKnowledge(psContext, kind);
			if (knowledge != null) {
				RuleSet rs = (RuleSet) knowledge;
				rs.addRule(r);
			}
			else {
				RuleSet rs = new RuleSet(psContext);
				rs.addRule(r);
				((NamedObject) nob).addKnowledge(psContext, rs, kind);
			}
		}
	}

	/**
	 * Sets the condition which must be true, so that the rule can fire. This
	 * Method also inserts this instance as knowledge (backward/forward) into
	 * the involved objects contained in the condition.
	 */
	public void setCondition(
			de.d3web.core.inference.condition.Condition newCondition) {

		/* check, if there are already some conditions */
		if (getCondition() != null) {
			removeFrom(
					this,
					getCondition().getTerminalObjects(),
					getProblemsolverContext(),
					MethodKind.FORWARD);
		}
		// removeRuleFromObjects(getCondition().getTerminalObjects());
		condition = newCondition;
		if (getCondition() != null) {
			insertInto(
					this,
					getCondition().getTerminalObjects(),
					getProblemsolverContext(),
					MethodKind.FORWARD);
			// insertRuleIntoObjects(getCondition().getTerminalObjects());
		}
	}

	/**
	 * Sets the specified <it>diagnosis context</it>. <BR>
	 * Diagnosis context is a condition which contains CondDState(diagnosis,
	 * ESTABLISHED) to formulate a context of established diagnoses in which the
	 * rule is able to fire. If specified diagnoses are not established, then
	 * rule must not fire. For checking the state of the diagnosis, the
	 * heuristic problem solver is used.
	 */
	public void setContext(Condition newDiagnosisContext) {

		/* check, if there are already some conditions */
		if (getContext() != null) {
			removeFrom(this,
					getContext().getTerminalObjects(),
					getProblemsolverContext(), MethodKind.FORWARD);
		}
		diagnosisContext = newDiagnosisContext;
		if (getContext() != null) {
			insertInto(this,
					getContext().getTerminalObjects(),
					getProblemsolverContext(), MethodKind.FORWARD);
		}

	}

	/**
	 * Sets exception when this rule must not fire.
	 */
	public void setException(
			de.d3web.core.inference.condition.Condition newException) {
		/* check, if there are already some conditions */
		if (getException() != null) {
			removeFrom(this, getException().getTerminalObjects(),
					getProblemsolverContext(),
					MethodKind.FORWARD);
		}
		exception = newException;
		if (getException() != null) {
			insertInto(this, getException().getTerminalObjects(),
					getProblemsolverContext(),
					MethodKind.FORWARD);
		}
	}

	/**
	 * Sets the state of the rule, if it has fired or not in context of the
	 * specified userCase.
	 */
	protected void setFired(boolean newFired, Session session) {
		((CaseRuleComplex) session.getCaseObject(this)).setFired(newFired);
	}

	public void setProblemsolverContext(Class<? extends PSMethod> problemsolverContext) { // NOSONAR
		if (this.problemsolverContext != problemsolverContext) {
			// remove old indexes
			if (getCondition() != null) {
				removeFrom(
							this,
							getCondition().getTerminalObjects(),
							getProblemsolverContext(),
							MethodKind.FORWARD);
			}
			if (getContext() != null) {
				removeFrom(this,
							getContext().getTerminalObjects(),
							getProblemsolverContext(), MethodKind.FORWARD);
			}
			if (getException() != null) {
				removeFrom(this, getException().getTerminalObjects(),
							getProblemsolverContext(),
							MethodKind.FORWARD);
			}
			if (getAction() != null && (getAction().getBackwardObjects() != null)) {
				removeFrom(
							this,
							getAction().getBackwardObjects(),
							getProblemsolverContext(),
							MethodKind.BACKWARD);
			}
			if (getAction() != null && (getAction().getForwardObjects() != null)) {
				removeFrom(
							this,
							getAction().getForwardObjects(),
							getProblemsolverContext(),
							MethodKind.FORWARD);
			}

			// insert new indexes
			this.problemsolverContext = problemsolverContext;
			if (getCondition() != null) {
				insertInto(
						this,
						getCondition().getTerminalObjects(),
						getProblemsolverContext(),
						MethodKind.FORWARD);
			}
			if (getContext() != null) {
				insertInto(this,
						getContext().getTerminalObjects(),
						getProblemsolverContext(), MethodKind.FORWARD);
			}
			if (getException() != null) {
				insertInto(this, getException().getTerminalObjects(),
						getProblemsolverContext(),
						MethodKind.FORWARD);
			}
			if ((getAction() != null)
					&& (getAction().getBackwardObjects() != null)) {
				insertInto(
						this,
						getAction().getBackwardObjects(),
						getProblemsolverContext(),
						MethodKind.BACKWARD);
			}
			if ((getAction() != null)
					&& (getAction().getForwardObjects() != null)) {
				insertInto(
						this,
						getAction().getForwardObjects(),
						getProblemsolverContext(),
						MethodKind.FORWARD);
			}
		}
	}

	/**
	 * Executes the backtracking mechanism of the rule's action and sets the
	 * rule state to "not fired".
	 */
	public void undo(Session session) {
		setFired(false, session);
		// session.trace("  <<RULE UNDO>> " + getId());
		if (getAction() != null) {
			getAction().undo(session, this, session.getPSMethodInstance(getProblemsolverContext()));
		}
	}

	/**
	 * Gives a description of the rule and the state of the rule (fired or not).
	 */
	public String verbalize(Session session) {
		return toString() + "\n fired: " + hasFired(session);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		else if (this == o) {
			return true;
		}
		if (o instanceof Rule) {
			Rule r = (Rule) o;
			boolean eq = super.equals(r);
			if (!eq) {
				return false;
			}
			eq = eq && equalConditions(getCondition(), r.getCondition());
			eq = eq && equalConditions(getException(), r.getException());
			eq = eq && equalConditions(getContext(), r.getContext());
			eq = eq && equalActions(getAction(), r.getAction());
			return eq;
		}
		return false;

	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		if (getAction() != null) {
			hash += getAction().hashCode();
		}
		if (getCondition() != null) {
			hash += getCondition().hashCode();
		}
		if (getException() != null) {
			hash += getException().hashCode();
		}
		if (getContext() != null) {
			hash += getContext().hashCode();
		}
		return hash;
	}

	private static boolean equalActions(PSAction a1, PSAction a2) {
		if (a1 != null && a2 != null) {
			return a1.equals(a2);
		}
		else {
			return (a1 == null && a2 == null);
		}
	}

	private static boolean equalConditions(Condition c1, Condition c2) {
		if (c1 != null && c2 != null) {
			return c1.equals(c2);
		}
		else {
			return (c1 == null && c2 == null);
		}
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

	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("IF   " + this.getCondition() + " \n");
		b.append("THEN " + this.getAction() + "\n");
		if (this.getException() != null) {
			b.append("EXCEPT  ");
			b.append(this.getException());
		}
		if (this.getContext() != null) {
			b.append("CONTEXT ");
			b.append(this.getContext());
		}

		return b.toString();
	}

}