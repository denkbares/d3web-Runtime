package de.d3web.kernel.psMethods.compareCase;
import java.util.Iterator;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.psMethods.PSMethod;
import de.d3web.kernel.psMethods.compareCase.comparators.ComparatorResult;

/**
 * This Class represents a ProblemSolver Method which
 * depends on comparing a case with cases from a case repository
 * and taking the solution of the most similar case.
 * Creation date: (02.08.2001 15:38:27)
 * @author: Norman Brümmer
 */
public class PSMethodCaseComparator implements PSMethod {

	private boolean contributingToResult = true;



	/**
	 * this method calculates the resultquotient of a case
	 * Creation date: (08.08.2001 09:24:41)
	 * @return double
	 * @param results java.util.List
	 */
	public static double calcResult(List results) {
		double maxWeight = 0;
		double reachedWeight = 0;

		Iterator iter = results.iterator();
		while (iter.hasNext()) {
			ComparatorResult res = (ComparatorResult) iter.next();
			maxWeight += res.getMaxPoints();
			reachedWeight += res.getReachedPoints();
		}

		return reachedWeight / maxWeight;
	}

	/**
	 * returns the problemsolver context (class object of psmethod)
	 * Creation date: (14.08.2001 14:38:48)
	 * @return java.lang.Class
	 */
	public static Class getProblemSolverContext() {
		return PSMethodCaseComparator.class;
	}

	/**
	 * returns the state of a diagnosis.
	 * the diagnosisState was given by the problemSolver
	 * Creation date: (08.08.2001 09:10:11)
	 * @return EasyXPS.domainModel.DiagnosisState
	 * @param theCase EasyXPS.domainModel.XPSCase
	 */
	public DiagnosisState getState(XPSCase theCase, Diagnosis diagnosis) {
		return diagnosis.getState(theCase, PSMethodCaseComparator.class);
	}

	/**
	 * Some space for initial methods of a PSMethod.
	 * Creation date: (08.08.2001 09:10:11)
	 * @param theCase EasyXPS.domainModel.XPSCase the current case
	 */
	public void init(XPSCase theCase) {
	}

	/**
	 * propagate method comment.
	 */
	public void propagate(XPSCase theCase, NamedObject nob, Object[] values) {
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (07.01.2002 15:48:36)
	 * @return boolean
	 */
	public boolean isContributingToResult() {
		return contributingToResult;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (07.01.2002 15:48:36)
	 * @param newContributionToResult boolean
	 */
	public void setContributionToResult(boolean newContributingToResult) {
		contributingToResult = newContributingToResult;
	}
}