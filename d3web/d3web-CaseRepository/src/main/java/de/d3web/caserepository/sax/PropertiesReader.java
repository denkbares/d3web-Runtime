/*
 * Created on 06.10.2003
 */
package de.d3web.caserepository.sax;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.xml.sax.Attributes;

import de.d3web.caserepository.CaseObject;
import de.d3web.kernel.supportknowledge.Property;
import de.d3web.persistence.xml.loader.PropertiesUtilities;

/**
 * 06.10.2003 18:52:42
 * @author hoernlein
 */
public class PropertiesReader extends AbstractTagReader {
	
	protected PropertiesReader(String id) { super(id); }
	private static PropertiesReader instance;
	private PropertiesReader() { this("PropertiesReader"); }
	public static AbstractTagReader getInstance() {
		if (instance == null)
			instance = new PropertiesReader();
		return instance;
	}
	
	private Class currentClass = null;
	private Property currentProperty = null;

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#getTagNames()
	 */
	public List getTagNames() {
		return Arrays.asList(new String[] {
			"Property",
			"Comment", // [MISC]:aha:legacy code
			"SourceSystem", // [MISC]:aha:legacy code
		});
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	protected void startElement(String uri, String localName, String qName, Attributes attributes) {
		
		if ("Property".equals(qName)) {
			String cpn = attributes.getValue("name");
			currentProperty = Property.getProperty(cpn);
		
			String cn = attributes.getValue("class");
			try {
				currentClass = Class.forName(cn);
			} catch (ClassNotFoundException e) {
				Logger.getLogger(this.getClass().getName()).warning("can't handle Property with class " + cn);
				currentProperty = null;
			}
		}

	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	protected void endElement(String uri, String localName, String qName) {
		
		if ("Comment".equals(qName))  // [MISC]:aha:legacy code
			getCaseObject()
				.getProperties()
					.setProperty(
						Property.CASE_COMMENT,
						new PropertiesUtilities.CDataString(getTextBetweenCurrentTag()));
		else if ("SourceSystem".equals(qName)) {  // [MISC]:aha:legacy code
			String value = getTextBetweenCurrentTag();
			CaseObject.SourceSystem s = CaseObject.SourceSystem.getForName(value);
			if (s != null)
				getCaseObject()
					.getProperties()
						.setProperty(
							Property.CASE_SOURCE_SYSTEM,
							s);
			else
				Logger.getLogger(this.getClass().getName()).warning("can't handle SourceSystem '" + value + "'");
		} else if ("Property".equals(qName) && currentProperty != null) {
			Object value = null;
			String s = getTextBetweenCurrentTag();
			if (currentClass.equals(String.class))
				value = s;
			else if (currentClass.equals(Integer.class))
				value = Integer.valueOf(s);
			else if (currentClass.equals(Long.class))
				value = Long.valueOf(s);
			else if (currentClass.equals(Double.class))
				value = Double.valueOf(s);
			else if (currentClass.equals(Boolean.class))
				value = Boolean.valueOf(s);

			else if (currentClass.equals(CaseObject.SourceSystem.class))
				value = CaseObject.SourceSystem.getForName(s);
				
			else if (currentClass.equals(PropertiesUtilities.CDataString.class))
				value = new PropertiesUtilities.CDataString(s);
				
			if (value != null)
				getCaseObject().getProperties().setProperty(currentProperty, value);
			currentProperty = null;
		}
		
	}

}
