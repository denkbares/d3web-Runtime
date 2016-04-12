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

	private final int maxSteps;
	private final float increasingFactor;

	/**
	 * Creates a new {@link DefaultAbortStrategy} with the given parameters
	 * 
	 * @param steps if the calculation has reached a target, it will be aborted
	 *        after this amount of steps
	 * @param increasingFactor if not target is found, the calculation is
	 *        aborted after increasingFactor*steps
	 */
	public DefaultAbortStrategy(int steps, float increasingFactor) {
		maxSteps = steps;
		this.increasingFactor = increasingFactor;
	}

	/**
	 * Creates a new {@link DefaultAbortStrategy} with maximum steps of 100000
	 * and an increasing factor of 10.
	 * 
	 * @see #getMaxSteps()
	 * @see #getIncreasingFactor()
	 */
	public DefaultAbortStrategy() {
		// about 1,4 sec using IDS on Markus first laptop (RIP) ;-)
		this(100000, 10);
	}

	/**
	 * Creates a new {@link DefaultAbortStrategy} with the specified maximum
	 * steps and an increasing factor of 10.
	 * 
	 * @param maxSteps if the calculation has reached a target, it will be
	 *        aborted after this amount of steps
	 * 
	 * @see #getIncreasingFactor()
	 */
	public DefaultAbortStrategy(int maxSteps) {
		this(maxSteps, 10);
	}

	/**
	 * Returns the maximum number of steps searched until the best path should
	 * be used without further searching.
	 * 
	 * @created 29.08.2011
	 * @return maximum number of steps
	 */
	public int getMaxSteps() {
		return maxSteps;
	}

	/**
	 * Returns the factor, the maximum number of steps can be exceeded, if
	 * searched has not found any path yet.
	 * 
	 * @created 29.08.2011
	 * @return factor of exceeding maxSteps in worst case
	 */
	public float getIncreasingFactor() {
		return increasingFactor;
	}

	@Override
	public void init(SearchModel model) {
		DefaultAbortStrategySessionObject sessionObject = model.getSession().getSessionObject(this);
		sessionObject.steps = 0;
		sessionObject.model = model;
		sessionObject.abort = false;
	}

	@Override
	public void nextStep(Path path, Session session) throws AbortException {
		DefaultAbortStrategySessionObject sessionObject = session.getSessionObject(this);
		sessionObject.steps++;
		if (isAbortReached(sessionObject)) {
			throw new AbortException();
		}
	}

	private boolean isAbortReached(DefaultAbortStrategySessionObject sessionObject) {
		return sessionObject.abort
				|| (sessionObject.steps >= maxSteps && sessionObject.model.isAnyTargetReached())
				|| (sessionObject.steps >= maxSteps * increasingFactor);
	}

	/**
	 * If this method is called, the calculation will abort after the next step
	 * 
	 * @created 23.02.2012
	 * @param session Session in which the calculation should be aborted
	 */
	public void abort(Session session) {
		DefaultAbortStrategySessionObject sessionObject = session.getSessionObject(this);
		sessionObject.abort = true;
	}

	/**
	 * Returns the steps of the actual calculation in the specified session. If
	 * there is no active calculation, the steps of the last calculation are
	 * returned.
	 * 
	 * @created 23.02.2012
	 * @param session the specified Session
	 * @return The actual amount of steps done
	 */
	public long getSteps(Session session) {
		return session.getSessionObject(this).steps;
	}

	/**
	 * Checks if at least one target is reached in the current calculation. If
	 * there is no active calculation, the result of the last calculation is
	 * returned.
	 * 
	 * @created 23.02.2012
	 * @param session
	 * @return
	 */
	public boolean isAnyTargetReached(Session session) {
		DefaultAbortStrategySessionObject sessionObject = session.getSessionObject(this);
		if (sessionObject.model != null) {
			return sessionObject.model.isAnyTargetReached();
		}
		else {
			return false;
		}
	}

	@Override
	public DefaultAbortStrategySessionObject createSessionObject(Session session) {
		return new DefaultAbortStrategySessionObject();
	}

	public static class DefaultAbortStrategySessionObject implements SessionObject {

		private long steps;
		private SearchModel model;
		private boolean abort;
	}
}
