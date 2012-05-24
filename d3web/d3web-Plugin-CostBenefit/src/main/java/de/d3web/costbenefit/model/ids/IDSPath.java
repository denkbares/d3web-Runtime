/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.costbenefit.model.ids;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.session.Session;
import de.d3web.costbenefit.model.Path;

/**
 * A IDSPath is a sequence of Nodes. It provides basic methods like adding and
 * removing nodes
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class IDSPath implements Path {

	private Stack<Node> stack = new Stack<Node>();
	private double costs = 0;
	private double negativeCosts = 0;
	private Stack<Double> coststack = new Stack<Double>();

	@Override
	public double getCosts() {
		return costs;
	}

	/**
	 * Adds a new Node to the IDSPath and updates the costs
	 * 
	 * @param node
	 * @param session
	 */
	public void add(Node node, Session session) {
		stack.push(node);
		double nodeCosts = node.getCosts(session);
		costs += nodeCosts;
		if (nodeCosts < 0) negativeCosts += nodeCosts;
		coststack.push(nodeCosts);
	}

	/**
	 * Removes the last node added to the path and updates the costs
	 */
	public void pop() {
		stack.pop();
		Double cost = coststack.pop();
		costs -= cost;
		if (cost < 0) negativeCosts -= costs;
	}

	/**
	 * Returns a copy of this path
	 * 
	 * @return
	 */
	public IDSPath copy() {
		IDSPath copy = new IDSPath();
		for (int i = 0; i < stack.size(); i++) {
			copy.add(stack.get(i), coststack.get(i));
		}
		return copy;
	}

	private void add(Node node, Double cost) {
		stack.push(node);
		coststack.push(cost);
		costs += cost;
		if (cost < 0) negativeCosts += cost;
	}

	/**
	 * Checks if the Node is contained in the path
	 * 
	 * @param node
	 * @return
	 */
	public boolean contains(Node node) {
		return stack.contains(node);
	}

	/**
	 * Returns the last node added to the path.
	 * 
	 * @return
	 */
	public Node getLastNode() {
		return stack.peek();
	}

	@Override
	public boolean isEmpty() {
		return stack.isEmpty();
	}

	@Override
	public List<QContainer> getPath() {
		LinkedList<QContainer> list = new LinkedList<QContainer>();
		for (Node n : stack) {
			list.add(n.getQContainer());
		}
		return list;
	}

	@Override
	public String toString() {
		return "IDS-Path: " + getPath() + " (costs: " + getCosts() + ")";
	}

	@Override
	public double getNegativeCosts() {
		return negativeCosts;
	}

	@Override
	public boolean contains(QContainer qContainer) {
		return getPath().contains(qContainer);
	}

	@Override
	public boolean containsAll(Collection<QContainer> qContainers) {
		return getPath().containsAll(qContainers);
	}

	@Override
	public boolean contains(Collection<QContainer> qContainers) {
		List<QContainer> path = getPath();
		for (QContainer qcon : qContainers) {
			if (path.contains(qcon)) {
				return true;
			}
		}
		return false;
	}
}
