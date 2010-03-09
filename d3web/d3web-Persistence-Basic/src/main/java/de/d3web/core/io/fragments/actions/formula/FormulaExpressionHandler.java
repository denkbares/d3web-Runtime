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

import de.d3web.abstraction.formula.FormulaElement;
import de.d3web.abstraction.formula.FormulaExpression;
import de.d3web.abstraction.formula.FormulaNumberElement;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.terminology.Question;
/**
 * Handels FormulaExpressions
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class FormulaExpressionHandler extends AbstractExpressionHandler {

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof FormulaExpression);
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		FormulaExpression formula = (FormulaExpression) object;
		Element element = doc.createElement(getNodeName());
		XMLUtil.appendQuestionLinkElement(element, formula.getQuestionNum());
		element.appendChild(PersistenceManager.getInstance().writeFragment(formula.getFormulaElement(), doc));
		return element;
	}

	@Override
	protected Object createObject(Question q, FormulaElement fe)
			throws IOException {
		if (fe instanceof FormulaNumberElement) {
			return new FormulaExpression(q, (FormulaNumberElement) fe);
		} else {
			throw new IOException("For a FormulaExpression the FormulaElement must be a FormulaNumberElement.");
		}
	}

	@Override
	protected String getNodeName() {
		return "FormulaExpression";
	}

}
