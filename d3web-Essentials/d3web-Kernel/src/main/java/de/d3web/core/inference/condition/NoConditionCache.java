/*
 * Copyright (C) 2018 denkbares GmbH. All rights reserved.
 */

package de.d3web.core.inference.condition;

import de.d3web.core.session.Session;

/**
 * Implementation that does no caching at all.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 04.05.2018
 */
public class NoConditionCache implements ConditionCache {
	private final Session session;

	public NoConditionCache(Session session) {
		this.session = session;
	}

	@Override
	public Session getSession() {
		return session;
	}

	@Override
	public boolean eval(Condition condition) throws NoAnswerException, UnknownAnswerException {
		return condition.eval(session);
	}

	@Override
	public ConditionResult getResult(Condition condition) {
		try {
			return eval(condition) ? ConditionResult.TRUE : ConditionResult.FALSE;
		}
		catch (NoAnswerException e) {
			return ConditionResult.UNDEFINED;
		}
		catch (UnknownAnswerException e) {
			return ConditionResult.UNKNOWN;
		}
	}
}
