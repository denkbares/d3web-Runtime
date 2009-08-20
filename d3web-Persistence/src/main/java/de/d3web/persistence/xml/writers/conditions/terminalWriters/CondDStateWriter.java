package de.d3web.persistence.xml.writers.conditions.terminalWriters;

import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondDState;
import de.d3web.persistence.xml.writers.conditions.ConditionWriter;

/**
 * This is the writer-class for CondDState-Objects
 * @author merz
 */
public class CondDStateWriter extends ConditionWriter {

	public String toXML(AbstractCondition ac) {
		CondDState cds = (CondDState) ac;

		String diagnosisId = "";
		String status = "";
		
		if(cds.getDiagnosis() != null) {
			diagnosisId = cds.getDiagnosis().getId();
		}
		if(cds.getStatus() != null) {
			status = cds.getStatus().toString();
		}
		
		
		return "<Condition type='DState' ID='"
			+ diagnosisId
			+ "' value='"
			+ status
			+ "'/>\n";
	}
	/**
	 * @see ConditionWriter#getSourceObject()
	 */
	public Class getSourceObject() {
		return CondDState.class;
	}

}
