/*
 * Created on 16.08.2004 by Chris
 * 
 */
package de.d3web.caserepository.addons.train.findings;

import de.d3web.kernel.domainModel.Diagnosis;

/**
 * DiagnosisRefRelation
 * 
 * @author Chris 16.08.2004
 */
public class DiagnosisRelation extends FindingDiagnosisRelation {

	private Diagnosis diag;

	public DiagnosisRelation() {
		super();
	}

	/**
	 * overridden method
	 * 
	 * @see de.d3web.caserepository.addons.train.findings.FindingDiagnosisRelation#getDiagnosis()
	 */
	public Diagnosis getDiagnosis() {
		return diag;
	}

	public void setDiagnosis(Diagnosis diag) {
		this.diag = diag;
	}
	
	/**
	 * overridden method
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + diag.getId() + ", " + (getScore()==null?null:getScore().getSymbol()) + ")";
	}

}