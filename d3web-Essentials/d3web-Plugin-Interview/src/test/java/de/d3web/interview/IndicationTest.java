package de.d3web.interview;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

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

import static de.d3web.core.knowledge.Indication.State.INDICATED;
import static de.d3web.core.knowledge.Indication.State.INSTANT_INDICATED;
import static de.d3web.core.knowledge.Indication.State.RELEVANT;
import static de.d3web.core.manage.RuleFactory.setRuleParams;
import static org.junit.Assert.assertEquals;

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
			relevantIndicationQuestion, repeatedIndicationQuestion, instantIndicationQuestion;

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
	}

	@Test
	public void testRelevantIndication() {

		Session session = SessionFactory.createSession(kb);

		indicate(session, relevantIndicationQuestion, RELEVANT);
		// without its parent being indicated, relevantQuestion should not be active
		assertActiveQuestions(session);
		indicate(session, indicationQuestion1, RELEVANT);
		// same here, still no active questions
		assertActiveQuestions(session);
		indicate(session, indicationContainer, INDICATED);
		// with its parent indicated, question1 should now be active
		assertActiveQuestions(session, indicationQuestion1);

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

	private void answer(Session session, Question question, Value value) {
		Fact valueFact = FactFactory.createUserEnteredFact(question, value);
		session.getBlackboard().addValueFact(valueFact);
	}

	private void indicate(Session session, QASet qaSet, Indication.State state) {
		Fact indicationFact = FactFactory.createIndicationFact(qaSet, new Indication(state, kb.getManager()
						.getTreeIndex(qaSet)),
				PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance());
		session.getBlackboard().addInterviewFact(indicationFact);
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

	private void assertActiveQuestions(Session session, QASet... qaSetsToIndicate) {
		assertEquals("Content of next form was not as expected", Arrays.asList(qaSetsToIndicate), getNextForm(session).getActiveQuestions());
	}

	private Form getNextForm(Session session) {
		Interview interview = session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class));
		return interview.nextForm();
	}

}
