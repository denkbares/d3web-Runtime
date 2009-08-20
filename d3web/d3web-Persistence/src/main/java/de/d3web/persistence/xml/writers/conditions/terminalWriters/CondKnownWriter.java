package de.d3web.persistence.xml.writers.conditions.terminalWriters;

import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondKnown;
import de.d3web.persistence.xml.writers.conditions.ConditionWriter;

/**
 * This is the writer-class for CondKnown-Objects
 * @author merz
 */
public class CondKnownWriter extends ConditionWriter {

	public String toXML(AbstractCondition ac) {
		CondKnown cu = (CondKnown) ac;

		String questionId = "";
		if(cu.getQuestion() != null) {
			questionId = cu.getQuestion().getId();
		}
		
		return "<Condition type='known' ID='"
			+ questionId
			+ "'/>\n";
	}
	/**
	 * @see ConditionWriter#getSourceObject()
	 */
	public Class getSourceObject() {
		return CondKnown.class;
	}
}
