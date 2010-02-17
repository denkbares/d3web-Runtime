package de.d3web.costBenefit;

import de.d3web.costBenefit.model.Path;
import de.d3web.costBenefit.model.SearchModel;

/**
 * The DefaultAbortyStrategy throws an AbortException, when the maximum amount
 * of steps is exceeded and at least one target is reached.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class DefaultAbortStrategy implements AbortStrategy {

	private long steps;

	private long maxsteps;
	private SearchModel model;

	@Override
	public void init(SearchModel model) {
		steps = 0;
		this.model = model;
	}

	@Override
	public void nextStep(Path path) throws AbortException {
		steps++;
		if ((steps >= maxsteps && model.oneTargetReached())||(steps >= maxsteps*10)) {
			throw new AbortException();
		}

	}
	
	public DefaultAbortStrategy(long steps) {
		maxsteps=steps;
	}
	
	public DefaultAbortStrategy() {
		// about 1,4 sec on my laptop ;-)
		this(100000);
	}

}
