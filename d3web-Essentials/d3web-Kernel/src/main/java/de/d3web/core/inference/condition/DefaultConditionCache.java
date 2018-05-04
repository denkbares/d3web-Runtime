/*
 * Copyright (C) 2018 denkbares GmbH. All rights reserved.
 */

package de.d3web.core.inference.condition;

import java.util.HashMap;
import java.util.Map;

import de.d3web.core.session.Session;
import de.d3web.core.session.values.Unknown;

/**
 * Utility class for faster evaluating conditions multiple times. An instance of this class can only be used as long as
 * the blackboard of the session does not change.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 04.05.2018
 */
public class DefaultConditionCache implements ConditionCache {

	private final Session session;
	private final Map<Condition, ConditionResult> cache = new HashMap<>();

	public DefaultConditionCache(Session session) {
		this.session = session;
	}

	@Override
	public Session getSession() {
		return session;
	}

	/**
	 * Evaluates the specified condition with respect to the findings given in the {@link Session} of this instance. The
	 * condition may be evaluated or a cached value may be used.
	 *
	 * @param condition the condition to be evaluated
	 * @return true/false for positive/negative evaluation; an appropriate {@link Exception} otherwise
	 * @throws NoAnswerException      when a required sub-condition of this condition has a question with no answer
	 *                                currently set
	 * @throws UnknownAnswerException when a required sub-conditions contains a question having an {@link Unknown}
	 *                                assigned
	 */
	@Override
	public boolean eval(Condition condition) throws NoAnswerException, UnknownAnswerException {
		switch (getResult(condition)) {
			case FALSE:
				return false;
			case TRUE:
				return true;
			case UNDEFINED:
				throw NoAnswerException.getInstance();
			case UNKNOWN:
				throw UnknownAnswerException.getInstance();
		}
		throw new IllegalStateException();
	}

	/**
	 * Evaluates the specified condition with respect to the findings given in the {@link Session} of this instance. The
	 * condition may be evaluated or a cached value may be used.
	 *
	 * @param condition the condition to be evaluated
	 * @return The resulting value of the condition
	 */
	@Override
	public ConditionResult getResult(Condition condition) {
		return cache.computeIfAbsent(condition, this::evalToResult);
	}

	protected ConditionResult evalToResult(Condition condition) {
		try {
			return condition.eval(session) ? ConditionResult.TRUE : ConditionResult.FALSE;
		}
		catch (NoAnswerException e) {
			return ConditionResult.UNDEFINED;
		}
		catch (UnknownAnswerException e) {
			return ConditionResult.UNKNOWN;
		}
	}

	protected void removeResult(Condition condition) {
		cache.remove(condition);
	}
}
