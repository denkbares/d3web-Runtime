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
package de.d3web.core.io.fragments.conditions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;

/**
 * FragmentHandler for CondAnds
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class AndConditionHandler implements FragmentHandler<KnowledgeBase> {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkCondition(element, "and");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof CondAnd);
	}

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		List<Element> childNodes = XMLUtil.getElementList(element.getChildNodes());
		List<Condition> conds = new ArrayList<>();
		for (Element child : childNodes) {
			conds.add((Condition) persistence.readFragment(child));
		}
		return new CondAnd(conds);
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		CondAnd cond = (CondAnd) object;
		Element element = XMLUtil.writeCondition(persistence.getDocument(), "and");
		for (Condition ac : cond.getTerms()) {
			element.appendChild(persistence.writeFragment(ac));
		}
		return element;
	}

}
