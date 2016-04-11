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

import de.d3web.core.inference.condition.CondMofN;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;

/**
 * FragmentHandler for CondMofNs
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class MofNConditionHandler implements FragmentHandler<KnowledgeBase> {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkCondition(element, "MofN");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof CondMofN);
	}

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		int min = Integer.parseInt(element.getAttribute("min"));
		int max = Integer.parseInt(element.getAttribute("max"));
		List<Element> childNodes = XMLUtil.getElementList(element.getChildNodes());
		List<Condition> conds = new ArrayList<Condition>();
		for (Element child : childNodes) {
			conds.add((Condition) persistence.readFragment(child));
		}
		return new CondMofN(conds, min, max);
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		CondMofN cond = (CondMofN) object;
		Element element = XMLUtil.writeCondition(persistence.getDocument(), "MofN");
		element.setAttribute("min", "" + cond.getMin());
		element.setAttribute("max", "" + cond.getMax());
		element.setAttribute("size", "" + cond.getTerms().size());
		for (Condition ac : cond.getTerms()) {
			element.appendChild(persistence.writeFragment(ac));
		}
		return element;
	}
}
