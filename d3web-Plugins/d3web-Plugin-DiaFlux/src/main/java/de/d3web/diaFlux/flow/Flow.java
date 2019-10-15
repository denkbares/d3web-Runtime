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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.AbstractNamedObject;

/**
 * @author Reinhard Hatko
 */
public class Flow extends AbstractNamedObject {

	private final List<Edge> edges;
	private final List<Node> nodes;
	private boolean autostart;
	private final KnowledgeBase kb;

	Flow(KnowledgeBase kb, String name, List<Node> nodes, List<Edge> edges) {
		super(name);
		if (nodes == null) throw new IllegalArgumentException("nodes is null");

		if (edges == null) throw new IllegalArgumentException("edges is null");

		if (name == null) throw new IllegalArgumentException("name is null");

		this.nodes = nodes;
		this.edges = edges;
		this.autostart = false;
		this.kb = kb;
		checkFlow();
	}

	/**
	 * Checks the consistency of nodes and edges and sets back-reference to flow in nodes. Also adds the node
	 * back-references from effected objects to nodes.
	 */
	private void checkFlow() {
		for (Node node : nodes) {
			node.setFlow(this);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Flow other = (Flow) obj;
		return Objects.equals(getName(), other.getName());
	}

	/**
	 * Adds an edge to the flow. You should not use this method directly, use {@link FlowFactory#addEdge(Edge)}
	 * instead.
	 */
	protected void addEdge(Edge edge) {
		if (!edges.contains(edge)) {
			edges.add(edge);
		}
	}

	/**
	 * Removes an edge from the flow. You should not use this method directly, use {@link FlowFactory#removeEdge(Edge)}
	 * instead.
	 */
	protected void removeEdge(Edge edge) {
		edges.remove(edge);
	}

	/**
	 * Adds a node to the flow. You should not use this method directly, use {@link FlowFactory#addNode(Flow, Node)}
	 * instead.
	 */
	protected void addNode(Node node) {
		if (!nodes.contains(node)) {
			nodes.add(node);
		}
	}

	/**
	 * Removes a node from the flow. You should not use this method directly, use {@link FlowFactory#removeNode(Node)}
	 * instead.
	 */
	protected void removeNode(Node node) {
		nodes.remove(node);
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public <T> Collection<T> getNodesOfClass(Class<T> clazz) {
		Collection<T> result = new HashSet<>();
		for (Node node : nodes) {
			if (clazz.isInstance(node)) {
				result.add(clazz.cast(node));
			}
		}
		return Collections.unmodifiableCollection(result);
	}

	public boolean isAutostart() {
		return autostart;
	}

	public void setAutostart(boolean autostart) {
		this.autostart = autostart;
	}

	public List<StartNode> getStartNodes() {
		return getNodesOfType(StartNode.class);
	}

	public List<EndNode> getExitNodes() {
		return getNodesOfType(EndNode.class);
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> getNodesOfType(Class<T> clazz) {
		List<T> result = new LinkedList<>();

		for (Node node : nodes) {
			if (clazz.isAssignableFrom(node.getClass())) result.add((T) node);
		}

		return result;
	}

	@Override
	public String toString() {
		return "Flow [" + getName() + "]"
				+ "@" + Integer.toHexString(hashCode());
	}

	public KnowledgeBase getKnowledgeBase() {
		return this.kb;
	}
}
