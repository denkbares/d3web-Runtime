package de.d3web.kernel.psMethods.setCovering.algorithms;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.psMethods.setCovering.strategies.hypothesesGeneration.BestDiagnosesSelectionStrategy;
import de.d3web.kernel.psMethods.setCovering.strategies.hypothesesGeneration.BestFindingSelectionStrategy;

/**
 * @author bruemmer
 * 
 */
public interface HypothesisGenerationAlgorithm {
	public void generateHypotheses(XPSCase theCase, BestFindingSelectionStrategy maxFindStrat,
			BestDiagnosesSelectionStrategy bestDiagStrat, int maxCount, double aprioriThreshold,
			int hypSizeThreshold);
}
