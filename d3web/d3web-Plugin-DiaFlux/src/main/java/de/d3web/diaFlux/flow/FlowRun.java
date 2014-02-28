/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Reinhard Hatko
 * @created 17.02.2011
 */
public class FlowRun {

	private final Map<Node, Set<DiaFluxElement>> nodeSupports;
	private final Set<Node> startNodes;

	public FlowRun() {
		this.nodeSupports = new HashMap<Node, Set<DiaFluxElement>>();
		this.startNodes = new HashSet<Node>();
	}

	/**
	 * Returns if the node is active within this flow run. Active means the node
	 * is either a start node or has been activated during the flow run.
	 * 
	 * @created 28.02.2011
	 * @param node the node to be checked to be active
	 * @return if the node is active
	 */
	public boolean isActive(Node node) {
		return nodeSupports.containsKey(node) || startNodes.contains(node);
	}

	/**
	 * Returns if the node has been activated by propagation within this flow
	 * run. This means that the node has got an incoming edge in this flow run.
	 * Please note that {@link #isActive(Node)} also returns true for start
	 * nodes, regardless whether they have incoming active edges, while this
	 * method does not.
	 * 
	 * @created 28.02.2011
	 * @param node the node to be checked to be activated
	 * @return if the node is activated
	 */
	public boolean isActivated(Node node) {
		return nodeSupports.containsKey(node);
	}

	/**
	 * Returns if the node has been activated by propagation within this flow
	 * run. this means that the node has got an incoming edge in this flow run.
	 * Please note that {@link #isActive(Node)} also returns true for start
	 * nodes, regardless whether they have incoming active edges, while this
	 * method does not.
	 * 
	 * @created 28.02.2011
	 * @param edge the edge to be checked to be activated
	 * @return if the edge is activated
	 */
	public boolean isActivated(Edge edge) {
		Set<DiaFluxElement> supports = nodeSupports.get(edge.getEndNode());
		if (supports == null) {
			return false;
		}
		else {
			return supports.contains(edge);
		}

		// return nodeSupports.containsKey(node);
	}

	/**
	 * Adds support for the specified node.
	 * 
	 * @created 02.09.2013
	 * @param node the node to add support to
	 * @param support the support to be added. may only be null for the
	 *        autostart nodes
	 * @return s true, if the node was not supported before, ie should be
	 *         activated now
	 */
	public boolean addSupport(Node node, DiaFluxElement support) {
		boolean contained = nodeSupports.containsKey(node);
		Set<DiaFluxElement> supports;
		if (!contained) {
			supports = new HashSet<DiaFluxElement>(5);
			nodeSupports.put(node, supports);
		}
		else {
			supports = nodeSupports.get(node);
		}
		supports.add(support);

		return !contained;
	}

	/**
	 * Removes the support from the node.
	 * 
	 * @created 02.09.2013
	 * @return s if the node is still supported
	 */
	public boolean removeSupport(Node node, DiaFluxElement support) {
		Set<DiaFluxElement> supports = nodeSupports.get(node);

		if (supports == null) return false;
		supports.remove(support);

		if (supports.isEmpty()) {
			nodeSupports.remove(node);
			return false;
		}

		return true;
	}

	public boolean addStartNode(Node node) {
		return this.startNodes.add(node);
	}

	public Collection<Node> getActiveNodes() {
		Collection<Node> activeNodes = new HashSet<Node>();
		activeNodes.addAll(startNodes);
		activeNodes.addAll(nodeSupports.keySet());
		return Collections.unmodifiableCollection(activeNodes);
	}

	/**
	 * Return the active nodes of that flow run, that matches the specified
	 * class (being of this class or a subclass.
	 * 
	 * @created 28.02.2011
	 * @param <T>
	 * @param clazz the class for the nodes
	 * @return the active nodes
	 */
	public <T> Collection<T> getActiveNodesOfClass(Class<T> clazz) {
		Collection<T> activeNodes = new HashSet<T>();
		for (Node node : startNodes) {
			if (clazz.isInstance(node)) {
				activeNodes.add(clazz.cast(node));
			}
		}
		for (Node node : nodeSupports.keySet()) {
			if (clazz.isInstance(node)) {
				activeNodes.add(clazz.cast(node));
			}
		}
		return Collections.unmodifiableCollection(activeNodes);
	}

	/**
	 * Return the activated nodes of that flow run, that matches the specified
	 * class (being of this class or a subclass. Activated nodes are those notes
	 * that have active incoming edges, regardless if they are start nodes or
	 * not.
	 * 
	 * @created 28.02.2011
	 * @param <T>
	 * @param clazz the class for the nodes
	 * @return the activated nodes
	 */
	public <T> Collection<T> getActivatedNodesOfClass(Class<T> clazz) {
		Collection<T> activeNodes = new HashSet<T>();
		for (Node node : nodeSupports.keySet()) {
			if (clazz.isInstance(node)) {
				activeNodes.add(clazz.cast(node));
			}
		}
		return Collections.unmodifiableCollection(activeNodes);
	}

	public boolean isStartNode(Node node) {
		return startNodes.contains(node);
	}

	public Collection<Node> getStartNodes() {
		return Collections.unmodifiableCollection(this.startNodes);
	}

}
