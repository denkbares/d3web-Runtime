package de.d3web.interview;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.indication.ActionNextQASet;
import de.d3web.indication.inference.PSMethodStrategic;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.interview.indication.ActionRepeatedIndication;
import de.d3web.interview.inference.PSMethodInterview;
import de.d3web.plugin.test.InitPluginManager;

import static de.d3web.core.knowledge.Indication.State.*;
import static de.d3web.core.manage.RuleFactory.setRuleParams;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests the different indication states
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 17.06.16
 */
public class IndicationTest {

	protected KnowledgeBase kb;
	protected QuestionMC indicationChooser;
	protected QContainer indicationContainer;
	protected QuestionOC indicationQuestion1, indicationQuestion2,
			relevantIndicationQuestion, relevantIndicationChildQuestion, repeatedIndicationQuestion,
			instantIndicationQuestion, multipleIndicationQuestion1, multipleIndicationQuestion2, multipleIndicationQuestion3;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		QContainer init = new QContainer(kb.getRootQASet(), "init");
		indicationContainer = new QContainer(init, "indicationContainer");
		indicationQuestion1 = new QuestionOC(indicationContainer, "IndicationQuestion1");
		indicationQuestion2 = new QuestionOC(indicationContainer, "IndicationQuestion2");
		repeatedIndicationQuestion = new QuestionOC(init, "RepeatedIndicationQuestion");
		instantIndicationQuestion = new QuestionOC(init, "InstantIndicationQuestion");
		relevantIndicationQuestion = new QuestionOC(init, "RelevantIndicationQuestion");
		multipleIndicationQuestion1 = new QuestionOC(init, "multipleIndicationQuestion1");
		multipleIndicationQuestion2 = new QuestionOC(init, "multipleIndicationQuestion2");
		multipleIndicationQuestion3 = new QuestionOC(init, "multipleIndicationQuestion3");
		relevantIndicationChildQuestion = new QuestionOC(relevantIndicationQuestion, "RelevantIndicationChildQuestion");
		indicationChooser = new QuestionMC(init, "IndicationChooser",
				indicationQuestion1.getName(), indicationQuestion2.getName(), relevantIndicationQuestion.getName(),
				instantIndicationQuestion.getName(), repeatedIndicationQuestion.getName(), indicationContainer.getName());

