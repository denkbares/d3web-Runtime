package de.d3web.interview;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.Rule;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.indication.ActionNextQASet;
import de.d3web.indication.inference.PSMethodStrategic;
import de.d3web.interview.indication.ActionRepeatedIndication;
import de.d3web.interview.inference.condition.CondActive;
import de.d3web.plugin.test.InitPluginManager;

import static de.d3web.core.manage.RuleFactory.setRuleParams;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Test for CondActive.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 10.06.16
 */
public class CondActiveTest {

	private KnowledgeBase kb;
	private QuestionOC indicationQuestion, question1, question2, repeatedIndicationQuestion, instantIndicationQuestion;
	private Condition conditionQ1Yes;
	private ChoiceValue choiceValueYes, choiceValueNo;
	private String question1Name = "Question1";
	private String question2Name = "Question2";
	private String repeatedIndicationQuestionName = "RepeatedIndicationQuestion";
	private String instantIndicationQuestionName = "InstantIndicationQuestion";

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		QContainer init = new QContainer(kb.getRootQASet(), "init");
		indicationQuestion = new QuestionOC(init, "IndicateQuestion",
				question1Name, question2Name, repeatedIndicationQuestionName, instantIndicationQuestionName);
		question1 = new QuestionOC(init, question1Name);
		question2 = new QuestionOC(init, question2Name);
		repeatedIndicationQuestion = new QuestionOC(init, repeatedIndicationQuestionName);
		instantIndicationQuestion = new QuestionOC(init, instantIndicationQuestionName);

		RuleFactory.createIndicationRule(question1, new CondEqual(indicationQuestion, new ChoiceValue(question1Name)));
		RuleFactory.createIndicationRule(question2, new CondEqual(indicationQuestion, new ChoiceValue(question2Name)));
		RuleFactory.createInstantIndicationRule(instantIndicationQuestion, new CondEqual(indicationQuestion, new ChoiceValue(instantIndicationQuestionName)));
		createRepeatedIndicationRule(new CondEqual(indicationQuestion, new ChoiceValue(repeatedIndicationQuestionName)), repeatedIndicationQuestion);

	}

	private Rule createRepeatedIndicationRule(Condition condition, QASet... qaSetsToIndicate) {
		Rule rule = new Rule(PSMethodStrategic.class);
		ActionNextQASet ruleAction = new ActionRepeatedIndication(qaSetsToIndicate);
		setRuleParams(rule, ruleAction, condition, null);
		return rule;
	}

	@Test
	public void testIndication() throws UnknownAnswerException, NoAnswerException {
		Session session = SessionFactory.createSession(kb);

		CondActive condActive1 = new CondActive(question1);
		CondActive condActive2 = new CondActive(question2);

		String message = "At the start of the session there shouldn't be any indicated question";
		assertFalse(message, condActive1.eval(session));
		assertFalse(message, condActive2.eval(session));

		session.getBlackboard()
				.addValueFact(FactFactory.createUserEnteredFact(indicationQuestion, new ChoiceValue(question1Name)));

		assertTrue(question1.getName() + " should be indicated and active now but isn't", condActive1.eval(session));
		assertFalse(question2.getName() + " should NOT be indicated or active now but is", condActive2.eval(session));

		session.getBlackboard()
				.addValueFact(FactFactory.createUserEnteredFact(indicationQuestion, new ChoiceValue(question2Name)));

		assertFalse(question1.getName() + " should NOT be indicated or active now but is", condActive1.eval(session));
		assertTrue(question2.getName() + " should be indicated and active now but isn't", condActive2.eval(session));
	}

	@Test
	public void testInstantIndication() throws UnknownAnswerException, NoAnswerException {
		Session session = SessionFactory.createSession(kb);

		CondActive condActive = new CondActive(instantIndicationQuestion);

		String message = "At the start of the session there shouldn't be any indicated question";
		assertFalse(message, condActive.eval(session));

		Fact instantIndicationFact = FactFactory.createUserEnteredFact(indicationQuestion, new ChoiceValue(instantIndicationQuestionName));
		session.getBlackboard().addValueFact(instantIndicationFact);

		assertTrue(instantIndicationQuestion.getName() + " should be indicated and active now but isn't", condActive.eval(session));

		// TODO: Why isn't it sufficient to just remove the the instantIndicationFact?
		// session.getBlackboard().removeValueFact(instantIndicationFact);
		session.getBlackboard()
				.addValueFact(FactFactory.createUserEnteredFact(instantIndicationQuestion, Unknown.getInstance()));

		assertFalse(instantIndicationQuestion.getName() + " should NOT be indicated or active now but is", condActive.eval(session));
	}

	@Test
	public void testRepeatedIndication() throws UnknownAnswerException, NoAnswerException {
		Session session = SessionFactory.createSession(kb);

		CondActive condActive = new CondActive(repeatedIndicationQuestion);

		String message = "At the start of the session there shouldn't be any indicated question";
		assertFalse(message, condActive.eval(session));

		Fact userEnteredFact = FactFactory.createUserEnteredFact(indicationQuestion, new ChoiceValue(repeatedIndicationQuestionName));
		session.getBlackboard().addValueFact(userEnteredFact);

		assertTrue(repeatedIndicationQuestion.getName() + " should be indicated and active now but isn't", condActive.eval(session));

		session.getBlackboard().removeValueFact(userEnteredFact);

		assertFalse(repeatedIndicationQuestion.getName() + " should NOT be indicated or active now but is", condActive.eval(session));
	}
}
