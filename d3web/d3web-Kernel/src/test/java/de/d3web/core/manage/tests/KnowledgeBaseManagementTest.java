package de.d3web.core.manage.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;

/**
 * Tests the correct behaviour of the KnowledgeBaseUtils class and
 * contained methods
 * 
 * @author Martina Freiberg
 * @created 02.09.2010
 */
public class KnowledgeBaseManagementTest {

	/**
	 * Test findValue: MCValue as single value
	 * 
	 * @created 09.09.2010
	 */
	@Test
	public void testFindValue_MCValSingleVal() {
		Choice choice1 = new Choice("choice1");
		Choice choice2 = new Choice("choice2");
		List<Choice> choiceList = new LinkedList<Choice>();
		choiceList.add(choice1);
		Value mcValToFind = MultipleChoiceValue.fromChoices(choiceList);
		QuestionChoice qmc = new QuestionMC(new KnowledgeBase(), "Please enter: ");
		qmc.addAlternative(choice1);
		qmc.addAlternative(choice2);

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
	public void testFindValue_MCValRealMC() {

		Choice choice1 = new Choice("choice1");
		Choice choice2 = new Choice("choice2");
		List<Choice> choiceList = new LinkedList<Choice>();
		choiceList.add(choice1);
		choiceList.add(choice2);
		Value mcValToFind = MultipleChoiceValue.fromChoices(choiceList);
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		QuestionChoice qmc = new QuestionMC(kb, "Please enter: ");
		qmc.addAlternative(choice1);
		qmc.addAlternative(choice2);

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
	public void testFindValue_ChoiceVal() {

		// test oc question
		Choice choice1 = new Choice("choice1");
		Choice choice2 = new Choice("choice2");
		Value choiceValToFind1 = new ChoiceValue(choice1);
		Value choiceValToFind2 = new ChoiceValue(choice2);
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		QuestionChoice qc = new QuestionOC(kb, "Please enter: ");
		qc.addAlternative(choice1);
		qc.addAlternative(choice2);
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
	public void testFindValue_NumVal() {
		Value numToGet = new NumValue(1.0);
		String numValInput = "1.0";
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
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
	public void testFindValue_TextVal() {
		Value textToGet = new TextValue("My Text");
		String textValInput = "My Text";
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
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
	public void testFindValue_DateVal() {

		Value dateToGet = null;
		final DateFormat format = new
				SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		try {
			Date date =
					format.parse("2010-09-02-12-13-30");
			dateToGet = new DateValue(date);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		String dateValInput = "2010-09-02-12-13-30";
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		QuestionDate qd = new
				QuestionDate(kb, "Please enter: ");
		assertThat(KnowledgeBaseUtils.findValue(qd,
				dateValInput), is(dateToGet));
		dateValInput = "wrong date format";
		assertNull(KnowledgeBaseUtils.findValue(qd,
				dateValInput));
	}

	/**
	 * check the logic of the entire findValue Method, i.e., conditions
	 * undefined and unknown at the beginning
	 * 
	 * @created 09.09.2010
	 */
	@Test
	public void testFindValue_complete() {
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

}
