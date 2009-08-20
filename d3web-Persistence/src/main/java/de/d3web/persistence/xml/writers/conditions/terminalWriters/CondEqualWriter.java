package de.d3web.persistence.xml.writers.conditions.terminalWriters;

import java.util.Iterator;
import java.util.logging.Logger;

import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerUnknown;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondEqual;
import de.d3web.persistence.xml.writers.conditions.ConditionWriter;
/**
 * This is the writer-class for CondEqual-Objects
 * @author merz
 */
public class CondEqualWriter extends ConditionWriter {

	public String toXML(AbstractCondition ac) {
		CondEqual ce = (CondEqual) ac;

		String questionId = "";
		if(ce.getQuestion() != null) {
			questionId = ce.getQuestion().getId();
		}
		
		String ret =
			"<Condition type='equal' ID='"
				+ questionId
				+ "' value='";

		Iterator iter = ce.getValues().iterator();

		if (iter.hasNext()) {
			ret += this.getId(iter.next());
		}

		while (iter.hasNext()) {
			ret += "," + this.getId(iter.next());
		}

		ret += "'/>\n";

		return ret;
	}

	/**
	 * @see ConditionWriter#getSourceObject()
	 */
	public Class getSourceObject() {
		return CondEqual.class;
	}

	private String getId(Object answer) {
		if (answer instanceof AnswerChoice)
			return ((AnswerChoice) answer).getId();
		else if (answer instanceof AnswerUnknown)
			return ((AnswerUnknown) answer).getId();
		else {
			Logger.getLogger(this.getClass().getName()).warning(
				"Could not convert "
					+ answer
					+ ". Check "
					+ this.getClass()
					+ ".getId(Object)!");
			return answer.toString();
		}
	}

}
