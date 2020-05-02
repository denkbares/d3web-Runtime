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
import de.d3web.costbenefit.model.Path;
import de.d3web.costbenefit.model.SearchModel;

/**
 * Strategy which decides, when a calculation should be aborted or not.
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public interface AbortStrategy {

	/**
	 * Initializes the abortion strategy with a SearchModel. This is called each time before the search is executed.
	 *
	 * @param model the search model to prepare the abort strategy for
	 */
	void init(SearchModel model);

	/**
	 * This method decides if the path search should be aborted, even if (potentially) the best path is not found yet.
	 * The selected abort strategy if the search model is notified with each test steps that is expanded, so the
	 * AbortStrategy, can stop the calculation by throwing an {@link AbortException}, depending on the current state of
	 * the path and session.
	 *
	 * @param path    the path that has currently been expanded
	 * @param session the original session where the search is performed on
	 * @throws AbortException if the calculation should be stopped
	 */
	void nextStep(Path path, Session session) throws AbortException;
}
