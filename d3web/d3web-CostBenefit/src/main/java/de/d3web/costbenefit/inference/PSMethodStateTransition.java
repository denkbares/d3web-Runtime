package de.d3web.costbenefit.inference;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.indication.inference.PSMethodUserSelected;

public class PSMethodStateTransition extends PSMethodUserSelected {

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
		private int number;

		public StateTransitionFact(TerminologyObject terminologyObject, Value value) {
			super(terminologyObject, value, new Object(), PSMethodStateTransition.getInstance());
			number = counter++;
		}

	}

}
