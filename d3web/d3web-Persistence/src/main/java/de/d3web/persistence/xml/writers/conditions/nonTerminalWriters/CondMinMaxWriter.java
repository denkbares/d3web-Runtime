package de.d3web.persistence.xml.writers.conditions.nonTerminalWriters;

import java.util.Iterator;

import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondMofN;
import de.d3web.persistence.xml.writers.conditions.ConditionWriter;
import de.d3web.persistence.xml.writers.conditions.ConditionsPersistenceHandler;

/**
 * This is the writer class for CondMinMax-Objects
 * @author merz
 */
public class CondMinMaxWriter extends ConditionWriter {

	AbstractCondition absCond;

	public String toXML(AbstractCondition ac) {

		CondMofN cmn = (CondMofN) ac;

		String ret =
			"<Condition type='MofN' min='"
				+ cmn.getMin()
				+ "' max='"
				+ cmn.getMax()
				+ "' size='"
				+ cmn.getTerms().size()
				+ "'>\n";

		Iterator iter = cmn.getTerms().iterator();
		while (iter.hasNext()) {
			absCond = (AbstractCondition) iter.next();
			ret += ConditionsPersistenceHandler.getInstance().toXML(absCond);
		}

		return ret + "</Condition>\n";
	}

	/**
	 * @see ConditionWriter#getSourceObject()
	 */
	public Class getSourceObject() {
		return CondMofN.class;
	}

}
