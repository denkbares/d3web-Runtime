package de.d3web.core.manage.tests;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.plugin.test.InitPluginManager;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Tests the correct behavior of the KnowledgeBaseUtils class and contained
 * methods
 *
 * @author Martina Freiberg
 * @created 02.09.2010
 */
public class KnowledgeBaseUtilsTest {

	private Choice choice1;
	private Choice choice2;
	private KnowledgeBase kb;
	private QuestionChoice qmc;
	private QuestionChoice qc;

	@Before
	public void setUp() {
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		choice1 = new Choice("choice1");
		choice2 = new Choice("choice2");
		qmc = new QuestionMC(kb.getRootQASet(), "qmc");
		qmc.addAlternative(choice1);
		qmc.addAlternative(choice2);
		qc = new QuestionOC(kb.getRootQASet(), "qc");
		qc.addAlternative(choice1);
		qc.addAlternative(choice2);

	}

	/**
	 * Test findValue: MCValue as single value
	 *
	 * @created 09.09.2010
	 */
	@Test
	public void findValueMCValSingleVal() {
		List<Choice> choiceList = new LinkedList<>();
		choiceList.add(choice1);
		Value mcValToFind = MultipleChoiceValue.fromChoices(choiceList);

		assertThat(KnowledgeBaseUtils.findValue(qmc, "choice1"),
				is(mcValToFind));
		assertNull(KnowledgeBaseUtils.findValue(qmc,
				"FindchoiceNULL"));
	}

	/**
	 * Test findValue: MCValue as "real" MultipleChoiceValue
	 *
	 * @created 09.09.2010
	 */
	@Test
	public void findValueMCValRealMC() {
		List<Choice> choiceList = new LinkedList<>();
		choiceList.add(choice1);
		choiceList.add(choice2);
		Value mcValToFind = MultipleChoiceValue.fromChoices(choiceList);

		assertThat(KnowledgeBaseUtils.findValue(qmc, "choice1#####choice2"),
				is(mcValToFind));
		assertNull(KnowledgeBaseUtils.findValue(qmc,
				"FindchoiceNULL#####2"));
		assertNull(KnowledgeBaseUtils.findValue(qmc,
				"FindchoiceNULL"));
	}

	/**
	 * Test findValue: ChoiceValue, i.e., question oc or question yn
	 *
	 * @created 09.09.2010
	 */
	@Test
	public void findValueChoiceVal() {

		// test oc question
		Value choiceValToFind1 = new ChoiceValue(choice1);
		Value choiceValToFind2 = new ChoiceValue(choice2);

		assertThat(KnowledgeBaseUtils.findValue(qc, "choice1"),
				is(choiceValToFind1));
		assertThat(KnowledgeBaseUtils.findValue(qc, "choice2"),
				is(choiceValToFind2));
		assertNull(KnowledgeBaseUtils.findValue(qc, "FindchoiceNULL"));

		// test yes no question
		Choice YES = new Choice("Yes");
		Choice NO = new Choice("No");
		Value yes = new ChoiceValue(YES);
		Value no = new ChoiceValue(NO);
		QuestionYN qyn = new QuestionYN(kb, "");

		assertThat(KnowledgeBaseUtils.findValue(qyn, "Yes"),
				is(yes));
		assertThat(KnowledgeBaseUtils.findValue(qyn, "No"),
				is(no));
		assertNull(KnowledgeBaseUtils.findValue(qyn, "FindchoiceNULL"));
	}

	/**
	 * Test findValue: NumValue
	 *
	 * @created 02.09.2010
	 */
	@Test
	public void findValueNumVal() {
		Value numToGet = new NumValue(1.0);
		String numValInput = "1.0";
		QuestionNum qn = new QuestionNum(kb, "Please enter: ");
		assertThat(KnowledgeBaseUtils.findValue(qn, numValInput),
				is(numToGet));
	}

	/**
	 * Test findValue: TextValue
	 *
	 * @created 02.09.2010
	 */
	@Test
	public void findValueTextVal() {
		Value textToGet = new TextValue("My Text");
		String textValInput = "My Text";
		QuestionText qt = new QuestionText(kb, "Please enter: ");
		assertThat(KnowledgeBaseUtils.findValue(qt, textValInput),
				is(textToGet));
	}

