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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.info.BasicProperties;

/**
 * A Target is a List of QContainer with a combined benefit and a (minimal) path which contains all QContainers in this
 * list.
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class Target implements Comparable<Target> {

	private final List<QContainer> qContainers = new LinkedList<>();
	private final double costs;
	private double benefit = 0.0;
	private Path minPath;

	/**
	 * Creates a new Target with a specified QContainer to be reached.
	 *
	 * @param targetContainer the QContainer to be reached
	 */
	public Target(QContainer targetContainer) {
		this.qContainers.add(targetContainer);
		this.costs = summarizeCosts(qContainers);
	}

	/**
	 * Creates a new Target with a set of specified QContainer to be reached.
	 *
	 * @param targetContainers the QContainers to be reached
	 */
	public Target(Collection<QContainer> targetContainers) {
		this.qContainers.addAll(targetContainers);
		this.costs = summarizeCosts(qContainers);
	}

	private static double summarizeCosts(List<QContainer> qContainers) {
		double costs = 0.0;
		for (QContainer item : qContainers) {
			costs += item.getInfoStore().getValue(BasicProperties.COST);
		}
		return costs;
	}

	@Override
	@SuppressWarnings({ "CloneDoesntDeclareCloneNotSupportedException", "MethodDoesntCallSuperMethod" })
	protected Target clone() {
		Target copy = new Target(qContainers);
		copy.benefit = this.benefit;
		copy.minPath = this.minPath;
		return copy;
	}

	/**
	 * Checks if this Target is reached by the specified path. It is reached, when all QContainers are in the path.
	 *
	 * @param path the path to be checked
	 * @return if the path covers the target
	 */
	public boolean isReached(Path path) {
		boolean[] reached = new boolean[this.getQContainers().size()];
		for (QContainer qcon : path.getPath()) {
			if (this.getQContainers().contains(qcon)) {
				reached[this.getQContainers().indexOf(qcon)] = true;
			}
		}
		for (boolean checker : reached) {
			if (!checker) return false;
		}
		return true;
	}

	/**
	 * Returns the benefit calculated for this target. The benefit is the information gain that is assumed to receive
	 * when the {@link QContainer}s of this target will have been answered.
	 *
	 * @return the target's benefit
	 * @created 07.03.2011
	 */
	public double getBenefit() {
		return benefit;
	}

	/**
	 * Sets the benefit if this target. See {@link #getBenefit()} for more details on benefit.
	 *
	 * @param benefit the benefit calculated for this target
	 * @created 07.03.2011
	 */
	public void setBenefit(double benefit) {
		this.benefit = benefit;
	}

	/**
	 * Returns the minimal path calculated for this target. The minimal path is the "least-cost" path that covers all
	 * QContainers contained in this target. The paths of the targets are calculated during executing the search
	 * algorithm of the cost/benefit calculations. Please note that the search algorithm will not necessarily calculate
	 * such paths for all targets. This does <b>not</b> mean that no such path exists.
	 *
	 * @return the minimal path of this target or null if not such path has been calculated
	 * @created 07.03.2011
	 */
	public Path getMinPath() {
		return minPath;
	}

	/**
	 * Sets the minimal path towards that target. See {@link #getMinPath()} for more details about minimal paths.
	 *
	 * @param minPath the minPath to be set
	 * @created 07.03.2011
	 */
	public void setMinPath(Path minPath) {
		this.minPath = minPath;
	}

	/**
	 * Returns the static costs of all {@link QContainer}s included in this target. Please note that additional costs
	 * may be required for that target when preparing the state for the transition model. This the costs of the minimal
	 * path are usually higher than this costs.
	 *
	 * @return the cost of this target
	 * @created 07.03.2011
	 */
	public double getCosts() {
		return costs;
	}

	/**
	 * Returns the cost per benefit of that target. The result is calculated based on the actual minimal path. If no
	 * such path is available, the costs are assumed to be extraordinary high, returning {@link Float#MAX_VALUE}.
	 *
	 * @return the cost per benefit ratio
	 */
	public double getCostBenefit() {
		double theBenefit = getBenefit();
		Path theMinPath = getMinPath();
		if (theMinPath == null || theBenefit <= 0f) return Float.MAX_VALUE;
		return theMinPath.getCosts() / theBenefit;
	}

	/**
	 * Returns the list of {@link QContainer}s to be reached in this target.
	 *
	 * @return the QContainers of this target
	 * @created 07.03.2011
	 */
	public List<QContainer> getQContainers() {
		return Collections.unmodifiableList(qContainers);
	}

	@Override
	public String toString() {
		return "Target" + this.qContainers +
				"#B:" + this.benefit +
				"#C:" + (minPath != null ? minPath.getCosts() : "?");
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Target other = (Target) obj;
		return qContainers.equals(other.qContainers);
	}

	@Override
	public int hashCode() {
		return 31 + qContainers.hashCode();
	}

	@Override
	public int compareTo(@NotNull Target o) {
		double benefitdifference = this.benefit - o.benefit;
		if (benefitdifference > 0.0) {
			return 1;
		}
		else if (benefitdifference < 0.0) {
			return -1;
		}
		else {
			return qContainers.toString().compareTo(o.qContainers.toString());
		}
	}
}
