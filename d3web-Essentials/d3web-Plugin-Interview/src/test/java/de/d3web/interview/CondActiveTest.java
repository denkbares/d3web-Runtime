package de.d3web.interview;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.io.KnowledgeBasePersistence;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.interview.inference.PSMethodInterview;
import de.d3web.interview.inference.condition.CondActive;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * Test for CondActive, including persistence.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 10.06.16
 */
public class CondActiveTest extends IndicationTest {

	@Test(expected = IllegalArgumentException.class)
	public void testCondActiveIllegalConstructorUsage1() {
		new CondActive(indicationChooser, indicationContainer);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCondActiveIllegalConstructorUsage2() {
		new CondActive(kb.getRootQASet(), indicationContainer);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCondActiveIllegalConstructorUsage3() {
		new CondActive();
	}

	@Test
	public void testMultiQuestionIndication() throws UnknownAnswerException, NoAnswerException, ParserConfigurationException, TransformerException, SAXException, IOException {
		Session session = SessionFactory.createSession(kb);
		getInterview(session).setFormStrategy(new CurrentQContainerFormStrategy());
		CondActive condActive1 = new CondActive(true, indicationQuestion1, indicationQuestion2);
		CondActive condActive2 = new CondActive(true, indicationContainer, indicationQuestion1, indicationQuestion2);
		CondActive condActive3 = new CondActive(true, indicationContainer);
		CondActive condActive4 = new CondActive(false, indicationContainer);
		CondActive condActive5 = new CondActive(true, indicationContainer, null);
		CondActive condActive6 = new CondActive(false, indicationContainer, null);

		testMultiQuestionIndication(session, condActive1, condActive2, condActive3, condActive4, condActive5, condActive6);

		session = SessionFactory.createSession(kb);
		getInterview(session).setFormStrategy(new CurrentQContainerFormStrategy());

		condActive1 = saveAndLoadCondition(condActive1);
		condActive2 = saveAndLoadCondition(condActive2);
		condActive3 = saveAndLoadCondition(condActive3);
		condActive4 = saveAndLoadCondition(condActive4);
		condActive5 = saveAndLoadCondition(condActive5);
		condActive6 = saveAndLoadCondition(condActive6);

		testMultiQuestionIndication(session, condActive1, condActive2, condActive3, condActive4, condActive5, condActive6);

	}

	private void testMultiQuestionIndication(Session session, CondActive condActive1, CondActive condActive2,
											 CondActive condActive3, CondActive condActive4,
											 CondActive condActive5, CondActive condActive6)
			throws NoAnswerException, UnknownAnswerException {

		Fact indicationContainerFact = FactFactory.createUserEnteredFact(indicationChooser, new ChoiceValue(indicationContainer
				.getName()));
		session.getBlackboard().addValueFact(indicationContainerFact);

		assertTrue("Since the indicationContainer was indicated, the CondActive with both indicationQuestions" +
				" should eval to true", condActive1.eval(session));
		assertTrue("Since the indicationContainer was indicated, the CondActive with the indicationContainer" +
				" and both indicationQuestions should eval to true", condActive2.eval(session));
		assertFalse("Since the indicationContainer was indicated, the CondActive with only the indicationContainer" +
				" and exclusivity should eval to false, because there are also active questions", condActive3.eval(session));
		assertTrue("Since the indicationContainer was indicated, the CondActive with only the indicationContainer" +
				" and no exclusivity should eval to true, because the other active questions are allowed", condActive4.eval(session));
		assertFalse("Since the indicationContainer was indicated, the CondActive with only the indicationContainer" +
				" and exclusivity should eval to false, because there are also active questions", condActive5.eval(session));
		assertTrue("Since the indicationContainer was indicated, the CondActive with only the indicationContainer" +
				" and no exclusivity should eval to true, because the other active questions are allowed", condActive6.eval(session));
	}

	@Test
	public void testMixedQContainerAndQuestionIndication() throws UnknownAnswerException, NoAnswerException, ParserConfigurationException, TransformerException, SAXException, IOException {
		RuleFactory.createContraIndicationRule(indicationQuestion1, new CondEqual(indicationChooser, new ChoiceValue(indicationQuestion2
				.getName())));
		Session session = SessionFactory.createSession(kb);
		getInterview(session).setFormStrategy(new CurrentQContainerFormStrategy());

		CondActive condActive = new CondActive(true, indicationContainer, indicationQuestion2);
		testMixedQContainerAndQuestionIndication(session, condActive);

		condActive = saveAndLoadCondition(condActive);
		session = SessionFactory.createSession(kb);
		getInterview(session).setFormStrategy(new CurrentQContainerFormStrategy());
	}

	private void testMixedQContainerAndQuestionIndication(Session session, CondActive condActive) throws UnknownAnswerException, NoAnswerException {

		Fact indicationContainerFact = FactFactory.createUserEnteredFact(indicationChooser,
				new MultipleChoiceValue(new ChoiceID(indicationContainer.getName()),
						new ChoiceID(indicationQuestion2.getName())));
		session.getBlackboard().addValueFact(indicationContainerFact);

		assertTrue(indicationContainer.getName() + " is indicated, " + indicationQuestion1.getName()
				+ " is contra indicated, so the CondActive should eval to true", condActive.eval(session));
	}

	@Test
	public void testExclusiveCondActive() throws UnknownAnswerException, NoAnswerException, ParserConfigurationException, TransformerException, SAXException, IOException {
		Session session = SessionFactory.createSession(kb);

		CondActive condActive = new CondActive(true, indicationQuestion1);
		testExclusiveCondActive(session, condActive);

		condActive = saveAndLoadCondition(condActive);

		session = SessionFactory.createSession(kb);
		testExclusiveCondActive(session, condActive);
	}

	private void testExclusiveCondActive(Session session, CondActive exclusiveCondActive) throws UnknownAnswerException, NoAnswerException {
		assertFalse("At the start of the session there shouldn't be any indicated question", exclusiveCondActive.eval(session));

		Fact question1Fact = FactFactory.createUserEnteredFact(indicationChooser, new ChoiceValue(indicationQuestion1.getName()));
		session.getBlackboard().addValueFact(question1Fact);
		TestCase.assertTrue(indicationQuestion1.getName() + " should be indicated and active now but isn't", exclusiveCondActive
				.eval(session));

		session.getBlackboard().removeValueFact(question1Fact);
		assertFalse("After removing the fact, " + indicationQuestion1.getName() + " should not be active, but is!", exclusiveCondActive
				.eval(session));

		Fact question1And2Fact = FactFactory.createUserEnteredFact(indicationChooser, new ChoiceValue(indicationContainer
				.getName()));
		session.getBlackboard().addValueFact(question1And2Fact);
		TestCase.assertTrue(indicationQuestion1.getName() + " should be indicated and active now but isn't", exclusiveCondActive
				.eval(session));

		// with CurrentQContainerFormStrategy, the nextForm contains all questions of the indicationContainer, so indicationQuestion is no longer active exclusively
		getInterview(session).setFormStrategy(new CurrentQContainerFormStrategy());
		assertFalse(indicationQuestion1.getName() + " should be exclusively active, but is not", exclusiveCondActive.eval(session));
	}

	@Test
	public void testCondActiveWithNormalIndication() throws UnknownAnswerException, NoAnswerException, IOException, TransformerException, ParserConfigurationException, SAXException {
		Session session = SessionFactory.createSession(kb);

		CondActive condActive1 = new CondActive(indicationQuestion1);
		CondActive condActive2 = new CondActive(indicationQuestion2);

		testCondActives(session, condActive1, condActive2);

		condActive1 = saveAndLoadCondition(condActive1);
		condActive2 = saveAndLoadCondition(condActive2);

		session = SessionFactory.createSession(kb);
		testCondActives(session, condActive1, condActive2);

	}

	private Interview getInterview(Session session) {
		return session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class));
	}

