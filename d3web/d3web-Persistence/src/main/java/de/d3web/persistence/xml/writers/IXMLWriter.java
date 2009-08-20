/*
 * Created on 21.10.2003
 */
package de.d3web.persistence.xml.writers;

/**
 * 21.10.2003 12:23:18
 * @author hoernlein
 */
public interface IXMLWriter {
	/**
	 * Creation date: (06.06.2001 15:56:54)
	 * @return the generated XML-code for the given Object
	 **/
	public abstract String getXMLString(Object o);
}