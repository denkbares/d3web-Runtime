package de.d3web.persistence.xml.writers.conditions.nonTerminalWriters;

import java.util.Iterator;

import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondAnd;
import de.d3web.persistence.xml.writers.conditions.ConditionWriter;
import de.d3web.persistence.xml.writers.conditions.ConditionsPersistenceHandler;

/**
 * This is the writer class for CondAnd-Objects
 * @author merz
 */
public class CondAndWriter extends ConditionWriter {

	AbstractCondition absCond;

	public String toXML(AbstractCondition ac) {
		CondAnd ca = (CondAnd) ac;

		String ret = "<Condition type='and'>\n";

		Iterator iter = ca.getTerms().iterator();
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
		return CondAnd.class;
	}

}
