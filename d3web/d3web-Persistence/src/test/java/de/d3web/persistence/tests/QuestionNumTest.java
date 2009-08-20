package de.d3web.persistence.tests;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.kernel.domainModel.NumericalInterval;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.domainModel.qasets.QuestionOC;
import de.d3web.kernel.supportknowledge.Property;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.persistence.tests.utils.XMLTagUtils;
import de.d3web.persistence.xml.MockCostObject;
import de.d3web.persistence.xml.MockQASet;
import de.d3web.persistence.xml.loader.NumericalIntervalsUtils;
import de.d3web.persistence.xml.writers.QuestionWriter;

/**
 * @author merz
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class QuestionNumTest extends TestCase {
	public void testQuestionWithProperties() throws Exception {
		q1.getProperties().setProperty(Property.HIDE_IN_DIALOG, new Boolean(true));
		q1.getProperties().setProperty(Property.COST, new Double(20));
		
		// Set propertyKeys = q1.getPropertyKeys();
		// MockPropertyDescriptor mpd = new MockPropertyDescriptor(q1,propertyKeys);
		
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
		
		xmlcode = qw.getXMLString(mq1);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Question", 0));		
					
		assertEquals("(2)", shouldTag, isTag);
	}

	private Question q1;
	private QuestionWriter qw;
	
	private MockQASet mq1;
	private MockCostObject mco1, mco2, mco3;
	
	private String xmlcode;

	private XMLTag isTag;
	private XMLTag shouldTag;
	

	/**
	 * Constructor for QuestionNumOutput.
	 * @param arg0
	 */
	public QuestionNumTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(QuestionNumTest.suite());
	}

	public static Test suite() {
		return new TestSuite(QuestionNumTest.class);
	}

	protected void setUp() {
		q1 = new QuestionNum();
		q1.setId("q1");
		q1.setText("q1-text");
		 
		mq1 = new MockQASet();
		mq1.setQASet(q1);

		qw = new QuestionWriter();

		shouldTag = new XMLTag("Question");
		shouldTag.addAttribute("ID", "q1");
		shouldTag.addAttribute("type", "Num");
		XMLTag child = new XMLTag("Text");
		child.setContent("q1-text");
		shouldTag.addChild(child);
	}

	public void testQuestionNumTestSimple() throws Exception{
		xmlcode = qw.getXMLString(mq1);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Question", 0));

		assertEquals("(0)", shouldTag, isTag);
	}
		
	public void testQuestionOCWithChildren() throws Exception{
		
		QuestionOC qtemp1 = new QuestionOC();
		qtemp1.setId("qtemp1");
		qtemp1.setText("q2-text");
		
		QuestionNum qtemp2 = new QuestionNum();
		qtemp2.setId("qtemp2");
		qtemp2.setText("q2-text");
		
		q1.addChild(qtemp1);
		q1.addChild(qtemp2);
		
		XMLTag children = new XMLTag("Children");
		
		XMLTag child1 = new XMLTag("Child");
		child1.addAttribute("ID", "qtemp1");
		children.addChild(child1);
		
		XMLTag child2 = new XMLTag("Child");
		child2.addAttribute("ID", "qtemp2");
		children.addChild(child2);
		
		shouldTag.addChild(children);
		
		xmlcode = qw.getXMLString(mq1);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Question", 0));
		
		assertEquals("(1)", shouldTag, isTag);
	}	
		
	
	public void testQuestionWithIntervals() throws Exception {
		
		List intervals = new LinkedList();
		intervals.add(new NumericalInterval(Double.NEGATIVE_INFINITY, 30, true, false));
		intervals.add(new NumericalInterval(30, 300.03, true, true));
		intervals.add(new NumericalInterval(300.03, Double.POSITIVE_INFINITY, false, true));
		((QuestionNum) q1).setValuePartitions(intervals);

		XMLTag intervalsTag = new XMLTag(NumericalIntervalsUtils.GROUPTAG);
		
		XMLTag intervalTag1 = new XMLTag(NumericalIntervalsUtils.TAG);
		intervalTag1.addAttribute("lower", "-INFINITY");
		intervalTag1.addAttribute("upper", "30.0");
		intervalTag1.addAttribute("type", "LeftOpenRightClosedInterval");

		XMLTag intervalTag2 = new XMLTag(NumericalIntervalsUtils.TAG);
		intervalTag2.addAttribute("lower", "30.0");
		intervalTag2.addAttribute("upper", "300.03");
		intervalTag2.addAttribute("type", "LeftOpenRightOpenInterval");

		XMLTag intervalTag3 = new XMLTag(NumericalIntervalsUtils.TAG);
		intervalTag3.addAttribute("lower", "300.03");
		intervalTag3.addAttribute("upper", "+INFINITY");
		intervalTag3.addAttribute("type", "LeftClosedRightOpenInterval");
		
		intervalsTag.addChild(intervalTag1);
		intervalsTag.addChild(intervalTag2);
		intervalsTag.addChild(intervalTag3);
		
		shouldTag.addChild(intervalsTag);
		xmlcode = qw.getXMLString(mq1);

		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Question", 0));		
		assertEquals("(intervals)", shouldTag, isTag);
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
		
		q1.getProperties().setProperty(Property.TIME, new Double(20));
		q1.getProperties().setProperty(Property.RISK, new Double(50.5));
		
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
		
		String xmlcode = qw.getXMLString(mq1);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Question", 0));

		assertEquals("(3)", shouldTag, isTag);
	}

}
