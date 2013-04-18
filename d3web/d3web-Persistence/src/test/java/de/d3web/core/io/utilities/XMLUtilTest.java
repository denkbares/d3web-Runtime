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
package de.d3web.core.io.utilities;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.InfoStore;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.core.utilities.Triple;
import de.d3web.plugin.test.InitPluginManager;

/**
 * 
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 18.04.2013
 */
public class XMLUtilTest {

	private Choice choice1;
	private Choice choice2;
	private KnowledgeBase kb;
	private QuestionChoice qmc;
	private QuestionChoice qoc;

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

	}

	@Test
	public void getValue() throws IOException {
		assertEquals("choice1", XMLUtil.getValue(qoc, new ChoiceValue(choice1)));
		assertEquals(Unknown.UNKNOWN_ID, XMLUtil.getValue(null, Unknown.getInstance()));
		MultipleChoiceValue mcchoice = new MultipleChoiceValue(
				new ChoiceID(choice1), new ChoiceID(choice2));
		assertEquals(ChoiceID.encodeChoiceIDs(mcchoice.getChoiceIDs()),
				XMLUtil.getValue(null, mcchoice));
		assertEquals("4.0", XMLUtil.getValue(null, new NumValue(4)));
		assertEquals("test", XMLUtil.getValue(null, new TextValue("test")));
	}

	@Test(expected = IOException.class)
	public void getValueException() throws IOException {
		XMLUtil.getValue(null, "provoke exception");
	}

	@Test
	public void getPrimitiveValue() throws IOException {
		assertEquals("text", XMLUtil.getPrimitiveValue("text", String.class.getName()));
		assertEquals(12512, XMLUtil.getPrimitiveValue("12512", Integer.class.getName()));
		assertEquals(125.12d, XMLUtil.getPrimitiveValue("125.12", Double.class.getName()));
		assertEquals(true, XMLUtil.getPrimitiveValue("true", Boolean.class.getName()));
		assertEquals(new URL("http://www,d3web.de"),
				XMLUtil.getPrimitiveValue("http://www,d3web.de", URL.class.getName()));
		assertEquals(12.512f, XMLUtil.getPrimitiveValue("12.512", Float.class.getName()));
	}

	@Test(expected = IOException.class)
	public void getPrimitiveValueException() throws IOException {
		XMLUtil.getPrimitiveValue(null, "provoke exception");
	}

	@Test
	public void sortEntries() throws IOException {
		InitPluginManager.init();
		Triple<Property<?>, Locale, Object> triple1 = new Triple<Property<?>, Locale, Object>(
				MMInfo.PROMPT, InfoStore.NO_LANGUAGE, "424");
		Triple<Property<?>, Locale, Object> triple2 = new Triple<Property<?>, Locale, Object>(
				MMInfo.PROMPT, Locale.GERMAN, "34");
		Triple<Property<?>, Locale, Object> triple3 = new Triple<Property<?>, Locale, Object>(
				MMInfo.PROMPT, Locale.GERMAN, "asd");
		Triple<Property<?>, Locale, Object> triple4 = new Triple<Property<?>, Locale, Object>(
				MMInfo.PROMPT, Locale.ENGLISH, "asw");
		Triple<Property<?>, Locale, Object> triple5 = new Triple<Property<?>, Locale, Object>(
				MMInfo.PROMPT, Locale.GERMAN, null);
		Triple<Property<?>, Locale, Object> triple6 = new Triple<Property<?>, Locale, Object>(
				MMInfo.PROMPT, Locale.ENGLISH, null);
		@SuppressWarnings("unchecked")
		List<Triple<Property<?>, Locale, Object>> expected = Arrays.asList(triple2, triple3,
				triple5, triple4, triple6, triple1);
		@SuppressWarnings("unchecked")
		List<Triple<Property<?>, Locale, Object>> triples = Arrays.asList(triple1, triple2,
				triple3, triple4, triple5, triple6);
		List<Triple<Property<?>, Locale, Object>> sortedTriples = XMLUtil.sortEntries(triples);
		assertEquals(expected, sortedTriples);
	}

}
