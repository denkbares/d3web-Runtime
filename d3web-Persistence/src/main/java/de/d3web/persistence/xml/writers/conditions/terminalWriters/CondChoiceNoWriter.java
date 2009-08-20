package de.d3web.persistence.xml.writers.conditions.terminalWriters;

import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondChoiceNo;
import de.d3web.persistence.xml.writers.conditions.ConditionWriter;
/**
 * This is the writer-class for CondChoiceNo-Objects
 * @author merz
 */
public class CondChoiceNoWriter extends ConditionWriter {

	public String toXML(AbstractCondition ac) {

		CondChoiceNo ccn = (CondChoiceNo) ac;
		String questionId = "";
		
		if(ccn.getQuestion() != null) {
			questionId = ccn.getQuestion().getId();
		}
		
		return "<Condition type='choiceNo' ID='"
			+ questionId
			+ "'/>\n";
	
	}

	/**
	 * @see ConditionWriter#getSourceObject()
	 */
	public Class getSourceObject() {
		return CondChoiceNo.class;
	}

}
