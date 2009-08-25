package de.d3web.kernel.psMethods.setCovering.utils.comparators;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.psMethods.setCovering.ObservableFinding;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;

/**
 * Compares diagnoses by their covering strength given the specified finding
 * 
 * @author bruemmer
 * 
 */
public class DiagnosisByStrengthComparator implements Comparator {

	private ObservableFinding obsF = null;
	private XPSCase theCase = null;

	public DiagnosisByStrengthComparator(XPSCase theCase, ObservableFinding obsF) {
		this.obsF = obsF;
		this.theCase = theCase;
	}

	public int compare(Object arg0, Object arg1) {

		try {
			SCDiagnosis diag0 = (SCDiagnosis) arg0;
			SCDiagnosis diag1 = (SCDiagnosis) arg1;

			double prob0 = computeProbability(theCase, diag0);
			double prob1 = computeProbability(theCase, diag1);

			if (prob0 < prob1) {
				return 1;
			} else if (prob0 > prob1) {
				return -1;
			} else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private double computeProbability(XPSCase theCase, SCDiagnosis diag) {
		Collection explained = diag.getTransitiveExplainedFindings(theCase);
		Collection paramPred = diag.getTransitiveParametricPredictedFindings(theCase);
		double ret = -1;
		Iterator iter = explained.iterator();
		while (iter.hasNext()) {
			PredictedFinding ef = (PredictedFinding) iter.next();
			if (ef.covers(theCase, obsF)) {
				ret = diag.getTransitiveCoveringStrength(ef);
			}
		}
		if (ret == -1) {
			iter = paramPred.iterator();
			while (iter.hasNext()) {
				PredictedFinding pf = (PredictedFinding) iter.next();
				double prob = diag.getTransitiveCoveringStrength(pf) * pf.calculateSimilarity(obsF);
				if (ret < prob) {
					ret = prob;
				}
			}
		}
		return ret;
	}

}
