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
		double sum = 0.0;
		for (AStarPath item = this; item != null; item = item.predecessor) {
			sum += item.costs;
		}
		return sum;
	}

	public int getLength() {
		int length = 1;
		for (AStarPath item = this.predecessor; item != null; item = item.predecessor) {
			length++;
		}
		return length;
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

	/**
	 * Checks if this path has the specified prefix (or is equal to the prefix).
	 * 
	 * @created 08.09.2011
	 * @param prefix the prefix to look for
	 * @return if this path has the prefix
	 */
	public boolean hasPrefix(AStarPath prefix) {
		int thisLen = getLength();
		int prefixLen = prefix.getLength();

		// check is this one has at least same length as prefix
		if (thisLen < prefixLen) return false;

		// get thisPrefix of the expected prefix lenght
		AStarPath thisPrefix = this;
		for (int i = thisLen; i > prefixLen; i--) {
			thisPrefix = thisPrefix.predecessor;
		}

		// and the check if thisPrefix an prefix are equal
		return thisPrefix.equals(prefix);
	}

	/**
	 * Returns a path has the specified new prefix and the rest of this path
	 * after the old prefix. Normally a new path is created, based on the new
	 * prefix. If this path is equal to the prefix to be replaced, the new
	 * prefix is returned. This method if not destructive to this path.
	 * <p>
	 * It is not checked if the prefix to be replaced is part of this path. This
	 * method only replaces the length of the old prefix by the new one. If the
	 * old prefix path is longer than this one an
	 * {@link ArrayIndexOutOfBoundsException} is thrown. If one of the prefixes
	 * is null, a {@link NullPointerException} is thrown.
	 * 
	 * @created 08.09.2011
	 * @param oldPrefix the prefix to look for
	 * @param newPrefix the new prefix replace
	 * @return a newly created path with the replaced prefix (or this)
	 * @throws ArrayIndexOutOfBoundsException the oldPrefix is longer than this
	 *         path
	 * @throws NullPointerException oldPrefix or newPrefix is null
	 */
	public AStarPath replacePrefix(AStarPath oldPrefix, AStarPath newPrefix) {
		int thisLen = getLength();
		int oldPrefixLen = oldPrefix.getLength();

		// check is this path is equal or longer as the prefix
		if (thisLen < oldPrefixLen) throw new ArrayIndexOutOfBoundsException();
		return copyAndReplace(this, thisLen - oldPrefixLen, newPrefix);
	}

	private AStarPath copyAndReplace(AStarPath original, int itemsBeforePrefix, AStarPath newPrefix) {
		if (itemsBeforePrefix == 0) return newPrefix;
		return new AStarPath(
				original.qContainer,
				copyAndReplace(original.predecessor, itemsBeforePrefix - 1, newPrefix),
				original.costs);
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
