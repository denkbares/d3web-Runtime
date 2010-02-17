package de.d3web.costBenefit.ids;

import de.d3web.core.session.XPSCase;
import de.d3web.costBenefit.AbortStrategy;
import de.d3web.costBenefit.SearchAlgorithm;
import de.d3web.costBenefit.model.SearchModel;

/**
 * Encapsulates the call of a new IterativeDeepeningSearch
 * search. For each call a new instance of the IterativeDeepeningSearch is
 * created.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class IterativeDeepeningSearchAlgorithm implements SearchAlgorithm {

	private AbortStrategy abortStrategy;
	
	@Override
	public void search(XPSCase theCase, SearchModel model) {
		IterativeDeepeningSearch iterativeDeepeningSearch = new IterativeDeepeningSearch(model);
		if (abortStrategy!=null) iterativeDeepeningSearch.setAbortStrategy(abortStrategy);
		iterativeDeepeningSearch.search(theCase);
	}

	public AbortStrategy getAbortStrategy() {
		return abortStrategy;
	}

	public void setAbortStrategy(AbortStrategy abortStrategy) {
		this.abortStrategy = abortStrategy;
	}
	
	
}
