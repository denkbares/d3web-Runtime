package de.d3web.persistence.xml.writers.conditions.terminalWriters;

import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondChoiceYes;
import de.d3web.persistence.xml.writers.conditions.ConditionWriter;
/**
 * This is the writer-class for CondChoiceYes-Objects
 * @author merz
 */
public class CondChoiceYesWriter extends ConditionWriter {

	public String toXML(AbstractCondition ac) {

		CondChoiceYes ccy = (CondChoiceYes) ac;
		String questionId = "";
		
		if(ccy.getQuestion() != null) {
			questionId = ccy.getQuestion().getId();
		}
		return "<Condition type='choiceYes' ID='"
			+ questionId
			+ "'/>\n";
	}

	/**
	 * @see ConditionWriter#getSourceObject()
	 */
	public Class getSourceObject() {
		return CondChoiceYes.class;
	}

}
