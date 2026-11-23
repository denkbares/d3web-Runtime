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

import org.jetbrains.annotations.NotNull;

import de.d3web.core.session.Session;
import de.d3web.costbenefit.model.SearchModel;

/**
 * This Interface provides a method to start a search in a model. The targets, the nodes etc. are stored in the model.
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public interface SearchAlgorithm {

	/**
	 * Starts a search for the targets of the model. The result is stored in the model.
	 *
	 * @param session the session to calculate the path(s) for
	 * @param model   the initialized search model to work on
	 */
	void search(Session session, SearchModel model);

	/**
	 * Returns the abort strategy that is used for searching with this search algorithm
	 *
	 * @return the abort strategy
	 */
	@NotNull
	AbortStrategy getAbortStrategy();

	/**
	 * Sets the specified abort strategy to be used for searching with this algorithm.
	 *
	 * @param abortStrategy the new abort strategy to be set
	 */
	void setAbortStrategy(AbortStrategy abortStrategy);

	/**
	 * Returns true if the most recent search of this algorithm for the session has been performed on the specified
	 * model, has been aborted (e.g. by its abort strategy), and can be continued, see {@link #continueSearch(Session,
	 * SearchModel)}. By default, a search cannot be continued.
	 *
	 * @param session the session of the search
	 * @param model   the search model of the search
	 * @return true if the search can be continued
	 */
	default boolean canContinue(Session session, SearchModel model) {
		return false;
	}

	/**
	 * Continues the most recent, aborted search for the targets of the model, using the current abort strategy of this
	 * algorithm. The result is stored in the model, as for a new search. It must only be called if {@link
	 * #canContinue(Session, SearchModel)} returns true.
	 *
	 * @param session the session to continue the search for
	 * @param model   the search model of the search to be continued
	 */
	default void continueSearch(Session session, SearchModel model) {
		throw new UnsupportedOperationException("search algorithm " + getClass().getSimpleName()
												+ " cannot continue a search");
	}
}
