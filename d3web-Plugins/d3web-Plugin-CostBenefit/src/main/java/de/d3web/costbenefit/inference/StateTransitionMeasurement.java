/*
 * Copyright (C) 2017 denkbares GmbH. All rights reserved.
 */

package de.d3web.costbenefit.inference;

import java.util.Map;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.interview.measure.Measurement;

/**
 * Special implementation of {@link de.d3web.interview.measure.Measurement} that creates it's facts
 * as state transitions to smoothly interact with other cost/benefit state transitions. By using
 * this measurement implementation, the state transitions will overwrite measured states and vica
 * verce.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 26.01.2017
 */
public class StateTransitionMeasurement extends Measurement {
	public StateTransitionMeasurement(String identifier, Map<String, String> mapping, Condition startCondition, Condition stopCondition) {
		super(identifier, mapping, startCondition, stopCondition);
	}

	public StateTransitionMeasurement(Measurement other) {
		this(other.getIdentifier(), other.getMapping(), other.getStartCondition(), other.getStopCondition());
	}

	@Override
	protected Fact addFact(Session session, Question question, Value value) {
		Fact fact = new PSMethodStateTransition.StateTransitionFact(session, question, value);
		addFactToSession(session, fact);
		return fact;
	}

	@Override
	protected void removeFact(Session session, Question question) {
		// do nothing; state transitions cannot be removed
	}

	@Override
	protected boolean isAnswered(Session session, Question question) {
		// state transitions are never assumed to be answered,
		// they can be measured every time, scanning for further changes
		return false;
	}
}
