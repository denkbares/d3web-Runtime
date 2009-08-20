package de.d3web.persistence.xml.writers.conditions.terminalWriters;

import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondNumLessEqual;
import de.d3web.persistence.xml.writers.conditions.ConditionWriter;

/**
 * This is the writer-class for CondNumLess-Objects
 * @author merz
 */
public class CondNumLessEqualWriter extends ConditionWriter {

	public String toXML(AbstractCondition ac) {
		CondNumLessEqual cnl = (CondNumLessEqual) ac;

		String questionId = "";
		if(cnl.getQuestion() != null) {
			questionId = cnl.getQuestion().getId();
		}
		
		return "<Condition type='numLessEqual' ID='"
			+ questionId
			+ "' value='"
			+ cnl.getAnswerValue()
			+ "'/>\n";
	}

	/**
	 * @see ConditionWriter#getSourceObject()
	 */
	public Class getSourceObject() {
		return CondNumLessEqual.class;
	}

}
