package de.d3web.kernel.psMethods;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisScore;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.psMethods.heuristic.PSMethodHeuristic;

/**
 * An adapter class with some empty method bodies and some defulat
 * implementations. Creation date: (27.09.00 14:22:54)
 * 
 * @author Joachim Baumeister
 * @see PSMethod
 */
public abstract class PSMethodAdapter implements PSMethod {
	private boolean contributingToResult = false;

	protected PSMethodAdapter() {
		super();
	}

	/**
	 * By default the problem solver does not count for a diagnosis. :-) Every
	 * problem-solver has to decide how it calculates the state of a diagnosis.
	 * Creation date: (05.10.00 13:41:07)
	 * 
	 * @see DiagnosisState
	 */
	public DiagnosisState getState(XPSCase theCase, Diagnosis diagnosis) {
		DiagnosisScore diagnosisScore = diagnosis.getScore(theCase, PSMethodHeuristic.class);
		if (diagnosisScore == null)
			return DiagnosisState.UNCLEAR;
		else
			return DiagnosisState.getState(diagnosisScore);
	}

	/**
	 * Does nothing.
	 */
	public void init(XPSCase theCase) {
	}

	/**
	 * @see PSMethod
	 */
	public boolean isContributingToResult() {
		return contributingToResult;
	}

	/**
	 * @see PSMethod
	 */
	public void propagate(XPSCase theCase, NamedObject nob, Object[] values) {
	}

	/**
	 * @see PSMethod
	 */
	public void setContributingToResult(boolean newContributingToResult) {
		contributingToResult = newContributingToResult;
	}
}