package de.d3web.persistence.tests;

import java.util.HashSet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionText;
import de.d3web.kernel.supportknowledge.Property;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.persistence.tests.utils.XMLTagUtils;
import de.d3web.persistence.xml.MockCostObject;
import de.d3web.persistence.xml.MockQASet;
import de.d3web.persistence.xml.writers.QContainerWriter;

/**
 * @author merz
 *
 * !!! property-test missing !!!
 */
public class QContainerTest extends TestCase {

	private QContainer qc1;
	private QContainerWriter qcw;
	private MockQASet mq1;
	private MockCostObject mco1, mco2, mco3;
	
	private XMLTag isTag;
	private XMLTag shouldTag;
	
	
	/**
	 * Constructor for QContainerOutputTest.
	 * @param arg0
	 */
	public QContainerTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(QContainerTest.suite());
	}
	
	public static Test suite() {
		return new TestSuite(QContainerTest.class);
	}
	
	protected void setUp(){
		
		qc1 = new QContainer();
		qc1.setId("c1");
		qc1.setText("c1-text");
		
		shouldTag = new XMLTag("QContainer");
		shouldTag.addAttribute("ID", "c1");
		
		XMLTag shouldTextTag = new XMLTag("Text");
		shouldTextTag.setContent("c1-text");
		shouldTag.addChild(shouldTextTag);
		
		mq1 = new MockQASet();
		mq1.setQASet(qc1);
		
		qcw = new QContainerWriter();		
		
	}
	
	public void testQContainerSimple() throws Exception{
		String xmlcode = qcw.getXMLString(mq1);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "QContainer", 0));

		assertEquals("(0)", shouldTag, isTag);
	}
	
	public void testQContainerWithPriority() throws Exception{
		qc1.setPriority(new Integer(1));
		shouldTag.addAttribute("priority", "1");
		
		String xmlcode = qcw.getXMLString(mq1);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "QContainer", 0));

		assertEquals("(1)", shouldTag, isTag);
	}
	
	public void testQContainerWithChildren() throws Exception{
		Question q1 = new QuestionText();
		q1.setId("q1");
		q1.setText("q1-text");
		q1.addParent(qc1);
		
		Question q2 = new QuestionText();
		q2.setId("q2");
		q2.setText("q2-text");
		q2.addParent(qc1);
		
		XMLTag shouldChildrenTag = new XMLTag("Children");
		
		XMLTag childTag1 = new XMLTag("Child");
		childTag1.addAttribute("ID", "q1");
		shouldChildrenTag.addChild(childTag1);
		
		XMLTag childTag2 = new XMLTag("Child");
		childTag2.addAttribute("ID", "q2");
		shouldChildrenTag.addChild(childTag2);
		
		shouldTag.addChild(shouldChildrenTag);

		String xmlcode = qcw.getXMLString(mq1);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "QContainer", 0));

		assertEquals("(2)", shouldTag, isTag);
	}
	
	public void testQContainerWithCosts() throws Exception{
		HashSet costSet = new HashSet();
		mco1 = new MockCostObject("timeexpenditure", "Minuten", "Arztzeit");
		costSet.add(mco1);
  		mco2 = new MockCostObject("risk", "Punkte", "Patientenbelastung");
  		costSet.add(mco2);
		mco3 = new MockCostObject("cost", "Euro", "Kosten");
		costSet.add(mco3);
		mq1.setCostObjects(costSet);
		
		qc1.getProperties().setProperty(Property.TIME, new Double(20));
		qc1.getProperties().setProperty(Property.RISK, new Double(50.5));
		
		/*
		// old way of costs writing
		
		XMLTag shouldCostsTag = new XMLTag("Costs");
		
		XMLTag costTag1 = new XMLTag("Cost");
		costTag1.addAttribute("ID", "timeexpenditure");
		costTag1.addAttribute("value", Double.toString(20));
		shouldCostsTag.addChild(costTag1);
		
		XMLTag costTag2 = new XMLTag("Cost");
		costTag2.addAttribute("ID", "risk");
		costTag2.addAttribute("value", Double.toString(50.5));
		shouldCostsTag.addChild(costTag2);
		
		shouldTag.addChild(shouldCostsTag);
		*/

		XMLTag shouldCostsTag = new XMLTag("Properties");
		
		XMLTag costTag1 = new XMLTag("Property");
		costTag1.addAttribute("name", "timeexpenditure");
		costTag1.addAttribute("class", Double.class.getName());
		costTag1.setContent(Double.toString(20));
		shouldCostsTag.addChild(costTag1);
		
		XMLTag costTag2 = new XMLTag("Property");
		costTag2.addAttribute("name", "risk");
		costTag2.addAttribute("class", Double.class.getName());
		costTag2.setContent(Double.toString(50.5));
		shouldCostsTag.addChild(costTag2);
		
		shouldTag.addChild(shouldCostsTag);
		
		
		String xmlcode = qcw.getXMLString(mq1);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "QContainer", 0));

		assertEquals("(3)", shouldTag, isTag);
	}
	
	public void testQContainerWithProperties() throws Exception {
		qc1.getProperties().setProperty(Property.HIDE_IN_DIALOG, new Boolean(true));
		qc1.getProperties().setProperty(Property.COST, new Double(20));
		
		// Set propertyKeys = qc1.getPropertyKeys();
		// MockPropertyDescriptor mpd = new MockPropertyDescriptor(qc1,propertyKeys);
		
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
		
		String xmlcode = qcw.getXMLString(mq1);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "QContainer", 0));		
					
		assertEquals("(4)", shouldTag, isTag);
	}
}
