package de.d3web.core.test;


import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondNumEqual;
import de.d3web.core.manage.AnswerFactory;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.CaseFactory;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.terminology.Answer;
import de.d3web.core.terminology.Diagnosis;
import de.d3web.core.terminology.DiagnosisState;
import de.d3web.core.terminology.QContainer;
import de.d3web.core.terminology.QuestionChoice;
import de.d3web.core.terminology.QuestionNum;
import de.d3web.scoring.Score;

public class BasicSessionRunTest {

	private KnowledgeBaseManagement mgt = KnowledgeBaseManagement.createInstance();
	private QuestionChoice questionChoice, questionMChoice;
	private QuestionNum    questionNum;
	private Diagnosis      solution1, solution2, solution3;
	private QContainer     qcontainer;
	private AnswerChoice   value1, mcvalue1, mcvalue2; 
	
	@Before
	public void setUp() throws Exception {
		solution1 = mgt.createDiagnosis("Solution1");
		solution2 = mgt.createDiagnosis("Solution2");
		solution3 = mgt.createDiagnosis("Solution3");
		qcontainer = mgt.createQContainer("Questionnaire");
		questionChoice = mgt.createQuestionOC("Question1", qcontainer, new String[] {"value1", "value2"});
		value1 = questionChoice.getAllAlternatives().get(0);
		questionNum = mgt.createQuestionNum("QuestionNum", qcontainer);

		questionMChoice = mgt.createQuestionMC("Question2", 
				 qcontainer, new String[] {"mcvalue1", "mcvalue2", "mcvalue3"});
		mcvalue1 = questionMChoice.getAllAlternatives().get(0);
		mcvalue2 = questionMChoice.getAllAlternatives().get(1);
		
		// questionChoice = value1 -> solution1 = P7
		RuleFactory.createHeuristicPSRule(mgt.createRuleID(), 
				solution1, Score.P7, 
				new CondEqual(questionChoice, value1));
		
		// questionNum = 1 -> solution2 = N7
		RuleFactory.createHeuristicPSRule(mgt.createRuleID(), 
				solution2, Score.N7, 
				new CondNumEqual(questionNum, new Double(1)));
		
		// questionMChoice = "mcvalue1, mcvalue2" -> solution3 = P7
		RuleFactory.createHeuristicPSRule(mgt.createRuleID(), 
				solution3, Score.P7, 
				new CondEqual(questionMChoice, 
						Arrays.asList(new Answer[] {mcvalue1, mcvalue2})));
	}

	@Test
	public void simpleSolutionDerivation() {
		XPSCase theCase = CaseFactory.createXPSCase(mgt.getKnowledgeBase());
		
		// test for oc question: should establish solution1
		assertEquals(DiagnosisState.UNCLEAR, solution1.getState(theCase));
		
		//theCase.setValue(questionChoice, value1);
		assertEquals(DiagnosisState.ESTABLISHED, solution1.getState(theCase));
		
		// test for num question: should exclude solution1
		Answer num1 = AnswerFactory.createAnswerNum(2);
		//theCase.setValue(questionNum, num1);
		assertEquals(DiagnosisState.UNCLEAR, solution2.getState(theCase));

		Answer num2 = AnswerFactory.createAnswerNum(1);
		//theCase.setValue(questionNum, num2);
		assertEquals(DiagnosisState.EXCLUDED, solution2.getState(theCase));
		
		// test for mc question:should derive solution3
		assertEquals(DiagnosisState.UNCLEAR, solution3.getState(theCase));
		
		//MultipleChoiceValue mcValue = new MultipleChoiceValue(Arrays.asList(new AnswerChoice[]{mcvalue1,mcvalue2}));
		//theCase.setValue(questionMChoice, mcValue);
		assertEquals(DiagnosisState.ESTABLISHED, solution3.getState(theCase));
	
	}
}
