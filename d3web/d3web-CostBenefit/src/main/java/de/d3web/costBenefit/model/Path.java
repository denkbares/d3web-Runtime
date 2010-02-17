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
