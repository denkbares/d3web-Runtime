/*
 * Copyright (C) 2009 denkbares GmbH
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

import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.costbenefit.inference.DefaultAbortStrategy.DefaultAbortStrategySessionObject;
import de.d3web.costbenefit.model.Path;
import de.d3web.costbenefit.model.SearchModel;

/**
 * The DefaultAbortyStrategy throws an AbortException, when the maximum amount
 * of steps is exceeded and at least one target is reached.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class DefaultAbortStrategy implements AbortStrategy, SessionObjectSource<DefaultAbortStrategySessionObject> {

	private final long maxsteps;

	public long getMaxsteps() {
		return maxsteps;
	}

	@Override
	public void init(SearchModel model) {
		DefaultAbortStrategySessionObject sessionObject = model.getSession().getSessionObject(this);
		sessionObject.steps = 0;
		sessionObject.model = model;
	}

	@Override
	public void nextStep(Path path, Session session) throws AbortException {
		DefaultAbortStrategySessionObject sessionObject = session.getSessionObject(this);
		sessionObject.steps++;
		if (check(sessionObject)) {
			throw new AbortException();
		}

	}

	private boolean check(DefaultAbortStrategySessionObject sessionObject) {
		return (sessionObject.steps >= maxsteps && sessionObject.model.oneTargetReached())
				|| (sessionObject.steps >= maxsteps * 10);
	}

	public DefaultAbortStrategy(long steps) {
		maxsteps = steps;
	}

	public DefaultAbortStrategy() {
		// about 1,4 sec on my laptop ;-)
		this(100000);
	}

	@Override
	public DefaultAbortStrategySessionObject createSessionObject(Session session) {
		return new DefaultAbortStrategySessionObject();
	}

	public static class DefaultAbortStrategySessionObject implements SessionObject {

		private long steps;
		private SearchModel model;

		public long getSteps() {
			return steps;
		}
	}
}
