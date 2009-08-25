package de.d3web.kernel.psMethods.setCovering.strategies.hypothesesGeneration;

import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.psMethods.setCovering.ObservableFinding;

/**
 * This strategy selects the k best Diagnoses from a given Set of diagnoses by a
 * specified criterion
 * 
 * @author bruemmer
 * 
 */
public interface BestDiagnosesSelectionStrategy {
	public List selectBestKDiagnosesFor(XPSCase theCase, ObservableFinding f, int k);
}
