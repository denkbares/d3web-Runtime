/*
 * Copyright (C) 2009 denkbares GmbH
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.costBenefit.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Stack;

import de.d3web.core.session.XPSCase;

/**
 * A Path is a sequence of Nodes. It provides basic methods like adding and
 * removing nodes
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class Path {

	private Stack<Node> stack = new Stack<Node>();
	private double costs = 0;
	private Stack<Double> coststack = new Stack<Double>();

	public Collection<Node> getNodes() {
		return Collections.unmodifiableCollection(stack);
	}

	public double getCosts() {
		return costs;
	}

	/**
	 * Adds a new Node to the Path and updates the costs
	 * 
	 * @param node
	 * @param theCase
	 */
	public void add(Node node, XPSCase theCase) {
		stack.push(node);
		double nodeCosts = node.getCosts(theCase);
		costs += nodeCosts;
		coststack.push(nodeCosts);
	}

	/**
	 * Removes the last node added to the path and updates the costs
	 */
	public void pop() {
		stack.pop();
		Double cost = coststack.pop();
		costs -= cost;
	}

	/**
	 * Returns a copy of this path
	 * 
	 * @return
	 */
	public Path copy() {
		Path copy = new Path();
		for (int i = 0; i < stack.size(); i++) {
			copy.add(stack.get(i), coststack.get(i));
		}
		return copy;
	}

	private void add(Node node, Double double1) {
		stack.push(node);
		coststack.push(double1);
		costs += double1;

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

	/**
	 * Checks if the path is empty.
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return stack.isEmpty();
	}

}
