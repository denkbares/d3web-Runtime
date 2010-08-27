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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Unit tests for {@link AbstractAbnormality}
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 26.08.2010
 */
public class AbstractAbnormalityTest {

	KnowledgeBaseManagement kbm;
	KnowledgeBase kb;

	AbstractAbnormality abstractAbnormality;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kbm = KnowledgeBaseManagement.createInstance();
		kb = kbm.getKnowledgeBase();
		// the AbstractAbnormality instance Under Test
		abstractAbnormality = new AbstractAbnormality() {

			@Override
			public double getValue(Value ans) {
				return AbstractAbnormality.A2; // some hard-coded default value
												// for this test
			}
		};
	}

	/**
	 * Test method for {@link de.d3web.shared.AbstractAbnormality#convertConstantStringToValue(java.lang.String)}.
	 */
	@Test
	public void testConvertConstantStringToValue() {
		assertThat(AbstractAbnormality.convertConstantStringToValue("A0"),
				is(AbstractAbnormality.A0));
		assertThat(AbstractAbnormality.convertConstantStringToValue("A1"),
				is(AbstractAbnormality.A1));
		assertThat(AbstractAbnormality.convertConstantStringToValue("A2"),
				is(AbstractAbnormality.A2));
		assertThat(AbstractAbnormality.convertConstantStringToValue("A3"),
				is(AbstractAbnormality.A3));
		assertThat(AbstractAbnormality.convertConstantStringToValue("A4"),
				is(AbstractAbnormality.A4));
		assertThat(AbstractAbnormality.convertConstantStringToValue("A5"),
				is(AbstractAbnormality.A5));
		assertThat(AbstractAbnormality.convertConstantStringToValue("XX"),
				is(AbstractAbnormality.A0));
	}

	/**
	 * Test method for {@link de.d3web.shared.AbstractAbnormality#convertValueToConstantString(double)}.
	 */
	@Test
	public void testConvertValueToConstantString() {
		assertThat(AbstractAbnormality.convertValueToConstantString(0.0425), is("A0"));
		assertThat(AbstractAbnormality.convertValueToConstantString(0.111), is("A1"));
		assertThat(AbstractAbnormality.convertValueToConstantString(0.187), is("A2"));
		assertThat(AbstractAbnormality.convertValueToConstantString(0.399), is("A3"));
		assertThat(AbstractAbnormality.convertValueToConstantString(0.874), is("A4"));
		assertThat(AbstractAbnormality.convertValueToConstantString(1.311), is("A5"));
	}

	/**
	 * Test method for {@link AbstractAbnormality#getQuestion()} and
	 * {@link AbstractAbnormality#setQuestion(de.d3web.core.knowledge.terminology.Question)}
	 * .
	 */
	@Test
	public void testGetSetRemoveQuestion() {
		// If no question was set, getQuestion() should return null
		assertThat(abstractAbnormality.getQuestion(), is(equalTo(null)));
		// now create a new Question and set it on the abnormality
		QuestionText questionText = new QuestionText("questionText");
		abstractAbnormality.setQuestion(questionText);
		// validate, that getQuestion() provides the question
		assertThat(abstractAbnormality.getQuestion(), is(equalTo((Question) questionText)));
		// after removing the question from the abnormality, getQuestion()
		// should again return null
		abstractAbnormality.remove();
		assertThat(abstractAbnormality.getQuestion(), is(equalTo(null)));
	}

	/**
	 * Test method for {@link de.d3web.shared.AbstractAbnormality#getId()}.
	 */
	@Test
	public void testGetId() {
		// set the Question for an abnormality and assert that getId() returns
		// the correct ID (prepended with "A_")
		assertThat(abstractAbnormality.getId(), is(equalTo(null)));
		QuestionText questionText = new QuestionText("questionText");
		abstractAbnormality.setQuestion(questionText);
		assertThat(abstractAbnormality.getId(), is(equalTo("A_questionText")));
	}

	/**
	 * Test method for {@link de.d3web.shared.AbstractAbnormality#getProblemsolverContext()}.
	 */
	@Test
	public void testGetProblemsolverContext() {
		// Assure that getProblemsolverContext() returns a class which is
		// assignable from PSMethod
		assertThat(PSMethod.class.isAssignableFrom(abstractAbnormality.getProblemsolverContext()),
				is(true));
	}

	/**
	 * Test method for {@link de.d3web.shared.AbstractAbnormality#isUsed(de.d3web.core.session.Session)}.
	 */
	@Test
	public void testIsUsed() {
		// isUsed(Session) returns true in every case
		Session session = SessionFactory.createSession(kb);
		assertThat(abstractAbnormality.isUsed(session), is(true));
	}

	/**
	 * Test method for {@link de.d3web.shared.AbstractAbnormality#getAbnormality(de.d3web.core.knowledge.terminology.Question, de.d3web.core.session.Value)}.
	 */
	@Test
	public void testGetAbnormality() {
		QuestionChoice questionOC = kbm.createQuestionOC("questionYN", kb.getRootQASet(), new String[]{"yes","no"});
		ChoiceValue yes = new ChoiceValue(kbm.findChoice(questionOC, "yes"));
		// before setting the abnormality for this question, the
		// default-abnormality A5 should be returned:
		assertThat(AbstractAbnormality.getAbnormality(questionOC, yes), is(AbstractAbnormality.A5));
		// now set the question for the abnormality...
		abstractAbnormality.setQuestion(questionOC);
		// ...and retrieve the abnormality again:
		// It should be A2 (set in constructor)
		assertThat(AbstractAbnormality.getAbnormality(questionOC, yes), is(AbstractAbnormality.A2));
	}
}
