package de.d3web.costBenefit;

import de.d3web.core.session.XPSCase;
import de.d3web.costBenefit.model.SearchModel;

/**
 * This Interface provides a method to start a search in a model.
 * The targets, the nodes etc. are stored in the model.
 * @author Markus Friedrich (denkbares GmbH)
 */
public interface SearchAlgorithm {

	/**
	 * Starts a search for the targets of the model. The result is stored in the model.
	 * @param theCase
	 * @param model
	 */
	void search(XPSCase theCase, SearchModel model);
}
