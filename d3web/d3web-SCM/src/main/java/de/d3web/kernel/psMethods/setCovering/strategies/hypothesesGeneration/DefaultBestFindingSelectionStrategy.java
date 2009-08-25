package de.d3web.kernel.psMethods.setCovering.strategies.hypothesesGeneration;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.psMethods.setCovering.ObservableFinding;
import de.d3web.kernel.psMethods.setCovering.utils.comparators.FindingByWeightComparator;

/**
 * @author bruemmer
 * 
 */
public class DefaultBestFindingSelectionStrategy implements BestFindingSelectionStrategy {

	private static DefaultBestFindingSelectionStrategy instance = null;
	private DefaultBestFindingSelectionStrategy() {
	}
	public static DefaultBestFindingSelectionStrategy getInstance() {
		if (instance == null) {
			instance = new DefaultBestFindingSelectionStrategy();
		}
		return instance;
	}

	public ObservableFinding selectMaxFinding(List unexplained, XPSCase theCase) {
		Comparator comparator = new FindingByWeightComparator(theCase);
		List findings = new LinkedList(unexplained);

		if ((findings != null) && !findings.isEmpty()) {
			Collections.sort(findings, comparator);
			return (ObservableFinding) findings.get(0);
		}
		return null;
	}

}
