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
package de.d3web.costbenefit.ids;

import org.jetbrains.annotations.NotNull;

import de.d3web.core.session.Session;
import de.d3web.costbenefit.inference.AbortStrategy;
import de.d3web.costbenefit.inference.DefaultAbortStrategy;
import de.d3web.costbenefit.inference.SearchAlgorithm;
import de.d3web.costbenefit.model.SearchModel;

/**
 * Encapsulates the call of a new IterativeDeepeningSearch search. For each call a new instance of the
 * IterativeDeepeningSearch is created.
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class IterativeDeepeningSearchAlgorithm implements SearchAlgorithm {

	private AbortStrategy abortStrategy = new DefaultAbortStrategy();

	@Override
	public void search(Session session, SearchModel model) {
		new IterativeDeepeningSearch(model, abortStrategy).search(session);
	}

	@NotNull
	@Override
	public AbortStrategy getAbortStrategy() {
		return abortStrategy;
	}

	@Override
	public void setAbortStrategy(AbortStrategy abortStrategy) {
		this.abortStrategy = abortStrategy;
	}
}
