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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.persistence.xml.loader.KBLoader;

/**
 * @author bruemmer
 */
public class RuleLoader {

	private KBLoader kbLoader = null;
	private Map<String, RuleConditionPersistenceHandler> ruleConditionHandlers;
	private Map<String, RuleActionPersistenceHandler> ruleActionHandlers;

	public RuleLoader(KBLoader kbLoader, KnowledgeBase knowledgeBase) {
		ruleConditionHandlers = new HashMap<String, RuleConditionPersistenceHandler>();
		ruleActionHandlers = new HashMap<String, RuleActionPersistenceHandler>();
		this.kbLoader = kbLoader;
		
		addRuleActionHandler(AddValueRuleActionPersistenceHandler.getInstance());
		addRuleActionHandler(ClarifyRuleActionPersistenceHandler.getInstance());
		addRuleActionHandler(HeuristicRuleActionPersistenceHandler.getInstance());
		addRuleActionHandler(IndicationRuleActionPersistenceHandler.getInstance());
		addRuleActionHandler(NextQASetRuleActionPersistenceHandler.getInstance());
		addRuleActionHandler(RefineRuleActionPersistenceHandler.getInstance());
		addRuleActionHandler(SetValueRuleActionPersistenceHandler.getInstance());
		addRuleActionHandler(SuppressAnswerRuleActionPersistenceHandler.getInstance());
		addRuleActionHandler(InstantIndicationRuleActionPersistenceHandler.getInstance());
        addRuleActionHandler(ContraIndicationActionPersistenceHandler.getInstance());
	}

	public void addRuleConditionHandler(RuleConditionPersistenceHandler handler) {
		ruleConditionHandlers.put(handler.getName(), handler);
	}

	public RuleConditionPersistenceHandler getRuleConditionHandler(String name) {
		return ruleConditionHandlers.get(name);
	}

	public Collection<RuleConditionPersistenceHandler> getRuleConditionHandlers() {
		return ruleConditionHandlers.values();
	}

	public void addRuleActionHandler(RuleActionPersistenceHandler handler) {
		ruleActionHandlers.put(handler.getName(), handler);
	}

	public RuleActionPersistenceHandler getRuleActionHandler(String name) {
		return ruleActionHandlers.get(name);
	}

	public Collection<RuleActionPersistenceHandler> getRuleActionHandlers() {
		return ruleActionHandlers.values();
	}

	
	public RuleComplex loadRule(String id, Node slice) {
		
		AbstractCondition cond = null;
		AbstractCondition exception = null;
		AbstractCondition context = null;
		RuleComplex rule = null;
		String actionType = getActionType(slice);

		
		//[TODO]: Peter: refactor old load stuff:
		
		RuleActionPersistenceHandler raHandler = ruleActionHandlers.get(actionType);
		if(raHandler != null) {
			Class ruleContext = raHandler.getContext();
			cond = getCondition(slice, ruleContext);
			context = getContext(slice, ruleContext);
			exception = getException(slice, ruleContext);
			rule = raHandler.getRuleWithAction(slice, id, kbLoader, ruleContext);
		} else {
			Logger.getLogger(this.getClass().getName()).warning("No persistence handler registered for: " + actionType);
		}
		
		if(rule != null) {
			rule.setActive(getActiveState(slice));
			rule.setComment(getComment(slice));
			rule.setCondition(cond);
			rule.setContext(context);
			rule.setException(exception);
		}
		return rule;
	}

	private String getComment(Node slice) {
		Node commentNode = slice.getAttributes().getNamedItem("comment");
		if(commentNode != null) {
			String commentString = commentNode.getNodeValue();
			if(commentString != null) {
				return commentString;
			} 
		}
		return null;
	}

	private boolean getActiveState(Node slice) {
		Node activeNode = slice.getAttributes().getNamedItem("active");
		if(activeNode != null) {
			String activeString = activeNode.getNodeValue();
			if(activeString != null) {
				boolean active = Boolean.parseBoolean(activeString);
				return active;
			} 
		}
		return true;
	}

	private String getActionType(Node slice) {
		NodeList nl = slice.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node n = nl.item(i);
			if (n.getNodeName().equalsIgnoreCase("action")) {
				String type = n.getAttributes().getNamedItem("type").getNodeValue();
				return type;
			}
		}
		return null;
	}

	private AbstractCondition getCondition(Node slice, Class context) {
		NodeList nl = slice.getChildNodes();
		Node condNode = null;
		for (int i = 0; i < nl.getLength(); ++i) {
			condNode = nl.item(i);
			if (condNode.getNodeName().equalsIgnoreCase("Condition")) {
				String conditionType = condNode.getAttributes().getNamedItem("type").getNodeValue();
				AbstractCondition condition = null;
				for (RuleConditionPersistenceHandler handler : getRuleConditionHandlers()) {
					if (handler.checkCompatibility(condNode)) {
						if (condition == null) {
							condition = handler.loadCondition(condNode, kbLoader, context);
						} else {
							Logger.getLogger(getClass().getName()).severe(
									"more than one condition handler registered for type "
											+ conditionType + "!");
						}
					}
				}
				if (condition == null) {
					Logger.getLogger(getClass().getName()).severe(
							"no condition handler registered for type " + conditionType + "!");
				}
				return condition;
			}
		}
		return null;
	}

	private AbstractCondition getException(Node slice, Class context) {
		NodeList nl = slice.getChildNodes();
		Node exceptionNode = null;
		for (int i = 0; i < nl.getLength(); ++i) {
			exceptionNode = nl.item(i);
			if (exceptionNode.getNodeName().equalsIgnoreCase("Exception")) {
				/*
				 * exceptionNode.getFirstChild(); Node exceptionchildNode =
				 * exceptionNode.getFirstChild(); return
				 * getCondition(exceptionchildNode, context);
				 */
				return getCondition(exceptionNode, context);
			}
		}
		return null;
	}

	private AbstractCondition getContext(Node slice, Class context) {
		NodeList nl = slice.getChildNodes();
		Node contextNode = null;
		for (int i = 0; i < nl.getLength(); ++i) {
			contextNode = nl.item(i);
			if (contextNode.getNodeName().equalsIgnoreCase("Context")) {
				return getCondition(contextNode, context);
			}
		}
		return null;
	}
}