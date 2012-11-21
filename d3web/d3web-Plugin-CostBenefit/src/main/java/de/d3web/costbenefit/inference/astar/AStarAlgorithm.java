/*
 * Copyright (C) 2011 denkbares GmbH
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
package de.d3web.costbenefit.inference.astar;

import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.costbenefit.inference.AbortStrategy;
import de.d3web.costbenefit.inference.DefaultAbortStrategy;
import de.d3web.costbenefit.inference.SearchAlgorithm;
import de.d3web.costbenefit.inference.astar.AStarAlgorithm.AStarSessionObject;
import de.d3web.costbenefit.model.SearchModel;

/**
 * Calculates pathes to the targets in a SearchModel by using the A*-Algorithm
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 22.06.2011
 */
public class AStarAlgorithm implements SearchAlgorithm, SessionObjectSource<AStarSessionObject> {

	private Heuristic heuristic = new TPHeuristic();
	private AbortStrategy abortStrategy = new DefaultAbortStrategy(500000, 4);
	private boolean multiCore = true;

	@Override
	public void search(Session session, SearchModel model) {
		AStar lastSearch = new AStar(session, model, this);
		session.getSessionObject(this).lastSearch = lastSearch;
		lastSearch.search();
	}

	public Heuristic getHeuristic() {
		return heuristic;
	}

	public void setHeuristic(Heuristic heuristic) {
		this.heuristic = heuristic;
	}

	public AbortStrategy getAbortStrategy() {
		return abortStrategy;
	}

	public void setAbortStrategy(AbortStrategy abortStrategy) {
		this.abortStrategy = abortStrategy;
	}

	public void setMultiCore(boolean multiCore) {
		this.multiCore = multiCore;
	}

	public boolean isMultiCore() {
		return multiCore;
	}

	/**
	 * Returns an explanation component of the last search started. If there was
	 * no search yet, null is returned
	 * 
	 * @created 03.07.2012
	 * @return {@link AStarExplanationComponent}
	 */
	public AStarExplanationComponent getExplanationComponent(Session session) {
		AStar lastSearch = session.getSessionObject(this).lastSearch;
		if (lastSearch == null) {
			return null;
		}
		return new AStarExplanationComponent(lastSearch);
	}

	@Override
	public AStarSessionObject createSessionObject(Session session) {
		return new AStarSessionObject();
	}

	/**
	 * Holds a reference to the last calculation of a session
	 * 
	 * @author Markus Friedrich (denkbares GmbH)
	 * @created 21.11.2012
	 */
	public class AStarSessionObject implements SessionObject {

		private AStar lastSearch;
	}

}
