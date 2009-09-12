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

package de.d3web.persistence.xml.writers.actions;
import java.util.logging.Logger;

import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.formula.FormulaDateElement;
import de.d3web.kernel.domainModel.formula.FormulaDateExpression;
import de.d3web.kernel.domainModel.formula.FormulaExpression;
import de.d3web.kernel.domainModel.formula.FormulaNumberElement;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.psMethods.questionSetter.ActionSetValue;
import de.d3web.persistence.xml.writers.IXMLWriter;
/**
 * Generates the XML representation of a ActionSetValue Object
 * @author Michael Scharvogel
 */
public class ActionSetValueWriter implements IXMLWriter {

	public static final Class ID = ActionSetValue.class;

	/**
	 * @see AbstractXMLWriter#getXMLString(Object)
	 */
	public String getXMLString(java.lang.Object o) {
		StringBuffer sb = new StringBuffer();
		Object[] theValues = null;
		Question theQuestion = null;

		if (o == null) {
			Logger.getLogger(this.getClass().getName()).warning("null is no Action SetValue");
		} else if (!(o instanceof ActionSetValue)) {
			Logger.getLogger(this.getClass().getName()).warning(o.toString() + " is no ActionSetValue");
		} else {
			ActionSetValue theAction = (ActionSetValue) o;

			sb.append("<Action type='ActionSetValue'>\n");

			theValues = theAction.getValues();
			theQuestion = theAction.getQuestion();

			
			String questionId = "";
			if(theQuestion != null) {
				questionId = theQuestion.getId();
			}
			
			sb.append("<Question ID='" + questionId + "'/>\n");
			sb.append("<Values>\n");

			if (theValues != null) {
				for (int i = 0; i < theValues.length; i++) {

					// [MISC]:merz:vielleicht sollte der Cast noch sichergestellt werden..
					// auch bin ich mir nicht sicher, ob das immer den Wert bzw.
					// die ID liefert.
					// if (theValues[i] instanceof Evaluatable) {
					if (theValues[i] instanceof FormulaNumberElement) {
						sb.append("<Value type='evaluatable'>\n"
						// NO - AGAIN (see ActionAddValueWriter)
						// + XMLTools.prepareForXML(((FormulaElement) theValues[i]).getXMLString())
						+ ((FormulaNumberElement) theValues[i]).getXMLString()
							+ "</Value>\n");
					} else if (theValues[i] instanceof FormulaDateExpression) {
						sb.append("<Value type='evaluatable'>\n");
						sb.append(((FormulaDateExpression) theValues[i]).getXMLString());
						sb.append("</Value>\n");
					}else if (theValues[i] instanceof FormulaDateElement) {
						sb.append("<Value type='evaluatable'>\n");
						sb.append(((FormulaDateElement) theValues[i]).getXMLString());
						sb.append("</Value>\n");
					} else if (theValues[i] instanceof AnswerChoice) {
						sb.append("<Value type='answer' ID='" + ((AnswerChoice) theValues[i]).getId() + "'/>\n");
					} else if (theValues[i] instanceof FormulaExpression) {
						sb.append(
							"<Value type='evaluatable'>\n"
								+ ((FormulaExpression) theValues[i])
									.getXMLString()
								+ "</Value>\n");
					}
				}
			}

			sb.append("</Values>\n");
			sb.append("</Action>\n");
		}
		return sb.toString();
	}
}