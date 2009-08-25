package de.d3web.kernel.psMethods.setCovering.algorithms;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.psMethods.setCovering.Hypothesis;
import de.d3web.kernel.psMethods.setCovering.ObservableFinding;
import de.d3web.kernel.psMethods.setCovering.PSMethodSetCovering;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.kernel.psMethods.setCovering.SCNode;
import de.d3web.kernel.psMethods.setCovering.pools.HypothesisPool;
import de.d3web.kernel.psMethods.setCovering.strategies.hypothesesGeneration.BestDiagnosesSelectionStrategy;
import de.d3web.kernel.psMethods.setCovering.strategies.hypothesesGeneration.BestFindingSelectionStrategy;
import de.d3web.kernel.psMethods.setCovering.utils.FindingsByQuestionIdContainer;
import de.d3web.kernel.psMethods.setCovering.utils.TransitiveClosure;

/**
 * This is the basic greedy-like hypotheses generation algorithm
 * 
 * @author bruemmer
 */
public class EliminationHypothesesGenerationAlgorithm implements HypothesisGenerationAlgorithm {

	private EliminationStrategy eliminationStrategy = null;

	private static EliminationHypothesesGenerationAlgorithm instance = null;
	private EliminationHypothesesGenerationAlgorithm() {
	}

	public static EliminationHypothesesGenerationAlgorithm getInstance() {
		if (instance == null) {
			instance = new EliminationHypothesesGenerationAlgorithm();
		}
		return instance;
	}

	private int DIAGNOSES_SELECTION_COUNT = 10;

	private EliminationStrategy getEliminationStrategy() {
		if (eliminationStrategy == null) {
			// set the default value
			eliminationStrategy = FullEliminationStrategy.getInstance();
		}
		return eliminationStrategy;
	}

	public void setEliminationStrategy(EliminationStrategy eliminationStrategy) {
		this.eliminationStrategy = eliminationStrategy;
	}

	public void setDiagnosesSelectionCount(int k) {
		this.DIAGNOSES_SELECTION_COUNT = k;
	}

	private void removeCompletelyIsolatedFindings(XPSCase theCase, Collection observed) {
		Map findingsByQuestionId = FindingsByQuestionIdContainer.getInstance()
				.getFindingsByQuestionIdsFor(theCase.getKnowledgeBase());
		Iterator iter = observed.iterator();
		while (iter.hasNext()) {
			ObservableFinding obsF = (ObservableFinding) iter.next();
			Collection parametricEq = (Collection) findingsByQuestionId.get(obsF.getNamedObject()
					.getId());
			boolean parametricFound = false;
			if (parametricEq != null) {
				Iterator fIter = parametricEq.iterator();
				while (!parametricFound && fIter.hasNext()) {
					PredictedFinding predF = (PredictedFinding) fIter.next();
					if (predF.covers(theCase, obsF)) {
						parametricFound = true;
					}
				}
			}
			if (!parametricFound) {
				iter.remove();
			}
		}
	}

	/**
	 * generates the diagnoses in the described way.
	 */
	public void generateHypotheses(XPSCase theCase, BestFindingSelectionStrategy maxFindStrat,
			BestDiagnosesSelectionStrategy bestDiagStrat, int maxCount, double aprioriThreshold,
			int maxHypSize) {

		Hypothesis hypothesis = HypothesisPool.getInstance().getEmptyHypothesis();
		Set observed = PSMethodSetCovering.getInstance().getObservedFindings(theCase);

		if (observed == null)
			return;

		List fO = new LinkedList(observed);

		removeCompletelyIsolatedFindings(theCase, fO);

		Set genHyp = PSMethodSetCovering.getInstance().getGlobalHypothesesSet(theCase);
		// initialize global hypotheses set

		Iterator iter = genHyp.iterator();
		while (iter.hasNext()) {
			Hypothesis hyp = (Hypothesis) iter.next();
			HypothesisPool.getInstance().free(hyp);
		}
		genHyp.clear();

		Set allDiagnoses = PSMethodSetCovering.getInstance().getTransitiveClosure(
				theCase.getKnowledgeBase()).getNodes(SCDiagnosis.class);

		generate(theCase, maxFindStrat, bestDiagStrat, genHyp, hypothesis, fO, allDiagnoses,
				maxCount, aprioriThreshold, maxHypSize);
	}

	private void generate(XPSCase theCase, BestFindingSelectionStrategy maxFindStrat,
			BestDiagnosesSelectionStrategy bestDiagStrat, Set generatedHypotheses, Hypothesis hyp,
			List unexplained, Set allDiagnoses, int maxCount, double aprioriThreshold,
			int maxHypSize) {

		ObservableFinding f = maxFindStrat.selectMaxFinding(unexplained, theCase);

		if (f == null) {
			return;
		}

		List bestDiag = bestDiagStrat
				.selectBestKDiagnosesFor(theCase, f, DIAGNOSES_SELECTION_COUNT);
		Iterator bestDiagIter = bestDiag.iterator();
		while (bestDiagIter.hasNext()
				&& !terminate(generatedHypotheses, hyp, unexplained, allDiagnoses, maxCount,
						aprioriThreshold, maxHypSize)) {
			SCDiagnosis diag = (SCDiagnosis) bestDiagIter.next();

			if (!existsAnyPath(theCase, diag, hyp)) {

				Hypothesis newHyp = (Hypothesis) hyp.clone();
				newHyp.addSCDiagnosis(diag);

				generatedHypotheses.add(newHyp);

				List newUnexplained = getEliminationStrategy()
						.eliminate(theCase, diag, unexplained);

				generate(theCase, maxFindStrat, bestDiagStrat, generatedHypotheses, newHyp,
						newUnexplained, allDiagnoses, maxCount, aprioriThreshold, maxHypSize);
			}
		}
	}

	private boolean existsAnyPath(XPSCase theCase, SCDiagnosis diag, Hypothesis hyp) {
		Iterator iter = hyp.getSCDiagnoses().iterator();
		while (iter.hasNext()) {
			SCNode node = (SCNode) iter.next();
			TransitiveClosure closure = PSMethodSetCovering.getInstance().getTransitiveClosure(
					theCase.getKnowledgeBase());
			if (closure.existsPath(diag, node) || closure.existsPath(node, diag)) {
				return true;
			}
		}
		return false;
	}

	private boolean terminate(Set generatedHypotheses, Hypothesis hyp, List unexplained,
			Set allDiagnoses, int maxCount, double aprioriThreshold, int maxHypSize) {

		if (generatedHypotheses.size() >= maxCount) {
			return true;
		}

		if (hyp.getSCDiagnoses().equals(allDiagnoses)) {
			return true;
		}

		if (hyp.getSCDiagnoses().size() >= maxHypSize) {
			return true;
		}

		if (hyp.getSCDiagnoses().isEmpty()) {
			return false;
		}

		if (unexplained.isEmpty()) {
			return true;
		}

		if (hyp.getAprioriProbability() < aprioriThreshold) {
			return true;
		}

		return false;
	}

}
