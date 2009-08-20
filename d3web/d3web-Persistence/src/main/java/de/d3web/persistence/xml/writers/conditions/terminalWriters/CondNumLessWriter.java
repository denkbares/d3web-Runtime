package de.d3web.persistence.xml.writers.conditions.terminalWriters;

import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondNumLess;
import de.d3web.persistence.xml.writers.conditions.ConditionWriter;

/**
 * This is the writer-class for CondNumLess-Objects
 * @author merz
 */
public class CondNumLessWriter extends ConditionWriter {

	public String toXML(AbstractCondition ac) {
		CondNumLess cnl = (CondNumLess) ac;

		String questionId = "";
		if(cnl.getQuestion() != null) {
			questionId = cnl.getQuestion().getId();
		}
		
		return "<Condition type='numLess' ID='"
			+ questionId
			+ "' value='"
			+ cnl.getAnswerValue()
			+ "'/>\n";
	}

	/**
	 * @see ConditionWriter#getSourceObject()
	 */
	public Class getSourceObject() {
		return CondNumLess.class;
	}

}
