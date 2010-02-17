package de.d3web.costBenefit;

import de.d3web.costBenefit.model.Path;
import de.d3web.costBenefit.model.SearchModel;

/**
 * Strategy which decides, when a calculation should be aborted or not
 * @author Markus Friedrich (denkbares GmbH)
 *
 */
public interface AbortStrategy {

	/**
	 * Initializes the abortion strategy with a searchmodel
	 * @param model
	 */
	void init(SearchModel model);
	
	/**
	 * The next steps always should be committed to the AbortStrategy,
	 * depending on the infomations gained, the calculation can be aborted
	 * by throwing an {@link AbortException}
	 * @param path
	 * @throws AbortException
	 */
	void nextStep(Path path) throws AbortException;
}
