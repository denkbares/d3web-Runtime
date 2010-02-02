package de.d3web.kernel.psMethods.xclPattern;

import de.d3web.kernel.domainModel.DiagnosisState;

public class XCLRating extends DiagnosisState {

	private final XCLInferenceTrace inferenceTrace;

	public XCLRating(State state, XCLInferenceTrace inferenceTrace) {
		super(state);
		this.inferenceTrace = inferenceTrace;
	}

	/**
	 * @return the inferenceTrace
	 */
	public XCLInferenceTrace getInferenceTrace() {
		return inferenceTrace;
	}

	/**
	 * Returns the total score of this set covering rating.
	 * 
	 * @return the score of this rating
	 */
	public double getScore() {
		return inferenceTrace.getScore();
	}

	/**
	 * Returns the support of this set covering rating.
	 * 
	 * @return the support of this rating
	 */
	public double getSupport() {
		return inferenceTrace.getSupport();
	}

	@Override
	public int hashCode() {
		return (int) (super.hashCode() + 11 * getScore() + 47 * getSupport());
	}

	// oh my godness, here joba will cry...
	// TODO: how to order two set covering ratings if one has higher rating but less support?
	@Override
	public int compareTo(DiagnosisState otherState) {
		if (otherState instanceof XCLRating) {
			// if both ratings are heuristic ones, ...
			XCLRating other = (XCLRating) otherState;
			double deltaScore = this.getScore() - other.getScore();
			if (Math.abs(deltaScore) > 1e-6) {
				// compare by score if they are not almost identical
				return (int) Math.signum(deltaScore);
			}
			else {
				// otherwise compare the support
				double deltaSupport = this.getSupport() - other.getSupport();
				return (int) Math.signum(deltaSupport);
			}
		}
		else {
			// otherwise use common compare of ratings
			return super.compareTo(otherState);
		}
	}


}