		RuleFactory.createIndicationRule(this.indicationQuestion1, new CondEqual(indicationChooser, new ChoiceValue(indicationQuestion1
				.getName())));
		RuleFactory.createIndicationRule(this.indicationQuestion2, new CondEqual(indicationChooser, new ChoiceValue(indicationQuestion2
				.getName())));
		RuleFactory.createIndicationRule(indicationContainer, new CondEqual(indicationChooser, new ChoiceValue(indicationContainer
				.getName())));
		RuleFactory.createInstantIndicationRule(instantIndicationQuestion, new CondEqual(indicationChooser, new ChoiceValue(instantIndicationQuestion
				.getName())));
		createRepeatedIndicationRule(new CondEqual(indicationChooser, new ChoiceValue(repeatedIndicationQuestion.getName())), repeatedIndicationQuestion);
		RuleFactory.createRelevantIndicationRule(this.relevantIndicationQuestion, new CondEqual(indicationChooser, new ChoiceValue(relevantIndicationQuestion
				.getName())));
	}

	private Rule createRepeatedIndicationRule(Condition condition, QASet... qaSetsToIndicate) {
		Rule rule = new Rule(PSMethodStrategic.class);
		ActionNextQASet ruleAction = new ActionRepeatedIndication(qaSetsToIndicate);
		setRuleParams(rule, ruleAction, condition, null);
		return rule;
	}

	@Test
	public void testIndicationSorting() {
		Session session = SessionFactory.createSession(kb);
		indicate(session, indicationQuestion1, INDICATED, 2);
		indicate(session, indicationQuestion2, INDICATED, 1);

		assertActiveQuestions(session, indicationQuestion2);
		answer(session, indicationQuestion2, Unknown.getInstance());
		assertActiveQuestions(session, indicationQuestion1);
	}

	@Test
	public void testNormalIndication() {
		testIndicationRetraction(indicationQuestion1);

		Session session = SessionFactory.createSession(kb);

		indicate(session, indicationQuestion1, INDICATED);
		assertActiveQuestions(session, indicationQuestion1);
		answer(session, indicationQuestion1, Unknown.getInstance());
		assertActiveQuestions(session);
		indicate(session, indicationQuestion1, INDICATED);
		// because the question is already answered, the normal indication will not activate it again
		assertActiveQuestions(session);

		session = SessionFactory.createSession(kb);
		indicate(session, indicationContainer, INDICATED);

		assertActiveQuestions(session, indicationQuestion1);
		answer(session, indicationQuestion1, Unknown.getInstance());
		assertActiveQuestions(session, indicationQuestion2);
	}

	@Test
	public void testRelevantIndication() {

		Session session = SessionFactory.createSession(kb);

		Fact indicationFact = indicate(session, relevantIndicationChildQuestion, RELEVANT);
		// without its parent being indicated, relevantChild should not be active
		assertActiveQuestions(session);
		indicate(session, relevantIndicationQuestion, INDICATED);
		// with the parent question being indicated, both parent and child are now active
		// TODO: Is this correct for NextUnansweredQuestionFormStrategy?? Two questions active?
		assertActiveQuestions(session, relevantIndicationQuestion, relevantIndicationChildQuestion);

		session.getBlackboard().removeInterviewFact(indicationFact);
		assertActiveQuestions(session, relevantIndicationQuestion);

		answer(session, relevantIndicationQuestion, Unknown.getInstance());
		assertActiveQuestions(session);

	}

	@Test
	public void testRepeatedIndication() {
		testIndicationRetraction(repeatedIndicationQuestion);

		Session session = SessionFactory.createSession(kb);

		indicate(session, repeatedIndicationQuestion, Indication.State.REPEATED_INDICATED);
		assertActiveQuestions(session, repeatedIndicationQuestion);
		answer(session, repeatedIndicationQuestion, Unknown.getInstance());
		assertActiveQuestions(session);
		indicate(session, repeatedIndicationQuestion, Indication.State.REPEATED_INDICATED);

		// even if the question is answered already, the repeated indication will activate it again
		assertActiveQuestions(session, repeatedIndicationQuestion);
	}

	private Fact answer(Session session, Question question, Value value) {
		Fact valueFact = FactFactory.createUserEnteredFact(question, value);
		session.getBlackboard().addValueFact(valueFact);
		return valueFact;
	}

	private Fact indicate(Session session, QASet qaSet, Indication.State state) {
		return indicate(session, qaSet, state, PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance());
	}

	private Fact indicate(Session session, QASet qaSet, Indication.State state, Object source, PSMethod psMethod) {
		int sorting = kb.getManager().getTreeIndex(qaSet);
		return indicate(session, qaSet, state, sorting, source, psMethod);
	}

	private @NotNull Fact indicate(Session session, QASet qaSet, Indication.State indicated, int sorting) {
		return indicate(session, qaSet, indicated, sorting, PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance());
	}

	@NotNull
	private Fact indicate(Session session, QASet qaSet, Indication.State state, int sorting, Object source, PSMethod psMethod) {
		Fact indicationFact = FactFactory.createIndicationFact(qaSet, new Indication(state, sorting), source, psMethod);
		session.getBlackboard().addInterviewFact(indicationFact);
		return indicationFact;
	}

	@Test
	public void testInstantIndication() {
		testIndicationRetraction(instantIndicationQuestion);

		Session session = SessionFactory.createSession(kb);

		indicate(session, indicationQuestion1, INDICATED);
		assertActiveQuestions(session, indicationQuestion1);

		// indicating question2 additionally will not change, that question1 was indicated first and is still next
		indicate(session, indicationQuestion2, INDICATED);
		assertActiveQuestions(session, indicationQuestion1);

		// instant indicating instantIndicationQuestion will make it the next question, disregarding previous indications
		indicate(session, instantIndicationQuestion, INSTANT_INDICATED);
		assertActiveQuestions(session, instantIndicationQuestion);
	}

	private void testIndicationRetraction(Question questionToBeIndicated) {
		Session session = SessionFactory.createSession(kb);

		assertActiveQuestions(session);

		Fact userEnteredFact = FactFactory.createUserEnteredFact(indicationChooser, new ChoiceValue(questionToBeIndicated
				.getName()));
		session.getBlackboard().addValueFact(userEnteredFact);

		assertActiveQuestions(session, questionToBeIndicated);

		session.getBlackboard().removeValueFact(userEnteredFact);

		assertActiveQuestions(session);
	}

	@Test
	public void testContraIndication() {
		Session session = SessionFactory.createSession(kb);
		indicate(session, indicationQuestion1, INDICATED);
		assertActiveQuestions(session, indicationQuestion1);
		Fact contraIndicationFact = indicate(session, indicationQuestion1, CONTRA_INDICATED);
		assertActiveQuestions(session);
		session.getBlackboard().removeInterviewFact(contraIndicationFact);
		// we used same source for both facts, so the first indication is no longer present
		assertActiveQuestions(session);

		// do same test, but with different fact sources
		Fact indicationFact = indicate(session, indicationQuestion1, INDICATED, "IndicationSource", PSMethodStrategic.getInstance());
		assertActiveQuestions(session, indicationQuestion1);
		contraIndicationFact = indicate(session, indicationQuestion1, CONTRA_INDICATED, "ContraIndicationSource", PSMethodStrategic
				.getInstance());
		assertActiveQuestions(session);
		session.getBlackboard().removeInterviewFact(contraIndicationFact);
		// using different sources, the indicationFact should still be present and activate the question
		assertActiveQuestions(session, indicationQuestion1);
		session.getBlackboard().removeInterviewFact(indicationFact);
		assertActiveQuestions(session);

		// do same test, but with different order of adding the facts
		contraIndicationFact = indicate(session, indicationQuestion1, CONTRA_INDICATED, "ContraIndicationSource", PSMethodStrategic
				.getInstance());
		assertActiveQuestions(session);
		indicationFact = indicate(session, indicationQuestion1, INDICATED, "IndicationSource", PSMethodStrategic.getInstance());
		assertActiveQuestions(session);
		session.getBlackboard().removeInterviewFact(contraIndicationFact);
		// using different sources, the indicationFact should still be present and activate the question
		assertActiveQuestions(session, indicationQuestion1);
		session.getBlackboard().removeInterviewFact(indicationFact);
		assertActiveQuestions(session);
	}

	@Test
	public void testMultipleIndication() {
		Session session = SessionFactory.createSession(kb);
		Interview interview = session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class));

		indicate(session, multipleIndicationQuestion1, MULTIPLE_INDICATED, 1);
		indicate(session, multipleIndicationQuestion2, MULTIPLE_INDICATED, 2);
		indicate(session, multipleIndicationQuestion3, MULTIPLE_INDICATED, 3);

		assertActiveQuestions(session, multipleIndicationQuestion1);
		Fact answer1 = answer(session, multipleIndicationQuestion1, Unknown.getInstance());
		assertFalse(interview.getInterviewAgenda().onAgenda(multipleIndicationQuestion1));

		assertActiveQuestions(session, multipleIndicationQuestion2);
		Fact answer2 = answer(session, multipleIndicationQuestion2, Unknown.getInstance());
		assertFalse(interview.getInterviewAgenda().onAgenda(multipleIndicationQuestion2));

		assertActiveQuestions(session, multipleIndicationQuestion3);
		Fact answer3 = answer(session, multipleIndicationQuestion3, Unknown.getInstance());
		assertFalse(interview.getInterviewAgenda().onAgenda(multipleIndicationQuestion3));

		removeValueFact(session, answer1);
		removeValueFact(session, answer2);
		removeValueFact(session, answer3);

		indicate(session, multipleIndicationQuestion3, MULTIPLE_INDICATED, 1);
		Fact indicationFact = indicate(session, multipleIndicationQuestion1, MULTIPLE_INDICATED, 2);
		indicate(session, multipleIndicationQuestion2, MULTIPLE_INDICATED, 3);

		assertActiveQuestions(session, multipleIndicationQuestion3);
		answer(session, multipleIndicationQuestion3, Unknown.getInstance());
		assertFalse(interview.getInterviewAgenda().onAgenda(multipleIndicationQuestion3));

		session.getBlackboard().removeInterviewFact(indicationFact);
		assertActiveQuestions(session, multipleIndicationQuestion2);
		session.getBlackboard().addInterviewFact(indicationFact);

		assertActiveQuestions(session, multipleIndicationQuestion1);
		answer(session, multipleIndicationQuestion1, Unknown.getInstance());
		assertFalse(interview.getInterviewAgenda().onAgenda(multipleIndicationQuestion1));

		assertActiveQuestions(session, multipleIndicationQuestion2);
		answer(session, multipleIndicationQuestion2, Unknown.getInstance());
		assertFalse(interview.getInterviewAgenda().onAgenda(multipleIndicationQuestion2));
	}

	private void removeValueFact(Session session, Fact fact) {
		session.getBlackboard().removeValueFact(fact);
	}

	private void assertActiveQuestions(Session session, QASet... qaSetsToIndicate) {
		assertEquals("Content of next form was not as expected", Arrays.asList(qaSetsToIndicate), getNextForm(session).getActiveQuestions());
	}

	private Form getNextForm(Session session) {
		Interview interview = session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class));
		return interview.nextForm();
	}

}
