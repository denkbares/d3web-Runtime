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

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.io.KnowledgeBasePersistence;
import de.d3web.core.io.PersistenceManager;
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

	private void testCondActives(Session session, CondActive condActive1, CondActive condActive2) throws NoAnswerException, UnknownAnswerException {
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

		assertTrue(questionToBeIndicated.getName() + " should be indicated and active now but isn't", condActive.eval(session));

		session.getBlackboard().removeValueFact(userEnteredFact);

		assertFalse(questionToBeIndicated.getName() + " should NOT be indicated or active now but is", condActive.eval(session));
	}
}
