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
package de.d3web.costBenefit.inference;

import de.d3web.costBenefit.model.Path;
import de.d3web.costBenefit.model.SearchModel;

/**
 * The DefaultAbortyStrategy throws an AbortException, when the maximum amount
 * of steps is exceeded and at least one target is reached.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class DefaultAbortStrategy implements AbortStrategy {

	private long steps;

	private long maxsteps;

	public long getMaxsteps() {
		return maxsteps;
	}

	private SearchModel model;

	@Override
	public void init(SearchModel model) {
		steps = 0;
		this.model = model;
	}

	@Override
	public void nextStep(Path path) throws AbortException {
		steps++;
		if ((steps >= maxsteps && model.oneTargetReached()) || (steps >= maxsteps * 10)) {
			throw new AbortException();
		}

	}

	public DefaultAbortStrategy(long steps) {
		maxsteps = steps;
	}

	public DefaultAbortStrategy() {
		// about 1,4 sec on my laptop ;-)
		this(1000000000);
	}

}
