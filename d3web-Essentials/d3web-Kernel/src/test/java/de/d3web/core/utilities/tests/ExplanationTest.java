package de.d3web.core.utilities.tests;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.denkbares.plugin.test.InitPluginManager;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.utilities.ExplanationUtils;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 18.07.16
 */
public class ExplanationTest {

	private KnowledgeBase kb = null;
	private QuestionYN questionA;
	private QuestionYN questionB;
	private QuestionYN questionC;
	private QuestionYN questionD;

	@BeforeClass
	public static void init() throws IOException {
		InitPluginManager.init();
	}

	@Before
	public void setUp() {
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		questionA = new QuestionYN(kb, "A");
		questionB = new QuestionYN(kb, "B");
		questionC = new QuestionYN(kb, "C");
		questionD = new QuestionYN(kb, "D");

		RuleFactory.createSetValueRule(questionC, new ChoiceValue(questionC.getAnswerChoiceYes()),
				new CondEqual(questionB, new ChoiceValue(questionB.getAnswerChoiceYes())));

		RuleFactory.createSetValueRule(questionD, new ChoiceValue(questionD.getAnswerChoiceYes()),
				new CondAnd(Arrays.asList(new CondEqual(questionC, new ChoiceValue(questionC.getAnswerChoiceYes())),
						new CondEqual(questionA, new ChoiceValue(questionA.getAnswerChoiceYes())))));
	}

	@Test
	public void basic() {

		Session session = SessionFactory.createSession(kb);

		assertTrue(ExplanationUtils.getSourceFacts(session, questionC).isEmpty());

		Fact setBtoYes = FactFactory.createUserEnteredFact(kb, "B", "Yes");
		session.getBlackboard().addValueFact(setBtoYes);

		Collection<Fact> sourceFacts = ExplanationUtils.getSourceFactsNonBlocking(session, questionC);
		assertEquals(1, sourceFacts.size());
		assertEquals(setBtoYes, sourceFacts.iterator().next());

		Fact setAtoYes = FactFactory.createUserEnteredFact(kb, "A", "Yes");
		session.getBlackboard().addValueFact(setAtoYes);

		// still the same for C
		sourceFacts = ExplanationUtils.getSourceFactsNonBlocking(session, questionC);
		assertEquals(1, sourceFacts.size());
		assertEquals(setBtoYes, sourceFacts.iterator().next());

		sourceFacts = ExplanationUtils.getSourceFactsNonBlocking(session, questionD);
		assertEquals(2, sourceFacts.size());
		assertTrue(sourceFacts.contains(setBtoYes));
		assertTrue(sourceFacts.contains(setAtoYes));

		Collection<Fact> predecessorFacts = ExplanationUtils.getPredecessorFactsNonBlocking(session, questionD);
		assertEquals(2, sourceFacts.size());
		assertTrue(predecessorFacts.contains(session.getBlackboard().getValueFact(questionC)));
		assertTrue(sourceFacts.contains(setAtoYes));

	}
}
