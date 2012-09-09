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

import java.util.Collection;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.blackboard.CaseRuleComplex;

/**
 * Abstract super class for all rules. <BR>
 * It stores the condition, the check routine and if it has fired or not. The
 * action of a rule is specified by the extensions of Rule. Additionally, it is
 * possible to store an exception, when this rule must not fire.
 * 
 * @author Michael Wolber, joba
 */
public class Rule implements SessionObjectSource<CaseRuleComplex> {

	/**
	 * A condition which must be true, if rule should fire (obligatory).
	 */
	private Condition condition;

	/**
	 * A condition that must be false or undefined, if rule should fire
	 * (optional).
	 */
	private Condition exception;

	private Class<? extends PSMethodRulebased> problemsolverContext;

	/**
	 * The specified action the rule activates, if <it>condition</it> is true
	 * ,<it>exception</it> is false/undefined and <it>diagnosisContext</it> is
	 * true/undefined.
	 */
	private PSAction ruleAction;

	public Rule(Class<? extends PSMethodRulebased> context) {
		this.problemsolverContext = context;
		activateContextClass(context);
	}

	/**
	 * This is needed to fill the maps of FORWARD and BACKWARD in
	 * PSMethodRuleBased
	 * 
	 * @created 16.02.2011
	 * @param context
	 */
	private void activateContextClass(Class<? extends PSMethodRulebased> context) {
		if (context != null) {
			try {
				Class.forName(context.getCanonicalName());
			}
			catch (ClassNotFoundException e) {
				throw new IllegalArgumentException(e);
			}
		}
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
			 * The exception could not be tested --> just go on and treat it
			 * like there is no exception
			 */
		}
		catch (UnknownAnswerException uex) {
			/*
			 * The exception could not be tested --> just go on and treat it
			 * like there is no exception
			 */
		}

