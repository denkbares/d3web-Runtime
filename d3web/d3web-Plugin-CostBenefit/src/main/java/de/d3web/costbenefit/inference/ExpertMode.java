/*
 * Copyright (C) 2011 denkbares GmbH, Germany
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.session.CaseObjectSource;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.costbenefit.blackboard.CostBenefitCaseObject;
import de.d3web.costbenefit.model.SearchModel;
import de.d3web.costbenefit.model.Target;

/**
 * This class provides utility functions to enable an advanced user mode. Within
 * that mode the user is enabled to also see additional Targets with high
 * benefit (regardless to their costs). The user is also enabled to select on of
 * these targets. If the user has selected on the cost benefit strategic solver
 * is in charge to calculate a new path towards this target.
 * 
 * @author volker_belli
 * @created 07.03.2011
 */
public class ExpertMode extends SessionObject {

	private final Session session;

	private final static CaseObjectSource EXPERT_MODE_SOURCE = new CaseObjectSource() {

		@Override
		public SessionObject createCaseObject(Session session) {
			return new ExpertMode(session);
		}
	};

	private static final Comparator<Target> BENEFIT_COMPARATOR = new Comparator<Target>() {

		@Override
		public int compare(Target target1, Target target2) {
			// sort by the costs of the target
			return (int) Math.signum(target1.getBenefit() - target2.getBenefit());
		}
	};

	private ExpertMode(Session session) {
		super(EXPERT_MODE_SOURCE);
		this.session = session;
	}

	/**
	 * Returns the ExportMode object for the specified session.
	 * 
	 * @created 07.03.2011
	 * @param session the session to get the expert mode instance for
	 * @return the ExpertMode instance
	 */
	public static ExpertMode getExpertMode(Session session) {
		return (ExpertMode) session.getCaseObject(EXPERT_MODE_SOURCE);
	}

	/**
	 * Returns a list of all alternative targets that can be suggested to the
	 * user. The list is sorted by the targets benefit, regardless to the costs
	 * they may induce.
	 * <p>
	 * Please note that the calculated information contained in the
	 * {@link Target} (namely the minimal path and the benefit of the target),
	 * are based on the latest search of the cost benefit underlying search
	 * algorithm. Due to already answered test steps the minimal path may have
	 * become no longer applicable.
	 * 
	 * @created 07.03.2011
	 * @param session
	 * @return
	 */
	public List<Target> getAlternativeTargets() {
		PSMethodCostBenefit psm = getPSMethodCostBenefit();
		CostBenefitCaseObject pso = getCostBenefitCaseObject(psm);
		SearchModel searchModel = pso.getSearchModel();

		// create a list of all targets without the currently selected one
		List<Target> result = new ArrayList<Target>();
		result.addAll(pso.getDiscriminatingTargets());
		result.remove(searchModel.getBestCostBenefitTarget());

		// sort the list regarding to their benefit to become our result
		Collections.sort(result, BENEFIT_COMPARATOR);
		return Collections.unmodifiableList(result);
	}

	private CostBenefitCaseObject getCostBenefitCaseObject(PSMethodCostBenefit psm) {
		return (CostBenefitCaseObject) session.getCaseObject(psm);
	}

	private PSMethodCostBenefit getPSMethodCostBenefit() {
		return session.getPSMethodInstance(PSMethodCostBenefit.class);
	}

	/**
	 * Selects a new target for the interview strategy. This makes the cost
	 * benefit problem solver to arrange a new questionnaire sequence (path) to
	 * cover the selected target. Because it might be complex to find such a
	 * path (covering the target and preparing its preconditions), this
	 * operation may require a long calculation.
	 * <p>
	 * The selected target and the created path are valid until the user answer
	 * some unexpected value. If he does the path becomes invalid and the cost
	 * benefit strategic solver is in charge to create a new path to the
	 * cost/benefit optimal target (based on the new situation). If this
	 * behavior is not desired a new target has to be selected manually
	 * afterwards, using this method again.
	 * <p>
	 * <b>Note:</b><br>
	 * If the search algorithm is not able to provide such a path or if it
	 * implements an abort strategy that prevents the algorithm from finding
	 * such a path this method results in an {@link AbortException}.
	 * 
	 * @created 07.03.2011
	 * @param target the target to select
	 * @throws AbortException
	 * @see AbortException
	 * @see AbortStrategy
	 * @see SearchAlgorithm
	 * @see Target
	 */
	public void selectTarget(Target target) throws AbortException {
		PSMethodCostBenefit psm = getPSMethodCostBenefit();
		CostBenefitCaseObject pso = getCostBenefitCaseObject(psm);

		psm.calculateNewPathTo(pso, target);
	}

	public void selectTarget(QContainer targetQuestionnaire) throws AbortException {
		Target target = new Target(targetQuestionnaire);
		selectTarget(target);
	}

}
