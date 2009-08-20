package de.d3web.persistence.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.kernel.supportknowledge.DCElement;
import de.d3web.kernel.supportknowledge.DCMarkup;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.persistence.tests.utils.XMLTagUtils;
import de.d3web.persistence.xml.writers.DCMarkupWriter;

/**
 * @author merz
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class DCMarkupTest extends TestCase {

	private DCMarkup markup;
	private DCMarkupWriter dw;
	private String xmlcode;
	
	private XMLTag isTag;
	private XMLTag shouldTag;
	
	
	public DCMarkupTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(DCMarkupTest.suite());
	}
	
	public static Test suite() {
		return new TestSuite(DCMarkupTest.class);
	}	
	
		
	protected void setUp(){
		markup = new DCMarkup();
		markup.setContent(DCElement.SOURCE, "value1");
		dw = DCMarkupWriter.getInstance();
	}
	
	public void testSimpleDescriptor() throws Exception{
		
		shouldTag = new XMLTag("DCElement");
		shouldTag.addAttribute("label", DCElement.SOURCE.getLabel());
		shouldTag.setContent("value1");
		
		xmlcode = dw.getXMLString(markup);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "DCElement", 0));

		assertEquals("(0)", shouldTag, isTag);	
	}
}
