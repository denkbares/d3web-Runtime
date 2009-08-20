package de.d3web.persistence.tests;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.w3c.dom.Node;

import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionDate;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.domainModel.qasets.QuestionOC;
import de.d3web.kernel.domainModel.qasets.QuestionText;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.persistence.tests.utils.XMLTagUtils;
import de.d3web.persistence.xml.MockQASet;
import de.d3web.persistence.xml.writers.DiagnosisWriter;
import de.d3web.persistence.xml.writers.QContainerWriter;
import de.d3web.persistence.xml.writers.QuestionWriter;

/**
 * @author bates
 *
 */
public class KnowledgeBaseExportTest extends TestCase {

	public KnowledgeBaseExportTest(String name) throws Exception {
		super(name);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(KnowledgeBaseExportTest.suite());
	}

	public static Test suite() {
		return new TestSuite(KnowledgeBaseExportTest.class);
	}

	
	public void testQuestionTextOutput() throws Exception {
	
		Question q1 = new QuestionText();
		q1.setId("q1");
		q1.setText("q1-text");

		MockQASet mq1 = new MockQASet();
		mq1.setQASet(q1);

		QuestionWriter qw = new QuestionWriter();
		String xmlcode = qw.getXMLString(mq1);
		Node qNode = XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Question", 0);

		XMLTag isTag = new XMLTag(qNode);
		

		XMLTag shouldTag = new XMLTag("Question");
		shouldTag.addAttribute("ID","q1");
		shouldTag.addAttribute("type","Text");
		XMLTag child = new XMLTag("Text");
		child.setContent("q1-text");
		shouldTag.addChild(child);

		assertEquals("QuestionText-output not correct (0)", shouldTag, isTag);
	}

	public void testQuestionNumOutput() throws Exception {
	
		Question q1 = new QuestionNum();
		q1.setId("q1");
		q1.setText("q1-text");

		MockQASet mq1 = new MockQASet();
		mq1.setQASet(q1);

		QuestionWriter qw = new QuestionWriter();
		String xmlcode = qw.getXMLString(mq1);
		Node qNode = XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Question", 0);

		XMLTag isTag = new XMLTag(qNode);
		

		XMLTag shouldTag = new XMLTag("Question");
		shouldTag.addAttribute("ID","q1");
		shouldTag.addAttribute("type","Num");
		XMLTag child = new XMLTag("Text");
		child.setContent("q1-text");
		shouldTag.addChild(child);

		assertEquals("QuestionNum-output not correct (0)", shouldTag, isTag);
	}
	
	
	public void testQuestionDateOutput() throws Exception {
	
		Question q1 = new QuestionDate();
		q1.setId("q1");
		q1.setText("q1-text");

		MockQASet mq1 = new MockQASet();
		mq1.setQASet(q1);

		QuestionWriter qw = new QuestionWriter();
		String xmlcode = qw.getXMLString(mq1);
		Node qNode = XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Question", 0);

		XMLTag isTag = new XMLTag(qNode);
		

		XMLTag shouldTag = new XMLTag("Question");
		shouldTag.addAttribute("ID","q1");
		shouldTag.addAttribute("type","Date");
		XMLTag child = new XMLTag("Text");
		child.setContent("q1-text");
		shouldTag.addChild(child);

		assertEquals("QuestionNum-output not correct (0)", shouldTag, isTag);
	}	
	
	public void testQuestionOCOutput() throws Exception {
	
		QuestionOC q1 = new QuestionOC();
		q1.setId("q1");
		q1.setText("q1-text");
		List alternatives = new LinkedList();
		AnswerChoice a1 = new AnswerChoice();
		a1.setId("q1a1");
		a1.setText("q1a1-text");
		alternatives.add(a1);
		q1.setAlternatives(alternatives);
	
		MockQASet mq1 = new MockQASet();
		mq1.setQASet(q1);

		QuestionWriter qw = new QuestionWriter();
		String xmlcode = qw.getXMLString(mq1);
		
		Node qNode = XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Question", 0);

		XMLTag isTag = new XMLTag(qNode);
		

		XMLTag shouldTag = new XMLTag("Question");
		shouldTag.addAttribute("ID","q1");
		shouldTag.addAttribute("type","OC");
		XMLTag child = new XMLTag("Text");
		child.setContent("q1-text");
		shouldTag.addChild(child);
		
		XMLTag answersTag = new XMLTag("Answers");
		XMLTag answerTag = new XMLTag("Answer");
		answerTag.addAttribute("ID", "q1a1");
		answerTag.addAttribute("type", "AnswerChoice");
		XMLTag answerTextTag = new XMLTag("Text");
		answerTextTag.setContent("q1a1-text");
		answerTag.addChild(answerTextTag);
		answersTag.addChild(answerTag);			
		shouldTag.addChild(answersTag);

		assertEquals("QuestionOC-output not correct (0)", shouldTag, isTag);
	}
	
	public void testQContainerOutput() throws Exception {
		QContainer c1 = new QContainer();
		c1.setId("c1");
		c1.setText("c1-text");
		c1.setPriority(new Integer(1));

		Question q1 = new QuestionText();
		q1.setId("q1");
		q1.setText("q1-text");
		q1.addParent(c1);

		MockQASet mq1 = new MockQASet();
		mq1.setQASet(c1);
		
		QContainerWriter qcw = new QContainerWriter();
		String xmlcode = qcw.getXMLString(mq1);
		
		XMLTag isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "QContainer", 0));

		
		XMLTag shouldTag = new XMLTag("QContainer");
		shouldTag.addAttribute("ID", "c1");
		shouldTag.addAttribute("priority", "1");
		XMLTag shouldTextTag = new XMLTag("Text");
		shouldTextTag.setContent("c1-text");
		shouldTag.addChild(shouldTextTag);
		
		XMLTag shouldChildrenTag = new XMLTag("Children");
		XMLTag childTag = new XMLTag("Child");
		childTag.addAttribute("ID", "q1");
		shouldChildrenTag.addChild(childTag);
		
		shouldTag.addChild(shouldChildrenTag);

		assertEquals("Qcontainer-output not correct (0)", shouldTag, isTag);

	}

	
	public void testDiagnosisOutput() throws Exception{
		Diagnosis diag = new Diagnosis();
		diag.setId("d1");
		diag.setText("d1-text");

		Diagnosis diagChild = new Diagnosis();
		diagChild.setId("d11");
		diagChild.setText("d11-text");
		diagChild.addParent(diag);
		
		
		DiagnosisWriter dw = new DiagnosisWriter();
		String xmlcode = dw.getXMLString(diag);
		
		XMLTag isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Diagnosis", 0));		
		
		XMLTag shouldTag = new XMLTag("Diagnosis");
		shouldTag.addAttribute("ID", "d1");
		XMLTag shouldTextTag = new XMLTag("Text");
		shouldTextTag.setContent("d1-text");
		shouldTag.addChild(shouldTextTag);
		
		XMLTag shouldChildrenTag = new XMLTag("Children");
		XMLTag childTag = new XMLTag("Child");
		childTag.addAttribute("ID", "d11");
		shouldChildrenTag.addChild(childTag);
		
		shouldTag.addChild(shouldChildrenTag);

		assertEquals("Diagnosis-output not correct (0)", shouldTag, isTag);
		
	}

}
