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
		double costs = this.costs;
		if (predecessor != null) {
			costs += predecessor.getCosts();
		}
		return costs;
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
}
