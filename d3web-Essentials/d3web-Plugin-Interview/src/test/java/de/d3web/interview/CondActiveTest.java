package de.d3web.interview;

import org.junit.Test;

import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.interview.inference.condition.CondActive;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Test for CondActive.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 10.06.16
 */
public class CondActiveTest extends IndicationTest {


	@Test
	public void testCondActiveWithNormalIndication() throws UnknownAnswerException, NoAnswerException {
		Session session = SessionFactory.createSession(kb);

		CondActive condActive1 = new CondActive(indicationQuestion1);
		CondActive condActive2 = new CondActive(indicationQuestion2);

		String message = "At the start of the session there shouldn't be any indicated question";
		assertFalse(message, condActive1.eval(session));
		assertFalse(message, condActive2.eval(session));

		session.getBlackboard()
				.addValueFact(FactFactory.createUserEnteredFact(indicationChooser, new ChoiceValue(indicationQuestion1.getName())));

		assertTrue(indicationQuestion1.getName() + " should be indicated and active now but isn't", condActive1.eval(session));
		assertFalse(indicationQuestion2.getName() + " should NOT be indicated or active now but is", condActive2.eval(session));

		session.getBlackboard()
				.addValueFact(FactFactory.createUserEnteredFact(indicationChooser, new ChoiceValue(indicationQuestion2.getName())));

		assertFalse(indicationQuestion1.getName() + " should NOT be indicated or active now but is", condActive1.eval(session));
		assertTrue(indicationQuestion2.getName() + " should be indicated and active now but isn't", condActive2.eval(session));
	}

	@Test
	public void testCondActiveWithInstantIndication() throws UnknownAnswerException, NoAnswerException {
		testCondActive(instantIndicationQuestion);
	}

	@Test
	public void testCondActiveWithRepeatedIndication() throws UnknownAnswerException, NoAnswerException {
		testCondActive(repeatedIndicationQuestion);
	}

	private void testCondActive(Question questionToBeIndicated) throws NoAnswerException, UnknownAnswerException {
		Session session = SessionFactory.createSession(kb);

		CondActive condActive = new CondActive(questionToBeIndicated);

		String message = "At the start of the session there shouldn't be any indicated question";
		assertFalse(message, condActive.eval(session));

		Fact userEnteredFact = FactFactory.createUserEnteredFact(indicationChooser, new ChoiceValue(questionToBeIndicated
				.getName()));
		session.getBlackboard().addValueFact(userEnteredFact);

		assertTrue(questionToBeIndicated.getName() + " should be indicated and active now but isn't", condActive.eval(session));

		session.getBlackboard().removeValueFact(userEnteredFact);

		assertFalse(questionToBeIndicated.getName() + " should NOT be indicated or active now but is", condActive.eval(session));
	}
}