	private void testCondActives(Session session, CondActive condActive1, CondActive condActive2) throws NoAnswerException, UnknownAnswerException {
		String message = "At the start of the session there shouldn't be any indicated question";
		assertFalse(message, condActive1.eval(session));
		assertFalse(message, condActive2.eval(session));

		session.getBlackboard()
				.addValueFact(FactFactory.createUserEnteredFact(indicationChooser, new ChoiceValue(indicationQuestion1.getName())));

		TestCase.assertTrue(indicationQuestion1.getName() + " should be indicated and active now but isn't", condActive1
				.eval(session));
		assertFalse(indicationQuestion2.getName() + " should NOT be indicated or active now but is", condActive2.eval(session));

		session.getBlackboard()
				.addValueFact(FactFactory.createUserEnteredFact(indicationChooser, new ChoiceValue(indicationQuestion2.getName())));

		assertFalse(indicationQuestion1.getName() + " should NOT be indicated or active now but is", condActive1.eval(session));
		TestCase.assertTrue(indicationQuestion2.getName() + " should be indicated and active now but isn't", condActive2
				.eval(session));

		getInterview(session).setFormStrategy(new NextUnansweredQuestionFormStrategy());
		session.getBlackboard()
				.addValueFact(FactFactory.createUserEnteredFact(indicationChooser, new ChoiceValue(indicationContainer.getName())));
		// the questionnaire containing indicationQuestion1 and indicationQuestion2 is indicated, so those two  are the
		// next questions to be indicated
		TestCase.assertTrue(indicationQuestion1.getName() + " should be indicated and active now but isn't", condActive1
				.eval(session));
		assertFalse(indicationQuestion2.getName() + " should NOT be indicated or active now but is", condActive2.eval(session));

		// answering indicationQuestion1 should remove it from the agenda and indicationQuestion2 should be indicated
		session.getBlackboard()
				.addValueFact(FactFactory.createUserEnteredFact(indicationQuestion1, Unknown.getInstance()));
		assertFalse(indicationQuestion1.getName() + " should NOT be indicated or active now but is", condActive1.eval(session));
		TestCase.assertTrue(indicationQuestion2.getName() + " should be indicated and active now but isn't", condActive2
				.eval(session));
	}

