package de.d3web.persistence.xml.loader.rules;

import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Node;

import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.persistence.xml.loader.ConditionFactory;
import de.d3web.persistence.xml.loader.KBLoader;

/**
 * @author bruemmer
 */
public class DefaultRuleConditionPersistenceHandler implements RuleConditionPersistenceHandler {

	private List supportedConditionTypes = null;

	public DefaultRuleConditionPersistenceHandler() {
		super();
		supportedConditionTypes = Arrays.asList(new Object[]{"and", "MofN", "not", "or", "unknown",
				"numGreaterEqual", "numEqual", "known", "textEqual", "textContains", "numLess",
				"numLessEqual", "numIn", "numGreater", "equal", "DState", "dstate", "choiceYes", "choiceNo"});
	}

	/**
	 * @see de.d3web.persistence.xml.loader.rules.RuleConditionPersistenceHandler#getName()
	 */
	public String getName() {
		return "DefaultRuleConditionPersistenceHandler";
	}

	/**
	 * @see de.d3web.persistence.xml.loader.rules.RuleConditionPersistenceHandler#checkCompatibility(org.w3c.dom.Node)
	 */
	public boolean checkCompatibility(Node conditionNode) {
		String type = conditionNode.getAttributes().getNamedItem("type").getNodeValue();
		return supportedConditionTypes.contains(type);
	}

	/**
	 * @see de.d3web.persistence.xml.loader.rules.RuleConditionPersistenceHandler#loadCondition(org.w3c.dom.Node)
	 */
	public AbstractCondition loadCondition(Node node, KBLoader kbLoader, Class context) {
		return ConditionFactory.createCondition(node, kbLoader, context);
	}

}