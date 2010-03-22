/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.core.io.fragments;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleAction;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.manage.RuleFactory;
/**
 * FragmentHandler for Rules
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class RuleHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element node) {
		String nodeName = node.getNodeName();
		NamedNodeMap attributes = node.getAttributes();
		if (nodeName != null && nodeName.equalsIgnoreCase("knowledgeslice") && attributes!=null) {
			Node namedItem = attributes.getNamedItem("type");
			if (namedItem!=null) {
				String nodeValue = namedItem.getNodeValue();
				if (nodeValue!=null && nodeValue.equals("RuleComplex")) {
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
	public Object read(KnowledgeBase kb, Element node) throws IOException {
		String id = node.getAttribute("ID");
		String active = node.getAttribute("active");
		String comment = node.getAttribute("comment");
		RuleAction action = null;
		Condition condition = null;
		Condition exception = null;
		Condition context = null;
		List<Element> children = XMLUtil.getElementList(node.getChildNodes());
		PersistenceManager pm = PersistenceManager.getInstance();
		for (Element child: children) {
			if (child.getNodeName().equals("Exception")) {
				Object object = getGrandChildObject(kb, pm, child);
				if (object != null && exception instanceof Condition && exception== null) {
					exception = (Condition) object;
				} else {
					throw new IOException();
				}
			} else if (child.getNodeName().equals("Context")) {
				Object object = getGrandChildObject(kb, pm, child);
				if (object != null && context instanceof Condition && context== null) {
					context = (Condition) object;
				} else {
					throw new IOException();
				}
			} else {
				Object object = pm.readFragment(child, kb);
				if (object != null) {
					if (object instanceof RuleAction && action == null) {
						action = (RuleAction) object;
					} else if (object instanceof Condition && condition == null) {
						condition = (Condition) object;
					} else {
						throw new IOException();
					}
				} else {
					throw new IOException();
				}
			}
		}
		Rule rule = RuleFactory.createRule(id, action, condition, exception, context);
		if (active != null && active.length()>0) {
			rule.setActive(Boolean.parseBoolean(active));
		}
		if (comment != null && comment.length()>0) {
			rule.setComment(comment);
		}
		action.setRule(rule);
		return rule;
	}

	private Object getGrandChildObject(KnowledgeBase kb, PersistenceManager pm,
			Element child) throws IOException {
		List<Element> grandchildren = XMLUtil.getElementList(child.getChildNodes());
		if (grandchildren.size()==1) {
			Element grandchild = grandchildren.get(0);
			return pm.readFragment(grandchild, kb);
		}
		return null;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		if (!(object instanceof Rule)) {
			throw new IOException();
		}
		Rule rule = (Rule) object;
		Element node = doc.createElement("KnowledgeSlice");
		node.setAttribute("ID", rule.getId());
		node.setAttribute("type", "RuleComplex");
		if(!rule.isActive()) {
			node.setAttribute("active", ""+rule.isActive());
		}
		if(rule.getComment() != null) {
			node.setAttribute("comment", rule.getComment());
		}
		PersistenceManager pm = PersistenceManager.getInstance();
		// creating action node
		RuleAction action = rule.getAction();
		if (action != null) {
			Element actionNode = pm.writeFragment(action, doc);
			node.appendChild(actionNode);
		}
		// creating condition and exception node 	
		Condition condition = rule.getCondition();
		if (condition != null) {
			Element conditionNode = pm.writeFragment(condition, doc);
			node.appendChild(conditionNode);
		}
		Condition exception = rule.getException();
		if (exception != null) {
			Element exceptionRoot = doc.createElement("Exception");
			node.appendChild(exceptionRoot);
			Element exceptionNode = pm.writeFragment(exception, doc);
			exceptionRoot.appendChild(exceptionNode);
		}
		//create context node
		Condition context = rule.getContext();
		if (context != null) {
			Element contextRoot = doc.createElement("Context");
			node.appendChild(contextRoot);
			Element contextNode = pm.writeFragment(context, doc);
			contextRoot.appendChild(contextNode);
		}
		return node;
	}

}
