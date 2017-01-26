/*
 * Copyright (C) 2017 denkbares GmbH. All rights reserved.
 */

package de.d3web.core.knowledge.terminology.info.abnormality;

import de.d3web.core.session.Session;

/**
 * A special subset of abnormalities that have dynamic, session-dependent values. They can provide a
 * "static" abnormality based on a current session. If the session changes, a previously returned
 * static abnormality may not be updated automatically.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 26.01.2017
 */
public interface DynamicAbnormality extends Abnormality {
	/**
	 * Returns a static abnormality based on this dynamic abnormality and the specified session to
	 * calculate the dynamic values from. If the session changes, a Abnormality previously returned
	 * by this method may not change accordingly. Instead, you have to re-evaluate the method for
	 * the session.
	 *
	 * @param session the session to calculate the static abnormality for
	 * @return the static abnormality
	 */
	Abnormality eval(Session session);
}
