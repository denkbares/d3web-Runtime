/*
 * Copyright (C) 2014 denkbares GmbH
 */
package de.d3web.costbenefit.session.protocol;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.protocol.ProtocolEntry;

/**
 * Represents a calculated target
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 11.09.2014
 */
public class CalculatedTargetEntry implements ProtocolEntry {

	private final Collection<CalculatedTargetEntry.Target> targets;
	private final Target calculatedTarget;
	private final Set<String> sprintGroup;
	private final Date date;

	public CalculatedTargetEntry(de.d3web.costbenefit.model.Target calculatedTarget, Set<de.d3web.costbenefit.model.Target> targets, Date date, Collection<Solution> solutions) {
		super();
		this.calculatedTarget = convertTarget(calculatedTarget);
		this.targets = new HashSet<CalculatedTargetEntry.Target>();
		for (de.d3web.costbenefit.model.Target cbTarget : targets) {
			this.targets.add(convertTarget(cbTarget));
		}
		this.date = date;
		this.sprintGroup = new HashSet<String>();
		for (Solution s : solutions) {
			this.sprintGroup.add(s.getName());
		}
	}

	public CalculatedTargetEntry(Target calculatedTarget, Set<Target> targets, Date date, Set<String> solutions) {
		super();
		this.calculatedTarget = calculatedTarget;
		this.targets = targets;
		this.date = date;
		this.sprintGroup = solutions;
	}

	@Override
	public Date getDate() {
		return date;
	}

	public Collection<CalculatedTargetEntry.Target> getTargets() {
		return targets;
	}

	public Target getCalculatedTarget() {
		return calculatedTarget;
	}

	private static CalculatedTargetEntry.Target convertTarget(de.d3web.costbenefit.model.Target cbTarget) {
		Set<String> qContainerNames = new HashSet<String>();
		for (QContainer qContainer : cbTarget.getQContainers()) {
			qContainerNames.add(qContainer.getName());
		}
		return new Target(qContainerNames, cbTarget.getBenefit(), cbTarget.getCostBenefit());
	}

	public Set<String> getSprintGroup() {
		return sprintGroup;
	}

	public static class Target {

		private final Set<String> qContainerNames;
		private final double benefit;
		private final double costbenefit;

		public Target(Set<String> qContainerNames, double benefit) {
			this(qContainerNames, benefit, Float.MAX_VALUE);
		}

		public Target(Set<String> qContainerNames, double benefit, double costbenefit) {
			super();
			this.qContainerNames = qContainerNames;
			this.benefit = benefit;
			this.costbenefit = costbenefit;
		}

		public Set<String> getqContainerNames() {
			return qContainerNames;
		}

		public double getBenefit() {
			return benefit;
		}

		public double getCostbenefit() {
			return costbenefit;
		}

		@Override
		public String toString() {
			if (benefit != Float.MAX_VALUE) {
				return qContainerNames.toString() + " Benefit: " + benefit + " Cost/Benefit: "
						+ costbenefit;
			}
			else {
				return qContainerNames.toString() + " Benefit: " + benefit
						+ " Cost/Benefit: unknown";
			}
		}

	}

}
