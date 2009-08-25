package de.d3web.kernel.psMethods.setCovering.strategies.transitivePropagation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.kernel.psMethods.setCovering.SCProbability;
import de.d3web.kernel.psMethods.setCovering.SCRelation;
import de.d3web.kernel.psMethods.setCovering.pools.SetPool;
import de.d3web.kernel.psMethods.setCovering.utils.TransitiveClosure;

/**
 * This is the default implementation of StrengthCalculationStrategy. The
 * strengths of paths from a diagnosis to a finding will be calculated by
 * building the mean value of all covering strengths of relations on the path.
 * 
 * @author bruemmer
 * 
 */
public class DefaultStrengthCalculationStrategy implements StrengthCalculationStrategy {

	private static DefaultStrengthCalculationStrategy instance = null;

	private DefaultStrengthCalculationStrategy() {
	}

	public static DefaultStrengthCalculationStrategy getInstance() {
		if (instance == null) {
			instance = new DefaultStrengthCalculationStrategy();
		}
		return instance;
	}

	/**
	 * Calculates the strengths of all paths from diag to f by building the mean
	 * value
	 */
	public Set calculateTransitiveStrengths(TransitiveClosure closure, SCDiagnosis diag,
			PredictedFinding f) {
		Set strengths = new HashSet();
		//Set strengths = SetPool.getInstance().getEmptySet();
		Set paths = closure.getPaths(diag, f);
		Iterator pathIter = paths.iterator();
		while (pathIter.hasNext()) {
			List path = (List) pathIter.next();
			double meanProb = 0;
			Iterator relationIter = path.iterator();
			while (relationIter.hasNext()) {
				SCRelation relation = (SCRelation) relationIter.next();
				SCProbability prob = (SCProbability) relation.getKnowledge(SCProbability.class);

				if (prob != null) {
					meanProb += ((Double) prob.getValue()).doubleValue();
				}
			}
			meanProb /= path.size();
			strengths.add(new Double(meanProb));
		}
		return strengths;
	}

}
