/*
 * Copyright (C) 2009 denkbares GmbH
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.core.io.fragments;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.io.Persistence;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.manage.RuleFactory;

/**
 * FragmentHandler for Rules
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class RuleHandler implements FragmentHandler<KnowledgeBase> {

	@Override
	public boolean canRead(Element node) {
		String nodeName = node.getNodeName();
		NamedNodeMap attributes = node.getAttributes();
		if (nodeName != null && nodeName.equalsIgnoreCase("knowledgeslice") && attributes != null) {
			Node namedItem = attributes.getNamedItem("type");
			if (namedItem != null) {
				String nodeValue = namedItem.getNodeValue();
				if ("RuleComplex".equals(nodeValue)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof Rule);
	}

	@Override
	public Object read(Element node, Persistence<KnowledgeBase> persistence) throws IOException {
		PSAction action = null;
		Condition condition = null;
		Condition exception = null;
		Condition context = null;
		List<Element> children = XMLUtil.getElementList(node.getChildNodes());
		for (Element child : children) {
			if (child.getNodeName().equals("Exception")) {
				Object object = getGrandChildObject(persistence, child);
				if (object != null && exception == null) {
					exception = (Condition) object;
				}
				else {
					throw new IOException();
				}
			}
			else if (child.getNodeName().equals("Context")) {
				Object object = getGrandChildObject(persistence, child);
				if (object != null && context == null) {
					context = (Condition) object;
				}
				else {
					throw new IOException();
				}
			}
			else {
				Object object = persistence.readFragment(child);
				if (object != null) {
					if (object instanceof PSAction && action == null) {
						action = (PSAction) object;
					}
					else if (object instanceof Condition && condition == null) {
						condition = (Condition) object;
					}
					else {
						throw new IOException();
					}
				}
				else {
					throw new IOException();
				}
			}
		}
		// if there is a context defined (in old kbs), add id to the normal
		// condition
		if (context != null) {
			condition = new CondAnd(Arrays.asList(condition, context));
		}
		// PSMethodContext must be set by the method, which calls the fragment
		// handler
		Rule rule = RuleFactory.createRule(action, condition, exception, null);
		return rule;
	}

	private Object getGrandChildObject(Persistence<KnowledgeBase> persistence, Element child) throws IOException {
		List<Element> grandchildren = XMLUtil.getElementList(child.getChildNodes());
		if (grandchildren.size() == 1) {
			Element grandchild = grandchildren.get(0);
			return persistence.readFragment(grandchild);
		}
		return null;
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		if (!(object instanceof Rule)) {
			throw new IOException();
		}
		Rule rule = (Rule) object;
		Element node = persistence.getDocument().createElement("KnowledgeSlice");
		node.setAttribute("type", "RuleComplex");
		// creating action node
		PSAction action = rule.getAction();
		if (action != null) {
			Element actionNode = persistence.writeFragment(action);
			node.appendChild(actionNode);
		}
		// creating condition and exception node
		Condition condition = rule.getCondition();
		if (condition != null) {
			Element conditionNode = persistence.writeFragment(condition);
			node.appendChild(conditionNode);
		}
		Condition exception = rule.getException();
		if (exception != null) {
			Element exceptionRoot = persistence.getDocument().createElement("Exception");
			node.appendChild(exceptionRoot);
			Element exceptionNode = persistence.writeFragment(exception);
			exceptionRoot.appendChild(exceptionNode);
		}
		return node;
	}

}
