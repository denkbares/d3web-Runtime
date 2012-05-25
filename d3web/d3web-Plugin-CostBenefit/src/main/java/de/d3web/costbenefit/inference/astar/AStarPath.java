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

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	private Reference<List<QContainer>> pathReference;
	private Reference<Set<QContainer>> cache;
	private final double totalCostCache;
	private final double negativCostCache;
	private final double costs;
	private final int hashCache;

	public AStarPath(QContainer qContainer, AStarPath predecessor, double costs) {
		super();
		this.costs = costs;
		if (qContainer == null && predecessor != null) {
			throw new NullPointerException(
					"QContainer can only be null at the starting node (without predecessor).");
		}
		this.qContainer = qContainer;
		this.predecessor = predecessor;

		// calculate costs
		double sum = 0.0;
		if (predecessor != null) {
			sum += predecessor.getCosts();
		}
		sum += costs;
		totalCostCache = sum;

		// calculate negative costs
		sum = 0.0;
		if (predecessor != null) {
			sum += predecessor.getNegativeCosts();
		}
		if (costs < 0) sum += costs;
		negativCostCache = sum;

		this.hashCache = calculateHashCode();
	}

	public AStarPath getPredecessor() {
		return predecessor;
	}

	@Override
	public double getCosts() {
		return totalCostCache;
	}

	@Override
	public double getNegativeCosts() {
		return negativCostCache;
	}

	/**
	 * Returns the final {@link QContainer} of this pathReference.
	 * 
	 * @created 08.09.2011
	 * @return the final qcontainer in the pathReference
	 */
	public QContainer getQContainer() {
		return this.qContainer;
	}

	@Override
	public List<QContainer> getPath() {
		if (pathReference != null) {
			List<QContainer> path = pathReference.get();
			if (path != null) {
				return path;
			}
		}
		ArrayList<QContainer> newPath = new ArrayList<QContainer>();
		addQContainersToPath(newPath);
		List<QContainer> path = Collections.unmodifiableList(newPath);
		pathReference = new SoftReference<List<QContainer>>(path);
		return path;
	}

	/**
	 * This method is used to add QContainers to the actual pathReference
	 * recursively without having to create more than one list
	 * 
	 * @created 22.06.2011
	 * @param pathReference each predecessor adds its qContainer to this list
	 */
	private void addQContainersToPath(ArrayList<QContainer> path) {
		if (predecessor != null) {
			path.addAll(predecessor.getPath());
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
		// an A* pathReference can never be empty
		return false;
	}

	@Override
	public int hashCode() {
		return hashCache;
	}

	public int calculateHashCode() {
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

	@Override
	public boolean contains(QContainer qContainer) {
		Set<QContainer> set = getSet();
		return set.contains(qContainer);
	}

	@Override
	public boolean contains(Collection<QContainer> qContainers) {
		Set<QContainer> set = getSet();
		for (QContainer qcon : qContainers) {
			if (set.contains(qcon)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<QContainer> qContainers) {
		Set<QContainer> set = getSet();
		return set.containsAll(qContainers);
	}

	public Set<QContainer> getSet() {
		Set<QContainer> set = null;
		if (cache != null) {
			set = cache.get();
		}
		if (set == null) {
			set = new HashSet<QContainer>(getPath());
			cache = new SoftReference<Set<QContainer>>(set);
		}
		return set;
	}

}
