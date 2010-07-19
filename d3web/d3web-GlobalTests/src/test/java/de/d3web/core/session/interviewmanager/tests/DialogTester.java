package de.d3web.core.session.interviewmanager.tests;

import static junit.framework.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
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
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.plugin.test.InitPluginManager;

public class DialogTester {

	KnowledgeBaseManagement kbm;
	QContainer pregnancyQuestions, heightWeightQuestions;
	QuestionOC sex, pregnant, ask_for_pregnancy, initQuestion, pregnancyContainerIndication;
	QuestionNum weight, height;
	ChoiceValue female, dont_ask;
	private Session session;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kbm = KnowledgeBaseManagement.createInstance();

		QASet root = kbm.getKnowledgeBase().getRootQASet();
		pregnancyQuestions = kbm.createQContainer("pregnancyQuestions", root);
		sex = kbm.createQuestionOC("sex", pregnancyQuestions, new String[] {
				"male", "female" });
		pregnant = kbm.createQuestionOC("pregnant", sex, new String[] {
				"yes", "no" });
		female = new ChoiceValue(kbm.findChoice(sex, "female"));

		ask_for_pregnancy = kbm.createQuestionOC("ask for pregnancy", pregnancyQuestions,
				new String[] {
						"yes", "no" });
		dont_ask = new ChoiceValue(kbm.findChoice(ask_for_pregnancy, "no"));

		heightWeightQuestions = kbm.createQContainer("heightWeightQuestions", root);
		weight = kbm.createQuestionNum("weight", "weight", heightWeightQuestions);
		height = kbm.createQuestionNum("height", "height", heightWeightQuestions);

		initQuestion = kbm.createQuestionOC("initQuestion", root, new String[] {
				"all", "pregnacyQuestions", "height+weight" });

		pregnancyContainerIndication = kbm.createQuestionOC("pregnancyContainerIndication", root,
				new String[] {
						"yes", "no" });

		// Rule: sex = female => INDICATE ( pregnant )
		RuleFactory.createIndicationRule("r1", pregnant, new CondEqual(sex, female));
		// Rule: ask for pregnancy = no => CONTRA_INDICATE ( pregnant )
		RuleFactory.createContraIndicationRule("r2", pregnant, new CondEqual(ask_for_pregnancy,
				dont_ask));

		// Rule: initQuestion = pregnacyQuestions => INDICATE CONTAINER (
		// pregnancyQuestions )
		RuleFactory.createIndicationRule("r3", pregnancyQuestions,
				new CondEqual(initQuestion,
				new ChoiceValue(kbm.findChoice(initQuestion, "pregnacyQuestions"))));

		// Rule: initQuestion = height+weight => INDICATE CONTAINER (
		// heightWeightQuestions )
		RuleFactory.createIndicationRule("r4", heightWeightQuestions,
				new CondEqual(initQuestion,
				new ChoiceValue(kbm.findChoice(initQuestion, "height+weight"))));

		// Rule: initQuestion = all => INDICATE CONTAINER ( pregnancyQuestions,
		// heightWeightQuestions )
		RuleFactory.createIndicationRule("r5", Arrays.asList(new QASet[] {
				pregnancyQuestions, heightWeightQuestions }),
				new CondEqual(initQuestion, new ChoiceValue(kbm.findChoice(initQuestion, "all"))));

		// Rule: pregnancyContainerIndication = yes => INDICATE CONTAINER (
		// pregnancyQuestions )
		RuleFactory.createIndicationRule("r6", pregnancyQuestions,
				new CondEqual(pregnancyContainerIndication,
				new ChoiceValue(kbm.findChoice(pregnancyContainerIndication, "yes"))));

