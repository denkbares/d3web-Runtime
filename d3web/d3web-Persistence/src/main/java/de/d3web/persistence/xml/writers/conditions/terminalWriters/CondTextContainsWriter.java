package de.d3web.persistence.xml.writers.conditions.terminalWriters;

import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondTextContains;
import de.d3web.persistence.xml.writers.conditions.ConditionWriter;
import de.d3web.xml.utilities.XMLTools;

/**
 * This is the writer-class for CondTextContains-Objects
 * @author merz
 */
public class CondTextContainsWriter extends ConditionWriter {

	public String toXML(AbstractCondition ac) {
		CondTextContains ctc = (CondTextContains) ac;

		String questionId = "";
		if(ctc.getQuestion() != null) {
			questionId = ctc.getQuestion().getId();
		}
		
		return "<Condition type='textContains' ID='"
			+ questionId
			+ "'>\n" +			"<Value>" +			"<![CDATA[" + XMLTools.prepareForCDATA(ctc.getValue()) + "]]>" +			"</Value>\n" +			"</Condition>\n";
	}

	/**
	 * @see ConditionWriter#getSourceObject()
	 */
	public Class getSourceObject() {
		return CondTextContains.class;
	}

}
