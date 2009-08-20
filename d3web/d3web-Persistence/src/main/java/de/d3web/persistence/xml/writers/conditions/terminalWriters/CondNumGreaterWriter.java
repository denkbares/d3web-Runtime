package de.d3web.persistence.xml.writers.conditions.terminalWriters;

import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondNumGreater;
import de.d3web.persistence.xml.writers.conditions.ConditionWriter;

/**
 * This is the writer-class for CondNumGreater-Objects
 * @author merz
 */
public class CondNumGreaterWriter extends ConditionWriter {

	public String toXML(AbstractCondition ac) {
		CondNumGreater cng = (CondNumGreater) ac;

		String questionId = "";
		if(cng.getQuestion() != null) {
			questionId = cng.getQuestion().getId();
		}
		
		return "<Condition type='numGreater' ID='"
			+ questionId
			+ "' value='"
			+ cng.getAnswerValue()
			+ "'/>\n";
	}

	/**
	 * @see ConditionWriter#getSourceObject()
	 */
	public Class getSourceObject() {
		return CondNumGreater.class;
	}

}
