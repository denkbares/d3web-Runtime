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
package de.d3web.costbenefit.inference;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.Session;
import de.d3web.costbenefit.Util;
import de.d3web.costbenefit.model.Path;
import de.d3web.costbenefit.model.SearchModel;
import de.d3web.costbenefit.model.Target;

/**
 * Extends the path of the sub {@link SearchAlgorithm} by adding specially
 * comfortBenefit {@link QContainer} to the path, if it doesn't destroy the path
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 07.11.2011
 */
public class PathExtender implements SearchAlgorithm {

	private final SearchAlgorithm subalgorithm;
	private KnowledgeBase kb = null;
	private List<QContainer> qcontainersToAdd = new LinkedList<QContainer>();

	public static final Property<Boolean> comfortBenefit = Property.getProperty("comfortBenefit",
			Boolean.class);

	public PathExtender(SearchAlgorithm algorithm) {
		subalgorithm = algorithm;
	}

	public SearchAlgorithm getSubalgorithm() {
		return subalgorithm;
	}

	@Override
	public synchronized void search(Session session, SearchModel model) {
		if (session.getKnowledgeBase() != kb) {
			kb = session.getKnowledgeBase();
			qcontainersToAdd = new LinkedList<QContainer>();
			for (QContainer qcon : kb.getManager().getQContainers()) {
				if (qcon.getInfoStore().getValue(comfortBenefit)) {
					qcontainersToAdd.add(qcon);
				}
			}
		}
		subalgorithm.search(session, model);
		long startTime = System.currentTimeMillis();
		Target bestCostBenefitTarget = model.getBestCostBenefitTarget();
		if (bestCostBenefitTarget == null) return;
		Path path = bestCostBenefitTarget.getMinPath();
		CostFunction costFunction = session.getPSMethodInstance(PSMethodCostBenefit.class).getCostFunction();
		if (path != null && !qcontainersToAdd.isEmpty()) {
			for (QContainer qconToInclude : qcontainersToAdd) {
				Double staticCosts = qconToInclude.getInfoStore().getValue(BasicProperties.COST);
				// if it is already in the path, do nothing
				if (!path.getPath().contains(qconToInclude)) {
					Session copiedSession = Util.createSearchCopy(session);
					for (int i = 0; i < path.getPath().size(); i++) {
						// check if the qcontainer is applicable
						if (isApplicable(qconToInclude, copiedSession)) {
							// check if the dynamic costs are lower or equal to
							// the static costs
							if (costFunction.getCosts(qconToInclude, copiedSession) <= staticCosts) {
								// apply new QContainer
								Session extendedSession = Util.createSearchCopy(copiedSession);
								applyQContainer(qconToInclude, extendedSession);
								if (checkPath(path, extendedSession, i)) {
									path = getNewPath(i, qconToInclude, path, session);
									break;
								}
							}
						}
						QContainer actualQContainer = path.getPath().get(i);
						applyQContainer(actualQContainer, copiedSession);
					}
				}
			}
			bestCostBenefitTarget.setMinPath(path);
		}
		Logger.getLogger(PathExtender.class.getName()).info(
				"Time for extending path: " + (System.currentTimeMillis() - startTime) + "ms");
	}

	private void applyQContainer(QContainer qcontainer, Session extendedSession) {
		StateTransition stateTransition = StateTransition.getStateTransition(qcontainer);
		Util.setNormalValues(extendedSession, qcontainer, this);
		stateTransition.fire(extendedSession);
	}

	private Path getNewPath(int position, QContainer qconToInclude, Path path, Session session) {
		List<QContainer> qContainers = new LinkedList<QContainer>(path.getPath());
		qContainers.add(position, qconToInclude);
		ExtendedPath newPath = new ExtendedPath(qContainers, path.getCosts());
		return newPath;
	}

	private static boolean isApplicable(QContainer qcon, Session session) {
		StateTransition stateTransition = StateTransition.getStateTransition(qcon);
		if (stateTransition != null && stateTransition.getActivationCondition() != null) {
			try {
				return (stateTransition.getActivationCondition().eval(session));
			}
			catch (NoAnswerException e) {
				return false;
			}
			catch (UnknownAnswerException e) {
				return false;
			}
		}
		else {
			return true;
		}
	}

	private boolean checkPath(Path path, Session session, int position) {
		for (int i = position; i < path.getPath().size(); i++) {
			QContainer qContainer = path.getPath().get(i);
			if (!isApplicable(qContainer, session)) {
				return false;
			}
			Util.setNormalValues(session, qContainer, this);
			StateTransition stateTransition = StateTransition.getStateTransition(qContainer);
			if (stateTransition != null) stateTransition.fire(session);
		}
		return true;
	}

	private static class ExtendedPath implements Path {

		private List<QContainer> qContainers = new LinkedList<QContainer>();
		private double costs;

		private ExtendedPath(List<QContainer> qContainers, double costs) {
			super();
			this.qContainers = qContainers;
			this.costs = costs;
		}

		@Override
		public List<QContainer> getPath() {
			return Collections.unmodifiableList(qContainers);
		}

		@Override
		public double getCosts() {
			return costs;
		}

		@Override
		public boolean isEmpty() {
			return qContainers.isEmpty();
		}

		@Override
		public String toString() {
			return "Extended-Path: " + getPath() + " (costs: " + getCosts() + ")";
		}

	}

}