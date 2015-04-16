/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.core.session.values.tests;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.ValueUtils;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.plugin.test.InitPluginManager;

import static org.junit.Assert.assertEquals;

public class ValueUtilsTest {

	private Choice choice1;
	private Choice choice2;
	private KnowledgeBase kb;
	private QuestionMC qmc;
	private QuestionOC qoc;
	private QuestionNum qnum;
	private QuestionText qtext;
	private QuestionDate qdate;
	private QuestionDate qdate2;

	@Before
	public void setUp() throws IOException {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		choice1 = new Choice("choice1");
		choice2 = new Choice("choice2");
		qmc = new QuestionMC(kb.getRootQASet(), "qmc");
		qmc.addAlternative(choice1);
		qmc.addAlternative(choice2);
		qoc = new QuestionOC(kb.getRootQASet(), "qoc");
		qoc.addAlternative(choice1);
		qoc.addAlternative(choice2);
		qnum = new QuestionNum(kb.getRootQASet(), "qnum");
		qtext = new QuestionText(kb.getRootQASet(), "qtext");
		qdate = new QuestionDate(kb.getRootQASet(), "qdate");
		qdate2 = new QuestionDate(kb.getRootQASet(), "qdate2");
	}

	@Test
	public void createValue() {
		assertEquals(new ChoiceValue(choice1), ValueUtils.createValue(qoc, "choice1"));
		assertEquals(new MultipleChoiceValue(new ChoiceID(choice1), new ChoiceID(choice2)),
				ValueUtils.createValue(qmc, "choice1", new ChoiceValue(choice2)));
		assertEquals(new MultipleChoiceValue(new ChoiceID(choice2)),
				ValueUtils.createValue(qmc, "choice1", new MultipleChoiceValue(new ChoiceID(
						choice1), new ChoiceID(choice2))));
		assertEquals(Unknown.getInstance(),
				ValueUtils.createValue(qoc, Unknown.getInstance().getValue().toString()));
		assertEquals(new NumValue(4), ValueUtils.createValue(qnum, "4"));
		assertEquals(new TextValue("abc"), ValueUtils.createValue(qtext, "abc"));
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SS");
		String dateString = simpleDateFormat.format(date);
		assertEquals(new DateValue(date), ValueUtils.createValue(qdate, dateString));
	}

	@Test
	public void createQuestionChoiceValue() {
		assertEquals(new ChoiceValue(choice1),
				ValueUtils.createQuestionChoiceValue(qoc, "choice1"));
		assertEquals(new MultipleChoiceValue(new ChoiceID(choice1), new ChoiceID(choice2)),
				ValueUtils.createQuestionChoiceValue(qmc, "choice1", new ChoiceValue(choice2)));
	}

	@Test
	public void getID_or_Value() {
		assertEquals("choice1", ValueUtils.getID_or_Value(new ChoiceValue(choice1)));
		assertEquals(Unknown.UNKNOWN_ID,
				ValueUtils.getID_or_Value(Unknown.getInstance()));
		assertEquals(UndefinedValue.UNDEFINED_ID,
				ValueUtils.getID_or_Value(UndefinedValue.getInstance()));
		assertEquals("4.0", ValueUtils.getID_or_Value(new NumValue(4)));
	}

	@Test
	public void getTimeZone() {
		TimeZone utc = TimeZone.getTimeZone("UTC");
		TimeZone utc1 = ValueUtils.getTimeZone("UTC");
		System.out.println(utc.getDisplayName());
		System.out.println(utc1.getDisplayName());
		assertEquals(utc, utc1);
	}

	@Test
	public void createDateValue() {
		GregorianCalendar calendar = new GregorianCalendar(2015, 3, 15, 20, 0); // april is month 3
		Date date = calendar.getTime();
		DateValue dateValue = ValueUtils.createDateValue(qdate, "2015-04-15 20:00");
		assertEquals(date, dateValue.getDate());

		calendar = new GregorianCalendar(2015, 3, 15, 20, 0); // april is month 3
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		date = calendar.getTime();

		dateValue = ValueUtils.createDateValue(qdate, "2015-04-15 20:00 UTC");
		assertEquals(date, dateValue.getDate());

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "UTC");
		dateValue = ValueUtils.createDateValue(qdate2, "2015-04-15 20:00");
		assertEquals(date, dateValue.getDate());

		dateValue = ValueUtils.createDateValue(qdate, "2015-04-15 22:00 CEST");
		assertEquals(date, dateValue.getDate());

		dateValue = ValueUtils.createDateValue(qdate, "2015-04-15 21:00 CET");
		assertEquals(date, dateValue.getDate());

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "CEST");
		dateValue = ValueUtils.createDateValue(qdate2, "2015-04-15 22:00");
		assertEquals(date, dateValue.getDate());

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "CET");
		dateValue = ValueUtils.createDateValue(qdate2, "2015-04-15 21:00");
		assertEquals(date, dateValue.getDate());

		// try some not summer/day-light-saving dates

		calendar = new GregorianCalendar(2015, 0, 1, 20, 0); // april is month 3
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		date = calendar.getTime();

		dateValue = ValueUtils.createDateValue(qdate, "2015-01-01 20:00 UTC");
		assertEquals(date, dateValue.getDate());

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "UTC");
		dateValue = ValueUtils.createDateValue(qdate2, "2015-01-01 20:00");
		assertEquals(date, dateValue.getDate());

		dateValue = ValueUtils.createDateValue(qdate, "2015-01-01 22:00 CEST");
		assertEquals(date, dateValue.getDate());

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "CEST");
		dateValue = ValueUtils.createDateValue(qdate2, "2015-01-01 22:00");
		assertEquals(date, dateValue.getDate());

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "CET");
		dateValue = ValueUtils.createDateValue(qdate2, "2015-01-01 21:00");
		assertEquals(date, dateValue.getDate());


		// try some time zones on the southern hemisphere

		calendar = new GregorianCalendar(2015, 3, 15, 20, 0);
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		date = calendar.getTime();

		dateValue = ValueUtils.createDateValue(qdate, "2015-04-16 8:00 NZST");
		assertEquals(date, dateValue.getDate());

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "NZST");
		dateValue = ValueUtils.createDateValue(qdate2, "2015-04-16 8:00");
		assertEquals(date, dateValue.getDate());


		qdate2.getInfoStore().addValue(MMInfo.UNIT, "NZDT");
		dateValue = ValueUtils.createDateValue(qdate2, "2015-04-16 9:00");
		assertEquals(date, dateValue.getDate());

		// try summer / daylight-saving-time

		calendar = new GregorianCalendar(2015, 0, 1, 20, 0);
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		date = calendar.getTime();

		dateValue = ValueUtils.createDateValue(qdate, "2015-01-02 8:00 NZST");
		assertEquals(date, dateValue.getDate());

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "NZST");
		dateValue = ValueUtils.createDateValue(qdate2, "2015-01-02 8:00");
		assertEquals(date, dateValue.getDate());


		qdate2.getInfoStore().addValue(MMInfo.UNIT, "NZDT");
		dateValue = ValueUtils.createDateValue(qdate2, "2015-01-02 9:00");
		assertEquals(date, dateValue.getDate());

	}
}
