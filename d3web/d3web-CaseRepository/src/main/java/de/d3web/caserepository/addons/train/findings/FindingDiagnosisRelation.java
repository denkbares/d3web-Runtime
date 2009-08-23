/*
 * Created on 16.08.2004 by Chris
 * 
 */
package de.d3web.caserepository.addons.train.findings;

import de.d3web.kernel.domainModel.Diagnosis;


/**
 * FindingDiagnosisRelation: representing the relation between a single
 * finding an a diagnosis
 * 
 * @author Chris 20.07.2004
 */
public abstract class FindingDiagnosisRelation {

	private Rating score;


	/**
	 * @return Returns the diagnosis.
	 */
	public abstract Diagnosis getDiagnosis();

	/**
	 * @return Returns the score.
	 */
	public Rating getScore() {
		return score;
	}

	/**
	 * @return Returns the score.
	 */
	public void setScore(Rating score) {
		this.score = score;
	}

	
	public String getXMLCode() {
		StringBuffer sb = new StringBuffer();
		sb.append("<FindingDiagnosisRelation id='" + getDiagnosis().getId()
				+ "' rating='" + getScore().getSymbol() + "'/>\n");
		return sb.toString();
	}
}