package de.d3web.persistence.xml.writers;
import java.util.logging.Logger;

import de.d3web.kernel.supportknowledge.Properties;
import de.d3web.kernel.supportknowledge.Property;
import de.d3web.persistence.xml.loader.PropertiesUtilities;

/**
 * Generates the XML representation of Properties (Descriptor Objects)
 * @author Michael Scharvogel
 */
public class PropertiesWriter implements IXMLWriter {

	public static final String ID = PropertiesWriter.class.getName();

	/**
	 * @see AbstractXMLWriter#getXMLString(Object) 
	 */
	public java.lang.String getXMLString(Object o) {
		StringBuffer sb = new StringBuffer();

		if (o == null) {
			Logger.getLogger(this.getClass().getName()).warning("null is no Properties Object");
		} else if (!(o instanceof Properties)) {
			Logger.getLogger(this.getClass().getName()).warning(o.toString() + " is no no Properties Object");
		} else {
			sb.append(new PropertiesUtilities().propertiesToString((Properties) o, Property.getBasicPropertys()));
		}

		return sb.toString();
	}
}