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

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.inference.ConditionTrue;

/**
 * Abstract Implementation of Node.
 * 
 * Provides basic implementations of the methods
 * 
 * @author Reinhard Hatko
 * @created 08.08.2009
 */
public abstract class AbstractNode implements Node {

	private final List<Edge> outgoing;
	private final List<Edge> incoming;
	private final String id;
	private Flow flow;
	private final String name;

	public AbstractNode(String id, String name) {

		this.id = id;
		this.outgoing = new ArrayList<Edge>();
		this.incoming = new ArrayList<Edge>();
		this.name = name;
	}

	protected boolean addOutgoingEdge(Edge edge) {
		if (edge == null) {
			throw new IllegalArgumentException("edge must not be null");
		}

		if (edge.getStartNode() != this) {
			throw new IllegalArgumentException("edge '" + edge + "' does not start at: "
					+ this.toString());
		}

		return outgoing.add(edge);

	}

	protected boolean addIncomingEdge(Edge edge) {
		if (edge == null) {
			throw new IllegalArgumentException("edge must not be null");
		}

		if (edge.getEndNode() != this) {
			throw new IllegalArgumentException("edge '" + edge + "' does not end at: "
					+ this.toString());
		}

		return incoming.add(edge);

	}

	@Override
	public final List<Edge> getOutgoingEdges() {
		return Collections.unmodifiableList(outgoing);
	}

	@Override
	public List<Edge> getIncomingEdges() {
		return Collections.unmodifiableList(incoming);
	}

	@Override
	public Flow getFlow() {
		return flow;
	}

	@Override
	public void setFlow(Flow flow) {
		if (flow == null) {
			throw new IllegalArgumentException("Flow must not be null");
		}

		this.flow = flow;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public void execute(Session session, FlowRun run) {

	}

	@Override
	public void retract(Session session, FlowRun run) {

	}

	@Override
	public List<? extends TerminologyObject> getHookedObjects() {
		return Collections.emptyList();
	}

	@Override
	public Condition getEdgePrecondition() {
		return ConditionTrue.INSTANCE;
	}

	@Override
	public void takeSnapshot(Session session, SnapshotNode snapshotNode) {

	}

	@Override
	public boolean isReevaluate(Session session) {
		return false;
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

		AbstractNode other = (AbstractNode) obj;
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
		String nameString = getClass().getSimpleName() + "[" + getID() + ", " + getName() + "]";
		if (flow != null) {
			nameString += " in " + flow.getName();

		}

		return nameString;
	}

}
