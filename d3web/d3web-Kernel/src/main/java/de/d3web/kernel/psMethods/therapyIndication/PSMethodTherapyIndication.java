package de.d3web.kernel.psMethods.therapyIndication;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisScore;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.psMethods.PSMethodAdapter;

/**
 * Heuristic problem-solver which adds scores to diagnoses
 * on the basis of question values. If score of a diagnosis exceeds
 * a threshold this diagnosis will be established.
 * Creation date: (28.08.00 18:04:09)
 * @author joba
 */
public class PSMethodTherapyIndication extends PSMethodAdapter {
	private static PSMethodTherapyIndication instance = null;

	private PSMethodTherapyIndication() {
		super();
		setContributingToResult(true);
	}

	/**
	 * Creation date: (04.12.2001 12:36:25)
	 * @return the one and only instance of this ps-method (Singleton)
	 */
	public static PSMethodTherapyIndication getInstance() {
		if (instance == null) {
			instance = new PSMethodTherapyIndication();
		}
		return instance;
	}

	/**
	 * Calculates the state by checking the score of the diagnosis
	 * against a threshold value.
	 * Creation date: (05.10.00 13:41:07)
	 * @return de.d3web.kernel.domainModel.DiagnosisState
	 */
	public DiagnosisState getState(XPSCase theCase, Diagnosis diagnosis) {
		DiagnosisScore diagnosisScore =
			diagnosis.getScore(theCase, this.getClass());
		if (diagnosisScore == null)
			return DiagnosisState.UNCLEAR;
		else
			return DiagnosisState.getState(diagnosisScore);
	}

	/**
	 * Check if NamedObject has nextQASet rules and check them, if available
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
				try {
					RuleComplex rule = (RuleComplex) iter.next();
					rule.check(theCase);
				} catch (Exception e) {
					Logger.getLogger(this.getClass().getName()).throwing(
						this.getClass().getName(), "propagate", e);
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).throwing(
				this.getClass().getName(), "propagate", ex);
		}
	}

	public String toString() {
		return "heuristic problem-solver";
	}
}