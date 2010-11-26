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

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.CaseObjectSource;
import de.d3web.core.session.Session;

/**
 * @author Reinhard Hatko
 *
 */
public interface INode extends CaseObjectSource {

	/**
	 *
	 * @return s a list of this node's outgoing edges.
	 */
	List<IEdge> getOutgoingEdges();

	/**
	 *
	 * @return s a list of this node's incoming edges.
	 */
	List<IEdge> getIncomingEdges();

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
	 * Returns the collection of {@link Question} and {@link Solution}
	 * instances, that the node is interested in receiving notifications of.
	 * This list can usually be empty, as the conditions on outgoing edges are
	 * checked against all changes. Certain types (like formula nodes) but have
	 * to receive state changes of their own.
	 * 
	 * 
	 * @return s the list of questions and diagnosis, this node wants to be
	 *         notified of.
	 */
	List<? extends TerminologyObject> getForwardKnowledge();


	/**
	 * Does the action that is associated with this node.
	 *
	 * @param session
	 */
	void doAction(Session session);

	/**
	 * Undoes the action that is associated with this node.
	 *
	 * @param session
	 */
	void undoAction(Session session);

	/**
	 * This method returns if this node should be activated. Usually a node
	 * should only be activated each time its status changes from unsupported to
	 * supported. But there are exceptions, e.g. SnapshotNodes.
	 * 
	 * As the result of this methods usually depends on the support of a node,
	 * it must be called before changing a nodes support.
	 * 
	 * @created 12.11.2010
	 * @param session
	 * @return true if the node should be activated, false otherwise
	 */
	boolean couldActivate(Session session);

	/**
	 * This method returns if the outgoing edges of this node can be fired.
	 * 
	 * @created 25.11.2010
	 * @param session
	 * @return
	 */
	boolean canFireEdges(Session session);

	/**
	 * This method is called during a snapshot. It has to reset this node (TODO
	 * xplain) and can carry out node specific actions.
	 * 
	 * @created 12.11.2010
	 * @param session the current session
	 * @param snapshotNode the snapshot node that started this snapshot
	 * @param nodes the list of nodes snapshotted so far
	 */
	void takeSnapshot(Session session, SnapshotNode snapshotNode, List<INode> nodes);

}
