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
package de.d3web.core.io.fragments.actions.formula;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Element;

import de.d3web.abstraction.formula.FormulaElement;
import de.d3web.core.KnowledgeBase;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.terminology.Question;
/**
 * Provides basic functionalities for ExpressionHandlers
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public abstract class AbstractExpressionHandler implements FragmentHandler{

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals(getNodeName());
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		List<Element> childNodes = XMLUtil.getElementList(element.getChildNodes());
		Question q = null;
		FormulaElement fe = null;
		for (Element child: childNodes) {
			//in previous versions of the persistence, some Questions are saved with their type, e.g. QuestionNum
			if (child.getNodeName().startsWith("Question")) {
				String qid = child.getAttribute("ID");
				Question question = kb.searchQuestion(qid);
				if (q == null) {
					q=question;
				} else {
					throw new IOException("Only one question allowed.");
				}
			} else {
				Object object = PersistenceManager.getInstance().readFragment(child, kb);
				if (object instanceof FormulaElement && fe == null) {
					fe = (FormulaElement) object;
				} else {
					throw new IOException("Only one FormulaElement allowed.");
				}
			}
		}
		return createObject(q,fe);
	}
	
	protected abstract Object createObject(Question q, FormulaElement fe) throws IOException;
	
	protected abstract String getNodeName();
}
