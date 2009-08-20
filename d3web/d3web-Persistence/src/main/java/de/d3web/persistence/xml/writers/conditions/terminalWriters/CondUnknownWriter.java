package de.d3web.persistence.xml.writers.conditions.terminalWriters;

import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondUnknown;
import de.d3web.persistence.xml.writers.conditions.ConditionWriter;

/**
 * This is the writer-class for CondUnknown-Objects
 * @author merz
 */
public class CondUnknownWriter extends ConditionWriter {

	public String toXML(AbstractCondition ac) {
		CondUnknown cu = (CondUnknown) ac;

		String questionId = "";
		if(cu.getQuestion() != null) {
			questionId = cu.getQuestion().getId();
		}
		
		return "<Condition type='unknown' ID='"
			+ questionId
			+ "'/>\n";
	}

	/**
	 * @see ConditionWriter#getSourceObject()
	 */
	public Class getSourceObject() {
		return CondUnknown.class;
	}

}
