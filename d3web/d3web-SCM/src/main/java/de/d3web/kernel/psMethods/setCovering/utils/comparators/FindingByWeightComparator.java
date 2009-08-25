package de.d3web.kernel.psMethods.setCovering.utils.comparators;

import java.util.Comparator;
import java.util.Iterator;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.psMethods.setCovering.ObservableFinding;
import de.d3web.kernel.psMethods.setCovering.PSMethodSetCovering;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCNode;
import de.d3web.kernel.psMethods.setCovering.SCRelation;

/**
 * Compares Findings by their weight.
 * 
 * @author bruemmer
 * 
 */
public class FindingByWeightComparator implements Comparator {

	private XPSCase theCase = null;

	public FindingByWeightComparator(XPSCase theCase) {
		this.theCase = theCase;
	}

	/**
	 * @return 1, if finding1 is stronger than finding0, -1 if finding0 is
	 *         stronger than finding1, 0 otherwise.
	 */
	public int compare(Object arg0, Object arg1) {
		try {
			ObservableFinding f0 = (ObservableFinding) arg0;
			ObservableFinding f1 = (ObservableFinding) arg1;

			double diff = f1.getWeight(theCase) - f0.getWeight(theCase);

			if (diff < 0) {
				return -1;
			} else if (diff > 0) {
				return 1;
			} else {
				Iterator relIter = theCase.getKnowledgeBase().getAllKnowledgeSlicesFor(
						PSMethodSetCovering.class).iterator();
				double max0 = 0;
				double max1 = 0;
				while (relIter.hasNext()) {
					Object o = relIter.next();
					if (o instanceof SCRelation) {

						SCRelation relation = (SCRelation) o;
						SCNode target = relation.getTargetNode();
						if (target.isLeaf()) {
							PredictedFinding finding = (PredictedFinding) target;
							if (finding.covers(theCase, f0)) {
								if (max0 < relation.getProbability()) {
									max0 = relation.getProbability();
								}
							}
							if (finding.covers(theCase, f1)) {
								if (max1 < relation.getProbability()) {
									max1 = relation.getProbability();
								}
							}
						}
					}
				}
				if (max0 < max1) {
					return 1;
				} else if (max1 < max0) {
					return -1;
				} else {
					return 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
