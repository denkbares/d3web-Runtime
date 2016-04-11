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
package de.d3web.core.io.fragments.actions.formula;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Element;

import de.d3web.abstraction.formula.FormulaElement;
import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;

/**
 * Handels Expressions in old KBs. In new KBs the FormulaElements are directly
 * stored
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class ExpressionHandler implements FragmentHandler<KnowledgeBase> {

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals("FormulaDateExpression")
				|| element.getNodeName().equals("FormulaExpression");
	}

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		List<Element> childNodes = XMLUtil.getElementList(element.getChildNodes());
		FormulaElement fe = null;
		for (Element child : childNodes) {
			// in previous versions of the persistence, there were links to the
			// question, questionnum etc.
			if (child.getNodeName().startsWith("Question")) { // NOSONAR
				// Nothing todo, link to question not needed
			}
			else {
				Object object = persistence.readFragment(child);
				if (object instanceof FormulaElement && fe == null) {
					fe = (FormulaElement) object;
				}
				else {
					throw new IOException("Only one FormulaElement allowed.");
				}
			}
		}
		return fe;
	}

	@Override
	public boolean canWrite(Object object) {
		// FormulaDateExpression doesn't exist any more
		return false;
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		throw new IOException("This Fragment handler only exists to read old kbs.");
	}
}
