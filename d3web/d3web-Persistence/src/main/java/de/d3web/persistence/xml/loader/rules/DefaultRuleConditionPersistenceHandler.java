/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

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