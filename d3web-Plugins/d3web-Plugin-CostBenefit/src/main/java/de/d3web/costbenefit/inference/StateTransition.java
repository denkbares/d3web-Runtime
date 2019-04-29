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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.Conditions;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;

/**
 * A StateTransition is a KnowledgeSlice which belongs to a QContainer. It contains an activation condition and a list
 * of postTransitions. The QContainer is only applicable, when the activation condition is fullfilled. The value
 * transitions determine the actions, which are executed, when the QContainer is done.
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class StateTransition implements KnowledgeSlice {

	public static final KnowledgeKind<StateTransition> KNOWLEDGE_KIND =
			new KnowledgeKind<>("STATE_TRANSITION", StateTransition.class);

	private final Condition activationCondition;
	private final List<ValueTransition> postTransitions;
	private final QContainer qcontainer;

	/**
	 * Creates a new StateTransition for the specified qcontainer. It automatically adds itself to the qcontainer.
	 *
	 * @param activationCondition the condition required to use the qcontainer, or null if there is no precondition
	 * @param postTransitions     the transition to be executed after the qcontainer has been answered
	 * @param qcontainer          the qcontainer to create the state transition for
	 */
	public StateTransition(@Nullable Condition activationCondition, @NotNull List<ValueTransition> postTransitions, QContainer qcontainer) {
		super();
		this.activationCondition = activationCondition;
		this.postTransitions = postTransitions;
		this.qcontainer = qcontainer;
		qcontainer.getKnowledgeStore().addKnowledge(KNOWLEDGE_KIND, this);
	}

	@Nullable
	public Condition getActivationCondition() {
		return activationCondition;
	}

	@NotNull
	public List<ValueTransition> getPostTransitions() {
		return postTransitions;
	}

	@NotNull
	public QContainer getQcontainer() {
		return qcontainer;
	}

	/**
	 * This method is used to fire all ValueTransitions of the QContainer. For each question, the first ValueTransition
	 * whose condition is fulfilled, is used.
	 */
	public List<Fact> fire(Session session) {
		List<Fact> facts = new LinkedList<>();
		for (ValueTransition vt : postTransitions) {
			Question q = vt.getQuestion();
			List<ConditionalValueSetter> setters = vt.getSetters();
			for (ConditionalValueSetter cvs : setters) {
				Condition condition = cvs.getCondition();
				if (condition == null || Conditions.isTrue(cvs.getCondition(), session)) {
					Fact fact = new PSMethodStateTransition.StateTransitionFact(
							cvs, session, q, cvs.getAnswer());
					session.getBlackboard().addValueFact(fact);
					facts.add(fact);
					break;
				}
			}
		}
		return facts;
	}

	/**
	 * Returns true if the state transition's precondition if matched and therefore this state transition can be applied
	 * (fired) to the current session.
	 *
	 * @param session the session to check the precondition for
	 * @return true, if the activation condition is null or matches the session's facts
	 */
	public boolean isApplicable(Session session) {
		return (activationCondition == null) || Conditions.isTrue(activationCondition, session);
	}

	/**
	 * Returns the StateTransition of the QContainer
	 *
	 * @param qcon QContainer
	 * @return StateTransition of the QContainer or null if it has none
	 * @created 25.06.2010
	 */
	public static StateTransition getStateTransition(QContainer qcon) {
		return qcon.getKnowledgeStore().getKnowledge(KNOWLEDGE_KIND);
	}

	/**
	 * Returns the static costs of this {@link StateTransition}. The real costs may differ because of the cost function
	 * that is applied to calculate the real costs.
	 *
	 * @return the static costs of this transition
	 * @created 02.06.2012
	 */
	public double getCosts() {
		return qcontainer.getInfoStore().getValue(BasicProperties.COST);
	}
}