	@Test
	public void testCondActiveWithInstantIndication() throws UnknownAnswerException, NoAnswerException, IOException, TransformerException, ParserConfigurationException, SAXException {
		testCondActiveWithPersistence(instantIndicationQuestion);
	}

	@Test
	public void testCondActiveWithRepeatedIndication() throws UnknownAnswerException, NoAnswerException, IOException, TransformerException, ParserConfigurationException, SAXException {
		testCondActiveWithPersistence(repeatedIndicationQuestion);
	}

	private void testCondActiveWithPersistence(Question questionToBeIndicated) throws NoAnswerException, UnknownAnswerException, IOException, TransformerException, ParserConfigurationException, SAXException {

		CondActive condActive = new CondActive(questionToBeIndicated);
		Session session = SessionFactory.createSession(kb);

		testCondActive(questionToBeIndicated, condActive, session);

		condActive = saveAndLoadCondition(condActive);

		testCondActive(questionToBeIndicated, condActive, session);
	}

	private CondActive saveAndLoadCondition(CondActive condActive) throws IOException, TransformerException, ParserConfigurationException, SAXException {
		return toCondition(toConditionXMLString(condActive));
	}

	private CondActive toCondition(String conditionXMLString) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new StringReader(conditionXMLString)));
		KnowledgeBasePersistence persistence = new KnowledgeBasePersistence(PersistenceManager.getInstance(), kb, document);
		return (CondActive) persistence.readFragment(document.getDocumentElement());
	}

	private String toConditionXMLString(final Condition condition) throws IOException, TransformerException {
		KnowledgeBasePersistence knowledgeBasePersistence = new KnowledgeBasePersistence(PersistenceManager.getInstance(), kb);
		final Element conditionElement = knowledgeBasePersistence.writeFragment(condition);

		final StreamResult result = new StreamResult(new StringWriter());
		final DOMSource source = new DOMSource(conditionElement);
		TransformerFactory.newInstance().newTransformer().transform(source, result);
		return result.getWriter().toString();
	}

	private void testCondActive(Question questionToBeIndicated, CondActive condActive, Session session) throws NoAnswerException, UnknownAnswerException {
		String message = "At the start of the session there shouldn't be any indicated question";
		assertFalse(message, condActive.eval(session));

		Fact userEnteredFact = FactFactory.createUserEnteredFact(indicationChooser, new ChoiceValue(questionToBeIndicated
				.getName()));
		session.getBlackboard().addValueFact(userEnteredFact);

		TestCase.assertTrue(questionToBeIndicated.getName() + " should be indicated and active now but isn't", condActive
				.eval(session));

		session.getBlackboard().removeValueFact(userEnteredFact);

		assertFalse(questionToBeIndicated.getName() + " should NOT be indicated or active now but is", condActive.eval(session));
	}
}
