/*
 * Copyright (C) 2010 denkbares GmbH, Würzburg, Germany
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

package de.d3web.shared;

import org.junit.Before;
import org.junit.Test;

import com.denkbares.plugin.test.InitPluginManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.abnormality.Abnormality;
import de.d3web.core.knowledge.terminology.info.abnormality.AbnormalityUtils;
import de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.values.ChoiceValue;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link AbnormalityUtils}
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 26.08.2010
 */
public class AbstractAbnormalityTest {

	KnowledgeBase kb;

	DefaultAbnormality abstractAbnormality;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		// the AbstractAbnormality instance Under Test
		abstractAbnormality = new DefaultAbnormality();
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.knowledge.terminology.info.abnormality.AbnormalityUtils#convertConstantStringToValue(java.lang.String)}
	 * .
	 */
	@Test
	public void testConvertConstantStringToValue() {
		assertThat(AbnormalityUtils.convertConstantStringToValue("A0"),
				is(Abnormality.A0));
		assertThat(AbnormalityUtils.convertConstantStringToValue("A1"),
				is(Abnormality.A1));
		assertThat(AbnormalityUtils.convertConstantStringToValue("A2"),
				is(Abnormality.A2));
		assertThat(AbnormalityUtils.convertConstantStringToValue("A3"),
				is(Abnormality.A3));
		assertThat(AbnormalityUtils.convertConstantStringToValue("A4"),
				is(Abnormality.A4));
		assertThat(AbnormalityUtils.convertConstantStringToValue("A5"),
				is(Abnormality.A5));
		assertThat(AbnormalityUtils.convertConstantStringToValue("XX"),
				is(Abnormality.A0));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.knowledge.terminology.info.abnormality.AbnormalityUtils#convertValueToConstantString(double)}
	 * .
	 */
	@Test
	public void testConvertValueToConstantString() {
		assertThat(AbnormalityUtils.convertValueToConstantString(0.0425), is("A0"));
		assertThat(AbnormalityUtils.convertValueToConstantString(0.111), is("A1"));
		assertThat(AbnormalityUtils.convertValueToConstantString(0.187), is("A2"));
		assertThat(AbnormalityUtils.convertValueToConstantString(0.399), is("A3"));
		assertThat(AbnormalityUtils.convertValueToConstantString(0.874), is("A4"));
		assertThat(AbnormalityUtils.convertValueToConstantString(1.311), is("A5"));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.knowledge.terminology.info.abnormality.AbnormalityUtils#getAbnormality(de.d3web.core.knowledge.terminology.Question, de.d3web.core.session.Value)}
	 * .
	 */
	@Test
	public void testGetAbnormality() {
		QuestionChoice questionOC = new QuestionOC(kb.getRootQASet(), "questionYN", "yes", "no");
		ChoiceValue yes = new ChoiceValue(KnowledgeBaseUtils.findChoice(questionOC, "yes"));
		// before setting the abnormality for this question, the
		// default-abnormality A5 should be returned:
		assertThat(AbnormalityUtils.getAbnormality(questionOC, yes), is(Abnormality.A5));
		// now set the question for the abnormality...
		questionOC.getInfoStore().addValue(BasicProperties.DEFAULT_ABNORMALITY,
				abstractAbnormality);
		abstractAbnormality.addValue(yes, Abnormality.A2);
		// ...and retrieve the abnormality again:
		// It should be A2 (set in constructor)
		assertThat(AbnormalityUtils.getAbnormality(questionOC, yes), is(Abnormality.A2));
	}
}
