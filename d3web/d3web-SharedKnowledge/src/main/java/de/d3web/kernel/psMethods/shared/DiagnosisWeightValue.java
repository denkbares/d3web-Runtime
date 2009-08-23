package de.d3web.kernel.psMethods.shared;
/**
 * Insert the type's description here.
 * Creation date: (18.10.2001 19:03:52)
 * @author: Norman Brümmer
 */
public class DiagnosisWeightValue {
	private de.d3web.kernel.domainModel.Diagnosis diagnosis = null;
	private int value = 0;



/**
 * DiagnosisWeightValue constructor comment.
 */
public DiagnosisWeightValue() {
	super();
}



/**
 * Insert the method's description here.
 * Creation date: (18.10.2001 19:04:19)
 * @return de.d3web.kernel.domainModel.Diagnosis
 */
public de.d3web.kernel.domainModel.Diagnosis getDiagnosis() {
	return diagnosis;
}



/**
 * Insert the method's description here.
 * Creation date: (18.10.2001 19:04:32)
 * @return int
 */
public int getValue() {
	return value;
}



/**
 * Insert the method's description here.
 * Creation date: (18.10.2001 19:04:19)
 * @param newDiagnosis de.d3web.kernel.domainModel.Diagnosis
 */
public void setDiagnosis(de.d3web.kernel.domainModel.Diagnosis newDiagnosis) {
	diagnosis = newDiagnosis;
}



/**
 * Insert the method's description here.
 * Creation date: (18.10.2001 19:04:32)
 * @param newValue int
 */
public void setValue(int newValue) {
	value = newValue;
}
}