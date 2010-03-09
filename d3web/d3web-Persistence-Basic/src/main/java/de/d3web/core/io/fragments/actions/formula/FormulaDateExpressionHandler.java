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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.abstraction.formula.FormulaDateElement;
import de.d3web.abstraction.formula.FormulaDateExpression;
import de.d3web.abstraction.formula.FormulaElement;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.terminology.Question;
/**
 * Handels date formulas
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class FormulaDateExpressionHandler extends AbstractExpressionHandler {

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof FormulaDateExpression);
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		FormulaDateExpression formula = (FormulaDateExpression) object;
		Element element = doc.createElement(getNodeName());
		XMLUtil.appendQuestionLinkElement(element, formula.getQuestionDate());
		element.appendChild(PersistenceManager.getInstance().writeFragment(formula.getFormulaDateElement(), doc));
		return element;
	}
	
	@Override
	protected Object createObject(Question q, FormulaElement fe)
			throws IOException {
		if (fe instanceof FormulaDateElement) {
			return new FormulaDateExpression(q, (FormulaDateElement) fe);
		} else {
			throw new IOException("For a FormulaDateExpression the FormulaElement must be a FormulaDateElement.");
		}
	}

	@Override
	protected String getNodeName() {
		return "FormulaDateExpression";
	}
}
