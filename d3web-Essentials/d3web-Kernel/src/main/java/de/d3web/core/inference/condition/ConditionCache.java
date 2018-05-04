/*
 * Copyright (C) 2018 denkbares GmbH. All rights reserved.
 */

package de.d3web.core.inference.condition;

import de.d3web.core.session.Session;

/**
 * @author Volker Belli (denkbares GmbH)
 * @created 04.05.2018
 */
public interface ConditionCache {
	Session getSession();

	boolean eval(Condition condition) throws NoAnswerException, UnknownAnswerException;

	ConditionResult getResult(Condition condition);

	enum ConditionResult {
		FALSE, TRUE, UNDEFINED, UNKNOWN
	}
}
