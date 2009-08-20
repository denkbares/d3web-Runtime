package de.d3web.kernel.psMethods.diagnosisDecisionGraph;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisScore;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.dynamicObjects.CaseDiagnosis;
import de.d3web.kernel.psMethods.PSMethod;
import de.d3web.kernel.psMethods.heuristic.PSMethodHeuristic;
import de.d3web.kernel.psMethods.userSelected.PSMethodUserSelected;
import de.d3web.kernel.supportknowledge.Property;

/**
 * @author joba, georg
 */
public class PSMethodDiagnosisDecisionGraph implements PSMethod {

	public static final String DIAGNOSIS_TYPE_OR = "OR";
	public static final String DIAGNOSIS_TYPE_AND = "AND";
	public static final String DIAGNOSIS_TYPE_XOR = "XOR";
	public static final String DIAGNOSIS_TYPE_NORMAL = "NORMAL";

	private DiagnosisStateComparator diagStateComparator;

	private static PSMethod instance;

	class DiagnosisStateComparator implements Comparator {
		private final List allStati = Arrays.asList(new DiagnosisState[]{DiagnosisState.EXCLUDED,
				DiagnosisState.UNCLEAR, DiagnosisState.SUGGESTED, DiagnosisState.ESTABLISHED});

		public int compare(Object o1, Object o2) {
			if ((o1 instanceof DiagnosisState) && (o2 instanceof DiagnosisState)) {
				int index1 = allStati.indexOf(o1);
				int index2 = allStati.indexOf(o2);
				if ((index1 > -1) && (index2 > -1)) {
					return index1 - index2;
				}
			}
			return 0;
		}
	}

	public PSMethodDiagnosisDecisionGraph() {
		diagStateComparator = new DiagnosisStateComparator();
	}

	public static PSMethod getInstance() {
		if (instance == null) {
			instance = new PSMethodDiagnosisDecisionGraph();
		}
		return instance;
	}

	public void init(XPSCase theCase) {
	}

	public DiagnosisState getState(XPSCase theCase, Diagnosis theDiagnosis) {
		Object s = ((CaseDiagnosis) (theCase.getCaseObject(theDiagnosis)))
				.getValue(this.getClass());
		if (s == null) {
			s = DiagnosisState.UNCLEAR;
		} else if (s instanceof DiagnosisScore) {
			s = DiagnosisState.getState((DiagnosisScore) s);
		}
		return (DiagnosisState) s;
	}

	public boolean isContributingToResult() {
		return true;
	}

	public void propagate(XPSCase theCase, NamedObject nob, Object[] newValue) {
		if (nob instanceof Diagnosis) {
			Iterator parentIter = nob.getParents().iterator();
			while (parentIter.hasNext()) {
				Diagnosis diag = (Diagnosis) parentIter.next();
				checkDiagnosis(theCase, diag);
			}
		}

	}

	private void checkDiagnosis(XPSCase theCase, Diagnosis parent) {
		String property = (String) parent.getProperties().getProperty(Property.DIAGNOSIS_TYPE);

		if ((DIAGNOSIS_TYPE_OR.equalsIgnoreCase(property))
				|| (DIAGNOSIS_TYPE_XOR.equalsIgnoreCase(property))) {
			checkORDiagnosis(theCase, parent);
		}

		else if ((DIAGNOSIS_TYPE_AND.equalsIgnoreCase(property))) {
			checkANDDiagnosis(theCase, parent);
		}

	}

	private void checkORDiagnosis(XPSCase theCase, Diagnosis parent) {
		DiagnosisState maxState = DiagnosisState.EXCLUDED;
		Iterator iter = parent.getChildren().iterator();
		while (iter.hasNext()) {
			DiagnosisState state = getStateOfMostWeighingContext(theCase, (Diagnosis) iter.next());
			if (diagStateComparator.compare(state, maxState) > 0) {
				maxState = state;
			}
		}

		if (!parent.getState(theCase, this.getClass()).equals(maxState)) {
			theCase.setValue(parent, new Object[]{maxState}, this.getClass());
		}
	}

	private void checkANDDiagnosis(XPSCase theCase, Diagnosis parent) {
		DiagnosisState minState = DiagnosisState.ESTABLISHED;
		Iterator iter = parent.getChildren().iterator();
		while (iter.hasNext()) {
			DiagnosisState state = getStateOfMostWeighingContext(theCase, (Diagnosis) iter.next());
			if (diagStateComparator.compare(state, minState) < 0) {
				minState = state;
			}
		}

		if (!parent.getState(theCase, this.getClass()).equals(minState)) {
			theCase.setValue(parent, new Object[]{minState}, this.getClass());
		}
	}

	/**
	 * Most weighing context is PSMethodUserSelected, then
	 * PSMethodDiagnosisDecisionGraph, then PSMethodHeuristic.
	 */
	private DiagnosisState getStateOfMostWeighingContext(XPSCase theCase, Diagnosis diag) {
		DiagnosisState userState = diag.getState(theCase, PSMethodUserSelected.class);
		if (!DiagnosisState.UNCLEAR.equals(userState)) {
			return userState;
		} else {
			DiagnosisState decisionGraphState = diag.getState(theCase,
					PSMethodDiagnosisDecisionGraph.class);
			if (!DiagnosisState.UNCLEAR.equals(decisionGraphState)) {
				return decisionGraphState;
			} else {
				DiagnosisState heuristicState = diag.getState(theCase, PSMethodHeuristic.class);
				if (!DiagnosisState.UNCLEAR.equals(heuristicState)) {
					return heuristicState;
				} else {
					return DiagnosisState.UNCLEAR;
				}
			}
		}
	}
}