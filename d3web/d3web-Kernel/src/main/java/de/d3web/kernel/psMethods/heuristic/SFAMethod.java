package de.d3web.kernel.psMethods.heuristic;

import java.util.Iterator;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.psMethods.PSSubMethod;
import de.d3web.kernel.supportknowledge.Property;

public class SFAMethod extends PSSubMethod{

	/**
	 * Singleton
	 */
	private static SFAMethod instance = new SFAMethod();
	private SFAMethod() {
		super();
		setContributingToResult(true);
	}
	public static SFAMethod getInstance() {
		return instance;
	}
	
	Class PSCONTEXT = PSMethodHeuristic.class;
		
	
	/**
	 * initialization method for this PSMethod
	 */
	public void init(XPSCase theCase) {
	}
	
	/**
	 * propergates the new value of the given NamedObject for the given XPSCase
	 */
	public void propagate(XPSCase theCase, NamedObject nob, Object[] newValue) {
		if(nob instanceof Diagnosis) {
			Diagnosis diagnosis = (Diagnosis) nob;
			if (canCaseBeReenabled(theCase)) {
				enableCase(theCase);
			}
			if (isEstablished(theCase, diagnosis)) {
				if (isLeafDiagnosis(diagnosis)) {
					disableCase(theCase);
				}
			}
		}	
	}

	
	/**
	 * @param theCase
	 */
	private void disableCase(XPSCase theCase) {
		theCase.getProperties().setProperty(Property.HDT_ABORT_CASE_SFA, Boolean.TRUE);
		
	}		
	
	/**
	 * @param theCase
	 */
	private void enableCase(XPSCase theCase) {
		theCase.getProperties().setProperty(Property.HDT_ABORT_CASE_SFA, Boolean.FALSE);	
		
	}
	/**
	 * @param theCase
	 * @param diagnosis
	 * @return
	 */
	private boolean canCaseBeReenabled(XPSCase theCase) {
		Boolean b = (Boolean)theCase.getProperties().getProperty(Property.HDT_ABORT_CASE_SFA);
		if(b != null 
			&& b.booleanValue()
			&& !containsLeafDianosis(theCase.getDiagnoses(DiagnosisState.ESTABLISHED)))
			return true;
		return false;
	}
	
	/**
	 * @param diagnoses
	 * @return
	 */
	private boolean containsLeafDianosis(List diagnoses) {
		Iterator iter = diagnoses.iterator();
		while (iter.hasNext()) {
			if (isLeafDiagnosis((Diagnosis)iter.next()))
				return true;
		}
		return false;
	}
	private boolean isLeafDiagnosis(Diagnosis diagnosis) {
		return diagnosis.getChildren() == null || diagnosis.getChildren().isEmpty();
	}
	private boolean isEstablished(XPSCase theCase, Diagnosis diagnosis) {
		return diagnosis.getState(theCase, PSCONTEXT).equals(DiagnosisState.ESTABLISHED);
	}
	
	public boolean isActivated(XPSCase theCase) {
		Boolean b = (Boolean)theCase.getKnowledgeBase().getProperties().getProperty(Property.SINGLE_FAULT_ASSUMPTION);
		return  b != null && b.booleanValue(); 
	}
			
}
