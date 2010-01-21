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
package de.d3web.core.kpers.fragments.conditions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.kpers.PersistenceManager;
import de.d3web.core.kpers.fragments.FragmentHandler;
import de.d3web.core.kpers.utilities.XMLUtil;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondMofN;
/**
 * FragmentHandler for CondMofNs
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class MofNConditionHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkCondition(element, "MofN");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof CondMofN);
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		int min = Integer.parseInt(element.getAttribute("min"));
		int max = Integer.parseInt(element.getAttribute("max"));
		List<Element> childNodes = XMLUtil.getElementList(element.getChildNodes());
		PersistenceManager pm = PersistenceManager.getInstance();
		List<AbstractCondition> conds = new ArrayList<AbstractCondition>();
		for (Element child: childNodes) {
			conds.add((AbstractCondition) pm.readFragment(child, kb));
		}
		return new CondMofN(conds, min, max);
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		CondMofN cond = (CondMofN) object;
		Element element = XMLUtil.writeCondition(doc, "MofN");
		element.setAttribute("min", ""+cond.getMin());
		element.setAttribute("max", ""+cond.getMax());
		element.setAttribute("size", ""+cond.getTerms().size());
		PersistenceManager pm = PersistenceManager.getInstance();
		for (AbstractCondition ac: cond.getTerms()) {
			element.appendChild(pm.writeFragment(ac, doc));
		}
		return element;
	}
}
