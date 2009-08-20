/*
 * Created on 10.10.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.d3web.persistence.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.kernel.domainModel.qasets.QuestionDate;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.persistence.tests.utils.XMLTagUtils;
import de.d3web.persistence.xml.MockQASet;
import de.d3web.persistence.xml.writers.QuestionWriter;

/**
 * @author vogele
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class QuestionDateTest extends TestCase{
	private QuestionDate q1;
	private QuestionWriter qw;
	private MockQASet mq1;
	private String xmlcode;

	private XMLTag isTag;
	private XMLTag shouldTag;

	/**
	 * Constructor for QuestionTextTest.
	 * @param arg0
	 */
	public QuestionDateTest(String args) {
		super(args);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(QuestionDateTest.suite());
	}

	public static Test suite() {
		return new TestSuite(QuestionDateTest.class);
	}

	protected void setUp() {
		q1 = new QuestionDate();
		q1.setId("q1");
		q1.setText("q1-text");
		
		mq1 = new MockQASet();
		mq1.setQASet(q1);

		qw = new QuestionWriter();

		shouldTag = new XMLTag("Question");
		shouldTag.addAttribute("ID", "q1");
		shouldTag.addAttribute("type", "Date");
		XMLTag child = new XMLTag("Text");
		child.setContent("q1-text");
		shouldTag.addChild(child);
	}

	public void testQuestionDate() throws Exception {
		xmlcode = qw.getXMLString(mq1);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Question", 0));
		assertEquals("(0)", shouldTag, isTag);
	}

}
