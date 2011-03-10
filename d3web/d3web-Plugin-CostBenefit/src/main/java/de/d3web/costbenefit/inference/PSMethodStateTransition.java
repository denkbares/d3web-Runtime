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

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.indication.inference.PSMethodUserSelected;

public final class PSMethodStateTransition extends PSMethodUserSelected {

	private static PSMethodStateTransition instance;

	private PSMethodStateTransition() {

	}

	public static PSMethodStateTransition getInstance() {
		if (instance == null) {
			instance = new PSMethodStateTransition();
		}
		return instance;
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		StateTransitionFact max = null;
		for (Fact fact : facts) {
			StateTransitionFact stf = (StateTransitionFact) fact;
			if (max == null || max.number < stf.number) {
				max = stf;
			}
		}
		return max;
	}

	public static class StateTransitionFact extends DefaultFact {

		public static int counter = 0;
		private final int number;

		public StateTransitionFact(TerminologyObject terminologyObject, Value value) {
			super(terminologyObject, value, new Object(), PSMethodStateTransition.getInstance());
			number = counter++;
		}

	}

}
