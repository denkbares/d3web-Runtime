package de.d3web.persistence.xml.loader.rules;

import org.w3c.dom.Node;

import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.persistence.xml.loader.KBLoader;

/**
 * @author bruemmer
 */
public interface RuleConditionPersistenceHandler {

	public String getName();
	public boolean checkCompatibility(Node conditionNode);
	public AbstractCondition loadCondition(Node node, KBLoader kbLoader, Class context);
}