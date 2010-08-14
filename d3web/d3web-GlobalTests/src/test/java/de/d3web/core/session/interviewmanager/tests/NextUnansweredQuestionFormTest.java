package de.d3web.core.session.interviewmanager.tests;

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
import de.d3web.core.session.interviewmanager.EmptyForm;
import de.d3web.core.session.interviewmanager.Form;
import de.d3web.core.session.interviewmanager.InterviewAgenda;
import de.d3web.core.session.interviewmanager.NextUnansweredQuestionFormStrategy;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.plugin.test.InitPluginManager;

public class NextUnansweredQuestionFormTest {

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
		session.getInterview().setFormStrategy(new NextUnansweredQuestionFormStrategy());
		agenda = session.getInterview().getInterviewAgenda();
	}

	@Test
	public void testWithQuestionsOnAgenda() {
		// initially the agenda is empty
		assertTrue(agenda.isEmpty());

		// PUT the questions 'sex' and 'pregnant' onto the agenda
		agenda.append(sex);
		agenda.append(pregnant);
		assertFalse(agenda.isEmpty());

		// EXPECT: 'sex' to be the first question
		InterviewObject formQuestions = session.getInterview().nextForm().getInterviewObject();
		assertEquals(sex, formQuestions);

		// ANSWER: sex=female
		// EXPECT: pregnant to be the next question
		setValue(sex, female);
		formQuestions = session.getInterview().nextForm().getInterviewObject();
		assertEquals(pregnant, formQuestions);

		// ANSWER: pregnant=no
		// EXPECT: no more questions to ask
		setValue(pregnant, new ChoiceValue(kbm.findChoice(pregnant, "no")));
		Form form = session.getInterview().nextForm();
		assertEquals(EmptyForm.getInstance(), form);
	}

	@Test
	public void testWithOneQContainerOnAgenda_WithoutFollowUpQuestions() {
		// initially the agenda is empty
		assertTrue(agenda.isEmpty());
		// Put the QContainer pregnancyQuestions on the agenda
		agenda.append(pregnancyQuestions);
		assertFalse(agenda.isEmpty());

		// EXPECT the first question 'sex' to be the next question in the form
		InterviewObject nextQuestion = session.getInterview().nextForm().getInterviewObject();
		assertEquals(sex, nextQuestion);

		// SET question sex=male
		// EXPECT the second question 'ask_for_pregnancy' to be the next
		// question in the form
		setValue(sex, male);
		nextQuestion = session.getInterview().nextForm().getInterviewObject();
		assertEquals(ask_for_pregnancy, nextQuestion);

		// SET : question ask_for_pregnancy=no
		// EXPECT: since all questions of the qcontainer are answered, we expect
		// no more
		// questions to be asked next, i.e., the EmptyForm singleton is returned
		setValue(ask_for_pregnancy, kbm.findValue(ask_for_pregnancy, "no"));
		assertEquals(EmptyForm.getInstance(), session.getInterview().nextForm());
	}

	@Test
	public void testWithOneQContainerOnAgenda_WithFollowUpQuestions() {
		// We need this rule for the later indication of the follow-up question
		// "pregnant"
		// Rule: sex = female => INDICATE ( pregnant )
		RuleFactory.createIndicationRule("r1", pregnant, new CondEqual(sex, female));

		// initially the agenda is empty
		assertTrue(agenda.isEmpty());
		// Put the QContainer pregnancyQuestions on the agenda
		agenda.append(pregnancyQuestions);
		assertFalse(agenda.isEmpty());

		// EXPECT the first question 'sex' to be the next question in the form
		InterviewObject nextQuestion = session.getInterview().nextForm().getInterviewObject();
		assertEquals(sex, nextQuestion);

		// SET question sex=female
		// EXPECT the follow-up question 'pregnant' to be the next question in
		// the form
		setValue(sex, female);
		nextQuestion = session.getInterview().nextForm().getInterviewObject();

		// TODO: overwork FormStrategy to copy with follow-up questions
		assertEquals(pregnant, nextQuestion);
	}

	private void setValue(Question question, Value value) {
		session.getBlackboard().addValueFact(
				FactFactory.createFact(question, value,
						PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));
	}
}
