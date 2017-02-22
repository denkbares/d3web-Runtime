/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.costbenefit.inference;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PSMethodAdapter;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.costbenefit.CostBenefitUtil;
import de.d3web.costbenefit.blackboard.CostBenefitCaseObject;
import de.d3web.costbenefit.inference.PSMethodStateTransition.StateTransitionSessionObject;

public final class PSMethodStateTransition extends PSMethodAdapter implements SessionObjectSource<StateTransitionSessionObject> {

	public PSMethodStateTransition() {
	}

	private boolean isPartOfPermanentlyRelevantQContainer(Condition condition) {
		if (condition != null) {
			for (TerminologyObject termObject : condition.getTerminalObjects()) {
				if (CostBenefitUtil.hasPermanentlyRelevantParent(termObject)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		// remember: if a session is loaded from a record,
		// source psm max have DefaultFacts instead of their one ones
		Fact maxFact = null;
		int maxNumber = Integer.MIN_VALUE;
		for (Fact fact : facts) {

			if (fact instanceof StateTransitionFact) {
				StateTransitionFact stf = (StateTransitionFact) fact;
				// facts which set final questions have highest priority, if
				// they are set by permanently relevant test steps
				if (stf.getTerminologyObject().getInfoStore().getValue(
						PSMethodCostBenefit.FINAL_QUESTION)
						&& isPartOfPermanentlyRelevantQContainer(stf.cvs.getCondition())) {
					return stf;
				}
				// use the first one
				if (maxFact == null) {
					maxFact = fact;
				}
				// and overwrite existing max if if the current one is newer
				else {
					if (maxNumber < stf.number) {
						maxFact = stf;
						maxNumber = stf.number;
					}
				}
			}
			// for reloaded facts of final questions the maxvalue is set, so
			// they can only be overwritten by permanently relevant QContainers
			else if (fact.getTerminologyObject().getInfoStore().getValue(
					PSMethodCostBenefit.FINAL_QUESTION)) {
				maxFact = fact;
				maxNumber = Integer.MAX_VALUE;
			}
		}
		return maxFact;
	}

	@Override
	public int hashCode() {
		// hashCode and equals will be based on class identification only
		return getClass().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		// hashCode and equals will be based on class identification only
		return other.getClass() == getClass();
	}

	public static class StateTransitionFact extends DefaultFact {

		private static final PSMethodStateTransition psmInstance = new PSMethodStateTransition();
		public static int counter = 0;
		private final int number;
		private final ConditionalValueSetter cvs;

		/**
		 * Default method to create a {@link StateTransitionFact}, it should be
		 * used whenever possible
		 *
		 * @param cvs {@link ConditionalValueSetter} that has fired
		 * @param session actual {@link Session}
		 * @param terminologyObject {@link TerminologyObject} which value should be set
		 * @param value the Value that should be set
		 */
		public StateTransitionFact(ConditionalValueSetter cvs, Session session, TerminologyObject terminologyObject, Value value) {
			super(terminologyObject, value, new Object(), findPSM(session));
			this.cvs = cvs;
			number = counter++;
		}

		/**
		 * Creates a StateTransitionFact without a ConditionalValueSetter. This
		 * method must not be used for values of final questions. Generally the
		 * method using the {@link ConditionalValueSetter} should be preferred
		 *
		 * @param session actual {@link Session}
		 * @param terminologyObject {@link TerminologyObject} which value should be set
		 * @param value the Value that should be set
		 */
		public StateTransitionFact(Session session, TerminologyObject terminologyObject, Value value) {
			this(new ConditionalValueSetter(value, null), session, terminologyObject, value);
		}

		private static PSMethod findPSM(Session session) {
			PSMethod psm = session.getPSMethodInstance(PSMethodStateTransition.class);
			if (psm != null) {
				return psm;
			}
			return psmInstance;
		}
	}

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {
		Set<QContainer> answeredQuestionnaires = new HashSet<>();
		for (PropagationEntry entry : changes) {
			TerminologyObject object = entry.getObject();
			if (!entry.isStrategic() && entry.hasChanged() && object instanceof Question) {
				CostBenefitUtil.addParentContainers(answeredQuestionnaires, object);
			}
		}
		PSMethodCostBenefit costBenefit = session.getPSMethodInstance(PSMethodCostBenefit.class);
		if (costBenefit == null) return;
		CostBenefitCaseObject cbCaseObject = session.getSessionObject(costBenefit);
		QContainer qcontainer = cbCaseObject.getCurrentQContainer();
		StateTransitionSessionObject sessionObject = session.getSessionObject(this);
		for (QContainer qcon : answeredQuestionnaires) {
			if (CostBenefitUtil.isDone(qcon, session)) {
				// if the qcontainer is the actual qcontainer of the sequence,
				// fire its transitions
				boolean fire = false;
				if (qcon == qcontainer) {
					fire = true;
				}
				// if the qcontainer was the last qcontainer fired, retract it's
				// facts and fire the qcontainer again
				else if (sessionObject.qContainer == qcon) {
					// remove the old facts
					for (Fact fact : sessionObject.facts) {
						session.getBlackboard().removeValueFact(fact);
					}
					fire = true;
				}
				if (fire) {
					KnowledgeSlice ks = qcon.getKnowledgeStore().getKnowledge(
							StateTransition.KNOWLEDGE_KIND);
					if (ks != null) {
						StateTransition st = (StateTransition) ks;
						sessionObject.facts = st.fire(session);
						// check if there are any changes to our remembered solutions
						if (cbCaseObject.hasChangedUndiscriminatedSolutions()) {
							cbCaseObject.resetPath();
							return;
						}
						else {
							cbCaseObject.activateNextQContainer();
						}
						for (Fact fact : cbCaseObject.getIndicatedFacts()) {
							if (fact.getTerminologyObject() == st.getQcontainer()) {
								session.getBlackboard().removeInterviewFact(fact);
							}
						}
						sessionObject.qContainer = qcon;
					}
				}
			}
		}
	}

	@Override
	public boolean hasType(Type type) {
		return type == Type.source;
	}

	@Override
	public double getPriority() {
		return 5.5;
	}

	public static class StateTransitionSessionObject implements SessionObject {

		private List<Fact> facts = new LinkedList<>();
		private QContainer qContainer = null;
	}

	@Override
	public StateTransitionSessionObject createSessionObject(Session session) {
		return new StateTransitionSessionObject();
	}

	@Override
	public Set<TerminologyObject> getPotentialDerivationSources(TerminologyObject derivedObject) {
		// get all objects from all conditional value setter's conditions
		// that match on the requested object
		// due to not caching the StateTransitions, this method tends to be
		// slow...
		if (!(derivedObject instanceof Question)) return Collections.emptySet();
		Set<TerminologyObject> result = new HashSet<>();
		Collection<StateTransition> transitions = derivedObject.getKnowledgeBase()
				.getAllKnowledgeSlicesFor(
						StateTransition.KNOWLEDGE_KIND);
		for (StateTransition stateTransition : transitions) {
			List<ValueTransition> postTransitions = stateTransition.getPostTransitions();
			for (ValueTransition valueTransition : postTransitions) {
				if (valueTransition.getQuestion().equals(derivedObject)) {
					List<ConditionalValueSetter> setters = valueTransition.getSetters();
					for (ConditionalValueSetter setter : setters) {
						Condition condition = setter.getCondition();
						if (condition != null) {
							result.addAll(condition.getTerminalObjects());
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	public Set<TerminologyObject> getActiveDerivationSources(TerminologyObject derivedObject, Session session) {
		// we get the value setter responsible for the fact
		// and return the objects used in its conditon
		Fact fact = session.getBlackboard().getValueFact(derivedObject, this);
		if (fact instanceof StateTransitionFact) {
			Condition condition = ((StateTransitionFact) fact).cvs.getCondition();
			if (condition != null) {
				return new HashSet<>(condition.getTerminalObjects());
			}
		}
		return Collections.emptySet();
	}
}