	/**
	 * Test findValue: DateValue
	 *
	 * @created 02.09.2010
	 */
	@Test
	public void findValueDateVal() {

		Value dateToGet = null;
		SimpleDateFormat format = DateValue.getDefaultDateFormat();
		try {
			Date date = format.parse("2010-09-02 12:13:30.000 UTC");
			dateToGet = new DateValue(date);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		String dateValInput = "2010-09-02 12:13:30.000 UTC";
		QuestionDate qd = new QuestionDate(kb, "Please enter: ");
		assertThat(KnowledgeBaseUtils.findValue(qd, dateValInput), is(dateToGet));
		dateValInput = "wrong date format";
		assertNull(KnowledgeBaseUtils.findValue(qd, dateValInput));
	}

	/**
	 * check the logic of the entire findValue Method, i.e., conditions
	 * undefined and unknown at the beginning
	 *
	 * @created 09.09.2010
	 */
	@Test
	public void findValueComplete() {
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		Question q = new QuestionText(kb, "#");

		// test undefined id given
		assertEquals(KnowledgeBaseUtils.findValue(q,
				UndefinedValue.UNDEFINED_ID), UndefinedValue.getInstance());

		// test unknown id given
		assertEquals(KnowledgeBaseUtils.findValue(q,
				Unknown.UNKNOWN_ID), Unknown.getInstance());

		Value textToGet = new TextValue("My Text");
		String textValInput = "My Text";
		assertThat(KnowledgeBaseUtils.findValue(q, textValInput),
				is(textToGet));
	}

	@Test
	public void badCreationOfFacts() {
		QuestionChoice qc = new QuestionOC(kb, "Please enter: ");
		assertThat(qc != null, is(true));

		Fact badFact = FactFactory.createUserEnteredFact(kb, "Please enter: ", (String) null);
		assertThat(badFact == null, is(true));
	}

	@Test
	public void getAncestors() {
		QuestionOC qc2 = new QuestionOC(qc, "qc2");
		QuestionOC qc3 = new QuestionOC(qc2, "qc3");
		qmc.addChild(qc3);
		assertEquals(Arrays.asList(qc3, qc2, qc, kb.getRootQASet(), qmc),
				KnowledgeBaseUtils.getAncestors(qc3));
	}

	@Test
	public void getSuccessors() {
		QuestionOC qc2 = new QuestionOC(qc, "qc2");
		QuestionOC qc3 = new QuestionOC(qc2, "qc3");
		qmc.addChild(qc3);
		assertEquals(Arrays.asList(kb.getRootQASet(), qmc, qc3, qc, qc2),
				KnowledgeBaseUtils.getSuccessors(kb.getRootQASet()));
	}

	@Test
	public void sortQContainers() {
		QContainer qc1 = new QContainer(kb.getRootQASet(), "qc1");
		QContainer qc2 = new QContainer(kb.getRootQASet(), "qc2");
		QContainer qc3 = new QContainer(kb.getRootQASet(), "qc3");
		QContainer qc4 = new QContainer(qc2, "qc4");
		QContainer qc5 = new QContainer(qc4, "qc5");
		QContainer qc6 = new QContainer(qc3, "qc6");
		List<QContainer> qcs = Arrays.asList(qc6, qc5, qc4, qc3,
				qc2, qc1);
		KnowledgeBaseUtils.sortTerminologyObjects(qcs);
		assertEquals(Arrays.asList(qc1, qc2, qc4, qc5, qc3, qc6), qcs);
	}

	@Test
	public void getAvailableLocales() throws IOException {
		InitPluginManager.init();
		qc.getInfoStore().addValue(MMInfo.DESCRIPTION, Locale.GERMAN, "wf");
		qmc.getInfoStore().addValue(MMInfo.PROMPT, Locale.FRENCH, "qcf");
		choice1.getInfoStore().addValue(MMInfo.DESCRIPTION, Locale.CHINESE, "chinese?");
		assertEquals(
				new HashSet<>(Arrays.asList(Locale.FRENCH, Locale.CHINESE, Locale.GERMAN)),
				KnowledgeBaseUtils.getAvailableLocales(kb));
	}
}
