package de.d3web.testcase.persistence.test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.knowledge.terminology.info.NumericalInterval;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import com.denkbares.plugin.test.InitPluginManager;
import com.denkbares.strings.Identifier;
import de.d3web.testcase.model.PropertyCheck;
import de.d3web.testcase.model.PropertyCheckTemplate;
import de.d3web.testcase.model.TransformationException;
import de.d3web.testcase.persistence.TestCasePersistence;

import static junit.framework.TestCase.*;

/**
 * Test for {@link PropertyCheck} and {@link PropertyCheckTemplate}
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 21.06.16
 */
public class PropertyCheckTest {

	private KnowledgeBase kb;

	@BeforeClass
	public static void init() throws IOException {
		InitPluginManager.init();
	}

	@Before
	public void setUp() {
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		kb.setId("TestID");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor() {
		new PropertyCheckTemplate<>(new Identifier("TestKB"), BasicProperties.AUTHOR, Locale.GERMAN, "AS");
	}

	@Test
	public void testGetters() throws ParserConfigurationException, TransformerException, SAXException, IOException {
		PropertyCheckTemplate<?> testTemplate = new PropertyCheckTemplate<>(new Identifier("Test"), MMInfo.DESCRIPTION, Locale.CANADA, "Test");

		testGetterEquality(testTemplate);
		testTemplate = saveAndLoadCheck(testTemplate);
		testGetterEquality(testTemplate);
	}

	private void testGetterEquality(PropertyCheckTemplate<?> testTemplate) {
		assertEquals(new Identifier("Test"), testTemplate.getObjectIdentifier());
		assertEquals(MMInfo.DESCRIPTION, testTemplate.getProperty());
		assertEquals(Locale.CANADA, testTemplate.getLocale());
		assertEquals("Test", testTemplate.getPropertyValue());
	}

	@Test
	public void testQuestionOC() throws ParserConfigurationException, TransformerException, TransformationException, SAXException, IOException {
		QASet rootQASet = kb.getRootQASet();
		QuestionOC questionOC = new QuestionOC(rootQASet, "questionOC", "choice1", "choice2");
		questionOC.getInfoStore().addValue(MMInfo.DESCRIPTION, Locale.ENGLISH, "One-choice question");
		questionOC.getInfoStore().addValue(MMInfo.DESCRIPTION, "Einzelwahlfrage");
		questionOC.getInfoStore().addValue(BasicProperties.ABSTRACTION_QUESTION, true);
		KnowledgeBaseUtils.findChoice(questionOC, "choice1").getInfoStore().addValue(BasicProperties.APRIORI, 23f);

		Session session = SessionFactory.createSession(kb);
		PropertyCheckTemplate<String> questionOcEnDescriptionCheck = new PropertyCheckTemplate<>(new Identifier("questionOC"), MMInfo.DESCRIPTION, Locale.ENGLISH, "One-choice question");
		assertTrueWithPersistence(session, questionOcEnDescriptionCheck);

		PropertyCheckTemplate<String> questionOcDescriptionCheck = new PropertyCheckTemplate<>(new Identifier("questionOC"), MMInfo.DESCRIPTION, "Einzelwahlfrage");
		assertTrueWithPersistence(session, questionOcDescriptionCheck);

		PropertyCheckTemplate<Boolean> abstractionCheck = new PropertyCheckTemplate<>(new Identifier("questionOC"), BasicProperties.ABSTRACTION_QUESTION, true);
		assertTrueWithPersistence(session, abstractionCheck);

		PropertyCheckTemplate<Float> questionChoiceFloatCheck = new PropertyCheckTemplate<>(new Identifier("questionOC", "choice1"), BasicProperties.APRIORI, 23f);
		assertTrueWithPersistence(session, questionChoiceFloatCheck);
	}

	@Test
	public void testSolution() throws ParserConfigurationException, TransformerException, TransformationException, SAXException, IOException {
		Solution rootSolution = kb.getRootSolution();
		rootSolution.getInfoStore().addValue(MMInfo.PROMPT, Locale.GERMAN, "Wurzellösung");

		Session session = SessionFactory.createSession(kb);
		PropertyCheckTemplate<String> choiceCheck = new PropertyCheckTemplate<>(new Identifier("P000"), MMInfo.PROMPT, Locale.GERMAN, "Wurzellösung");
		assertTrueWithPersistence(session, choiceCheck);
	}

	@Test
	public void testQuestionYNChoice() throws ParserConfigurationException, TransformerException, TransformationException, SAXException, IOException {
		QASet rootQASet = kb.getRootQASet();
		QuestionYN questionYN = new QuestionYN(rootQASet, "questionYN");
		questionYN.getAnswerChoiceYes().getInfoStore().addValue(MMInfo.PROMPT, Locale.GERMAN, "Ja");

		Session session = SessionFactory.createSession(kb);

		PropertyCheckTemplate<String> choiceCheck = new PropertyCheckTemplate<>(new Identifier("questionYN", "Yes"), MMInfo.PROMPT, Locale.GERMAN, "Ja");
		assertTrueWithPersistence(session, choiceCheck);

		choiceCheck = new PropertyCheckTemplate<>(new Identifier("questionYN", "No"), MMInfo.PROMPT, Locale.GERMAN, "Ja");
		assertFalseWithPersistence(session, choiceCheck);
	}

	@Test
	public void testQuestionNum() throws ParserConfigurationException, TransformerException, TransformationException, SAXException, IOException {
		QASet rootQASet = kb.getRootQASet();
		QuestionNum questionNum = new QuestionNum(rootQASet, "questionNum");
		NumericalInterval value = new NumericalInterval(2, 5, true, false);
		questionNum.getInfoStore().addValue(BasicProperties.QUESTION_NUM_RANGE, value);

		Session session = SessionFactory.createSession(kb);

		PropertyCheckTemplate<NumericalInterval> numCheck = new PropertyCheckTemplate<>(new Identifier("questionNum"), BasicProperties.QUESTION_NUM_RANGE, value);
		assertTrueWithPersistence(session, numCheck);

		numCheck = new PropertyCheckTemplate<>(new Identifier("questionNum"), BasicProperties.QUESTION_NUM_RANGE, new NumericalInterval(2, 5, true, true));
		assertFalseWithPersistence(session, numCheck);
	}

	@Test
	public void testQContainerAndDateProperty() throws ParserConfigurationException, TransformerException, TransformationException, SAXException, IOException {
		QASet rootQASet = kb.getRootQASet();
		Date date = Date.from(Instant.parse("2011-12-03T10:15:30Z"));
		rootQASet.getInfoStore().addValue(BasicProperties.CREATED, date);

		Session session = SessionFactory.createSession(kb);

		PropertyCheckTemplate<Date> dateCheck = new PropertyCheckTemplate<>(new Identifier("Q000"), BasicProperties.CREATED, date);
		assertTrueWithPersistence(session, dateCheck);

	}

	@Test
	public void testKBProperty() throws TransformationException, ParserConfigurationException, TransformerException, SAXException, IOException {
		kb.getInfoStore().addValue(MMInfo.PROMPT, Locale.GERMAN, "TestWissensbasis");
		kb.getInfoStore().addValue(MMInfo.DESCRIPTION, "A test knowledge base");
		kb.getInfoStore().addValue(MMInfo.PROMPT, "TestKB");
		kb.getInfoStore().addValue(BasicProperties.AUTHOR, "AS");

		Session session = SessionFactory.createSession(kb);

		Identifier kbIdentifier = new Identifier("TestID");
		PropertyCheckTemplate<?> kbGerPrompt = new PropertyCheckTemplate<>(kbIdentifier, MMInfo.PROMPT, Locale.GERMAN, "TestWissensbasis");
		assertTrueWithPersistence(session, kbGerPrompt);

		PropertyCheckTemplate<?> kbDescription = new PropertyCheckTemplate<>(kbIdentifier, MMInfo.DESCRIPTION, "A test knowledge base");
		assertTrueWithPersistence(session, kbDescription);

		// since description is not available in german, the fallback without locale will be found and used
		PropertyCheckTemplate<?> kbEnDescription = new PropertyCheckTemplate<>(kbIdentifier, MMInfo.DESCRIPTION, Locale.GERMAN, "A test knowledge base");
		assertTrueWithPersistence(session, kbEnDescription);

		PropertyCheckTemplate<String> kbPrompt = new PropertyCheckTemplate<>(kbIdentifier, MMInfo.PROMPT, "TestKB");
		assertTrueWithPersistence(session, kbPrompt);

		PropertyCheckTemplate<String> kbAuthor = new PropertyCheckTemplate<>(kbIdentifier, BasicProperties.AUTHOR, "AS");
		assertTrueWithPersistence(session, kbAuthor);

		PropertyCheckTemplate<?> kbGerPromptFalse = new PropertyCheckTemplate<>(kbIdentifier, MMInfo.PROMPT, Locale.GERMAN, "TestKB");
		assertFalseWithPersistence(session, kbGerPromptFalse);

		PropertyCheckTemplate<?> kbGerPromptFalseEmpty = new PropertyCheckTemplate<>(kbIdentifier, MMInfo.PROMPT, Locale.GERMAN, null);
		assertFalseWithPersistence(session, kbGerPromptFalseEmpty);

		PropertyCheckTemplate<?> kbAffiliation = new PropertyCheckTemplate<>(kbIdentifier, BasicProperties.AFFILIATION, "TestAffiliation");
		assertFalseWithPersistence(session, kbAffiliation);
	}

	private void assertFalseWithPersistence(Session session, PropertyCheckTemplate<?> checkTemplate) throws TransformationException, IOException, TransformerException, ParserConfigurationException, SAXException {
		assertFalse(checkTemplate.toCheck(kb).check(session));
		checkTemplate = saveAndLoadCheck(checkTemplate);
		assertFalse(checkTemplate.toCheck(kb).check(session));
	}

	private void assertTrueWithPersistence(Session session, PropertyCheckTemplate<?> checkTemplate) throws TransformationException, IOException, TransformerException, ParserConfigurationException, SAXException {
		assertTrue(checkTemplate.toCheck(kb).check(session));
		checkTemplate = saveAndLoadCheck(checkTemplate);
		assertTrue(checkTemplate.toCheck(kb).check(session));
	}

	private PropertyCheckTemplate<?> saveAndLoadCheck(PropertyCheckTemplate<?> propertyCheck) throws IOException, TransformerException, ParserConfigurationException, SAXException {
		return toPropertyCheck(toPropertyCheckXMLString(propertyCheck));
	}

	private PropertyCheckTemplate<?> toPropertyCheck(String checkXMLString) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new StringReader(checkXMLString)));
		TestCasePersistence persistence = new TestCasePersistence(document);
		return (PropertyCheckTemplate) persistence.readFragment(document.getDocumentElement());
	}

	private String toPropertyCheckXMLString(final PropertyCheckTemplate<?> propertyCheckTemplate) throws IOException, TransformerException {
		TestCasePersistence testCasePersistence = new TestCasePersistence();
		final Element propertyCheckElement = testCasePersistence.writeFragment(propertyCheckTemplate);

		final StreamResult result = new StreamResult(new StringWriter());
		final DOMSource source = new DOMSource(propertyCheckElement);
		TransformerFactory.newInstance().newTransformer().transform(source, result);
		return result.getWriter().toString();
	}
}
