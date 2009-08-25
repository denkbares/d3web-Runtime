package de.d3web.kernel.psMethods.setCovering.strategies.transitivePropagation;

import java.util.Set;

import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.kernel.psMethods.setCovering.utils.TransitiveClosure;

/**
 * This interface describes a strategy for calculating the transitive covering
 * strength of paths from a diagnosis to a finding
 * 
 * @author bruemmer
 * 
 */
public interface StrengthCalculationStrategy {
	/**
	 * 
	 * @param diag
	 *            start node
	 * @param f
	 *            target node
	 * @return the calculated set of strengths for the paths
	 */
	public Set calculateTransitiveStrengths(TransitiveClosure closure, SCDiagnosis diag,
			PredictedFinding f);
}
