package de.d3web.utilities.xml;

/**
 * Utility class for xml-text-conversion
 * @author gbuscher
 */
public class XMLUtils {
	
	/**
	 * Interfering xml-controlling characters (like "&lt;", "&gt;", """) will be
	 * replaced by valid xml-strings.
	 * @param s String that shall be used e.g. as xml-attribute-value
	 * @return String with xml-compliant text
	 */
	public static String convertToXMLCompliantText(String s) {
		// right characters to metastrings
		s = s.replaceAll("&amp;","~°°~1");
		s = s.replaceAll("&apos;","~°°~2");
		s = s.replaceAll("&quot;","~°°~3");
		s = s.replaceAll("&gt;","~°°~4");
		s = s.replaceAll("&lt;","~°°~5");
		
		//	wrong characters to metastrings
		s = s.replaceAll("&","~°°~1");
		s = s.replaceAll("'","~°°~2");
		s = s.replaceAll("\"","~°°~3");
		s = s.replaceAll(">","~°°~4");
		s = s.replaceAll("<","~°°~5");
		
		// metastrings to right characters
		s = s.replaceAll("~°°~1","&amp;");
		s = s.replaceAll("~°°~2","&apos;");
		s = s.replaceAll("~°°~3","&quot;");
		s = s.replaceAll("~°°~4","&gt;");
		s = s.replaceAll("~°°~5","&lt;");
		
		return(s);
	}
	
	/**
	 * Interfering xml-controlling characters (like "&lt;", "&gt;", """)
	 * and special characters (like german umlauts) will be replaced by valid 
	 * html-strings, that can be interpreted by a browser without using
	 * the "iso-8859-1"-standard.
	 * @param s String that shall be printed within a html-page
	 * @return String with html-compliant text
	 */
	public static String convertToHTMLCompliantText(String s) {
		// first, it has to be xml-compliant
		s = convertToXMLCompliantText(s);
		
		// then replace special characters
		s = s.replaceAll("ß","&szlig;");
		s = s.replaceAll("ä","&auml;");
		s = s.replaceAll("ü","&uuml;");
		s = s.replaceAll("ö","&ouml;");
		s = s.replaceAll("Ä","&Auml;");
		s = s.replaceAll("Ü","&Uuml;");
		s = s.replaceAll("Ö","&Ouml;");
		s = s.replaceAll("°","&deg;");
		s = s.replaceAll("µ","&micro;");
		s = s.replaceAll("&apos;", "'");
		return(s);
	}

}
