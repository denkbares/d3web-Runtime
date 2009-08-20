package de.d3web.persistence.xml.writers;
import java.util.logging.Logger;

import de.d3web.persistence.xml.MockCostObject;
import de.d3web.xml.utilities.XMLTools;

/**
 * Generates the XML representation of a CostObject
 * @author Michael Scharvogel
 */
public class CostKBWriter implements IXMLWriter {

	public static final String ID = CostKBWriter.class.getName();

	/**
	 * this writer is for the cost definitions in the knowledgebase object only!
	 * @see AbstractXMLWriter#getXMLString(Object) 
	 */
	public String getXMLString(Object o) {
		String retValue = new String();

		if (o == null) {
			Logger.getLogger(this.getClass().getName()).warning("null is no CostObject");
		} else if (!(o instanceof MockCostObject)) {
			Logger.getLogger(this.getClass().getName()).warning(o.toString() + " is no Cost Object");
		} else {
			MockCostObject mco = (MockCostObject) o;

			retValue =
				"<Cost ID='" + mco.getID() + "'>\n" +				"<Verbalization>" +				"<![CDATA[" + XMLTools.prepareForCDATA(mco.getVerbalization()) + "]]>" +				"</Verbalization>\n" +				"<Unit>" +				"<![CDATA[" + XMLTools.prepareForCDATA(mco.getUnit()) + "]]>" +				"</Unit>\n" +				"</Cost>\n";
		}

		return retValue;
	}

}