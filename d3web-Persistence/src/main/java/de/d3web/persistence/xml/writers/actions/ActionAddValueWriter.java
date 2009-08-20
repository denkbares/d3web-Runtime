package de.d3web.persistence.xml.writers.actions;
import java.util.logging.Logger;

import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.formula.FormulaDateElement;
import de.d3web.kernel.domainModel.formula.FormulaDateExpression;
import de.d3web.kernel.domainModel.formula.FormulaExpression;
import de.d3web.kernel.domainModel.formula.FormulaNumberElement;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.psMethods.questionSetter.ActionAddValue;
import de.d3web.persistence.xml.writers.IXMLWriter;
/**
 * Generates the XML representation of an ActionAddValue Object
 * @author Michael Scharvogel
 */
public class ActionAddValueWriter implements IXMLWriter {

	public static final Class ID = ActionAddValue.class;

	/**
	 * @see AbstractXMLWriter#getXMLString(Object)
	 */
	public String getXMLString(java.lang.Object o) {
		StringBuffer sb = new StringBuffer();
		Object[] theValues = null;
		Question theQuestion = null;

		if (o == null) {
			Logger.getLogger(this.getClass().getName()).warning("null is no ActionAddValue");
		} else if (!(o instanceof ActionAddValue)) {
			Logger.getLogger(this.getClass().getName()).warning(o.toString() + " is no ActionAddValue");
		} else {
			ActionAddValue theAction = (ActionAddValue) o;
			theQuestion = theAction.getQuestion();
			theValues = theAction.getValues();
			
			sb.append("<Action type='ActionAddValue'>\n");
			
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
					if (theValues[i] instanceof FormulaExpression) {
						sb
							.append(
								"<Value type='evaluatable'>\n"
								+ ((FormulaExpression) theValues[i])
									.getXMLString()
								+ 
						// NO - AGAIN (see ActionNextQASetWriter) 
						// XMLTools.prepareForXML(((FormulaExpression) theValues[i]).getXMLString()) + 
						"</Value>\n");
					} else if (theValues[i] instanceof FormulaNumberElement) {
						sb.append("<Value type='evaluatable'>\n"
						// NO - AGAIN (see ActionAddValueWriter)
						// + XMLTools.prepareForXML(((FormulaElement) theValues[i]).getXMLString())
						+ ((FormulaNumberElement) theValues[i]).getXMLString()
						+ "</Value>\n");					
					} else if (theValues[i] instanceof FormulaDateExpression) {
						sb.append("<Value type='evaluatable'>\n");
						sb.append(((FormulaDateExpression) theValues[i]).getXMLString());
						sb.append("</Value>\n");
					} else if (theValues[i] instanceof FormulaDateElement) {
						sb.append("<Value type='evaluatable'>\n");
						sb.append(((FormulaDateElement) theValues[i]).getXMLString());
						sb.append("</Value>\n");
					} else if (theValues[i] != null) {
						sb.append(
							"<Value type='answer' ID='"
								+ ((Answer) theValues[i]).getId()
								+ "'/>\n");
					}
				}
			}

			sb.append("</Values>\n");
			sb.append("</Action>\n");
		} 
			
		return sb.toString();
	}
}