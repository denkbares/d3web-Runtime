/*
 * Copyright (C) 2020 denkbares GmbH, Germany
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

import com.denkbares.plugin.test.InitPluginManager;
import com.denkbares.utils.Java;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Value;
import de.d3web.core.session.ValueUtils;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ValueUtilsTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ValueUtilsTest.class);

	private Choice choice1;
	private Choice choice2;
	private Choice choice3;
	private QuestionMC qmc;
	private QuestionOC qoc;
	private QuestionYN qyn;
	private QuestionNum qnum;
	private QuestionText qtext;
	private QuestionDate qdate;
	private QuestionDate qdate2;

	@Before
	public void setUp() throws IOException {
		InitPluginManager.init();
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		choice1 = new Choice("choice1");
		choice2 = new Choice("choice2");
		choice3 = new Choice("choice3");
		qmc = new QuestionMC(kb.getRootQASet(), "qmc");
		qmc.addAlternative(choice1);
		qmc.addAlternative(choice2);
		qmc.addAlternative(choice3);
		qoc = new QuestionOC(kb.getRootQASet(), "qoc");
		qoc.addAlternative(choice1);
		qoc.addAlternative(choice2);
		qyn = new QuestionYN(kb.getRootQASet(), "qyn");
		qnum = new QuestionNum(kb.getRootQASet(), "qnum");
		qtext = new QuestionText(kb.getRootQASet(), "qtext");
		qdate = new QuestionDate(kb.getRootQASet(), "qdate");
		qdate2 = new QuestionDate(kb.getRootQASet(), "qdate2");
	}

	@Test
	public void createValue() {
		assertEquals(new ChoiceValue(choice1), ValueUtils.createValue(qoc, "choice1"));

		Value choice1 = ValueUtils.createQuestionValue(qmc, "choice1");
		ChoiceValue choice2 = new ChoiceValue(this.choice2);
		MultipleChoiceValue mcValue = MultipleChoiceValue.fromChoices(this.choice1, this.choice2);
		Value actualMcValue = ValueUtils.handleExistingValue(qmc, choice1, choice2);
		assertEquals(mcValue, actualMcValue);

		Value actualMcValue2 = ValueUtils.handleExistingValue(qmc, mcValue, mcValue);
		assertEquals(Unknown.getInstance(), actualMcValue2);

		assertEquals(new ChoiceValue(qyn.getAnswerChoiceYes()), ValueUtils.createValue(qyn, QuestionYN.YES_STRING));
		assertEquals(new ChoiceValue(qyn.getAnswerChoiceNo()), ValueUtils.createValue(qyn, QuestionYN.NO_STRING));

		assertEquals(new MultipleChoiceValue(new ChoiceID(this.choice2)), ValueUtils.handleExistingValue(qmc, choice1, mcValue));

		assertEquals(Unknown.getInstance(), ValueUtils.createValue(qoc, Unknown.getInstance().getValue().toString()));

		assertEquals(new NumValue(4), ValueUtils.createValue(qnum, "4"));

		assertEquals(new TextValue("abc"), ValueUtils.createValue(qtext, "abc"));

		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SS");
		String dateString = simpleDateFormat.format(date);
		assertEquals(new DateValue(date), ValueUtils.createValue(qdate, dateString));
	}

	@Test
	public void mergeValue() {
		MultipleChoiceValue mcValue12 = MultipleChoiceValue.fromChoices(this.choice1, this.choice2);
		MultipleChoiceValue mcValue23 = MultipleChoiceValue.fromChoices(this.choice2, this.choice3);
		MultipleChoiceValue mcValue31 = MultipleChoiceValue.fromChoices(this.choice3, this.choice1);

		// two occurrences of each, cancels out all
		MultipleChoiceValue merged1 = ValueUtils.mergeChoiceValuesXOR(qmc, mcValue12, mcValue23, mcValue31);
		assertEquals(MultipleChoiceValue.fromChoices(), merged1);

		// all canceled out except 1 and 2
		MultipleChoiceValue merged2 = ValueUtils.mergeChoiceValuesXOR(qmc, mcValue12, mcValue23, mcValue31, mcValue12);
		assertEquals(mcValue12, merged2);

		// first 3 values and following 2 each cancel out each other
		MultipleChoiceValue merged3 = ValueUtils.mergeChoiceValuesXOR(qmc, mcValue12, mcValue23, mcValue31, mcValue12, mcValue12);
		assertEquals(MultipleChoiceValue.fromChoices(), merged3);

		// normal OR merge...
		MultipleChoiceValue mergedOR = ValueUtils.mergeChoiceValuesOR(qmc, mcValue12, mcValue23, mcValue31, mcValue12, mcValue12);
		assertEquals(MultipleChoiceValue.fromChoices(choice1, choice2, choice3), mergedOR);
	}

	@Test
	public void createQuestionChoiceValue() {
		assertEquals(new ChoiceValue(choice1), ValueUtils.createQuestionChoiceValue(qoc, "choice1"));
		ChoiceValue choice2 = new ChoiceValue(this.choice2);
		Value choice1 = ValueUtils.createQuestionChoiceValue(qmc, "choice1");
		assertEquals(new MultipleChoiceValue(new ChoiceID(this.choice1), new ChoiceID(this.choice2)), ValueUtils.handleExistingValue(qmc, choice1, choice2));
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
		compareTimeZones("UTC", "UTC");
		compareTimeZones("UTC", "Utc");
		compareTimeZones("GMT", "GMT");
		compareTimeZones("GMT", "gmt");
		compareTimeZones("Europe/Berlin", "CET");
		compareTimeZones("Europe/Berlin", "CEST");
		compareTimeZones("Europe/Berlin", "cest");
		compareTimeZones("America/Los_Angeles", "PST");
		compareTimeZones("America/Los_Angeles", "PDT");
		compareTimeZones("America/Los_Angeles", "PDT");
		compareTimeZones("America/Los_Angeles", "pdT");
		compareTimeZones("America/Los_Angeles", "Pacific Standard Time");

		if (Java.getVersion() > 8) {
			if (System.getProperty("java.locale.providers") != null) {
				// If anyone is inclined to write a correct parser for the locale providers, this could be improved.
				LOGGER.warn("Skipping certain Timezone tests as a Java locale provider order has been set.");
			}
			else {
				compareTimeZones("ECT", "Central European Time");
				compareTimeZones("ECT", "Central European Summer Time");
			}
		}
		else {
			compareTimeZones("Europe/Berlin", "Central European Time");
			compareTimeZones("Europe/Berlin", "Central European Summer Time");
		}

		compareTimeZones("GMT-08:00", "GMT-08:00");
		compareTimeZones("GMT-08:00", "GMT-8:00");
		compareTimeZones("GMT+08:00", "GMT+08:00");
		compareTimeZones("GMT+08:00", "GMT+8:00");
	}

	private void compareTimeZones(String s1, String s2) {
		qdate2.getInfoStore().addValue(MMInfo.UNIT, s2);
		assertEquals(TimeZone.getTimeZone(s1), ValueUtils.getTimeZone(qdate2));
	}

	@Test
	public void createDateValue() {
		GregorianCalendar calendar = new GregorianCalendar(2015, 3, 15, 20, 0); // april is month 3
		Date date = calendar.getTime();
		DateValue dateValue;
		assertEquals(date, ValueUtils.createDateValue(qdate, "2015-04-15 20:00").getDate());

		calendar = new GregorianCalendar(2015, 3, 15, 20, 0); // april is month 3
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		date = calendar.getTime();

		assertEquals(date, ValueUtils.createDateValue(qdate, "2015-04-15 20:00 UTC").getDate());

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "UTC");
		assertEquals(date, ValueUtils.createDateValue(qdate2, "2015-04-15 20:00").getDate());

		assertEquals(date, ValueUtils.createDateValue(qdate, "2015-04-15 22:00 CEST").getDate());

		assertEquals(date, ValueUtils.createDateValue(qdate, "2015-04-15 22:00 CET").getDate());

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "CEST");
		assertEquals(date, ValueUtils.createDateValue(qdate2, "2015-04-15 22:00").getDate());

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "CET");
		assertEquals(date, ValueUtils.createDateValue(qdate2, "2015-04-15 22:00").getDate());

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "PDT");
		assertEquals(date, ValueUtils.createDateValue(qdate2, "2015-04-15 13:00").getDate());

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "PST");
		assertEquals(date, ValueUtils.createDateValue(qdate2, "2015-04-15 13:00").getDate());

		// test GMT+-X format

		assertEquals(date, ValueUtils.createDateValue(qdate, "2015-04-15 22:00 GMT+2:00").getDate());

		assertEquals(date, ValueUtils.createDateValue(qdate, "2015-04-15 22:00 GMT+02:00").getDate());

		assertEquals(date, ValueUtils.createDateValue(qdate, "2015-04-16 1:00 GMT+5:00").getDate());

		assertEquals(date, ValueUtils.createDateValue(qdate, "2015-04-16 1:00 GMT+05:00").getDate());

		assertEquals(date, ValueUtils.createDateValue(qdate, "2015-04-16 07:00 GMT+11:00").getDate());

		assertEquals(date, ValueUtils.createDateValue(qdate, "2015-04-16 6:00 GMT+10:00").getDate());

		assertEquals(date, ValueUtils.createDateValue(qdate, "2015-04-15 09:00 GMT-11:00").getDate());

		assertEquals(date, ValueUtils.createDateValue(qdate, "2015-04-15 10:00 GMT-10:00").getDate());

		assertEquals(date, ValueUtils.createDateValue(qdate, "2015-04-15 19:00 GMT-1:00").getDate());

		assertEquals(date, ValueUtils.createDateValue(qdate, "2015-04-15 19:00 GMT-01:00").getDate());

		// try some not summer/day-light-saving dates

		calendar = new GregorianCalendar(2015, 0, 1, 20, 0); // april is month 3
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		date = calendar.getTime();

		assertEquals(date, ValueUtils.createDateValue(qdate, "2015-01-01 20:00 UTC").getDate());

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "UTC");
		assertEquals(date, ValueUtils.createDateValue(qdate2, "2015-01-01 20:00").getDate());

		assertEquals(date, ValueUtils.createDateValue(qdate, "2015-01-01 21:00 CEST").getDate());

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "CEST");
		assertEquals(date, ValueUtils.createDateValue(qdate2, "2015-01-01 21:00").getDate());

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "CET");
		assertEquals(date, ValueUtils.createDateValue(qdate2, "2015-01-01 21:00").getDate());

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "PST");
		assertEquals(date, ValueUtils.createDateValue(qdate2, "2015-01-01 12:00").getDate());

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "pst");
		assertEquals(date, ValueUtils.createDateValue(qdate2, "2015-01-01 12:00").getDate());

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "PDT");
		assertEquals(date, ValueUtils.createDateValue(qdate2, "2015-01-01 12:00").getDate());

		// try some time zones on the southern hemisphere

		calendar = new GregorianCalendar(2015, 3, 15, 20, 0);
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		date = calendar.getTime();

		assertEquals(date, ValueUtils.createDateValue(qdate, "2015-04-16 8:00 NZST").getDate());

		assertEquals(date, ValueUtils.createDateValue(qdate, "2015-04-16 8:00 New Zealand Daylight Time").getDate());

		assertEquals(date, ValueUtils.createDateValue(qdate, "2015-04-16 8:00 new zealand daylight time").getDate());

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "NZST");
		assertEquals(date, ValueUtils.createDateValue(qdate2, "2015-04-16 8:00").getDate());

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "NZDT");
		assertEquals(date, ValueUtils.createDateValue(qdate2, "2015-04-16 8:00").getDate());

		// try summer / daylight-saving-time

		calendar = new GregorianCalendar(2015, 0, 1, 20, 0);
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		date = calendar.getTime();

		dateValue = ValueUtils.createDateValue(qdate, "2015-01-02 9:00 NZST");
		assertEquals(date, dateValue.getDate());

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "NZST");
		assertEquals(date, ValueUtils.createDateValue(qdate2, "2015-01-02 9:00").getDate());

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "NZDT");
		assertEquals(date, ValueUtils.createDateValue(qdate2, "2015-01-02 9:00").getDate());

		// compare time zone given in string and given as attribute
		compareStringAndAttributeTimeZone("2015-04-15 16:01:14", "CEST");

		compareStringAndAttributeTimeZone("2015-04-15 16:01:14", "CET");

		compareStringAndAttributeTimeZone("2015-04-16 8:01:14", "NZST");

		compareStringAndAttributeTimeZone("2015-04-16 8:01:14", "NZDT");
	}

	private void compareStringAndAttributeTimeZone(String date, String timeZone) {
		qdate2.getInfoStore().addValue(MMInfo.UNIT, timeZone);
		Date date1 = ValueUtils.createDateValue(date + " " + timeZone).getDate();
		Date date2 = ValueUtils.createDateValue(qdate2, date).getDate();
		assertEquals(date1, date2);
	}

	@Test
	public void getDateVerbalization() {
		Date date = ValueUtils.createDateValue("2015-04-15 14:00").getDate();

		String dateVerbalization = ValueUtils.getDateVerbalization(null, date, ValueUtils.TimeZoneDisplayMode.NEVER, false);
		assertEquals("2015-04-15 14:00:00.000", dateVerbalization);

		dateVerbalization = ValueUtils.getDateVerbalization(null, date, ValueUtils.TimeZoneDisplayMode.IF_NOT_DEFAULT, false);
		assertEquals("2015-04-15 14:00:00.000", dateVerbalization);

		dateVerbalization = ValueUtils.getDateVerbalization(null, date, ValueUtils.TimeZoneDisplayMode.IF_NOT_DEFAULT, true);
		assertEquals("2015-04-15 14:00", dateVerbalization);

		date = ValueUtils.createDateValue("2015-04-15 14:01:14").getDate();

		dateVerbalization = ValueUtils.getDateVerbalization(null, date, ValueUtils.TimeZoneDisplayMode.IF_NOT_DEFAULT, false);
		assertEquals("2015-04-15 14:01:14.000", dateVerbalization);

		dateVerbalization = ValueUtils.getDateVerbalization(null, date, ValueUtils.TimeZoneDisplayMode.IF_NOT_DEFAULT, true);
		assertEquals("2015-04-15 14:01:14", dateVerbalization);

		date = ValueUtils.createDateValue("2015-04-15 14:01:14").getDate();

		dateVerbalization = ValueUtils.getDateVerbalization(null, date, ValueUtils.TimeZoneDisplayMode.IF_NOT_DEFAULT, false);
		assertEquals("2015-04-15 14:01:14.000", dateVerbalization);

		dateVerbalization = ValueUtils.getDateVerbalization(null, date, ValueUtils.TimeZoneDisplayMode.IF_NOT_DEFAULT, true);
		assertEquals("2015-04-15 14:01:14", dateVerbalization);

		date = ValueUtils.createDateValue("2015-04-15 14:01:14 UTC").getDate();

		dateVerbalization = ValueUtils.getDateVerbalization(ValueUtils.getTimeZone("UTC"), date, ValueUtils.TimeZoneDisplayMode.ALWAYS);
		assertEquals("2015-04-15 14:01:14 UTC", dateVerbalization);

		TimeZone defTimeZone = TimeZone.getDefault();
		TimeZone.setDefault(ValueUtils.getTimeZone("CET"));
		dateVerbalization = ValueUtils.getDateVerbalization(ValueUtils.getTimeZone("UTC"), date, ValueUtils.TimeZoneDisplayMode.IF_NOT_DEFAULT);
		assertEquals("2015-04-15 14:01:14 UTC", dateVerbalization);

		TimeZone.setDefault(ValueUtils.getTimeZone("UTC"));
		dateVerbalization = ValueUtils.getDateVerbalization(ValueUtils.getTimeZone("UTC"), date, ValueUtils.TimeZoneDisplayMode.IF_NOT_DEFAULT);
		assertEquals("2015-04-15 14:01:14", dateVerbalization);
		TimeZone.setDefault(null);
		assertEquals(defTimeZone, TimeZone.getDefault());

		dateVerbalization = ValueUtils.getDateVerbalization(ValueUtils.getTimeZone("UTC"), date, ValueUtils.TimeZoneDisplayMode.NEVER);
		assertEquals("2015-04-15 14:01:14", dateVerbalization);

		dateVerbalization = ValueUtils.getDateVerbalization((TimeZone) null, date, ValueUtils.TimeZoneDisplayMode.IF_NOT_DEFAULT);
		assertTrue(dateVerbalization.matches("^2015-04-1\\d \\d\\d:01:14$"));

		dateVerbalization = ValueUtils.getDateVerbalization((TimeZone) null, date, ValueUtils.TimeZoneDisplayMode.ALWAYS);
		assertTrue(dateVerbalization.matches("^2015-04-1\\d \\d\\d:01:14 .+$"));

		dateVerbalization = ValueUtils.getDateVerbalization(ValueUtils.getTimeZone("Central European Time"), date, ValueUtils.TimeZoneDisplayMode.NEVER);
		assertEquals("2015-04-15 16:01:14", dateVerbalization); // CET will be CEST in summer time

		dateVerbalization = ValueUtils.getDateVerbalization(ValueUtils.getTimeZone("CET"), date, ValueUtils.TimeZoneDisplayMode.NEVER);
		assertEquals("2015-04-15 16:01:14", dateVerbalization); // CET will be CEST in summer time

		dateVerbalization = ValueUtils.getDateVerbalization(ValueUtils.getTimeZone("CET"), date, ValueUtils.TimeZoneDisplayMode.ALWAYS);
		assertEquals("2015-04-15 16:01:14 CEST", dateVerbalization); // CET will be CEST in summer time

		dateVerbalization = ValueUtils.getDateVerbalization(ValueUtils.getTimeZone("Central European Time"), date, ValueUtils.TimeZoneDisplayMode.ALWAYS);
		assertEquals("2015-04-15 16:01:14 CEST", dateVerbalization); // CET will be CEST in summer time

		dateVerbalization = ValueUtils.getDateVerbalization(ValueUtils.getTimeZone("CEST"), date, ValueUtils.TimeZoneDisplayMode.ALWAYS);
		assertEquals("2015-04-15 16:01:14 CEST", dateVerbalization);

		dateVerbalization = ValueUtils.getDateVerbalization(ValueUtils.getTimeZone("PST"), date, ValueUtils.TimeZoneDisplayMode.ALWAYS);
		assertEquals("2015-04-15 07:01:14 PDT", dateVerbalization); // PST will be PDT in summer time

		dateVerbalization = ValueUtils.getDateVerbalization(ValueUtils.getTimeZone("PDT"), date, ValueUtils.TimeZoneDisplayMode.ALWAYS);
		assertEquals("2015-04-15 07:01:14 PDT", dateVerbalization);

		dateVerbalization = ValueUtils.getDateVerbalization(ValueUtils.getTimeZone("NZST"), date, ValueUtils.TimeZoneDisplayMode.ALWAYS);
		assertEquals("2015-04-16 02:01:14 NZST", dateVerbalization);

		dateVerbalization = ValueUtils.getDateVerbalization(ValueUtils.getTimeZone("NZDT"), date, ValueUtils.TimeZoneDisplayMode.ALWAYS);
		assertEquals("2015-04-16 02:01:14 NZST", dateVerbalization);  // NZDT will be NZST in winter time

		date = ValueUtils.createDateValue("2015-01-01 14:01:14 UTC").getDate();

		dateVerbalization = ValueUtils.getDateVerbalization(ValueUtils.getTimeZone("CET"), date, ValueUtils.TimeZoneDisplayMode.ALWAYS);
		assertEquals("2015-01-01 15:01:14 CET", dateVerbalization);

		dateVerbalization = ValueUtils.getDateVerbalization(ValueUtils.getTimeZone("CEST"), date, ValueUtils.TimeZoneDisplayMode.ALWAYS);
		assertEquals("2015-01-01 15:01:14 CET", dateVerbalization); // CEST will be CET in winter time

		dateVerbalization = ValueUtils.getDateVerbalization(ValueUtils.getTimeZone("PST"), date, ValueUtils.TimeZoneDisplayMode.ALWAYS);
		assertEquals("2015-01-01 06:01:14 PST", dateVerbalization);

		dateVerbalization = ValueUtils.getDateVerbalization(ValueUtils.getTimeZone("Pacific Standard Time"), date, ValueUtils.TimeZoneDisplayMode.ALWAYS);
		assertEquals("2015-01-01 06:01:14 PST", dateVerbalization);

		dateVerbalization = ValueUtils.getDateVerbalization(ValueUtils.getTimeZone("PDT"), date, ValueUtils.TimeZoneDisplayMode.ALWAYS);
		assertEquals("2015-01-01 06:01:14 PST", dateVerbalization); // PDT will be PST in summer time

		dateVerbalization = ValueUtils.getDateVerbalization(ValueUtils.getTimeZone("Pacific Daylight Time"), date, ValueUtils.TimeZoneDisplayMode.ALWAYS);
		assertEquals("2015-01-01 06:01:14 PST", dateVerbalization);

		dateVerbalization = ValueUtils.getDateVerbalization(ValueUtils.getTimeZone("NZST"), date, ValueUtils.TimeZoneDisplayMode.ALWAYS);
		assertEquals("2015-01-02 03:01:14 NZDT", dateVerbalization); // NZST will be NZDT in summer time

		dateVerbalization = ValueUtils.getDateVerbalization(ValueUtils.getTimeZone("NZDT"), date, ValueUtils.TimeZoneDisplayMode.ALWAYS);
		assertEquals("2015-01-02 03:01:14 NZDT", dateVerbalization);

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "NZST");
		dateVerbalization = ValueUtils.getDateVerbalization(qdate2, new DateValue(date), ValueUtils.TimeZoneDisplayMode.ALWAYS);
		assertEquals("2015-01-02 03:01:14 NZDT", dateVerbalization);

		dateVerbalization = ValueUtils.getDateVerbalization(qdate2, new DateValue(date), ValueUtils.TimeZoneDisplayMode.ALWAYS, false);
		assertEquals("2015-01-02 03:01:14.000 NZDT", dateVerbalization);

		dateVerbalization = ValueUtils.getDateVerbalization(qdate2, date, ValueUtils.TimeZoneDisplayMode.ALWAYS);
		assertEquals("2015-01-02 03:01:14 NZDT", dateVerbalization);

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "NZDT");
		dateVerbalization = ValueUtils.getDateVerbalization(qdate2, date, ValueUtils.TimeZoneDisplayMode.ALWAYS);
		assertEquals("2015-01-02 03:01:14 NZDT", dateVerbalization);
	}

	@Test
	public void roundTrip() {
		Date date = ValueUtils.createDateValue("2015-04-15 14:01:14 UTC").getDate();

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "CET");
		String dateVerbalization = ValueUtils.getDateVerbalization(qdate2, date, ValueUtils.TimeZoneDisplayMode.NEVER);
		assertEquals("2015-04-15 16:01:14", dateVerbalization);
		Date date2 = ValueUtils.createDateValue(qdate2, dateVerbalization).getDate();
		assertEquals(date, date2);

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "CEST");
		dateVerbalization = ValueUtils.getDateVerbalization(qdate2, date, ValueUtils.TimeZoneDisplayMode.NEVER);
		assertEquals("2015-04-15 16:01:14", dateVerbalization);
		date2 = ValueUtils.createDateValue(qdate2, dateVerbalization).getDate();
		assertEquals(date, date2);

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "NZST");
		dateVerbalization = ValueUtils.getDateVerbalization(qdate2, date, ValueUtils.TimeZoneDisplayMode.NEVER);
		assertEquals("2015-04-16 02:01:14", dateVerbalization);
		date2 = ValueUtils.createDateValue(qdate2, dateVerbalization).getDate();
		assertEquals(date, date2);

		qdate2.getInfoStore().addValue(MMInfo.UNIT, "NZDT");
		dateVerbalization = ValueUtils.getDateVerbalization(qdate2, date, ValueUtils.TimeZoneDisplayMode.NEVER);
		assertEquals("2015-04-16 02:01:14", dateVerbalization);
		date2 = ValueUtils.createDateValue(qdate2, dateVerbalization).getDate();
		assertEquals(date, date2);
	}
}
