package de.d3web.persistence.xml.writers.actions;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.psMethods.nextQASet.ActionNextQASet;
import de.d3web.persistence.xml.writers.IXMLWriter;
/**
 * Generates the XML representation of a ActionNextQASet Object
 * @author Michael Scharvogel
 */
public class ActionNextQASetWriter implements IXMLWriter {
	
	public static final Class ID = ActionNextQASet.class;

	/**
	 * @see AbstractXMLWriter#getXMLString(Object)
	 */
	public String getXMLString(Object o) {
		StringBuffer sb = new StringBuffer();
		List theList = null;
		Iterator iter = null;

		if (o == null) {
			Logger.getLogger(this.getClass().getName()).warning("null is no " + getNextQASetType());
		} else if (!(o instanceof ActionNextQASet)) {
			Logger.getLogger(this.getClass().getName()).warning(o.toString() + " is no " + getNextQASetType());
		} else {
			ActionNextQASet theAction = (ActionNextQASet) o;

			sb.append("<Action type='" + getNextQASetType() + "'>\n");

			// this must be wrong
			// in the tests here appears a "<targetDiagnosis ID='diag1-id'/>"
			// if this is prepared then it is transformed to "&lt;targetDiagnosis ID='diag1-id'/&gt;"
			// sb.append(XMLTools.prepareForXML(getTarget(theAction)));
			// ->
			sb.append(getTarget(theAction));

			theList = theAction.getQASets();
			if (theList != null) {
				if (!(theList.isEmpty())) {
					sb.append("<TargetQASets>\n");

					iter = theList.iterator();
					while (iter.hasNext())
						sb.append("<QASet ID='" + ((QASet) iter.next()).getId() + "'/>\n");

					sb.append("</TargetQASets>\n");
				}
			}
			sb.append("</Action>\n");
		}
		return sb.toString();
	}

	protected String getTarget(ActionNextQASet action) {
		return "";
	}

	protected String getNextQASetType() {
		return "ActionNextQASet";
	}

}