package de.d3web.core.session.interviewmanager.tests;


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.interviewmanager.EmptyForm;
import de.d3web.core.session.interviewmanager.InterviewAgenda;
import de.d3web.core.session.interviewmanager.NextUnansweredQuestionFormStrategy;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.costbenefit.inference.PSMethodCostBenefit;
import de.d3web.plugin.test.InitPluginManager;

public class CostBenefitAgendaSortingTest {
	KnowledgeBaseManagement kbm;
	Session session;
	InterviewAgenda agenda;

	QContainer pregnancyQuestions, heightWeightQuestions;
	QuestionOC sex, pregnant;
	QuestionText name;
	QuestionNum weight, height;
	ChoiceValue female, male, dont_ask;
	PSMethodCostBenefit costBenefit;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kbm = KnowledgeBaseManagement.createInstance();
		
		QASet root = kbm.getKnowledgeBase().getRootQASet();

		// root {container}
		// - pregnancyQuestions {container}
		// - sex  [oc]
		// - name [text]
		//
		// - heightWeightQuestions {container}
		// - weight [num]
		// - height [num]

		pregnancyQuestions = kbm.createQContainer("pregnancyQuestions", root);
		sex = kbm.createQuestionOC("sex", pregnancyQuestions, new String[] {
				"male", "female" });
		female = new ChoiceValue(kbm.findChoice(sex, "female"));
		male = new ChoiceValue(kbm.findChoice(sex, "male"));
		name = kbm.createQuestionText("name", pregnancyQuestions);
		
		// Container: heightWeightQuestions = { weight, height } 
		heightWeightQuestions = kbm.createQContainer("heightWeightQuestions", root);
		weight = kbm.createQuestionNum("weight", "weight", heightWeightQuestions);
		height = kbm.createQuestionNum("height", "height", heightWeightQuestions);

		session = SessionFactory.createSession(kbm.getKnowledgeBase());
		costBenefit = new PSMethodCostBenefit();
		costBenefit.init(session);
		
		session.getInterview().setFormStrategy(new NextUnansweredQuestionFormStrategy());
		agenda = session.getInterview().getInterviewAgenda();
	}
	
	@Test
	public void simpleIndicationTest() {
		// initially the agenda is empty
		assertTrue(agenda.isEmpty());

		// PUT the containers onto the agenda by indication, order must not change
		Fact factHeig = FactFactory.createFact(heightWeightQuestions, new Indication(State.INDICATED),
				costBenefit, costBenefit);
		session.getBlackboard().addInterviewFact(factHeig);
		Fact factPreg = FactFactory.createFact(pregnancyQuestions, new Indication(State.INDICATED),
				costBenefit, costBenefit);
		session.getBlackboard().addInterviewFact(factPreg);
		assertFalse(agenda.isEmpty());

		
		// EXPECT: weight is the next question
		assertEquals(weight, session.getInterview().nextForm().getInterviewObject());
		// SET:    weight = 80
		session.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(weight, new NumValue(80)));		
		
		// EXPECT: height is the next question
		assertEquals(height, session.getInterview().nextForm().getInterviewObject());
		// SET:    height = 180
		session.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(height, new NumValue(180)));

		// EXPECT: all question have been answered, so the QContainer heightWeightQuestion should be removed
		assertFalse(session.getInterview().getInterviewAgenda().onAgenda(heightWeightQuestions));
		// EXPECT: QContainer pregnancyQuestions is still on agenda
		assertTrue(session.getInterview().getInterviewAgenda().onAgenda(pregnancyQuestions));
		
		// EXPECT: sex is the next question
		assertEquals(sex, session.getInterview().nextForm().getInterviewObject());
		// SET:    sex = male
		session.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(sex, male));

		// EXPECT: name is the next question
		assertEquals(name, session.getInterview().nextForm().getInterviewObject());
		// SET:    name = "joba"
		session.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(name, new TextValue("joba")));

		// EXPECT: all question have been answered, so the QContainer pregnancyQuestions should be removed
		assertFalse(session.getInterview().getInterviewAgenda().onAgenda(pregnancyQuestions));
		
		// EXPECT: the agenda is empty now
		assertEquals(session.getInterview().nextForm(), EmptyForm.getInstance());
	}
	
}
