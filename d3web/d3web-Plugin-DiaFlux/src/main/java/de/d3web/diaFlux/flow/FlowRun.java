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
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Reinhard Hatko
 * @created 17.02.2011
 */
public class FlowRun {

	private final Set<INode> nodes;
	private final Set<INode> startNodes;

	public FlowRun() {
		this.nodes = new HashSet<INode>();
		this.startNodes = new HashSet<INode>();
	}

	/**
	 * Returns if the node is active within this flow run. Active means the node
	 * is either a start node or has been activated during the flow run.
	 * 
	 * @created 28.02.2011
	 * @param node the node to be checked to be active
	 * @return if the node is active
	 */
	public boolean isActive(INode node) {
		return nodes.contains(node) || startNodes.contains(node);
	}

	/**
	 * Returns if the node has been activated by propagation within this flow
	 * run. this means that the node has got an incoming edge in this flow run.
	 * Please note that {@link #isActive(INode)} also returns true for start
	 * nodes, regardless wether they have incoming active edges, while this
	 * method does not.
	 * 
	 * @created 28.02.2011
	 * @param node the node to be checked to be activated
	 * @return if the node is activated
	 */
	public boolean isActivated(INode node) {
		return nodes.contains(node);
	}

	public boolean add(INode node) {
		return nodes.add(node);
	}

	public boolean remove(INode node) {
		return nodes.remove(node);
	}

	public boolean addStartNode(INode node) {
		return this.startNodes.add(node);
	}

	public Collection<INode> getActiveNodes() {
		Collection<INode> activeNodes = new HashSet<INode>();
		activeNodes.addAll(startNodes);
		activeNodes.addAll(nodes);
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
		for (INode node : startNodes) {
			if (clazz.isInstance(node)) {
				activeNodes.add(clazz.cast(node));
			}
		}
		for (INode node : nodes) {
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
		for (INode node : nodes) {
			if (clazz.isInstance(node)) {
				activeNodes.add(clazz.cast(node));
			}
		}
		return Collections.unmodifiableCollection(activeNodes);
	}

	public boolean isStartNode(INode node) {
		return startNodes.contains(node);
	}

	public Collection<INode> getStartNodes() {
		return Collections.unmodifiableCollection(this.startNodes);
	}

}
