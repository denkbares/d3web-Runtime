package de.d3web.kernel.psMethods.dialogControlling;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.psMethods.PSMethod;
import de.d3web.kernel.psMethods.combinied.PSMethodCombined;

/**
 * This is a combined PSMethod used for dialog controlling
 * @author Christian Betz
 */
public class PSMethodDialogControlling extends PSMethodCombined {

	public PSMethodDialogControlling() {
		super();
	}

	/**
	 * @return the maximum of scores as DiagnosisState.
	 * Creation date: (03.01.2002 16:17:28)
	 */
	public DiagnosisState getState(XPSCase theCase, Diagnosis theDiagnosis) {

		DiagnosisState diagnosisState = null;
		Iterator iter = getPSMethods().iterator();
		while (iter.hasNext()) {
			PSMethod psMethod = (PSMethod) iter.next();
			DiagnosisState dState = psMethod.getState(theCase, theDiagnosis);

			if (dState != null && dState.compareTo(diagnosisState) > 0) {
				diagnosisState = dState;
			}
		}

		if (diagnosisState == null)
			return DiagnosisState.UNCLEAR;
		else
			return diagnosisState;
	}

	/**
	 * Retrieves all dialog controlling PSMethods from the given XPSCase
	 * Creation date: (03.01.2002 16:17:28)
	 */
	public void init(XPSCase theCase) {
		setPSMethods(new LinkedList(theCase.getDialogControllingPSMethods()));
	}

	/**
	 * @see PSMethod
	 */
	public void propagate(
		XPSCase theCase,
		NamedObject nob,
		Object[] newValue) {
		try {
			List knowledgeSlices = (nob.getKnowledge(this.getClass()));
			if (knowledgeSlices == null) {
				return;
			}
			Iterator iter = knowledgeSlices.iterator();
			while (iter.hasNext()) {
				RuleComplex rule = (RuleComplex) iter.next();
				rule.check(theCase);
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).throwing(
				this.getClass().getName(), "propagate", ex);
		}
	}

	public String toString() {
		return "PSMethodDialogControlling";
	}
}