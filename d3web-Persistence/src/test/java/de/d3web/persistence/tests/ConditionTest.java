package de.d3web.persistence.tests;

import java.util.LinkedList;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerUnknown;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.domainModel.qasets.QuestionOC;
import de.d3web.kernel.domainModel.qasets.QuestionText;
import de.d3web.kernel.domainModel.qasets.QuestionYN;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondAnd;
import de.d3web.kernel.domainModel.ruleCondition.CondChoiceNo;
import de.d3web.kernel.domainModel.ruleCondition.CondChoiceYes;
import de.d3web.kernel.domainModel.ruleCondition.CondDState;
import de.d3web.kernel.domainModel.ruleCondition.CondEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondKnown;
import de.d3web.kernel.domainModel.ruleCondition.CondMofN;
import de.d3web.kernel.domainModel.ruleCondition.CondNot;
import de.d3web.kernel.domainModel.ruleCondition.CondNum;
import de.d3web.kernel.domainModel.ruleCondition.CondNumEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondNumGreater;
import de.d3web.kernel.domainModel.ruleCondition.CondNumIn;
import de.d3web.kernel.domainModel.ruleCondition.CondNumLess;
import de.d3web.kernel.domainModel.ruleCondition.CondOr;
import de.d3web.kernel.domainModel.ruleCondition.CondTextContains;
import de.d3web.kernel.domainModel.ruleCondition.CondTextEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondUnknown;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.persistence.tests.utils.XMLTagUtils;
import de.d3web.persistence.xml.writers.conditions.ConditionsPersistenceHandler;
import de.d3web.persistence.xml.writers.conditions.nonTerminalWriters.CondAndWriter;
import de.d3web.persistence.xml.writers.conditions.nonTerminalWriters.CondMinMaxWriter;
import de.d3web.persistence.xml.writers.conditions.nonTerminalWriters.CondNotWriter;
import de.d3web.persistence.xml.writers.conditions.nonTerminalWriters.CondOrWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondChoiceNoWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondChoiceYesWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondDStateWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondEqualWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondKnownWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondNumEqualWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondNumGreaterWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondNumInWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondNumLessWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondTextContainsWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondTextEqualWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondUnknownWriter;

