package de.d3web.kernel.psMethods.setCovering.algorithms;

import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;

/**
 * This strategy decides if a finding has to be removed from the unexplained
 * list in the EliminationHypothesesGenerationAlgorithm
 * 
 * @author bruemmer
 */

public interface EliminationStrategy {
	/**
	 * Eliminates findings by the defined criterion
	 * 
	 * @param theCase
	 *            current case
	 * @param hyp
	 *            hypothesis to consider
	 * @param diag
	 *            SCDiagnosis to consider
	 * @param unexplained
	 *            observed findings that are not yet considered
	 * @return reduced findings-list without the eliminated findings
	 */
	public List eliminate(XPSCase theCase, SCDiagnosis diag, List unexplained);

	public String verbalize();
}
