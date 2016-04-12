/*
 * Copyright (C) 2011 denkbares GmbH
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
package de.d3web.costbenefit.inference.astar;

import de.d3web.core.session.Session;

/**
 * Represents a Node in the graph of the A*-Search
 * 
 * For each state only one Node exists
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 22.06.2011
 */
public class Node implements Comparable<Node> {

	private final State state;
	private final Session session;
	private AStarPath path;
	private double fValue;

	public Node(State state, Session session, AStarPath path, double fValue) {
		super();
		this.state = state;
		this.session = session;
		this.path = path;
		this.fValue = fValue;
	}

	public State getState() {
		return state;
	}

	public Session getSession() {
		return session;
	}

	public AStarPath getPath() {
		return path;
	}

	public double getfValue() {
		return fValue;
	}

	public void setfValue(double fValue) {
		this.fValue = fValue;
	}

	/**
	 * Sets the path to this node to new path, if it is cheaper than the current
	 * path
	 * 
	 * @created 22.06.2011
	 * @param path new path to this Node
	 * @return true if the path was updated, false otherwise
	 */
	public boolean updatePath(AStarPath path) {
		if (path.getCosts() < this.getPath().getCosts()) {
			this.path = path;
			return true;
		}
		return false;
	}

	@Override
	public int compareTo(Node node2) {
		if (node2 == null) {
			return 1;
		}
		if (fValue == node2.fValue) {
			// if the f Value is equal, prefer nodes with higher path costs ->
			// closer to target
			return Double.compare(node2.path.getCosts(), path.getCosts());
		}
		return Double.compare(fValue, node2.fValue);
	}

}
