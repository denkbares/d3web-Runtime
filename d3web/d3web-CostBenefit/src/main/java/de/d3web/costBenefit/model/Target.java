package de.d3web.costBenefit.model;

import java.util.LinkedList;

import de.d3web.core.terminology.QContainer;

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
			if (!checker)
				return false;
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
		double benefit = getBenefit();
		Path minPath = getMinPath();
		if (minPath == null || benefit <= 0f)
			return Float.MAX_VALUE;
		return minPath.getCosts() / benefit;
	}
}
