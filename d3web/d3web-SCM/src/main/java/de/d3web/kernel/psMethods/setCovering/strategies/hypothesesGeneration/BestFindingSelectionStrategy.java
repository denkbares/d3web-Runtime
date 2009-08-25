package de.d3web.kernel.psMethods.setCovering.strategies.hypothesesGeneration;

import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.psMethods.setCovering.ObservableFinding;

/**
 * This strategy selects one out of a List of Findings by a specified criterion
 * 
 * @author bruemmer
 */
public interface BestFindingSelectionStrategy {
	public ObservableFinding selectMaxFinding(List findings, XPSCase theCase);
}
