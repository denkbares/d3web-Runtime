package de.d3web.core.manage.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;

/**
 * Tests the correct behaviour of the KnowledgeBaseManagement class and
 * contained methods
 * 
 * @author Martina Freiberg
 * @created 02.09.2010
 */
public class KnowledgeBaseManagementTest {


	@Test
	public void testFindValue_MCValSingleVal() {

	}

	@Test
	public void testFindValue_MCValRealMC() {

	}

	@Test
	public void testFindValue_ChoiceVal() {
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
		QuestionNum qn = new QuestionNum("Please enter: ");
		assertThat(KnowledgeBaseManagement.createInstance().findValue(qn, numValInput),
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
		QuestionText qt = new QuestionText("Please enter: ");
		assertThat(KnowledgeBaseManagement.createInstance().findValue(qt, textValInput),
				is(textToGet));
	}

	/**
	 * Test findValue: DateValue
	 * 
	 * @created 02.09.2010
	 */
	@Test
	public void testFindValue_DateVal() {

		/*
		 * Value dateToGet = null; final DateFormat format = new
		 * SimpleDateFormat("yyyy-MM-dd-HH-mm-ss"); try { Date date =
		 * format.parse("2010-09-02-12-13-30"); dateToGet = new DateValue(date);
		 * } catch (ParseException e) { e.printStackTrace(); } String
		 * dateValInput = "2010-09-02-12-13-30"; QuestionDate qd = new
		 * QuestionDate("Please enter: ");
		 * assertThat(KnowledgeBaseManagement.createInstance().findValue(qd,
		 * dateValInput), is(dateToGet));
		 */
	}

	@Test
	public void testFindValue_complete() {
	}

}
