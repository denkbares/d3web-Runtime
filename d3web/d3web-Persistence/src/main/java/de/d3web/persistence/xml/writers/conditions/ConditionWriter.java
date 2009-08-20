package de.d3web.persistence.xml.writers.conditions;

import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;

/**
 * This is the super class for all condition-writers 
 * @author merz
 */
public abstract class ConditionWriter {
	
	abstract public String toXML(AbstractCondition ac);
	
	/**
	 * @return the Class-instance of the underlying Condition
	 */
	abstract public Class getSourceObject();
		

}
