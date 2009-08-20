package de.d3web.kernel.psMethods;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.NamedObject;

/**
 * This is a 'marker' psmethod to represent all the initial values.
 * Especially used to add the initQASets to the QASetManager
 * Creation date: (21.02.2002 16:51:10)
 * @author Christian Betz
 */
public class PSMethodInit implements PSMethod {
	private static PSMethodInit instance = null;

	public static PSMethodInit getInstance() {
		if (instance == null) {
			instance = new PSMethodInit();
		}
		return instance;
	}

	public PSMethodInit() {
		super();
	}

	/**
	 * @return null
	 */
	public DiagnosisState getState(XPSCase theCase, Diagnosis theDiagnosis) {
		return null;
	}

	/**
	 * Some space for initial methods of a PSMethod.
	 * Does nothing.
	 * Creation date: (21.02.2002 16:51:10)
	 */
	public void init(XPSCase theCase) {
	}

	/**
	 * Indicates whether the problemsolver contributes to XPSCase.getDiagnoses(DiangosisState)
	 * Creation date: (21.02.2002 16:51:10)
	 * @return false
	 */
	public boolean isContributingToResult() {
		return false;
	}

	/**
	 * @see PSMethod
	 */
	public void propagate(
		XPSCase theCase,
		NamedObject nob,
		Object[] newValue) {
	}
}