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
import java.util.HashSet;
import java.util.Set;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PSMethodAdapter;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.costbenefit.Util;

public final class PSMethodStateTransition extends PSMethodAdapter {

	public PSMethodStateTransition() {
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		// remember: if a session is loaded from a record,
		// source psm max have DefaultFacts instead of their one ones
		Fact maxFact = null;
		int maxNumber = Integer.MIN_VALUE;
		for (Fact fact : facts) {
			// use the first one (also works for DefaultFacts)
			if (maxFact == null) {
				maxFact = fact;
			}
			// and overwrite existing max if it is
			// a normal fact or if the current one on newer
			else if (fact instanceof StateTransitionFact) {
				StateTransitionFact stf = (StateTransitionFact) fact;
				if (maxNumber < stf.number) {
					maxFact = stf;
					maxNumber = stf.number;
				}
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

		public StateTransitionFact(Session session, TerminologyObject terminologyObject, Value value) {
			super(terminologyObject, value, new Object(), findPSM(session));
			number = counter++;
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
		Set<QContainer> answeredQuestionnaires = new HashSet<QContainer>();
		for (PropagationEntry entry : changes) {
			TerminologyObject object = entry.getObject();
			if (!entry.isStrategic() && entry.hasChanged() && object instanceof Question) {
				Util.addParentContainers(answeredQuestionnaires, object);
			}
		}
		for (QContainer qcon : answeredQuestionnaires) {
			if (Util.isDone(qcon, session)) {
				// mark is the questionnaire is either indicated or in our
				// current sequence
				KnowledgeSlice ks = qcon.getKnowledgeStore().getKnowledge(
						StateTransition.KNOWLEDGE_KIND);
				if (ks != null) {
					StateTransition st = (StateTransition) ks;
					st.fire(session);
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
		return 1;
	}

}
