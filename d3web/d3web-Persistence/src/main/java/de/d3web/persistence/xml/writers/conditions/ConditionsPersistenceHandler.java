package de.d3web.persistence.xml.writers.conditions;

import java.util.HashMap;
import java.util.Map;

import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;

/**
 * This singleton class handles the XML-code generation for rule-conditions
 * @author merz
 */
public class ConditionsPersistenceHandler {

	private static ConditionsPersistenceHandler instance = null;
	private Map writers = new HashMap();

	/**
	 * @return the one and only instance of this handler
	 */
	public static ConditionsPersistenceHandler getInstance() {
		if (instance == null)
			instance = new ConditionsPersistenceHandler();
		return instance;
	}

	/**
	 * generates XML-code for the given AbstractCondition
	 * @param ac the condition to generate XML-code for
	 * @return the generated XML-code as String
	 */
	public String toXML(AbstractCondition ac) {
		if(ac == null) return null;
		ConditionWriter temp = (ConditionWriter) writers.get(ac.getClass());
		return temp.toXML(ac);
	}

	/**
	 * Adds a needed ConditionWriter (as "child")
	 * @param cw the writer to put to internal Map
	 */
	public void add(ConditionWriter cw) {
		this.writers.put(cw.getSourceObject(), cw);
	}

	/**
	 * Removed a registered ConditionWriter from the internal Map
	 * @param cw the ConditionWriter to remove
	 */
	public void remove(ConditionWriter cw) {
		this.writers.remove(cw.getSourceObject());
	}
}
