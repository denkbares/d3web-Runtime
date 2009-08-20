package de.d3web.persistence.xml.writers;
import java.util.logging.Logger;

import de.d3web.kernel.supportknowledge.DCMarkup;
import de.d3web.persistence.xml.loader.DCMarkupUtilities;

/**
 * Generates the XML representation of a Descriptor Object
 * @author Michael Scharvogel
 */

public class DCMarkupWriter implements IXMLWriter {

	public static final String ID = DCMarkupWriter.class.getName();

	private static DCMarkupWriter instance = null;
	public static DCMarkupWriter getInstance() {
		if (instance == null) {
			instance = new DCMarkupWriter();
		}
		return instance;
	}

	/**
	 * @see AbstractXMLWriter#getXMLString(Object) 
	 */
	public String getXMLString(Object o) {
		StringBuffer sb = new StringBuffer();

		if (o == null) {
			Logger.getLogger(this.getClass().getName()).warning("null is no DCMarkup!");
		} else if (
			!(o instanceof DCMarkup)) {
				Logger.getLogger(this.getClass().getName()).warning(o.toString() + " is no DCMarkup!");
		} else {
			sb.append(DCMarkupUtilities.dcmarkupToString((DCMarkup) o));
		}
		
		return sb.toString();
	}
}