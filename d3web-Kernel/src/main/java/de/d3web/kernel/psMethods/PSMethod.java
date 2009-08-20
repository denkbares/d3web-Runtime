package de.d3web.kernel.psMethods;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.NamedObject;

/**
 * Interface for all problen-solver methods to implement.
 * Each XPSCase has a list of currently used problem-solvers.
 * They are notified, if some value (question or diagnosis)
 * has changed.
 * Creation date: (28.08.00 17:22:54)
 * @author joba
 */
public interface PSMethod {

	/**
	 * Every problem-solver has to decide how it calculates
 	 * the state of a diagnosis.
	 * @return the DiagnosisState of the given Diagnosis depending on the given XPSCase
	 */
	public DiagnosisState getState(XPSCase theCase, Diagnosis theDiagnosis);

	/**
	 * initialization method for this PSMethod
	 */
	public void init(XPSCase theCase);
	
	/**
	 * Indicates whether the problemsolver contributes to XPSCase.getDiagnoses(DiangosisState)
	 */
	public boolean isContributingToResult();

	/**
	 * propergates the new value of the given NamedObject for the given XPSCase
	 */
	public void propagate(XPSCase theCase, NamedObject nob, Object[] newValue);
}