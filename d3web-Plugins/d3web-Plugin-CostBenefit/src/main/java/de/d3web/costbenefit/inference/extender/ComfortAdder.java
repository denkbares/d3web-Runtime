/*
 * Copyright (C) 2020 denkbares GmbH, Germany
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

package de.d3web.costbenefit.inference.extender;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import de.d3web.core.inference.condition.Conditions;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.session.Session;
import de.d3web.costbenefit.CostBenefitUtil;
import de.d3web.costbenefit.inference.ComfortBenefit;
import de.d3web.costbenefit.inference.CostBenefitProperties;
import de.d3web.costbenefit.inference.CostFunction;
import de.d3web.costbenefit.inference.PSMethodCostBenefit;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.costbenefit.model.Path;
import de.d3web.costbenefit.model.SearchModel;

/**
 * Path modifier that adds the test steps to a specified path that have comfort benefit and applies to the path.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 24.02.2020
 */
class ComfortAdder implements PathModifier {

	private List<QContainer> qcontainersToAdd;

	private void init(SearchModel model) {
		if (qcontainersToAdd != null) return;
		qcontainersToAdd = new ArrayList<>();
		for (QContainer qcon : model.getSession().getKnowledgeBase().getManager().getQContainers()) {
			//noinspection deprecation
			if (qcon.getInfoStore().getValue(CostBenefitProperties.COMFORT_BENEFIT)
					|| qcon.getKnowledgeStore().getKnowledge(ComfortBenefit.KNOWLEDGE_KIND) != null) {
				qcontainersToAdd.add(qcon);
			}
		}
	}

	@Override
	public boolean canApply(SearchModel model) {
		init(model);
		return !qcontainersToAdd.isEmpty() && model.getBestCostBenefitPath() != null;
	}

	@Override
	@NotNull
	public Path calculatePath(Session copiedSession, SearchModel model) {
		init(model);

		// only called when canApply succeeded
		Path path = model.getBestCostBenefitPath();
		assert path != null;

		for (QContainer qconToInclude : qcontainersToAdd) {
			// if it is already in the path, do nothing
			if (path.contains(qconToInclude)) continue;

			// try to expand for the current comfort test step
			path = extend(CostBenefitUtil.createDecoratedSession(copiedSession), model, path, qconToInclude);
		}

		return path;
	}

	@NotNull
	private Path extend(Session copiedSession, SearchModel model, Path path, QContainer qconToInclude) {
		CostFunction costFunction = model.getSession().getPSMethodInstance(PSMethodCostBenefit.class).getCostFunction();
		double staticCosts = costFunction.getStaticCosts(qconToInclude);
		for (int i = 0; i < path.getPath().size(); i++) {
			// check if the qcontainer is applicable
			if (CostBenefitUtil.isApplicable(qconToInclude, copiedSession)
					&& isComfortApplicable(qconToInclude, copiedSession)) {
				// check if the dynamic costs are lower or equal to the static costs
				if (costFunction.getCosts(qconToInclude, copiedSession) <= staticCosts) {
					// apply new QContainer
					// TODO use decoraded session here?!
					Session extendedSession = CostBenefitUtil.createDecoratedSession(copiedSession);
					applyQContainer(qconToInclude, extendedSession);
					if (CostBenefitUtil.checkPath(path.getPath(), extendedSession, i, true)) {
						// we find a position to integrate the comfort test step, to add to path and return
						return getNewPath(i, qconToInclude, path);
					}
				}
			}
			QContainer actualQContainer = path.getPath().get(i);
			applyQContainer(actualQContainer, copiedSession);
		}

		// cannot find a position to integrate the comfort test step, so return original path
		return path;
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
}
