/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.core.inference;

import java.util.Collection;

import de.d3web.core.session.Session;

/**
 * Interface for classes, which want to get notified about the different
 * propagation states
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 27.03.2012
 */
public interface PropagationListener {

	/**
	 * Informs this listener that a new propagation has been started. This is
	 * called right before the problem solvers getting activated sequentially.
	 * 
	 * @param session the session the propagation is started on
	 * @param entries the initial changes to be propagated
	 */
	void propagationStarted(Session session, Collection<PropagationEntry> entries);

	/**
	 * Informs this listener that a post-propagation has been started. Please
	 * note this might occur multiple times, if the post-propagation itself
	 * creates new facts that might be propagated again (and therefore followed
	 * by a newly post-propagation).
	 * 
	 * @param session the session the propagation is started on
	 * @param entries the changes propagated so far, since propagation started
	 *        or last post-propagation
	 */
	void postPropagationStarted(Session session, Collection<PropagationEntry> entries);

	/**
	 * Informs this listener that the propagation has been finished. This method
	 * is called right after all propagation has been finished. Please note that
	 * this method is called regardless if the propagation has finished
	 * successfully or has been externally terminated.
	 * 
	 * @param session the session the propagation is performed on
	 * @param entries all the changes propagated in this propagation
	 */
	void propagationFinished(Session session, Collection<PropagationEntry> entries);

	/**
	 * Informs this listener that some facts are going to be propagated by a
	 * specific problem solver. This method is called very frequently during
	 * propagation, at least once per problem solver, usually more often. After
	 * the propagation has been finished, each change will be propagated by each
	 * problem solver.
	 * 
	 * @created 14.02.2013
	 * @param session the session to perform the propagation on
	 * @param psMethod the problem solver to be activated
	 * @param entries the current entries to be propagated by the specified
	 *        solver at once
	 */
	void propagating(Session session, PSMethod psMethod, Collection<PropagationEntry> entries);

}
