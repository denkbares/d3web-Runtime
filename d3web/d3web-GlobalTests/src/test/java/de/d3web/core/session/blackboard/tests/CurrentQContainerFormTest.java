package de.d3web.core.session.blackboard.tests;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.interviewmanager.CurrentQContainerFormStrategy;
import de.d3web.core.session.interviewmanager.EmptyForm;
import de.d3web.core.session.interviewmanager.Form;
import de.d3web.core.session.interviewmanager.InterviewAgenda;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.plugin.test.InitPluginManager;


public class CurrentQContainerFormTest {
	KnowledgeBaseManagement kbm;
	Session session;
	InterviewAgenda agenda;

	QContainer pregnancyQuestions, heightWeightQuestions;
	QuestionOC sex, pregnant, ask_for_pregnancy, initQuestion;
	QuestionNum weight, height;
	ChoiceValue female, male, dont_ask;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kbm = KnowledgeBaseManagement.createInstance();

		QASet root = kbm.getKnowledgeBase().getRootQASet();

		// root {container}
		// - pregnancyQuestions {container}
		// - sex [oc]
		// -- pregnant [oc]
		// - ask_for_pregnancy [oc]
		//
		// - heightWeightQuestions {container}
		// - weight [num]
		// - height [num]

		// Container: pregnancyQuestions = { sex {pregnant}, ask_for_pregnancy
		// } 
		pregnancyQuestions = kbm.createQContainer("pregnancyQuestions", root);
		sex = kbm.createQuestionOC("sex", pregnancyQuestions, new String[] {
				"male", "female" });
		female = new ChoiceValue(kbm.findChoice(sex, "female"));
		male = new ChoiceValue(kbm.findChoice(sex, "male"));
		pregnant = kbm.createQuestionOC("pregnant", sex, new String[] {
				"yes", "no" });
		ask_for_pregnancy = kbm.createQuestionOC("ask for pregnancy", pregnancyQuestions,
				new String[] {
				"yes", "no" });

		// Container: heightWeightQuestions = { weight, height } 
		heightWeightQuestions = kbm.createQContainer("heightWeightQuestions", root);
		weight = kbm.createQuestionNum("weight", "weight", heightWeightQuestions);
		height = kbm.createQuestionNum("height", "height", heightWeightQuestions);

		initQuestion = kbm.createQuestionOC("initQuestion", root,
				new String[] {
				"all", "pregnacyQuestions", "height+weight" });
		session = SessionFactory.createSession(kbm.getKnowledgeBase());
		session.getInterviewManager().setFormStrategy(new CurrentQContainerFormStrategy());
		agenda = session.getInterviewManager().getInterviewAgenda();
	}
	
	@Test
	public void testWithTwoQContainers() {
		// initially the agenda is empty
		assertTrue(agenda.isEmpty());

		// PUT the containers 'pregnancyQuestions' and 'heightWeightQuestions' onto the agenda
		agenda.append(pregnancyQuestions);
		agenda.append(heightWeightQuestions);
		assertFalse(agenda.isEmpty());

		// EXPECT: 'pregnancyQuestions' to be the first interview object
		InterviewObject formObject = session.getInterviewManager().nextForm().getInterviewObject();
		assertEquals(pregnancyQuestions, formObject);
		
		// SET   : first question of pregnancyQuestions (no follow-up question indicated)
		// EXPECT: pregnancyQuestions should be still active 
		setValue(sex, male);
		formObject = session.getInterviewManager().nextForm().getInterviewObject();
		assertEquals(pregnancyQuestions, formObject);

		// SET   : second question of pregnancyQuestions
		// EXPECT: now 'heightWeightQuestions' should be active 
		setValue(ask_for_pregnancy, new ChoiceValue(kbm.findChoice(ask_for_pregnancy, "no")));
		formObject = session.getInterviewManager().nextForm().getInterviewObject();
		assertEquals(heightWeightQuestions, formObject);
		
		// SET   : first question of 'heightWeightQuestions' 
		// EXPECT: now 'heightWeightQuestions' should be still active 
		setValue(height, new NumValue(100));
		formObject = session.getInterviewManager().nextForm().getInterviewObject();
		assertEquals(heightWeightQuestions, formObject);

		// SET   : second question of 'heightWeightQuestions' 
		// EXPECT: now we expect an EMPTY_FORM since the agenda should be empty now 
		setValue(weight, new NumValue(100));
		assertEquals(EmptyForm.getInstance(), session.getInterviewManager().nextForm());
	}

	@Test
	public void testContainersWithFollowUpQuestion() {
		// We need this rule for the later indication of the 
		// follow-up question 'pregnant'
		// Rule: sex = female => INDICATE ( pregnant )
		RuleFactory.createIndicationRule("r1", pregnant, new CondEqual(sex, female));

		
		// initially the agenda is empty
		assertTrue(agenda.isEmpty());

		// PUT the container 'pregnancyQuestions' onto the agenda
		agenda.append(pregnancyQuestions);
		agenda.append(heightWeightQuestions);
		assertFalse(agenda.isEmpty());

		// EXPECT: 'pregnancyQuestions' to be the first interview object
		InterviewObject formObject = session.getInterviewManager().nextForm().getInterviewObject();
		assertEquals(pregnancyQuestions, formObject);
		
		// SET   : ask_for_pregnancy = no
		//         sex=female => follow-up question is indicated
		// EXPECT: pregnancyQuestions should be still active, because of follow-up-questions 
		setValue(ask_for_pregnancy,new ChoiceValue(kbm.findChoice(ask_for_pregnancy, "no")));
		setValue(sex, female);
		Form form = session.getInterviewManager().nextForm();
		assertEquals(pregnancyQuestions, form.getInterviewObject());
		
		// SET   : answer follow-up question 'pregnant=no'
		// EXPECT: no the next qcontainer 'heightWeightQuestions' should be active, 
		//         since all questions (including follow-ups) have been answered
		setValue(pregnant, new ChoiceValue(kbm.findChoice(pregnant, "no")));
		assertEquals(heightWeightQuestions, session.getInterviewManager().nextForm().getInterviewObject());
		
		
		// SET   : answer the questions 'height' and 'weight'
		// EXPECT: all questions on the agenda are answered, so next form should be empty
		setValue(height, new NumValue(100));
		setValue(weight, new NumValue(100));
		assertEquals(EmptyForm.getInstance(), session.getInterviewManager().nextForm());
	}
	
	
	private void setValue(Question question, Value value) {
		session.getBlackboard().addValueFact(
				FactFactory.createFact(question, value,
				PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));
	}
}




















