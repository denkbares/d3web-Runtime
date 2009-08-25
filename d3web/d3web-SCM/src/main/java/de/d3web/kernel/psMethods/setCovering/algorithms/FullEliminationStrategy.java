package de.d3web.kernel.psMethods.setCovering.algorithms;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.psMethods.setCovering.ObservableFinding;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;

/**
 * Eliminates all findings the ned diagnosis covers
 * 
 * @author bruemmer
 * 
 */
public class FullEliminationStrategy implements EliminationStrategy {

	private static FullEliminationStrategy instance = null;
	private FullEliminationStrategy() {
	}
	public static FullEliminationStrategy getInstance() {
		if (instance == null) {
			instance = new FullEliminationStrategy();
		}
		return instance;
	}

	/**
	 * @see EliminationStrategy#eliminate(XPSCase, SCDiagnosis, List)
	 */
	public List eliminate(XPSCase theCase, SCDiagnosis diag, List unexplained) {
		List newUnexplained = new LinkedList(unexplained);
		Iterator iter = newUnexplained.iterator();
		Hashtable unexplainedById = new Hashtable();
		while (iter.hasNext()) {
			ObservableFinding obs = (ObservableFinding) iter.next();
			unexplainedById.put(obs.getNamedObject().getId(), obs);
		}

		iter = diag.getTransitivePredictedFindings().iterator();
		while (iter.hasNext()) {
			PredictedFinding predicted = (PredictedFinding) iter.next();
			ObservableFinding unex = (ObservableFinding) unexplainedById.get(predicted
					.getNamedObject().getId());
			if (predicted.covers(theCase, unex)) {
				newUnexplained.remove(unex);
			}
		}
		return newUnexplained;
	}

	/**
	 * @see de.d3web.kernel.psMethods.setCovering.algorithms.EliminationStrategy#verbalize()
	 */
	public String verbalize() {
		return "full";
	}

}
