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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.costbenefit.model.Path;

/**
 * Represents a path to a an actual state of the session
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 22.06.2011
 */
public class AStarPath implements Path {

	private final QContainer qContainer;
	private final AStarPath predecessor;

	public AStarPath getPredecessor() {
		return predecessor;
	}

	private final double costs;

	public AStarPath(QContainer qContainer, AStarPath predecessor, double costs) {
		super();
		this.costs = costs;
		if (qContainer == null && predecessor != null) {
			throw new NullPointerException(
					"QContainer can only be null at the starting node (without predecessor).");
		}
		this.qContainer = qContainer;
		this.predecessor = predecessor;
	}

	@Override
	public double getCosts() {
		double sum = 0.0;
		for (AStarPath item = this; item != null; item = item.predecessor) {
			sum += item.costs;
		}
		return sum;
	}

	/**
	 * Returns the final {@link QContainer} of this path.
	 * 
	 * @created 08.09.2011
	 * @return the final qcontainer in the path
	 */
	public QContainer getQContainer() {
		return this.qContainer;
	}

	@Override
	public List<QContainer> getPath() {
		LinkedList<QContainer> path = new LinkedList<QContainer>();
		addQContainersToPath(path);
		return Collections.unmodifiableList(path);
	}

	/**
	 * This method is used to add QContainers to the actual path recursively
	 * without having to create more than one list
	 * 
	 * @created 22.06.2011
	 * @param path each predecessor adds its qContainer to this list
	 */
	private void addQContainersToPath(LinkedList<QContainer> path) {
		if (predecessor != null) {
			predecessor.addQContainersToPath(path);
		}
		if (qContainer != null) {
			path.add(qContainer);
		}
	}

	@Override
	public String toString() {
		return "A*-Path: " + getPath() + " (costs: " + getCosts() + ")";
	}

	@Override
	public boolean isEmpty() {
		// an A* path can never be empty
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(costs);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((predecessor == null) ? 0 : predecessor.hashCode());
		result = prime * result + ((qContainer == null) ? 0 : qContainer.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		AStarPath other = (AStarPath) obj;
		if (Double.doubleToLongBits(costs) != Double.doubleToLongBits(other.costs)) return false;
		if (predecessor == null) {
			if (other.predecessor != null) return false;
		}
		else if (!predecessor.equals(other.predecessor)) return false;
		if (qContainer == null) {
			if (other.qContainer != null) return false;
		}
		else if (!qContainer.equals(other.qContainer)) return false;
		return true;
	}

}
