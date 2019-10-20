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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.denkbares.collections.MinimizedLinkedHashSet;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.ConditionTrue;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;

/**
 * Abstract Implementation of Node.
 * <p>
 * Provides basic implementations of the methods
 *
 * @author Reinhard Hatko
 * @created 08.08.2009
 */
public abstract class AbstractNode implements Node {

	private final String id;
	private final String name;

	private Flow flow;
	private final Set<Edge> outgoing = new MinimizedLinkedHashSet<>();
	private final Set<Edge> incoming = new MinimizedLinkedHashSet<>();

	public AbstractNode(String id, String name) {
		this.id = id;
		this.name = name;
	}

	protected void addOutgoingEdge(@NotNull Edge edge) {
		checkStartNode(edge);
		outgoing.add(edge);
	}

	protected void removeOutgoingEdge(@NotNull Edge edge) {
		checkStartNode(edge);
		outgoing.remove(edge);
	}

	protected void addIncomingEdge(@NotNull Edge edge) {
		checkEndNode(edge);
		incoming.add(edge);
	}

	protected void removeIncomingEdge(@NotNull Edge edge) {
		checkEndNode(edge);
		incoming.remove(edge);
	}

	private void checkStartNode(Edge edge) {
		if (edge == null || edge.getStartNode() != this) {
			throw new IllegalArgumentException("edge '" + edge + "' does not start at: " + this);
		}
	}

	private void checkEndNode(Edge edge) {
		if (edge == null || edge.getEndNode() != this) {
			throw new IllegalArgumentException("edge '" + edge + "' does not end at: " + this);
		}
	}

	@Override
	public final Set<Edge> getOutgoingEdges() {
		return outgoing;
	}

	@Override
	public Set<Edge> getIncomingEdges() {
		return incoming;
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
	public List<TerminologyObject> getHookedObjects() {
		return Collections.emptyList();
	}

	@Override
	public Condition getEdgePrecondition() {
		return ConditionTrue.INSTANCE;
	}

	@Override
	public void takeSnapshot(Session session) {
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractNode)) return false;
		AbstractNode that = (AbstractNode) o;
		return Objects.equals(id, that.id) &&
				Objects.equals(flow, that.flow);
	}

	@Override
	public int hashCode() {
		// as the flow may change, "this.flow" MUST NOT be part of the hash code,
		// otherwise set/map will not work for nodes and edges
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		String nameString = getClass().getSimpleName() + "[" + getID() + ", " + getName() + "]";
		if (flow != null) {
			nameString += " in " + flow.getName();
		}
		return nameString;
	}

	@Override
	public void update(Session session, FlowRun run) {
		// by default, nothing to do here...
	}
}
