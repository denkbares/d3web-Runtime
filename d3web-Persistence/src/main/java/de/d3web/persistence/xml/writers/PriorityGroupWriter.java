package de.d3web.persistence.xml.writers;
import java.util.Iterator;
import java.util.logging.Logger;

import de.d3web.kernel.domainModel.PriorityGroup;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.xml.utilities.XMLTools;

/**
 * Generates the XML representation of a PriorityGroup
 * @author Michael Scharvogel
 */
public class PriorityGroupWriter implements IXMLWriter {
	
	public static final String ID = PriorityGroupWriter.class.getName();

	/**
	 * @see AbstractXMLWriter#getXMLString(Object) 
	 */
	public String getXMLString(Object o) {
		StringBuffer sb = new StringBuffer();

		if (o == null) {
			Logger.getLogger(this.getClass().getName()).warning("null is no Priority Group");
		} else if (!(o instanceof PriorityGroup)) {
			Logger.getLogger(this.getClass().getName()).warning(o.toString() + " is no Priority Group");
		} else {
			PriorityGroup thePG = (PriorityGroup) o;

			// handling ParentChild Relationships...
			Iterator childIter = thePG.getChildren().iterator();
			boolean hasChildren = childIter.hasNext();

			sb.append("<PriorityGroup ID='" + thePG.getId() + "'>\n");
			sb.append("<Text><![CDATA[" + XMLTools.prepareForCDATA(thePG.getText()) + "]]></Text>\n");
			if (thePG.getMinLevel() != null) {
				sb.append("<MinLevel value='" + thePG.getMinLevel() + "'></MinLevel>\n");
			}

			if (thePG.getMaxLevel() != null) {
				sb.append("<MaxLevel value='" + thePG.getMaxLevel() + "'></MaxLevel>\n");
			}

			if (hasChildren) {
				sb.append("<Children>\n");

				while (childIter.hasNext())
					sb.append("<Child ID='" + ((QASet) childIter.next()).getId() + "'/>\n");

				sb.append("</Children>\n");
			}

			//Properties
			sb.append(new PropertiesWriter().getXMLString(thePG.getProperties()));

			sb.append("</PriorityGroup>\n");

		}

		return sb.toString();
	}
}