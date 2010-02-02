/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.kernel.psMethods.diagnosisDecisionGraph;

import java.util.Collection;

import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.Facts;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisScore;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.domainModel.DiagnosisState.State;
import de.d3web.kernel.dynamicObjects.CaseDiagnosis;
import de.d3web.kernel.psMethods.PSMethod;
import de.d3web.kernel.psMethods.PropagationEntry;
import de.d3web.kernel.psMethods.heuristic.HeuristicRating;
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

	// private DiagnosisStateComparator diagStateComparator;

	private static PSMethod instance;

	// class DiagnosisStateComparator implements Comparator<DiagnosisState> {
	// private final List<DiagnosisState> allStati = Arrays.asList(
	// new DiagnosisState[] {
	// DiagnosisState.EXCLUDED,
	// DiagnosisState.UNCLEAR,
	// DiagnosisState.SUGGESTED,
	// DiagnosisState.ESTABLISHED });
	//
	// public int compare(DiagnosisState o1, DiagnosisState o2) {
	// int index1 = allStati.indexOf(o1);
	// int index2 = allStati.indexOf(o2);
	// if ((index1 > -1) && (index2 > -1)) {
	// return index1 - index2;
	// }
	// return 0;
	// }
	// }

	public PSMethodDiagnosisDecisionGraph() {
		// diagStateComparator = new DiagnosisStateComparator();
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
			s = new DiagnosisState(State.UNCLEAR);
		}
		else if (s instanceof DiagnosisScore) {
			s = new HeuristicRating(((DiagnosisScore) s).getScore());
		}
		return (DiagnosisState) s;
	}

	public boolean isContributingToResult() {
		return true;
	}

	public void propagate(XPSCase theCase, Collection<PropagationEntry> changes) {
		for (PropagationEntry change : changes) {
			if (change.getObject() instanceof Diagnosis) {
				for (NamedObject diag : change.getObject().getParents()) {
					checkDiagnosis(theCase, (Diagnosis) diag);
				}
			}
		}
	};

	private void checkDiagnosis(XPSCase theCase, Diagnosis parent) {
		String property = (String) parent.getProperties().getProperty(
				Property.DIAGNOSIS_TYPE);

		if ((DIAGNOSIS_TYPE_OR.equalsIgnoreCase(property))
				|| (DIAGNOSIS_TYPE_XOR.equalsIgnoreCase(property))) {
			checkORDiagnosis(theCase, parent);
		}

		else if ((DIAGNOSIS_TYPE_AND.equalsIgnoreCase(property))) {
			checkANDDiagnosis(theCase, parent);
		}

	}

	private void checkORDiagnosis(XPSCase theCase, Diagnosis parent) {
		DiagnosisState maxState = null;
		for (NamedObject diag : parent.getChildren()) {
			DiagnosisState state = getStateOfMostWeighingContext(theCase,
					(Diagnosis) diag);
			if (maxState == null || state.compareTo(maxState) > 0) {
				maxState = state;
			}
		}

		if (!parent.getState(theCase, this.getClass()).equals(maxState)) {
			theCase.setValue(parent, new Object[] { maxState }, this.getClass());
		}
	}

	private void checkANDDiagnosis(XPSCase theCase, Diagnosis parent) {
		DiagnosisState minState = null;
		for (NamedObject diag : parent.getChildren()) {
			DiagnosisState state = getStateOfMostWeighingContext(theCase,
					(Diagnosis) diag);
			if (minState == null || state.compareTo(minState) < 0) {
				minState = state;
			}
		}

		if (!parent.getState(theCase, this.getClass()).equals(minState)) {
			theCase.setValue(parent, new Object[] { minState }, this.getClass());
		}
	}

	/**
	 * Most weighing context is PSMethodUserSelected, then
	 * PSMethodDiagnosisDecisionGraph, then PSMethodHeuristic.
	 */
	private DiagnosisState getStateOfMostWeighingContext(XPSCase theCase, Diagnosis diag) {
		DiagnosisState userState = diag.getState(theCase, PSMethodUserSelected.class);
		if (!userState.hasState(State.UNCLEAR)) {
			return userState;
		}
		DiagnosisState graphState = diag.getState(theCase, PSMethodDiagnosisDecisionGraph.class);
		if (!graphState.hasState(State.UNCLEAR)) {
			return graphState;
		}
		DiagnosisState heuristicState = diag.getState(theCase, PSMethodHeuristic.class);
		if (!heuristicState.hasState(State.UNCLEAR)) {
			return heuristicState;
		}
		return new DiagnosisState(State.UNCLEAR);
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		return Facts.mergeSolutionFacts(facts);
	}
}