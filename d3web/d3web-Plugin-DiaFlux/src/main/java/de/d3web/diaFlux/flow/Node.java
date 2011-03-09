/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

/**
 *
 */
package de.d3web.diaFlux.flow;

import java.util.List;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;

/**
 * @author Reinhard Hatko
 * 
 */
public interface Node {

	/**
	 * 
	 * @return s a list of this node's outgoing edges.
	 */
	List<Edge> getOutgoingEdges();

	/**
	 * 
	 * @return s a list of this node's incoming edges.
	 */
	List<Edge> getIncomingEdges();

	/**
	 * @return s the id of the node
	 */
	String getID();

	/**
	 * @return s the flow this node belongs to
	 */
	Flow getFlow();

	/**
	 * sets this nodes containing flow
	 */
	void setFlow(Flow flow);

	/**
	 * 
	 * 
	 * @return s the name of the node
	 */
	String getName();

	/**
	 * Returns the collection of questions and solutions, that the node is
	 * interested in receiving notifications of. This list can usually be empty,
	 * as the conditions on outgoing edges are checked against all changes.
	 * Certain types (like formula nodes) but have to receive state changes of
	 * their own.
	 * 
	 * 
	 * @return s the list of questions and diagnosis, this node wants to be
	 *         notified of.
	 */
	List<? extends TerminologyObject> getHookedObjects();

	/**
	 * Does the action that is associated with this node.
	 * 
	 * @param session the session
	 * @param run TODO
	 */
	void execute(Session session, FlowRun run);

	/**
	 * Undoes the action that is associated with this node.
	 * 
	 * @param session the session
	 * @param run TODO
	 */
	void retract(Session session, FlowRun run);

	/**
	 * This method is called during a snapshot. It can carry out node specific
	 * actions.
	 * 
	 * @created 12.11.2010
	 * @param session the current session
	 */
	void takeSnapshot(Session session);

	/**
	 * Returns if the node should be re-evaluated on changes in its forward
	 * knowledge. Nodes containing e.g. calculations have to return true
	 * 
	 * @created 10.12.2010
	 * @param session
	 * @return true if the node should be reevaluated
	 */
	boolean isReevaluate(Session session);

	/**
	 * A node can specify a special condition that has to be true, before its
	 * outgoing edges can fire.
	 * 
	 * 
	 * @created 02.03.2011
	 * @return a Condition
	 */
	Condition getEdgePrecondition();

}
