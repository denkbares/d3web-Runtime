package de.d3web.kernel.psMethods.userSelected;
import java.text.RuleBasedCollator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisScore;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.dynamicObjects.CaseDiagnosis;
import de.d3web.kernel.psMethods.PSMethodAdapter;
import de.d3web.kernel.psMethods.PSMethodRulebased;

/**
 * This PSMethod is for user selections (e.g. in a Dialog)
 * Creation date: (03.01.2002 16:17:28)
 * @author Christian Betz
 */
public class PSMethodUserSelected extends PSMethodRulebased {
	private static PSMethodUserSelected instance = null;

	private PSMethodUserSelected() {
		super();
		setContributingToResult(true);
	}

	/**
	 * @return the one and only instance of this PSMethod (Singleton)
	 */
	public static PSMethodUserSelected getInstance() {
		if (instance == null) {
			instance = new PSMethodUserSelected();
		}
		return instance;
	}

	/**
	 * @return the (calculated) state of the given Diagnosis for the current (given) case.
	 * Creation date: (03.01.2002 17:32:38)
	 */
	public DiagnosisState getState(XPSCase theCase, Diagnosis diagnosis) {
		Object value = ((CaseDiagnosis)(theCase.getCaseObject(diagnosis))).getValue(this.getClass());
		if (value instanceof DiagnosisScore) {
			return DiagnosisState.getState((DiagnosisScore) value);
		} else if (value instanceof DiagnosisState) {
			return (DiagnosisState) value;
		} else {
			return DiagnosisState.UNCLEAR;
		}
	}

	/**
	 * @see de.d3web.kernel.psMethods.PSMethod
	 */
	public void init(de.d3web.kernel.XPSCase theCase) {
	}


	public String toString() {
		return "User selections";
	}
}