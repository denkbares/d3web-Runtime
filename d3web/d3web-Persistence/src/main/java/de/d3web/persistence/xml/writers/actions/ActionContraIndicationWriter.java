package de.d3web.persistence.xml.writers.actions;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.psMethods.contraIndication.ActionContraIndication;
import de.d3web.persistence.xml.writers.IXMLWriter;
/**
 * Generates the XML representation of a ActionContraIndication Object
 * @author Michael Scharvogel
 */
public class ActionContraIndicationWriter implements IXMLWriter {

	public static final Class ID = ActionContraIndication.class;

	/**
	 * @see AbstractXMLWriter#getXMLString(Object)
	 */
	public String getXMLString(java.lang.Object o) {
		StringBuffer sb = new StringBuffer();
		List theList = null;
		Iterator iter = null;

		if (!(o instanceof ActionContraIndication)) {
			Logger.getLogger(this.getClass().getName()).warning(o.toString() + " is no ActionContraIndication");
		} else {
			ActionContraIndication theAction = (ActionContraIndication) o;

			sb.append("<Action type='ActionContraIndication'>\n");

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
}