		try {
			return getCondition().eval(session);
		}
		catch (NoAnswerException ex) {
			/*
			 * the condition could not be tested --> return false
			 */
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

		boolean hasFired = hasFired(session);
		try {
			boolean canFire = canFire(session);

			if (!hasFired && canFire) {
				executeRuleAction = true;
			}

			if (hasFired && !canFire) {
				undoRuleAction = true;
			}

			// if this is a multipleFire-rule that has fired AND can fire again
			// AND any depending value has changed, its action will be undone
			// and executed again.
			// This change fixes the "fire-undo-fire-bug" (when a question gets
			// the same value several times (see MQDialogController)) and some
			// problems with the "cycle-check".
			if (hasFired && canFire) {
				// if the action is a question setter action, changes in
				// depending values (e.g. elements of a formula)
				// will be noticed
				boolean isQuestionSetterActionWithChangedValues =
						getAction().hasChangedValue(session);
				if (isQuestionSetterActionWithChangedValues) {
					undoRuleAction = true;
					executeRuleAction = true;
				}
			}
		}
		catch (UnknownAnswerException ex) {
			if (hasFired) {
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
	public CaseRuleComplex createSessionObject(Session session) {
		return new CaseRuleComplex();
	}

	/**
	 * Executes the action of the rule.
	 */
	public void doIt(Session session) {
		setFired(true, session);
		if (getAction() != null) {
			getAction().doIt(session, this, session.getPSMethodInstance(getProblemsolverContext()));
		}

	}

	public PSAction getAction() {
		return ruleAction;
	}

	/**
	 * @return the condition which must be true, so that the rule can fire.
	 */
	public Condition getCondition() {
		return condition;
	}

	/**
	 * @return the exception when this rule must not fire.
	 */
	public Condition getException() {
		return exception;
	}

	public Class<? extends PSMethodRulebased> getProblemsolverContext() {
		return problemsolverContext;
	}

	/**
	 * Simply checks if the rule has already been fired in context of the
	 * specified user session.
	 */
	public boolean hasFired(Session session) {
		return (session.getSessionObject(this)).hasFired();
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
	private void updateActionReferences(
			PSAction oldAction,
			PSAction newAction) {
		// do not add any knowledge when the problem solver context is still
		// null
		if (getProblemsolverContext() == null) return;

		if ((oldAction != null)
				&& (oldAction.getBackwardObjects() != null)) {
			removeFrom(
					this,
					oldAction.getBackwardObjects(),
					PSMethodRulebased.getBackwardKind(getProblemsolverContext()));
		}
		if ((oldAction != null)
				&& (oldAction.getForwardObjects() != null)) {
			removeFrom(
					this,
					oldAction.getForwardObjects(),
					PSMethodRulebased.getForwardKind(getProblemsolverContext()));
		}
		if ((newAction != null)
				&& (newAction.getBackwardObjects() != null)) {
			insertInto(
					this,
					newAction.getBackwardObjects(),
					PSMethodRulebased.getBackwardKind(getProblemsolverContext()));
		}
		if ((newAction != null)
				&& (newAction.getForwardObjects() != null)) {
			insertInto(
					this,
					newAction.getForwardObjects(),
					PSMethodRulebased.getForwardKind(getProblemsolverContext()));
		}
		updateConditionTerminals(oldAction, newAction, getCondition());
		updateConditionTerminals(oldAction, newAction, getException());
	}

	/**
	 * Remove terminal objects of specified condition from the old action and
	 * insert them into the specified new action.
	 * */
	private void updateConditionTerminals(
			PSAction oldAction,
			PSAction newAction,
			Condition condi) {
		if (condi != null) {
			if (oldAction != null) {
				removeFrom(
						this,
						condi.getTerminalObjects(),
						PSMethodRulebased.getForwardKind(getProblemsolverContext()));
			}
			if (newAction != null) {
				insertInto(
						this,
						condi.getTerminalObjects(),
						PSMethodRulebased.getForwardKind(getProblemsolverContext()));
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
			Collection<? extends TerminologyObject> namedObjects,
			KnowledgeKind<RuleSet> kind) {
		if (namedObjects != null) {
			for (TerminologyObject nob : namedObjects) {
				removeFrom(r, kind, nob);
			}
		}
	}

	/**
	 * Removes the specified rule from the knowledge of the specified object.
	 * 
	 * @param r specified rule
	 * @param kind knowledge kind
	 * @param nob specified object
	 */
	public static void removeFrom(Rule r, KnowledgeKind<RuleSet> kind, TerminologyObject nob) {
		if (nob != null) {
			RuleSet rs = nob.getKnowledgeStore().getKnowledge(
					kind);
			if (rs != null) {
				rs.removeRule(r);
				if (rs.isEmpty()) {
					nob.getKnowledgeStore().removeKnowledge(kind, rs);
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
			Collection<? extends TerminologyObject> namedObjects,
			KnowledgeKind<RuleSet> kind) {
		if (namedObjects != null) {
			for (TerminologyObject nob : namedObjects) {
				insertInto(r, kind, nob);
			}
		}
	}

	/**
	 * Adds the specified rule to the knowledge map of the specified objects.
	 * 
	 * @param r specified rule
	 * @param kind knowledge kind
	 * @param nob specified object
	 */
	public static void insertInto(Rule r, KnowledgeKind<RuleSet> kind, TerminologyObject nob) {
		if (nob != null) {
			KnowledgeSlice knowledge = nob.getKnowledgeStore().getKnowledge(
					kind);
			if (knowledge != null) {
				RuleSet rs = (RuleSet) knowledge;
				rs.addRule(r);
			}
			else {
				RuleSet rs = new RuleSet();
				rs.addRule(r);
				nob.getKnowledgeStore().addKnowledge(kind, rs);
			}
		}
	}

	/**
	 * Sets the condition which must be true, so that the rule can fire. This
	 * method also inserts this instance as knowledge (backward/forward) into
	 * the involved objects contained in the condition.
	 */
	public void setCondition(
			de.d3web.core.inference.condition.Condition newCondition) {
		// do not add any knowledge when the problem-solver context is still
		// null
		if (getProblemsolverContext() == null) {
			condition = newCondition;
			return;
		}

		/* check, if there are already some conditions */
		if (getCondition() != null) {
			removeFrom(
					this,
					getCondition().getTerminalObjects(),
					PSMethodRulebased.getForwardKind(getProblemsolverContext()));
		}
		condition = newCondition;
		if (getCondition() != null) {
			insertInto(
					this,
					getCondition().getTerminalObjects(),
					PSMethodRulebased.getForwardKind(getProblemsolverContext()));
		}
	}

	/**
	 * Sets exception when this rule must not fire.
	 */
	public void setException(
			de.d3web.core.inference.condition.Condition newException) {
		// do not add any knowledge when the problem-solver context is still
		// null
		if (getProblemsolverContext() == null) {
			exception = newException;
			return;
		}

		/* check, if there are already some conditions */
		if (getException() != null) {
			removeFrom(this, getException().getTerminalObjects(),
					PSMethodRulebased.getForwardKind(getProblemsolverContext()));
		}
		exception = newException;
		if (getException() != null) {
			insertInto(this, getException().getTerminalObjects(),
					PSMethodRulebased.getForwardKind(getProblemsolverContext()));
		}
	}

	/**
	 * Sets the state of the rule, if it has fired or not in context of the
	 * specified userCase.
	 */
	private void setFired(boolean newFired, Session session) {
		(session.getSessionObject(this)).setFired(newFired);
	}

	public void setProblemsolverContext(Class<? extends PSMethodRulebased> problemsolverContext) { // NOSONAR
		if (problemsolverContext == null) {
			throw new NullPointerException(
					"Setting the problemsolver context to null is not allowed.");
		}
		if (this.problemsolverContext != problemsolverContext) {
			activateContextClass(problemsolverContext);
			// when the problem-solver context was null, nothing was added to
			// the
			// knowledge stores
			if (getProblemsolverContext() != null) {
				// remove old indexes
				if (getCondition() != null) {
					removeFrom(
							this,
							getCondition().getTerminalObjects(),
							PSMethodRulebased.getForwardKind(getProblemsolverContext()));
				}
				if (getException() != null) {
					removeFrom(this, getException().getTerminalObjects(),
							PSMethodRulebased.getForwardKind(getProblemsolverContext()));
				}
				if (getAction() != null && (getAction().getBackwardObjects() != null)) {
					removeFrom(
							this,
							getAction().getBackwardObjects(),
							PSMethodRulebased.getBackwardKind(getProblemsolverContext()));
				}
				if (getAction() != null && (getAction().getForwardObjects() != null)) {
					removeFrom(
							this,
							getAction().getForwardObjects(),
							PSMethodRulebased.getBackwardKind(getProblemsolverContext()));
				}
			}
			// insert new indexes
			this.problemsolverContext = problemsolverContext;
			if (getCondition() != null) {
				insertInto(
						this,
						getCondition().getTerminalObjects(),
						PSMethodRulebased.getForwardKind(getProblemsolverContext()));
			}
			if (getException() != null) {
				insertInto(this, getException().getTerminalObjects(),
						PSMethodRulebased.getForwardKind(getProblemsolverContext()));
			}
			if ((getAction() != null)
					&& (getAction().getBackwardObjects() != null)) {
				insertInto(
						this,
						getAction().getBackwardObjects(),
						PSMethodRulebased.getBackwardKind(getProblemsolverContext()));
			}
			if ((getAction() != null)
					&& (getAction().getForwardObjects() != null)) {
				insertInto(
						this,
						getAction().getForwardObjects(),
						PSMethodRulebased.getForwardKind(getProblemsolverContext()));
			}
		}
	}

	/**
	 * Executes the backtracking mechanism of the rule's action and sets the
	 * rule state to "not fired".
	 */
	public void undo(Session session) {
		setFired(false, session);
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

	public void remove() {
		setException(null);
		setCondition(null);
		setAction(null);
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

		return b.toString();
	}

}