package de.d3web.costBenefit;

import de.d3web.core.session.XPSCase;
import de.d3web.core.terminology.QContainer;

/**
 * This interface provides a method to calculate the costs of a QContainer
 * depending on a case.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public interface CostFunction {
	/**
	 * Calculates the costs of a qcontainer in dependency on theCase.
	 * 
	 * @param qcon
	 * @param theCase
	 * @return
	 */
	double getCosts(QContainer qcon, XPSCase theCase);
}
