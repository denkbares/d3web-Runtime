package de.d3web.core.session.blackboard.tests;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.interviewmanager.EmptyForm;
import de.d3web.core.session.interviewmanager.Form;
import de.d3web.core.session.interviewmanager.InterviewAgenda;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.plugin.test.InitPluginManager;

public class OneQuestionFormTest {

	KnowledgeBaseManagement kbm;
	Session session;
	InterviewAgenda agenda;

	QContainer pregnancyQuestions, heightWeightQuestions;
	QuestionOC sex, pregnant, ask_for_pregnancy, initQuestion;
	QuestionNum weight, height;
	ChoiceValue female, dont_ask;

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
		agenda = session.getInterviewManager().getInterviewAgenda();
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
		Form form = session.getInterviewManager().nextForm();
		InterviewObject formQuestion = form.getInterviewObjects().get(0);
		assertEquals(sex, formQuestion);

		// ANSWER: sex=female
		// EXPECT: pregnant to be the next question
		setValue(sex, female);
		form = session.getInterviewManager().nextForm();
		formQuestion = form.getInterviewObjects().get(0);
		assertEquals(pregnant, formQuestion);

		// ANSWER: pregnant=no
		// EXPECT: no more questions to ask
		setValue(pregnant, new ChoiceValue(kbm.findChoice(pregnant, "no")));
		form = session.getInterviewManager().nextForm();
		assertEquals(EmptyForm.getInstance(), form);
	}

	private void setValue(Question question, Value value) {
		session.getBlackboard().addValueFact(
				FactFactory.createFact(question, value,
				PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));
	}
}
