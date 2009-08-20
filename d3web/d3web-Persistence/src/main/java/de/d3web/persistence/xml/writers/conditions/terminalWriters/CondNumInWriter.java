package de.d3web.persistence.xml.writers.conditions.terminalWriters;

import de.d3web.kernel.domainModel.NumericalInterval;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondNumIn;
import de.d3web.persistence.xml.loader.NumericalIntervalsCodec;
import de.d3web.persistence.xml.writers.conditions.ConditionWriter;

/**
 * THis is the writer-class for CondNumIn-Objects
 * @author merz
 */
public class CondNumInWriter extends ConditionWriter {

	public String toXML(AbstractCondition ac) {
		CondNumIn cni = (CondNumIn) ac;
		NumericalInterval interval = cni.getInterval();

		String questionId = "";
		if(cni.getQuestion() != null) {
			questionId = cni.getQuestion().getId();
		}
		
		StringBuffer ret = new StringBuffer();
		ret.append("<Condition type='numIn' ID='" + questionId + "'>\n");
		ret.append(NumericalIntervalsCodec.getInstance().encode(interval));
		ret.append("</Condition>");

		return ret.toString();

	}

	/**
	 * @see ConditionWriter#getSourceObject()
	 */
	public Class getSourceObject() {
		return CondNumIn.class;
	}

}
