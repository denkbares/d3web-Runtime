package de.d3web.persistence.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.persistence.tests.utils.XMLTagUtils;

/**
 * @author merz, brümmer :-)  ->danke!
 */

public class TestDummy extends TestCase {


//	private TestClass test;
//	private TestWriter tw;
	private String xmlcode;
	
	private XMLTag isTag;
	private XMLTag shouldTag;
	/**
	 * Constructor for RuleComplexTest.
	 * @param arg0
	 */
	public TestDummy(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestDummy.suite());
	}
	
	public static Test suite() {
		return new TestSuite(TestDummy.class);
	}
	
	protected void setUp() {
		
//		test = new TestDummy();
//		test.setId("d1");
//		test.setText("d1-text");
		
//		dw = new DiagnosisWriter();

		shouldTag = new XMLTag("testClass");
		shouldTag.addAttribute("ID", "d1");
		
		XMLTag shouldTextTag = new XMLTag("Text");
		shouldTextTag.setContent("d1-text");
		shouldTag.addChild(shouldTextTag);
		
	}
	
	public void testDiagnosisSimpleState() throws Exception{
//		xmlcode = dw.getXMLString(diag);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Diagnosis", 0));
		
		assertEquals("(0)", shouldTag, isTag);
	}
}