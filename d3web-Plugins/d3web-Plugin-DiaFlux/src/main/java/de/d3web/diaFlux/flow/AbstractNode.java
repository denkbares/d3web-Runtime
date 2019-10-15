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
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

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
	private final List<Edge> outgoing;
	private final List<Edge> incoming;

	public AbstractNode(String id, String name) {
		this.id = id;
		this.outgoing = new ArrayList<>();
		this.incoming = new ArrayList<>();
		this.name = name;
	}

	protected void addOutgoingEdge(@NotNull Edge edge) {
		if (!outgoing.contains(edge)) {
			checkStartNode(edge);
			outgoing.add(edge);
		}
	}

	protected boolean removeOutgoingEdge(@NotNull Edge edge) {
		checkStartNode(edge);
		return outgoing.remove(edge);
	}

	protected void addIncomingEdge(@NotNull Edge edge) {
		if (!incoming.contains(edge)) {
			checkEndNode(edge);
			incoming.add(edge);
		}
	}

	protected boolean removeIncomingEdge(@NotNull Edge edge) {
		checkEndNode(edge);
		return incoming.remove(edge);
	}

	private void checkStartNode(@NotNull Edge edge) {
		if (edge.getStartNode() != this) {
			throw new IllegalArgumentException("edge '" + edge + "' does not start at: " + this);
		}
	}

	private void checkEndNode(@NotNull Edge edge) {
		if (edge.getEndNode() != this) {
			throw new IllegalArgumentException("edge '" + edge + "' does not end at: " + this);
		}
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
		return Objects.hash(id, flow);
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
