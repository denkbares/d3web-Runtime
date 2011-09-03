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

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.indication.inference.PSMethodUserSelected;

public final class PSMethodStateTransition extends PSMethodUserSelected {

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
	public double getPriority() {
		// high priority (but not higher that the user itself)
		return 1;
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
			if (psm == null) {
				return psmInstance;
			}
			return psm;
		}

	}

}
