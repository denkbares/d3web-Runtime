package de.d3web.persistence.xml.writers.conditions.nonTerminalWriters;

import java.util.Iterator;

import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondNot;
import de.d3web.persistence.xml.writers.conditions.ConditionWriter;
import de.d3web.persistence.xml.writers.conditions.ConditionsPersistenceHandler;

/**
 * This is the writer-class for CondNot-Objects
 * @author merz
 */
public class CondNotWriter extends ConditionWriter {

	AbstractCondition absCond;

	public String toXML(AbstractCondition ac) {
		CondNot cn = (CondNot) ac;

		String ret = "<Condition type='not'>\n";

		Iterator iter = cn.getTerms().iterator();
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
		return CondNot.class;
	}

}
