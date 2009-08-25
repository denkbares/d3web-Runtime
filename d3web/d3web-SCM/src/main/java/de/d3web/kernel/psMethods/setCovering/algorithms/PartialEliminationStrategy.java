package de.d3web.kernel.psMethods.setCovering.algorithms;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.psMethods.setCovering.ObservableFinding;
import de.d3web.kernel.psMethods.setCovering.PSMethodSetCovering;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.kernel.psMethods.setCovering.SCProbability;
import de.d3web.kernel.psMethods.setCovering.utils.TransitiveClosure;

/**
 * Eliminates findings from the given unexplained-list by considering the
 * covering-strength of the new diagnosis in comparison with the strength of all
 * yet considered hypotheses. If the maximum of the strengthes is greater than
 * the specified threshold, the finding will be eliminated.
 * 
 * @author bruemmer
 * 
 */
public class PartialEliminationStrategy implements EliminationStrategy {

	private double strengthThreshold = ((Double) SCProbability.P5.getValue()).doubleValue();
	// [TODO]: bates: determine the max. possible prob for each findng. Perhaps
	// P5 is too much.

	private static PartialEliminationStrategy instance = null;

	private PartialEliminationStrategy() {
	}

	public static PartialEliminationStrategy getInstance() {
		if (instance == null) {
			instance = new PartialEliminationStrategy();
		}
		return instance;
	}

	public List eliminate(XPSCase theCase, SCDiagnosis diag, List unexplained) {
		TransitiveClosure closure = PSMethodSetCovering.getInstance().getTransitiveClosure(
				theCase.getKnowledgeBase());
		List ret = new LinkedList(unexplained);
		Iterator unexplainedIter = unexplained.iterator();
		while (unexplainedIter.hasNext()) {
			ObservableFinding f = (ObservableFinding) unexplainedIter.next();
			// retrieve predicted as observed => != null if explained
			PredictedFinding predForF = (PredictedFinding) closure
					.getFindingByNamedObjectAndAnswersEquality(f);
			double diagStrengthForF = diag.getCoveringStrength(predForF);
			if (diagStrengthForF >= strengthThreshold) {
				ret.remove(f);
			}
		}
		return ret;
	}

	/**
	 * @see de.d3web.kernel.psMethods.setCovering.algorithms.EliminationStrategy#verbalize()
	 */
	public String verbalize() {
		return "partial";
	}

}
