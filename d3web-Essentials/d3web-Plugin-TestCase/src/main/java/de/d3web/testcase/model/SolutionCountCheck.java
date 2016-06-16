package de.d3web.testcase.model;

import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.session.Session;

/**
 * A check to assure the count of solutions with the given state is as expected.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 16.06.16
 */
public class SolutionCountCheck implements Check {

	private final Rating.State state;
	private final int count;

	public SolutionCountCheck(Rating.State state, int count) {
		this.state = state;
		this.count = count;
	}

	@Override
	public boolean check(Session session) {
		return session.getBlackboard().getSolutions(state).size() == count;
	}

	@Override
	public String getCondition() {
		return "Count of Solutions with state " + state + " = " + count;
	}
}