		session = SessionFactory.createSession(kbm.getKnowledgeBase());
	}

	@Test
	public void testIndication() {
		// SET: sex = female
		// EXPECT: question "pregnant" is INDICATED
		setValue(sex, female);
		assertEquals(
				new Indication(State.INDICATED),
				session.getBlackboard().getIndication(pregnant));

		// SET: sex = undefined
		// EXPCECT: question "pregnant" is NEURTRAL
		setValue(sex, UndefinedValue.getInstance());
		assertEquals(
				new Indication(State.NEUTRAL),
				session.getBlackboard().getIndication(pregnant));
	}

	@Test
	public void testContraIndication() {
		// SET: sex = female
		// EXPECT: question "pregnant" is INDICATED
		setValue(sex, female);
		assertEquals(
				new Indication(State.INDICATED),
				session.getBlackboard().getIndication(pregnant));

		// SET: ask_for_pregnancy = no
		// EXPCECT: question "pregnant" is CONTRA_INDICATED
		setValue(ask_for_pregnancy, dont_ask);
		assertEquals(
				new Indication(State.CONTRA_INDICATED),
				session.getBlackboard().getIndication(pregnant));

		// SET: ask_for_pregnancy = UNDEFINED
		// EXPCECT: question "pregnant" is INDICATED again
		setValue(ask_for_pregnancy, UndefinedValue.getInstance());
		assertEquals(
				new Indication(State.INDICATED),
				session.getBlackboard().getIndication(pregnant));
	}

	@Test
	public void testQContainerIndication() {
		// SET: initQuestion = all
		// EXPECT: INDICATE CONTAINER ( pregnancyQuestions,
		// heightWeightQuestions )
		setValue(initQuestion, new ChoiceValue(kbm.findChoice(initQuestion, "all")));
		assertEquals(
				new Indication(State.INDICATED),
				session.getBlackboard().getIndication(pregnancyQuestions));
		assertEquals(
				new Indication(State.INDICATED),
				session.getBlackboard().getIndication(heightWeightQuestions));

		// SET: initQuestion = Undefined (i.e., retract the previous action)
		// EXPECT: NEUTRAL CONTAINER ( pregnancyQuestions, heightWeightQuestions
		// )
		setValue(initQuestion, UndefinedValue.getInstance());
		assertEquals(
				new Indication(State.NEUTRAL),
				session.getBlackboard().getIndication(pregnancyQuestions));
		assertEquals(
				new Indication(State.NEUTRAL),
				session.getBlackboard().getIndication(heightWeightQuestions));
	}

	@Test
	public void testQContainerIndicationByMutlipleRules() {
		// SET: initQuestion = all
		// EXPECT: INDICATE CONTAINER ( pregnancyQuestions,
		// heightWeightQuestions )
		setValue(initQuestion, new ChoiceValue(kbm.findChoice(initQuestion, "all")));
		assertEquals(
				new Indication(State.INDICATED),
				session.getBlackboard().getIndication(pregnancyQuestions));
		assertEquals(
				new Indication(State.INDICATED),
				session.getBlackboard().getIndication(heightWeightQuestions));

		// SET: pregnancyContainerIndication = yes
		// EXPECT: INDICATE CONTAINER ( pregnancyQuestions ) // i.e. doubled
		// indication of pregnancyQuestions
		setValue(pregnancyContainerIndication, new ChoiceValue(kbm.findChoice(
				pregnancyContainerIndication, "yes")));
		assertEquals(
				new Indication(State.INDICATED),
				session.getBlackboard().getIndication(pregnancyQuestions));

		// SET: initQuestion = Undefined (i.e., retract the previous action)
		// EXPECT: NEUTRAL CONTAINER ( heightWeightQuestions )
		// INDICATE CONTAINER ( pregnancyQuestions ) // due to doubled
		// indication
		setValue(initQuestion, UndefinedValue.getInstance());
		assertEquals(
				new Indication(State.INDICATED),
				session.getBlackboard().getIndication(pregnancyQuestions));
		assertEquals(
				new Indication(State.NEUTRAL),
				session.getBlackboard().getIndication(heightWeightQuestions));
	}

	private void setValue(Question question, Value value) {
		session.getBlackboard().addValueFact(
				FactFactory.createFact(question, value,
				PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));
	}
}
