package de.d3web.costBenefit;

import de.d3web.costBenefit.model.Path;
import de.d3web.costBenefit.model.SearchModel;

public class NoAbortStrategie implements AbortStrategy {

	@Override
	public void init(SearchModel model) {
	}

	@Override
	public void nextStep(Path path) throws AbortException {
	}

}
