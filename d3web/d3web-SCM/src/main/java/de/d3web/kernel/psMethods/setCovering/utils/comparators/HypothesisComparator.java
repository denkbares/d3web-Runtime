package de.d3web.kernel.psMethods.setCovering.utils.comparators;

import java.util.Comparator;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.psMethods.setCovering.Hypothesis;

/**
 * Compares Hypotheses by their quality
 * 
 * @author bruemmer
 * 
 */
public class HypothesisComparator implements Comparator {

	private XPSCase theCase = null;

	public HypothesisComparator(XPSCase theCase) {
		this.theCase = theCase;
	}

	/**
	 * Compares two hypotheses by their quality. If the quality is equal, they
	 * will be compared by their false prediction account.
	 * 
	 * @return -1, if hyp0 has greater quality or smaller fpc, 0 if both values
	 *         are equal, -1 otherwise
	 */
	public int compare(Object arg0, Object arg1) {
		try {

			Hypothesis hyp0 = (Hypothesis) arg0;
			Hypothesis hyp1 = (Hypothesis) arg1;

			double diff = hyp1.getQuality(theCase) - hyp0.getQuality(theCase);
			if (diff == 0) {
				diff = hyp0.getFalsePredictionAccount(theCase)
						- hyp1.getFalsePredictionAccount(theCase);
			}

			if (diff < 0) {
				return -1;
			} else if (diff > 0) {
				return 1;
			} else
				return 0;

		} catch (Exception e) {
			return 0;
		}
	}

}
