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

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.ValueFactory;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;

public class ValueFactoryTest {

	private Choice choice1;
	private Choice choice2;
	private KnowledgeBase kb;
	private QuestionMC qmc;
	private QuestionOC qoc;
	private QuestionNum qnum;
	private QuestionText qtext;
	private QuestionDate qdate;

	@Before
	public void setUp() {
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
	}

	@Test
	public void createValue() {
		assertEquals(new ChoiceValue(choice1), ValueFactory.createValue(qoc, "choice1"));
		assertEquals(new MultipleChoiceValue(new ChoiceID(choice1), new ChoiceID(choice2)),
				ValueFactory.createValue(qmc, "choice1", new ChoiceValue(choice2)));
		assertEquals(Unknown.getInstance(),
				ValueFactory.createValue(qoc, Unknown.getInstance().getValue().toString()));
		assertEquals(new NumValue(4), ValueFactory.createValue(qnum, "4"));
		assertEquals(new TextValue("abc"), ValueFactory.createValue(qtext, "abc"));
		Date date = new Date();
		String dateString = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SS").format(date);
		assertEquals(new DateValue(date), ValueFactory.createValue(qdate, dateString));
	}

	@Test
	public void createQuestionChoiceValue() {
		assertEquals(new ChoiceValue(choice1),
				ValueFactory.createQuestionChoiceValue(qoc, "choice1"));
		assertEquals(new MultipleChoiceValue(new ChoiceID(choice1), new ChoiceID(choice2)),
				ValueFactory.createQuestionChoiceValue(qmc, "choice1", new ChoiceValue(choice2)));
	}

	@Test
	public void getID_or_Value() {
		assertEquals("choice1", ValueFactory.getID_or_Value(new ChoiceValue(choice1)));
		assertEquals(Unknown.UNKNOWN_ID,
				ValueFactory.getID_or_Value(Unknown.getInstance()));
		assertEquals(UndefinedValue.UNDEFINED_ID,
				ValueFactory.getID_or_Value(UndefinedValue.getInstance()));
		assertEquals("4.0", ValueFactory.getID_or_Value(new NumValue(4)));
	}
}