/**
 * @author merz
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ConditionTest extends TestCase {
	private AbstractCondition ac1, ac21, ac22, ac3;
	
	private CondChoiceNo cChoiceNo1;
	private CondChoiceYes cChoiceYes1;
	private CondDState cDState1;
	private CondEqual cEqual1;
	private CondKnown cKnown1;
	private CondNum cNumE1,cNumG1,cNumIn1,cNumL1;
	private CondTextContains cTextContains1;
	private CondTextEqual cTextEqual1;
	private CondUnknown cUnknown1;
	
	private Diagnosis d1;

	private String xmlcode;
	
	private XMLTag isTag;
	private XMLTag shouldTag;
	
	public ConditionTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(ConditionTest.suite());
	}
	
	public static Test suite() {
		return new TestSuite(ConditionTest.class);
	}
	
	protected void setUp() {
		ConditionsPersistenceHandler.getInstance().add(new CondAndWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondNotWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondOrWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondMinMaxWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondNotWriter());
		
		ConditionsPersistenceHandler.getInstance().add(new CondChoiceNoWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondChoiceYesWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondDStateWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondEqualWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondKnownWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondNumEqualWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondNumGreaterWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondNumInWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondNumLessWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondTextContainsWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondTextEqualWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondUnknownWriter());
		
		
		d1 = new Diagnosis();
		d1.setId("d1-id");
		
		Question qoc1 = new QuestionOC();
		qoc1.setId("qoc1-id");
		QuestionNum qnum1 = new QuestionNum();
		qnum1.setId("qnum1-id");
		QuestionNum qnum2 = new QuestionNum();
		qnum2.setId("qnum2-id");
		QuestionYN qyn1 = new QuestionYN();
		qyn1.setId("qyn1-id");
		QuestionYN qyn2 = new QuestionYN();
		qyn2.setId("qyn2-id");
		QuestionText qt1 = new QuestionText();
		qt1.setId("qt1-id");
		qt1.setText("qt1-text");
		
		
		Vector val1 = new Vector();
		Vector val2 = new Vector();
		
		AnswerChoice ach1 = new AnswerChoice();
		ach1.setId("ach1-id");
		ach1.setText("ach1-text");
		val1.add(ach1);
		
		AnswerChoice ach2 = new AnswerChoice();
		ach2.setId("ach2-id");
		ach2.setText("ach2-text");
		val1.add(ach2);
		val2.add(ach2);
		
		
		cChoiceNo1 = new CondChoiceNo(qyn1);
		cChoiceYes1 = new CondChoiceYes(qyn2);
		
		cDState1 = new CondDState(d1, DiagnosisState.SUGGESTED, null);
		
		cEqual1 = new CondEqual(qnum1, new AnswerUnknown() );
		cEqual1.setValues(val1);
		
		cKnown1 = new CondKnown(qnum1);
		
		cNumL1 =  new CondNumEqual(qnum1, new Double(4.5));
		cNumG1 = new CondNumGreater(qnum1, new Double(10));
		cNumIn1 = new CondNumIn(qnum2, new Double(4.0),new Double(12));
		cNumE1 = new CondNumLess(qnum2, new Double(3));
		
		cTextContains1 = new CondTextContains(qt1,"text");
		cTextEqual1 = new CondTextEqual(qt1,"qt1-text");
		
		cUnknown1 = new CondUnknown(qoc1);
	}
	
	public void _testAllCondNums() throws Exception{
		
		LinkedList l1 = new LinkedList();
		l1.add(cNumL1);
		l1.add(cNumIn1);
		l1.add(cNumG1);
		l1.add(cNumE1);
		ac1 = new CondAnd(l1);
		
		shouldTag = new XMLTag("Condition");
		shouldTag.addAttribute("type", "and");
		
		XMLTag condNumE = new XMLTag("Condition");
		condNumE.addAttribute("type", "numEqual");
		condNumE.addAttribute("ID","qnum1-id");
		condNumE.addAttribute("value","4.5");
		
		XMLTag condNumIn = new XMLTag("Condition");
		condNumIn.addAttribute("type", "numIn");
		condNumIn.addAttribute("ID","qnum2-id");
		condNumIn.addAttribute("minValue","4.0");
		condNumIn.addAttribute("maxValue","12.0");
		
		XMLTag condNumG = new XMLTag("Condition");
		condNumG.addAttribute("type", "numGreater");
		condNumG.addAttribute("ID","qnum1-id");
		condNumG.addAttribute("value","10.0");
		
		XMLTag condNumL = new XMLTag("Condition");
		condNumL.addAttribute("type", "numLess");
		condNumL.addAttribute("ID","qnum2-id");
		condNumL.addAttribute("value","3.0");
		
		shouldTag.addChild(condNumE);
		shouldTag.addChild(condNumIn);
		shouldTag.addChild(condNumG);
		shouldTag.addChild(condNumL);
		
		xmlcode = ConditionsPersistenceHandler.getInstance().toXML(ac1);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Condition", 0));
		
		assertEquals("(0)", shouldTag,isTag);
	}
	
	public void testAllNonTerminalConditions() throws Exception{
		
		ac3 = new CondNot(cDState1);
		
		LinkedList l21 = new LinkedList();
		l21.add(cEqual1);
		l21.add(cNumG1);
		l21.add(ac3);
		ac21 = new CondAnd(l21);
		
		
		LinkedList l22 = new LinkedList();
		l22.add(cChoiceYes1);
		l22.add(cChoiceNo1);
		l22.add(cTextContains1);
		ac22 = new CondMofN(l22, 1, 3);
		
		LinkedList l1 = new LinkedList();
		l1.add(ac21);
		l1.add(ac22);
		ac1 = new CondOr(l1);
		
		
		shouldTag = new XMLTag("Condition");
		shouldTag.addAttribute("type", "or");
		
			XMLTag andTag1 = new XMLTag("Condition");
			andTag1.addAttribute("type", "and");
			
				XMLTag equalTag1 = new XMLTag("Condition");
				equalTag1.addAttribute("type", "equal");
				equalTag1.addAttribute("ID", "qnum1-id");
				equalTag1.addAttribute("value", "ach1-id,ach2-id");
				andTag1.addChild(equalTag1);
				
				XMLTag numGreaterTag1 = new XMLTag("Condition");
				numGreaterTag1.addAttribute("type", "numGreater");
				numGreaterTag1.addAttribute("ID", "qnum1-id");
				numGreaterTag1.addAttribute("value", "10.0");
				andTag1.addChild(numGreaterTag1);
		
				XMLTag notTag1 = new XMLTag("Condition");
				notTag1.addAttribute("type", "not");
				
					XMLTag dStateTag1 = new XMLTag("Condition");
					dStateTag1.addAttribute("type", "DState");
					dStateTag1.addAttribute("ID", "d1-id");
					dStateTag1.addAttribute("value", "suggested");
					notTag1.addChild(dStateTag1);
				
				andTag1.addChild(notTag1);	
			
			shouldTag.addChild(andTag1);
				
			XMLTag mofnTag1 = new XMLTag("Condition");
			mofnTag1.addAttribute("type", "MofN");
			mofnTag1.addAttribute("min", "1");
			mofnTag1.addAttribute("max", "3");
			mofnTag1.addAttribute("size", "3");						
					
				XMLTag choiceYesTag1 = new XMLTag("Condition");
				choiceYesTag1.addAttribute("type", "choiceYes");
				choiceYesTag1.addAttribute("ID", "qyn2-id");
				mofnTag1.addChild(choiceYesTag1);
					
				XMLTag choiceNoTag1 = new XMLTag("Condition");
				choiceNoTag1.addAttribute("type", "choiceNo");
				choiceNoTag1.addAttribute("ID", "qyn1-id");
				mofnTag1.addChild(choiceNoTag1);
					
				XMLTag tContainsTag1 = new XMLTag("Condition");
				tContainsTag1.addAttribute("type", "textContains");
				tContainsTag1.addAttribute("ID", "qt1-id");
//				tContainsTag1.addAttribute("value", "qt1-text");
				XMLTag var1 = new XMLTag("Value");
				var1.setContent("text");
				tContainsTag1.addChild(var1);
				mofnTag1.addChild(tContainsTag1);
							
			shouldTag.addChild(mofnTag1);
		
		xmlcode = ConditionsPersistenceHandler.getInstance().toXML(ac1);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Condition", 0));
		
		assertEquals("(1)", shouldTag,isTag);
	}
	
	public void testAllCondText() throws Exception{
		
		LinkedList l1 = new LinkedList();
		l1.add(cTextContains1);
		l1.add(cTextEqual1);
		ac1 = new CondOr(l1);
		
		shouldTag = new XMLTag("Condition");
		shouldTag.addAttribute("type", "or");
			
		XMLTag tContainsTag1 = new XMLTag("Condition");
		tContainsTag1.addAttribute("type", "textContains");
		tContainsTag1.addAttribute("ID", "qt1-id");
		
//		tContainsTag1.addAttribute("value", "text");
		XMLTag var1 = new XMLTag("Value");
		var1.setContent("text");
		tContainsTag1.addChild(var1);
		
		shouldTag.addChild(tContainsTag1);
				
		XMLTag tEqualTag1 = new XMLTag("Condition");
		tEqualTag1.addAttribute("type", "textEqual");
		tEqualTag1.addAttribute("ID", "qt1-id");

//		tEqualTag1.addAttribute("value", "");
		XMLTag var2 = new XMLTag("Value");
		var2.setContent("qt1-text");
		tEqualTag1.addChild(var2);

		shouldTag.addChild(tEqualTag1);
		
		xmlcode = ConditionsPersistenceHandler.getInstance().toXML(ac1);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Condition", 0));
		
		assertEquals("(2)", shouldTag, isTag);
	}
	
	public void testCondKnownAndUnknown() throws Exception{
		
		LinkedList l1 = new LinkedList();
		l1.add(cKnown1);
		l1.add(cUnknown1);
		ac1 = new CondOr(l1);
		
		shouldTag = new XMLTag("Condition");
		shouldTag.addAttribute("type", "or");
		
		XMLTag cKnownTag1 = new XMLTag("Condition");
		cKnownTag1.addAttribute("type", "known");
		cKnownTag1.addAttribute("ID", "qnum1-id");
		shouldTag.addChild(cKnownTag1);
		
		XMLTag cUnKnownTag1 = new XMLTag("Condition");
		cUnKnownTag1.addAttribute("type", "unknown");
		cUnKnownTag1.addAttribute("ID", "qoc1-id");
		shouldTag.addChild(cUnKnownTag1);
			
		
		xmlcode = ConditionsPersistenceHandler.getInstance().toXML(ac1);
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "Condition", 0));
		
		assertEquals("(3)", shouldTag,isTag);
	}
}
