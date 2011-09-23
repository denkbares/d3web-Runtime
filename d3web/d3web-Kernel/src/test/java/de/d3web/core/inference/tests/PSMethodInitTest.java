/*
 * Copyright (C) 2011 denkbares GmbH
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
package de.d3web.core.inference.tests;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;

import de.d3web.core.inference.PSMethodInit;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.QuestionZC;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Tests PSMethodInit
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 20.09.2011
 */
public class PSMethodInitTest {

	@Test
	public void testSessionStartup() throws Exception {
		InitPluginManager.init();
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		QuestionOC oc = new QuestionOC(kb.getRootQASet(), "oc");
		String choiceString = "choice1";
		Choice choice = new Choice(choiceString);
		oc.addAlternative(choice);
		oc.getInfoStore().addValue(BasicProperties.INIT, choiceString);
		QuestionOC ocUnknown = new QuestionOC(kb.getRootQASet(), "ocUnknown");
		ocUnknown.getInfoStore().addValue(BasicProperties.INIT, "unknown");
		QuestionMC mc = new QuestionMC(kb.getRootQASet(), "mc");
		String mcChoiceString = "mcChoice1";
		Choice mcChoice = new Choice(mcChoiceString);
		mc.addAlternative(mcChoice);
		String mcChoiceString2 = "mcChoice2";
		Choice mcChoice2 = new Choice(mcChoiceString2);
		mc.addAlternative(mcChoice2);
		mc.getInfoStore().addValue(BasicProperties.INIT, mcChoiceString + ";" + mcChoiceString2);
		QuestionDate date = new QuestionDate(kb.getRootQASet(), "date");
		date.getInfoStore().addValue(BasicProperties.INIT, "09.11.1989");
		QuestionText text = new QuestionText(kb.getRootQASet(), "text");
		String textAnswer = "Answer";
		text.getInfoStore().addValue(BasicProperties.INIT, textAnswer);
		QuestionNum num = new QuestionNum(kb.getRootQASet(), "num");
		num.getInfoStore().addValue(BasicProperties.INIT, "300");
		Session session = SessionFactory.createSession(kb);
		Blackboard blackboard = session.getBlackboard();
		Assert.assertEquals(choiceString, blackboard.getValue(oc).toString());
		MultipleChoiceValue mcvalue = (MultipleChoiceValue) blackboard.getValue(mc);
		Iterator<ChoiceID> iterator = mcvalue.getChoiceIDs().iterator();
		Assert.assertEquals(mcChoiceString, iterator.next().toString());
		Assert.assertEquals(mcChoiceString2, iterator.next().toString());
		Assert.assertFalse(iterator.hasNext());
		Date dateValue = (Date) blackboard.getValue(date).getValue();
		checkDate(dateValue, 1989, 11, 9);
		Assert.assertEquals(textAnswer, blackboard.getValue(text).getValue().toString());
		NumValue numValue = (NumValue) blackboard.getValue(num);
		Assert.assertEquals(300.0, numValue.getDouble());
		Assert.assertTrue(Unknown.assignedTo(blackboard.getValue(ocUnknown)));
	}

	private void checkDate(Date date, int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Assert.assertEquals(year, calendar.get(Calendar.YEAR));
		// january = 0...
		Assert.assertEquals(month - 1, calendar.get(Calendar.MONTH));
		Assert.assertEquals(day, calendar.get(Calendar.DAY_OF_MONTH));
	}

	public void checkTime(Date date, int hour, int minute, int second) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Assert.assertEquals(hour, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(minute, calendar.get(Calendar.MINUTE));
		Assert.assertEquals(second, calendar.get(Calendar.SECOND));
	}

	@Test
	public void testUtils() throws IOException {
		InitPluginManager.init();
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		QuestionDate date = new QuestionDate(kb.getRootQASet(), "date");
		DateValue dateValue = (DateValue) PSMethodInit.getValue(date, "2011-09-23");
		checkDate(dateValue.getDate(), 2011, 9, 23);
		dateValue = (DateValue) PSMethodInit.getValue(date, "2011-09-23 16:53:55");
		checkDate(dateValue.getDate(), 2011, 9, 23);
		checkTime(dateValue.getDate(), 16, 53, 55);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUtilErrorDate() throws IOException {
		InitPluginManager.init();
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		QuestionDate date = new QuestionDate(kb.getRootQASet(), "date");
		PSMethodInit.getValue(date, "2011 09 23");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUtilErrorZC() throws IOException {
		InitPluginManager.init();
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		QuestionZC zc = new QuestionZC(kb.getRootQASet(), "zc");
		PSMethodInit.getValue(zc, "foo");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUtilErrorNum() throws IOException {
		InitPluginManager.init();
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		QuestionNum num = new QuestionNum(kb.getRootQASet(), "num");
		PSMethodInit.getValue(num, "255a");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUtilErrorOC() throws IOException {
		InitPluginManager.init();
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		QuestionOC oc = new QuestionOC(kb.getRootQASet(), "oc");
		PSMethodInit.getValue(oc, "choice1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUtilErrorMC() throws IOException {
		InitPluginManager.init();
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		QuestionMC oc = new QuestionMC(kb.getRootQASet(), "mc");
		PSMethodInit.getValue(oc, "choice1");
	}
}
