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

package de.d3web.diaFlux.flow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.d3web.core.session.CaseObjectSource;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.diaFlux.inference.DiaFluxUtils;

/**
 *
 *
 * @author Reinhard Hatko
 * @created 08.08.2009
 */
public abstract class Node implements INode, CaseObjectSource {

	protected final List<IEdge> outgoing;
	protected final List<IEdge> incoming;
	protected final String id;
	protected Flow flow;
	protected String name;

	public Node(String id, String name) {

		this.id = id;
		this.outgoing = new ArrayList<IEdge>();
		this.incoming = new ArrayList<IEdge>();
		this.name = name;
	}


	protected boolean addOutgoingEdge(IEdge edge) {
		if (edge == null) throw new IllegalArgumentException("edge must not be null");

		if (edge.getStartNode() != this) throw new IllegalArgumentException("edge '" + edge
				+ "' does not start at: " + this.toString());

		return outgoing.add(edge);

	}

	protected boolean addIncomingEdge(IEdge edge) {
		if (edge == null) throw new IllegalArgumentException("edge must not be null");

		if (edge.getEndNode() != this) throw new IllegalArgumentException("edge '" + edge
				+ "' does not end at: " + this.toString());

		return incoming.add(edge);

	}

	@Override
	public final List<IEdge> getOutgoingEdges() {
		return Collections.unmodifiableList(outgoing);
	}

	@Override
	public List<IEdge> getIncomingEdges() {
		return Collections.unmodifiableList(incoming);
	}

	@Override
	public Flow getFlow() {
		return flow;
	}

	@Override
	public void setFlow(Flow flow) {
		this.flow = flow;
	}

	@Override
	public String getName() {
		return name;
	}



	@Override
	public SessionObject createCaseObject(Session session) {
		return new NodeData(this);
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public void doAction(Session session) {

	}

	@Override
	public void undoAction(Session session) {

	}

	@Override
	public void takeSnapshot(Session session, SnapshotNode snapshotNode) {

		resetNodeData(session);

	}

	/**
	 * Deactivates this node. Removes all support and resets all incoming edges.
	 *
	 * @param session
	 */
	protected void resetNodeData(Session session) {

		INodeData nodeData = DiaFluxUtils.getNodeData(this, session);

		for (ISupport support : nodeData.getSupports()) {
			nodeData.removeSupport(support);
		}


		for (IEdge edge : getIncomingEdges()) {

			EdgeData edgeData = DiaFluxUtils.getEdgeData(edge, session);

			if (edgeData.hasFired()) {
				edgeData.setHasFired(false);
			}
		}

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((flow == null) ? 0 : flow.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;

		if (getClass() != obj.getClass()) return false;

		Node other = (Node) obj;
		if (flow == null) {
			if (other.flow != null) return false;
		}
		else if (!flow.equals(other.flow)) return false;
		if (id == null) {
			if (other.id != null) return false;
		}
		else if (!id.equals(other.id)) return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getID() + ", " + getName() + "]";
	}

}
