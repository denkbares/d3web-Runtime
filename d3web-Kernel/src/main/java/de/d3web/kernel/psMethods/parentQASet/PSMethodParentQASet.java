package de.d3web.kernel.psMethods.parentQASet;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.psMethods.PSMethodAdapter;

/**
 * This is a psmethod to mark QContainers (QASets) which are (contra-)indicated
 * due to the (contra-)indication of a parent-QContainer (QASet).
 * @author Georg
 */
public class PSMethodParentQASet extends PSMethodAdapter {

	private static PSMethodParentQASet instance = null;

	private PSMethodParentQASet() {
		super();
		setContributingToResult(false);
	}

	/**
	 * @return the one and only instance of this PSMethod (Singleton)
	 */
	public static PSMethodParentQASet getInstance() {
		if (instance == null) {
			instance = new PSMethodParentQASet();
		}
		return instance;
	}


	public DiagnosisState getState(XPSCase theCase, Diagnosis diagnosis) {
		return null;
	}

}
