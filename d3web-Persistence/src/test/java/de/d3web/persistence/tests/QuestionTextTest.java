package de.d3web.persistence.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionText;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.persistence.tests.utils.XMLTagUtils;
import de.d3web.persistence.xml.MockQASet;
import de.d3web.persistence.xml.writers.QuestionWriter;

/**
 * @author merz
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class QuestionTextTest extends TestCase {

	private Question q1;
	private QuestionWriter qw;
	private MockQASet mq1;
	private String xmlcode;

	private XMLTag isTag;
	private XMLTag shouldTag;

	/**
	 * Constructor for QuestionTextTest.
	 * @param arg0
	 */
	public QuestionTextTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(QuestionTextTest.suite());
	}

	public static Test suite() {
		return new TestSuite(QuestionTextTest.class);
	}

	protected void setUp() {
		q1 = new QuestionText();
		q1.setId("q1");
		q1.setText("q1-text");
		
		mq1 = new MockQASet();
		mq1.setQASet(q1);

		qw = new QuestionWriter();

		shouldTag = new XMLTag("Question");
		shouldTag.addAttribute("ID", "q1");
		shouldTag.addAttribute("type", "Text");
		XMLTag child = new XMLTag("Text");
		child.setContent("q1-text");
		shouldTag.addChild(child);
	}

	public void testQuestionNumTestSimple() throws Exception {
		xmlcode = qw.getXMLString(mq1);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Question", 0));

		assertEquals("(0)", shouldTag, isTag);
	}
}
