package de.d3web.kernel.psMethods.setCovering.strategies.evaluation;

import java.util.Set;

/**
 * This interface describes the main functionality of an evaluation stategy for
 * hypotheses.
 * 
 * @author bates
 */
public interface HypothesisEvaluationStrategy {
	/**
	 * computes the quality of the given predicted findings using the observed
	 * findings.
	 * 
	 * @return the quality of the predicted findings
	 */
	public double computeQuality(Set diagnoses, Set predictedFindings, Set observedFindings,
			double hypothesisAprioriProbability);
}
