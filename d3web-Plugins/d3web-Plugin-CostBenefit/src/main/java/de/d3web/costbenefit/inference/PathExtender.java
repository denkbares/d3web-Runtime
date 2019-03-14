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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.denkbares.utils.Log;
import de.d3web.core.inference.condition.Conditions;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.session.Session;
import de.d3web.costbenefit.CostBenefitUtil;
import de.d3web.costbenefit.model.Path;
import de.d3web.costbenefit.model.SearchModel;
import de.d3web.costbenefit.model.Target;

/**
 * Extends the path of the sub SearchAlgorithm by adding specially comfortBenefit {@link QContainer} to the
 * path, if it doesn't destroy the path
 *
 * @author Markus Friedrich (denkbares GmbH)
 * @created 07.11.2011
 */
public class PathExtender implements SearchAlgorithm {

	private final SearchAlgorithm subalgorithm;
	private KnowledgeBase kb = null;
	private List<QContainer> qcontainersToAdd = new LinkedList<>();

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
			qcontainersToAdd = new LinkedList<>();
			for (QContainer qcon : kb.getManager().getQContainers()) {
				//noinspection deprecation
				if (qcon.getInfoStore().getValue(CostBenefitProperties.COMFORT_BENEFIT)
						|| qcon.getKnowledgeStore().getKnowledge(ComfortBenefit.KNOWLEDGE_KIND) != null) {
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
				double staticCosts = costFunction.getStaticCosts(qconToInclude);
				// if it is already in the path, do nothing
				if (!path.contains(qconToInclude)) {
					Session copiedSession = CostBenefitUtil.createSearchCopy(session);
					for (int i = 0; i < path.getPath().size(); i++) {
						// check if the qcontainer is applicable
						if (CostBenefitUtil.isApplicable(qconToInclude, copiedSession)
								&& isComfortApplicable(qconToInclude, copiedSession)) {
							// check if the dynamic costs are lower or equal to the static costs
							if (costFunction.getCosts(qconToInclude, copiedSession) <= staticCosts) {
								// apply new QContainer
								Session extendedSession = CostBenefitUtil.createSearchCopy(copiedSession);
								applyQContainer(qconToInclude, extendedSession);
								if (CostBenefitUtil.checkPath(path.getPath(), extendedSession, i, true)) {
									path = getNewPath(i, qconToInclude, path);
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
		Log.info("Time for extending path: " + (System.currentTimeMillis() - startTime) + "ms");
	}

	private boolean isComfortApplicable(QContainer container, Session session) {
		//noinspection deprecation
		if (container.getInfoStore().getValue(CostBenefitProperties.COMFORT_BENEFIT)) return true;
		ComfortBenefit comfort = container.getKnowledgeStore().getKnowledge(ComfortBenefit.KNOWLEDGE_KIND);
		if (comfort == null) return false;
		return Conditions.isTrue(comfort.getCondition(), session);
	}

	private void applyQContainer(QContainer qcontainer, Session extendedSession) {
		StateTransition stateTransition = StateTransition.getStateTransition(qcontainer);
		if (stateTransition == null) return;
		CostBenefitUtil.setNormalValues(extendedSession, qcontainer, this);
		stateTransition.fire(extendedSession);
	}

	private Path getNewPath(int position, QContainer qconToInclude, Path path) {
		List<QContainer> qContainers = new LinkedList<>(path.getPath());
		qContainers.add(position, qconToInclude);
		return new ExtendedPath(qContainers, path.getCosts(), path.getNegativeCosts());
	}

	private static final class ExtendedPath implements Path {

		private final List<QContainer> qContainers;
		private final double costs;
		private final double negativeCosts;

		private ExtendedPath(List<QContainer> qContainers, double costs, double negativeCosts) {
			super();
			this.qContainers = qContainers;
			this.costs = costs;
			this.negativeCosts = negativeCosts;
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
		public String toString() {
			return "Extended-Path: " + getPath() + " (costs: " + getCosts() + ")";
		}

		@Override
		public double getNegativeCosts() {
			return negativeCosts;
		}

		@Override
		public boolean contains(QContainer qContainer) {
			return qContainers.contains(qContainer);
		}

		@Override
		public boolean containsAll(Collection<QContainer> qContainers) {
			return this.qContainers.containsAll(qContainers);
		}

		@Override
		public boolean contains(Collection<QContainer> qContainers) {
			for (QContainer qcon : qContainers) {
				if (contains(qcon)) {
					return true;
				}
			}
			return false;
		}
	}
}
