/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.persistence.tests;
import java.util.LinkedList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.PriorityGroup;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.Score;
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.domainModel.qasets.QuestionOC;
import de.d3web.kernel.domainModel.ruleCondition.CondDState;
import de.d3web.kernel.psMethods.MethodKind;
import de.d3web.kernel.psMethods.heuristic.ActionHeuristicPS;
import de.d3web.kernel.psMethods.heuristic.PSMethodHeuristic;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.persistence.tests.utils.XMLTagUtils;
import de.d3web.persistence.xml.BasicPersistenceHandler;

/**
 * @author merz
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class BasicPersistenceHandlerTest extends TestCase {

	private KnowledgeBase kb;
	private BasicPersistenceHandler bph;
	private String xmlcode;
	
	private Question q1,q2;
	private Diagnosis diag1;
	
	private XMLTag isTag;
	private XMLTag shouldTag;
	
	/**
	 * Constructor for RuleComplexTest.
	 * @param arg0
	 */
	public BasicPersistenceHandlerTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		TestRunner.run(BasicPersistenceHandlerTest.suite());
	}
	
	public static Test suite() {
		return new TestSuite(BasicPersistenceHandlerTest.class);
	}
	
	protected void setUp() {
		kb = new KnowledgeBase(); 
		
		q1 = new QuestionNum();
		q1.setId("q1");
		q1.setText("q1-text");
		q1.setKnowledgeBase(kb);
		
		q2 = new QuestionOC();
		q2.setId("q2");
		q2.setText("q2-text");
		q2.setKnowledgeBase(kb);
		
		diag1 = new Diagnosis();
		diag1.setId("d1");
		diag1.setText("d1-text");
	}
	
	public void testBasicPersistenceHandler() throws Exception{
		shouldTag = new XMLTag("KnowledgeBase");
		shouldTag.addAttribute("type", "basic");
		shouldTag.addAttribute("system", "d3web");
		
		this.addPriorityGroups();
	
		this.addInitQuestions();
		
		this.addCosts();
		
		this.addQContainers();
			
		this.addQuestions();
		
		this.addDiagnoses();
		
		
		//XMLTag knowledgeSlicesTag = new XMLTag("KnowledgeSlices");
		//shouldTag.addChild(knowledgeSlicesTag);
		this.addKnowledgeSlices();
		
		bph = new BasicPersistenceHandler();
		bph.save(kb);
		xmlcode = bph.getKnowledgeBaseToXMLStringForTests();
		isTag = new XMLTag(XMLTagUtils.generateNodeFromXMLCode(xmlcode, "KnowledgeBase", 0));
		
		assertEquals("(0)", shouldTag, isTag);
	}
	
	private void addCosts(){
		kb.setCostUnit("timeexpenditure-id","Minuten");
		kb.setCostVerbalization("timeexpenditure-id","Arztzeit");
		
		kb.setCostUnit("risk-id","Punkte");
		kb.setCostVerbalization("risk-id","Patientenbelastung");
		
		XMLTag costsTag = new XMLTag("Costs");
		shouldTag.addChild(costsTag);
		
		XMLTag costTag1 = new XMLTag("Cost");	
		costTag1.addAttribute("ID", "risk-id");
		costTag1.addAttribute("verbalization", "Patientenbelastung");
		costTag1.addAttribute("unit", "Punkte");
		costsTag.addChild(costTag1);
		
		XMLTag costTag2 = new XMLTag("Cost");	
		costTag2.addAttribute("ID", "timeexpenditure-id");
		costTag2.addAttribute("verbalization", "Arztzeit");
		costTag2.addAttribute("unit", "Minuten");
		costsTag.addChild(costTag2);
	}
	
	private void addQContainers(){
		QContainer qc1 = new QContainer();
		qc1.setId("qc1");
		qc1.setText("qc1-text");
		
		QContainer qc2 = new QContainer();
		qc2.setId("qc2");
		qc2.setText("qc2-text");
		
		kb.add(qc1);
		kb.add(qc2);
		
		XMLTag qContainersTag = new XMLTag("QContainers");
		shouldTag.addChild(qContainersTag);
		
		XMLTag qContainerTag1 = new XMLTag("QContainer");
		qContainerTag1.addAttribute("ID", "qc1");
		XMLTag qContainerTextTag1 = new XMLTag("Text");
		qContainerTextTag1.setContent("qc1-text");
		qContainerTag1.addChild(qContainerTextTag1);
		XMLTag qContainerCostTag1 = new XMLTag("Costs");
		qContainerTag1.addChild(qContainerCostTag1);
		qContainersTag.addChild(qContainerTag1);
		
		
		XMLTag qContainerTag2 = new XMLTag("QContainer");
		qContainerTag2.addAttribute("ID", "qc2");
		XMLTag qContainerTextTag2 = new XMLTag("Text");
		qContainerTextTag2.setContent("qc2-text");
		qContainerTag2.addChild(qContainerTextTag2);
		XMLTag qContainerCostTag2 = new XMLTag("Costs");
		qContainerTag2.addChild(qContainerCostTag2);
		qContainersTag.addChild(qContainerTag2);
	}
	
	private void addPriorityGroups(){
		
		PriorityGroup pg1 = new PriorityGroup();
		pg1.setId("id1");
		pg1.setText("text1");
		pg1.setKnowledgeBase(kb);
		kb.add(pg1);
		
		
		PriorityGroup pg2 = new PriorityGroup();
		pg2.setId("id2");
		pg2.setText("text2");
		pg2.setKnowledgeBase(kb);
		
		
		XMLTag priorityGroupsTag = new XMLTag("PriorityGroups");
		shouldTag.addChild(priorityGroupsTag);
		
		XMLTag priorityGroup1Tag = new XMLTag("PriorityGroup");
		priorityGroup1Tag.addAttribute("ID", "id1");
		
		XMLTag priorityGroup1TextTag = new XMLTag("Text");
		priorityGroup1TextTag.setContent("text1");
		priorityGroup1Tag.addChild(priorityGroup1TextTag);
		
		priorityGroupsTag.addChild(priorityGroup1Tag);
		
		
		XMLTag priorityGroup2Tag = new XMLTag("PriorityGroup");
		priorityGroup2Tag.addAttribute("ID", "id2");
		
		XMLTag priorityGroup2TextTag = new XMLTag("Text");
		priorityGroup2TextTag.setContent("text2");
		priorityGroup2Tag.addChild(priorityGroup2TextTag);
		
		priorityGroupsTag.addChild(priorityGroup2Tag);
	}
	
	private void addQuestions(){		
		XMLTag questionsTag = new XMLTag("Questions");
		shouldTag.addChild(questionsTag);
		
		XMLTag questionTag1 = new XMLTag("Question");
		questionTag1.addAttribute("ID", "q1");
		questionTag1.addAttribute("type", "Num");
		XMLTag questionTextTag1 = new XMLTag("Text");
		questionTextTag1.setContent("q1-text");
		questionTag1.addChild(questionTextTag1);
		XMLTag questionCostTag1 = new XMLTag("Costs");
		questionTag1.addChild(questionCostTag1);
		questionsTag.addChild(questionTag1);
		
		XMLTag questionTag2 = new XMLTag("Question");
		questionTag2.addAttribute("ID", "q2");
		questionTag2.addAttribute("type", "OC");
		XMLTag questionTextTag2 = new XMLTag("Text");
		questionTextTag2.setContent("q2-text");
		questionTag2.addChild(questionTextTag2);
		XMLTag questionCosTag2 = new XMLTag("Costs");
		questionTag2.addChild(questionCosTag2);
		XMLTag questionAnswersTag2 = new XMLTag("Answers");
		questionTag2.addChild(questionAnswersTag2);
		questionsTag.addChild(questionTag2);
	}
	
	public void addDiagnoses(){
		kb.add(diag1);
		
		Diagnosis diag2 = new Diagnosis();
		diag2.setId("d2");
		diag2.setText("d2-text");
		kb.add(diag2);	
		
		XMLTag diagnosesTag = new XMLTag("Diagnoses");
		shouldTag.addChild(diagnosesTag);
		
		XMLTag diagnosisTag1 = new XMLTag("Diagnosis");
		diagnosisTag1.addAttribute("ID", "d1");
		XMLTag diagnosisTagTextTag1 = new XMLTag("Text");
		diagnosisTagTextTag1.setContent("d1-text");
		diagnosisTag1.addChild(diagnosisTagTextTag1);
		diagnosesTag.addChild(diagnosisTag1);
		
		XMLTag diagnosisTag2 = new XMLTag("Diagnosis");
		diagnosisTag2.addAttribute("ID", "d2");
		XMLTag diagnosisTagTextTag2 = new XMLTag("Text");
		diagnosisTagTextTag2.setContent("d2-text");
		diagnosisTag2.addChild(diagnosisTagTextTag2);
		diagnosesTag.addChild(diagnosisTag2);
	}
	
	public void addInitQuestions(){
		LinkedList initList = new LinkedList();
		initList.add(q1);
		initList.add(q2);
		
		kb.setInitQuestions(initList);
		
		XMLTag initQuestionsTag = new XMLTag("InitQuestions");
		shouldTag.addChild(initQuestionsTag);
		
		XMLTag initQuestionTag1 = new XMLTag("Question");
		initQuestionTag1.addAttribute("ID", "q1");
		initQuestionsTag.addChild(initQuestionTag1);
		
		XMLTag initQuestionTag2 = new XMLTag("Question");
		initQuestionTag2.addAttribute("ID", "q2");	
		initQuestionsTag.addChild(initQuestionTag2);
	}
	
	public void addKnowledgeSlices(){
		RuleComplex rcomp1 = new RuleComplex();
		rcomp1.setId("rc-id1");
		
		ActionHeuristicPS ah = new ActionHeuristicPS(rcomp1);
		ah.setDiagnosis(diag1);
		ah.setScore(Score.P1);
		
		CondDState cDState1 = new CondDState(diag1, DiagnosisState.SUGGESTED, null);
		
		rcomp1.setAction(ah);
		rcomp1.setCondition(cDState1);
		
		kb.addKnowledge(PSMethodHeuristic.class, rcomp1, MethodKind.FORWARD);
		
		
		RuleComplex rcomp2 = new RuleComplex();
		rcomp2.setId("rc-id2");
		kb.addKnowledge(PSMethodHeuristic.class, rcomp2, MethodKind.FORWARD);
			
		rcomp2.setAction(ah);
		rcomp2.setException(cDState1);
		kb.addKnowledge(PSMethodHeuristic.class, rcomp2, MethodKind.FORWARD);
		
		
		XMLTag knowledgeSlicesTag = new XMLTag("KnowledgeSlices");
		shouldTag.addChild(knowledgeSlicesTag);
		
		XMLTag ruleComplexTag1 = new XMLTag("KnowledgeSlice");
		ruleComplexTag1.addAttribute("ID", "rc-id1");
		ruleComplexTag1.addAttribute("type", "RuleComplex");
		knowledgeSlicesTag.addChild(ruleComplexTag1);
		
		XMLTag actionTag1 = new XMLTag("Action");
		actionTag1.addAttribute("type", "ActionHeuristicPS");
		ruleComplexTag1.addChild(actionTag1);
		
			XMLTag scoreTag1 = new XMLTag("Score");
			scoreTag1.addAttribute("value", "P1");
			actionTag1.addChild(scoreTag1);
			
			XMLTag diagnosisTag1 = new XMLTag("Diagnosis");
			diagnosisTag1.addAttribute("ID", "d1");
			actionTag1.addChild(diagnosisTag1);
			
		/*
		XMLTag conditionsTag1 = new XMLTag("Condition");
		ruleComplexTag1.addChild(conditionsTag1);
		
			XMLTag conditionTag1 = new XMLTag("Condition");
			conditionTag1.addAttribute("ID", "d1");
			conditionTag1.addAttribute("type", "dstate");
			conditionTag1.addAttribute("value", "suggested");
			conditionsTag1.addChild(conditionTag1);
			*/
		
		XMLTag conditionTag1 = new XMLTag("Condition");
		conditionTag1.addAttribute("ID", "d1");
		conditionTag1.addAttribute("type", "dstate");
		conditionTag1.addAttribute("value", "suggested");
		ruleComplexTag1.addChild(conditionTag1);
		
		/*better*/
			
		XMLTag ruleComplexTag2 = new XMLTag("KnowledgeSlice");
		ruleComplexTag2.addAttribute("ID", "rc-id2");
		ruleComplexTag2.addAttribute("type", "RuleComplex");
		knowledgeSlicesTag.addChild(ruleComplexTag2);
				
		XMLTag actionTag2 = new XMLTag("Action");
		actionTag2.addAttribute("type", "ActionHeuristicPS");
		ruleComplexTag2.addChild(actionTag2);
		
			XMLTag scoreTag2 = new XMLTag("Score");
			scoreTag2.addAttribute("value", "P1");
			actionTag2.addChild(scoreTag2);
			
			XMLTag diagnosisTag2 = new XMLTag("Diagnosis");
			diagnosisTag2.addAttribute("ID", "d1");
			actionTag2.addChild(diagnosisTag2);
			
		XMLTag conditionsTag2 = new XMLTag("Exception");
		ruleComplexTag2.addChild(conditionsTag2);
		
			XMLTag conditionTag2 = new XMLTag("Condition");
			conditionTag2.addAttribute("ID", "d1");
			conditionTag2.addAttribute("type", "dstate");
			conditionTag2.addAttribute("value", "suggested");
			conditionsTag2.addChild(conditionTag2);
	}
}
