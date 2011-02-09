/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.costbenefit.inference;

import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;

/**
 * A StateTransition is a KnowledgeSlice which belongs to a QContainer. It
 * contains an activation condition and a list of postTransitions. The
 * QContainer is only applicable, when the activation condition is fullfilled.
 * The value transitions determine the actions, which are executed, when the
 * QContainer is done.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class StateTransition implements KnowledgeSlice {

	private static final Class<PSMethodCostBenefit> PROBLEMSOLVER = PSMethodCostBenefit.class;

	public static final MethodKind STATE_TRANSITION = new MethodKind("STATE_TRANSITION");

	private Condition activationCondition;
	private List<ValueTransition> postTransitions;
	private final QContainer qcontainer;

	public StateTransition(Condition activationCondition, List<ValueTransition> postTransitions, QContainer qcontainer) {
		super();
		this.activationCondition = activationCondition;
		this.postTransitions = postTransitions;
		this.qcontainer = qcontainer;
	}

	public Condition getActivationCondition() {
		return activationCondition;
	}

	public void setActivationCondition(Condition activationCondition) {
		this.activationCondition = activationCondition;
	}

	public List<ValueTransition> getPostTransitions() {
		return postTransitions;
	}

	public void setPostTransitions(List<ValueTransition> postTransition) {
		this.postTransitions = postTransition;
	}

	@Override
	public String getId() {
		if (qcontainer != null) {
			return "StateTransition_" + qcontainer.getName();
		}
		else {
			return "StateTransition_withoutQcontainer";
		}
	}

	public QContainer getQcontainer() {
		return qcontainer;
	}

	@Override
	public Class<? extends PSMethod> getProblemsolverContext() {
		return PROBLEMSOLVER;
	}

	@Override
	public void remove() {
		qcontainer.removeKnowledge(getProblemsolverContext(), this, STATE_TRANSITION);
	}

	/**
	 * This method is used to fire all ValueTransitions of the QContainer. For
	 * each question, the first ValueTransition whose condition is fulfilled, is
	 * used.
	 * 
	 * @param session
	 * @return
	 */
	public List<Fact> fire(Session session) {
		List<Fact> facts = new LinkedList<Fact>();
		for (ValueTransition vt : postTransitions) {
			Question q = vt.getQuestion();
			List<ConditionalValueSetter> setters = vt.getSetters();
			for (ConditionalValueSetter cvs : setters) {
				try {
					Condition condition = cvs.getCondition();
					if (condition == null || cvs.getCondition().eval(session)) {
						// Fact fact = FactFactory.createFact(q,
						// cvs.getAnswer(), new Object(),
						// PSMethodUserSelected.getInstance());
						Fact fact = new
								PSMethodStateTransition.StateTransitionFact(q,
										cvs.getAnswer());
						session.getBlackboard().addValueFact(fact);
						facts.add(fact);
						break;
					}
				}
				catch (NoAnswerException e) {
					// Nothing to do
				}
				catch (UnknownAnswerException e) {
					// Nothing to do
				}
			}
		}
		return facts;
	}

	/**
	 * Returns the StateTransition of the QContainer
	 * 
	 * @created 25.06.2010
	 * @param qcon QContainer
	 * @return StateTransition of the QContainer or null if it has none
	 */
	public static StateTransition getStateTransition(QContainer qcon) {
		return (StateTransition) qcon.getKnowledge(PROBLEMSOLVER, STATE_TRANSITION);
	}

	/**
	 * Fires the state transition of the QContainer in the Session
	 * 
	 * @created 25.06.2010
	 * @param qcon QContainer
	 * @param session Session
	 * @return List of Facts which where added during the firing of the
	 *         StateTransition, null if there is no StateTransition for the
	 *         QContainer
	 */
	public static List<Fact> fireStateTransition(QContainer qcon, Session session) {
		StateTransition st = getStateTransition(qcon);
		if (st != null) {
			return st.fire(session);
		}
		else {
			return null;
		}
	}
}
