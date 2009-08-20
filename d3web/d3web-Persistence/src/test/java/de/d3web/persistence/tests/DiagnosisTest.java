package de.d3web.persistence.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.Score;
import de.d3web.kernel.supportknowledge.Property;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.persistence.tests.utils.XMLTagUtils;
import de.d3web.persistence.xml.writers.DiagnosisWriter;

/**
 * @author merz
 *
 * !!! tests for checking prperties missing
 */
public class DiagnosisTest extends TestCase {
	
	private Diagnosis diag;
	private DiagnosisWriter dw;
	private String xmlcode;
	
	private XMLTag isTag;
	private XMLTag shouldTag;
	
	/**
	 * Constructor for DiagnosisOutputTest.
	 * @param arg0
	 */
	public DiagnosisTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(DiagnosisTest.suite());
	}
	
	public static Test suite() {
		return new TestSuite(DiagnosisTest.class);
	}
	
	protected void setUp() {
		
		//create the diagnosis
		diag = new Diagnosis();
		diag.setId("d1");
		diag.setText("d1-text");
		
		dw = new DiagnosisWriter();

		// first step in biulding shouldTag: id and text always added in shouldTag
		shouldTag = new XMLTag("Diagnosis");
		shouldTag.addAttribute("ID", "d1");
		
		XMLTag shouldTextTag = new XMLTag("Text");
		shouldTextTag.setContent("d1-text");
		shouldTag.addChild(shouldTextTag);
		
	}
	
	public void testDiagnosisSimpleState() throws Exception{
		xmlcode = dw.getXMLString(diag);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Diagnosis", 0));
		
		assertEquals("(0)", shouldTag, isTag);
	}
	
	public void testDiagnosisWithApriori() throws Exception{
		diag.setAprioriProbability(Score.N2);
		
		shouldTag.addAttribute("aPriProb", "N2");
		
		xmlcode = dw.getXMLString(diag);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Diagnosis", 0));
		
		assertEquals("(1)", shouldTag, isTag);
	}
	
	public void testDiagnosisWithChildren() throws Exception {
		// add two children
		Diagnosis d11 = new Diagnosis();
		d11.setId("d11");
		d11.setText("d11-text");
		d11.addParent(diag);
		
		Diagnosis d12 = new Diagnosis();
		d12.setId("d12");
		d12.setText("d12-text");
		d12.addParent(diag);
		
		XMLTag shouldChildrenTag = new XMLTag("Children");
		XMLTag childTag1 = new XMLTag("Child");
		childTag1.addAttribute("ID", "d11");
		shouldChildrenTag.addChild(childTag1);
			
		XMLTag childTag2 = new XMLTag("Child");
		childTag2.addAttribute("ID", "d12");
		shouldChildrenTag.addChild(childTag2);
		
		shouldTag.addChild(shouldChildrenTag);

		xmlcode = dw.getXMLString(diag);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Diagnosis", 0));		
					
		assertEquals("(2)", shouldTag, isTag);
	}
	
	public void testDiagnosisWithProperties() throws Exception {
		diag.getProperties().setProperty(Property.HIDE_IN_DIALOG, new Boolean(true));
		diag.getProperties().setProperty(Property.COST, new Double(20));
		
		// Set propertyKeys = diag.getPropertyKeys();
		// MockPropertyDescriptor mpd = new MockPropertyDescriptor(diag,propertyKeys);
		
		XMLTag propertiesTag = new XMLTag("Properties");
		
		XMLTag propertyTag1 = new XMLTag("Property");
		propertyTag1.addAttribute("name", "hide_in_dialog");
		// old: propertyTag1.addAttribute("descriptor", "hide_in_dialog");
		propertyTag1.addAttribute("class", "java.lang.Boolean");
		propertyTag1.setContent("true");
		
		XMLTag propertyTag2 = new XMLTag("Property");
		propertyTag2.addAttribute("name", "cost");
		// old: propertyTag2.addAttribute("descriptor", "cost");
		propertyTag2.addAttribute("class", "java.lang.Double");
		propertyTag2.setContent("20.0");
		
		propertiesTag.addChild(propertyTag1);
		propertiesTag.addChild(propertyTag2);
		
		shouldTag.addChild(propertiesTag);
		
		xmlcode = dw.getXMLString(diag);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Diagnosis", 0));		
					
		assertEquals("(3)", shouldTag, isTag);
	}
}
