package de.d3web.testcase.model;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Rating;

/**
 * A template to check to assure the count of solutions with the given state is as expected.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 16.06.16
 */
public class SolutionCountCheckTemplate implements CheckTemplate {

	private final Rating.State state;
	private final int count;

	public SolutionCountCheckTemplate(@NotNull Rating.State state, int count) {
		Objects.requireNonNull(state);
		if (count < 0) {
			throw new IllegalArgumentException("Count of solutions with state " + state + " cannot be negative (was " + count + ").");
		}
		this.state = state;
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public Rating.State getState() {
		return state;
	}

	@Override
	public Check toCheck(KnowledgeBase knowledgeBase) throws TransformationException {
		return new SolutionCountCheck(state, count);
	}
}
