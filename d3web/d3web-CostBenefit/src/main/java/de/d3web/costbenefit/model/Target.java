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
package de.d3web.costbenefit.model;

import java.util.LinkedList;

import de.d3web.core.knowledge.terminology.QContainer;

/**
 * A Target is a List of QContainer with a combined benefit and a (minimal) path
 * which contains all QContainers in this list.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class Target extends LinkedList<QContainer> {

	private static final long serialVersionUID = 1927072006554824366L;

	private double benefit = 0.0;
	private Path minPath;

	public Target() {

	}

	public Target(QContainer qaset) {
		this.add(qaset);
	}

	/**
	 * Checks if the Target is reached. It is reached, when all QContainers are
	 * in the path
	 * 
	 * @param path
	 * @return
	 */
	public boolean isReached(Path path) {
		boolean[] reached = new boolean[this.size()];
		for (Node node : path.getNodes()) {
			QContainer qcon = node.getQContainer();
			if (this.contains(qcon)) {
				reached[this.indexOf(qcon)] = true;
			}
		}
		for (boolean checker : reached) {
			if (!checker) return false;
		}
		return true;
	}

	public double getBenefit() {
		return benefit;
	}

	void setBenefit(double benefit) {
		this.benefit = benefit;
	}

	public Path getMinPath() {
		return minPath;
	}

	void setMinPath(Path minPath) {
		this.minPath = minPath;
	}

	/**
	 * Returns the CostBenefit based on the actual minpath
	 * 
	 * @return
	 */
	public double getCostBenefit() {
		double theBenefit = getBenefit();
		Path theMinPath = getMinPath();
		if (theMinPath == null || theBenefit <= 0f) return Float.MAX_VALUE;
		return theMinPath.getCosts() / theBenefit;
	}
}
