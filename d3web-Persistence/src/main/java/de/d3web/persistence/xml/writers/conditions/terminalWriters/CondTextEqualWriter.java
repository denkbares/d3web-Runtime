package de.d3web.persistence.xml.writers.conditions.terminalWriters;

import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondTextEqual;
import de.d3web.persistence.xml.writers.conditions.ConditionWriter;
import de.d3web.xml.utilities.XMLTools;

/**
 * This is the writer-class for CondTextEqual-Objects
 * @author merz
 */
public class CondTextEqualWriter extends ConditionWriter {

	public String toXML(AbstractCondition ac) {
		CondTextEqual cte = (CondTextEqual) ac;

		String questionId = "";
		if(cte.getQuestion() != null) {
			questionId = cte.getQuestion().getId();
		}
		
		return "<Condition type='textEqual' ID='"
			+ questionId
			+ "'>\n" +
			"<Value>" +
			"<![CDATA[" + XMLTools.prepareForCDATA(cte.getValue()) + "]]>" +
			"</Value>\n" +
			"</Condition>\n";
	}

	/**
	 * @see ConditionWriter#getSourceObject()
	 */
	public Class getSourceObject() {
		return CondTextEqual.class;
	}

}
