package de.d3web.persistence.xml.writers.actions;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.psMethods.suppressAnswer.ActionSuppressAnswer;
import de.d3web.persistence.xml.writers.IXMLWriter;
/**
 * Generates the XML representation of a ActionSuppressAnswer Object
 * @author Michael Scharvogel
 */
public class ActionSuppressAnswerWriter implements IXMLWriter {

	public static final Class ID = ActionSuppressAnswer.class;

	/**
	 * @see AbstractXMLWriter#getXMLString(Object)
	 */
	public String getXMLString(java.lang.Object o) {
		StringBuffer sb = new StringBuffer();
		List theSuppress = null;
		Iterator iter = null;
		Question theQuestion = null;

		if (o == null) {
			Logger.getLogger(this.getClass().getName()).warning("null is no ActionSuppressAnswer");
		} else if (!(o instanceof ActionSuppressAnswer)) {
			Logger.getLogger(this.getClass().getName()).warning(o.toString() + " is no ActionSuppressAnswer");
		} else {
			ActionSuppressAnswer theAction = (ActionSuppressAnswer) o;

			sb.append("<Action type='ActionSuppressAnswer'>\n");

			theSuppress = theAction.getSuppress();
			theQuestion = theAction.getQuestion();

			String questionId = "";
			if(theQuestion != null) {
				questionId = theQuestion.getId();
			}
			
			
			sb.append(
				"<Question ID='"
					+ questionId
					+ "'/>\n");

			if (theSuppress != null) {
				if (!theSuppress.isEmpty()) {
					sb.append("<Suppress>\n");
					iter = theSuppress.iterator();
					while (iter.hasNext()) {
						//					sb.append("<QASet ID='" + ((QASet)theQuestion).getId() + "'></QASet>\n");
						sb.append(
							"<Answer ID='"
								+ ((AnswerChoice) iter.next()).getId()
								+ "'/>\n");
					}
					sb.append("</Suppress>\n");
				}
			}

			sb.append("</Action>\n");
		}

		return sb.toString();
	}
}