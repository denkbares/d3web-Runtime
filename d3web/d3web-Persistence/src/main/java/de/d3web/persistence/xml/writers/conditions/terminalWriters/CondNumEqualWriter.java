package de.d3web.persistence.xml.writers.conditions.terminalWriters;

import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondNumEqual;
import de.d3web.persistence.xml.writers.conditions.ConditionWriter;

/**
 * This is the writer-class for CondNumEqual-Objects
 * @author merz
 */
public class CondNumEqualWriter extends ConditionWriter {

	public String toXML(AbstractCondition ac) {
		CondNumEqual cne = (CondNumEqual) ac;

		String questionId = "";
		if(cne.getQuestion() != null) {
			questionId = cne.getQuestion().getId();
		}
		
		return "<Condition type='numEqual' ID='"
			+ questionId
			+ "' value='"
			+ cne.getAnswerValue()
			+ "'/>\n";
	}

	/**
	 * @see ConditionWriter#getSourceObject()
	 */
	public Class getSourceObject() {
		return CondNumEqual.class;
	}

}
