package de.d3web.interview;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.Rule;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.indication.ActionNextQASet;
import de.d3web.indication.inference.PSMethodStrategic;
import de.d3web.interview.indication.ActionRepeatedIndication;
import de.d3web.interview.inference.PSMethodInterview;
import de.d3web.plugin.test.InitPluginManager;

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
	protected QuestionOC indicationChooser, indicationQuestion1, indicationQuestion2,
			relevantIndicationQuestion, repeatedIndicationQuestion, instantIndicationQuestion;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		QContainer init = new QContainer(kb.getRootQASet(), "init");
		indicationQuestion1 = new QuestionOC(init, "IndicationQuestion1");
		indicationQuestion2 = new QuestionOC(init, "IndicationQuestion2");
		repeatedIndicationQuestion = new QuestionOC(init, "RepeatedIndicationQuestion");
		instantIndicationQuestion = new QuestionOC(init, "InstantIndicationQuestion");
		relevantIndicationQuestion = new QuestionOC(init, "RelevantIndicationQuestion");
		indicationChooser = new QuestionOC(init, "IndicationChooser",
				indicationQuestion1.getName(), indicationQuestion2.getName(), relevantIndicationQuestion.getName(),
				repeatedIndicationQuestion.getName(), relevantIndicationQuestion.getName());

		RuleFactory.createIndicationRule(this.indicationQuestion1, new CondEqual(indicationChooser, new ChoiceValue(indicationQuestion1
				.getName())));
		RuleFactory.createIndicationRule(this.indicationQuestion2, new CondEqual(indicationChooser, new ChoiceValue(indicationQuestion2
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
		testIndication(indicationQuestion1);
	}

	@Test
	public void testRelevantIndication() {
		testIndication(indicationQuestion1);
	}

	@Test
	public void testRepeatedIndication() {
		testIndication(indicationQuestion1);
	}

	@Test
	public void testInstantIndication() {
		testIndication(indicationQuestion1);
	}

	private void testIndication(Question questionToBeIndicated) {
		Session session = SessionFactory.createSession(kb);

		assertIndication(session);

		Fact userEnteredFact = FactFactory.createUserEnteredFact(indicationChooser, new ChoiceValue(questionToBeIndicated
				.getName()));
		session.getBlackboard().addValueFact(userEnteredFact);

		assertIndication(session, questionToBeIndicated);

		session.getBlackboard().removeValueFact(userEnteredFact);

		assertIndication(session);
	}

	private void assertIndication(Session session, QASet... qaSetsToIndicate) {
		assertEquals("Content of next form was not as expected", Arrays.asList(qaSetsToIndicate), getNextForm(session).getActiveQuestions());
	}

	private Form getNextForm(Session session) {
		Interview interview = session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class));
		return interview.nextForm();
	}

}
