/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.d3web.persistence.tests;

import java.io.IOException;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

import de.d3web.abstraction.ActionSetQuestion;
import de.d3web.core.io.KnowledgeBasePersistence;
import de.d3web.core.io.Persistence;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.fragments.actions.QuestionSetterActionHandler;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.plugin.test.InitPluginManager;

/**
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 07.08.2013
 */
public class QuestionSetterActionTest {

	private KnowledgeBase kb;
	private QuestionSetterActionHandler questionSetterActionHandler;
	private ActionSetQuestion actionSetQuestion;
	private QuestionNum questionNum;
	private Persistence<KnowledgeBase> persistence;

	@Before
	public void setup() throws IOException {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		persistence = new KnowledgeBasePersistence(PersistenceManager.getInstance(), kb);
		questionNum = new QuestionNum(kb, "dummy");
		questionSetterActionHandler = new QuestionSetterActionHandler();
		actionSetQuestion = new ActionSetQuestion();
		actionSetQuestion.setQuestion(questionNum);
	}

	@Test
	public void testNum() throws IOException {
		double doubleValue = 34.2;
		actionSetQuestion.setValue(new NumValue(doubleValue));
		ActionSetQuestion asq = reload();
		Assert.assertTrue(asq.getValue() instanceof NumValue);
		Assert.assertEquals(doubleValue, ((NumValue) asq.getValue()).getDouble());
	}

	@Test
	public void testDate() throws IOException {
		Date dateValue = new Date();
		actionSetQuestion.setValue(new DateValue(dateValue));
		ActionSetQuestion asq = reload();
		Assert.assertTrue(asq.getValue() instanceof DateValue);
		Assert.assertEquals(dateValue, ((DateValue) asq.getValue()).getDate());
	}

	@Test
	public void testText() throws IOException {
		String stringValue = "test";
		actionSetQuestion.setValue(new TextValue(stringValue));
		ActionSetQuestion asq = reload();
		Assert.assertTrue(asq.getValue() instanceof TextValue);
		Assert.assertEquals(stringValue, ((TextValue) asq.getValue()).getText());
	}

	@Test
	public void testMC() throws IOException {
		ChoiceID c1 = new ChoiceID("c1");
		ChoiceID c2 = new ChoiceID("c2");
		actionSetQuestion.setValue(new MultipleChoiceValue(c1, c2));
		ActionSetQuestion asq = reload();
		Assert.assertTrue(asq.getValue() instanceof MultipleChoiceValue);
		MultipleChoiceValue mcv = (MultipleChoiceValue) asq.getValue();
		Assert.assertTrue(mcv.getChoiceIDs().contains(c1));
		Assert.assertTrue(mcv.getChoiceIDs().contains(c2));
		Assert.assertEquals(2, mcv.getChoiceIDs().size());
	}

	@Test
	public void testOC() throws IOException {
		QuestionChoice questionChoice = new QuestionOC(kb, "dummy2");
		ChoiceID c1 = new ChoiceID("c1");
		questionChoice.addAlternative(new Choice(c1.getText()));
		actionSetQuestion.setQuestion(questionChoice);
		actionSetQuestion.setValue(new ChoiceValue(c1));
		ActionSetQuestion asq = reload();
		Assert.assertEquals(questionChoice, asq.getQuestion());
		Assert.assertTrue(asq.getValue() instanceof ChoiceValue);
		Assert.assertEquals(c1, ((ChoiceValue) asq.getValue()).getChoiceID());
	}

	private ActionSetQuestion reload() throws IOException {
		Assert.assertTrue(questionSetterActionHandler.canWrite(actionSetQuestion));
		Element element = questionSetterActionHandler.write(actionSetQuestion, persistence);
		Assert.assertTrue(questionSetterActionHandler.canRead(element));
		Object reloadedValue = questionSetterActionHandler.read(element, persistence);
		Assert.assertTrue(reloadedValue instanceof ActionSetQuestion);
		ActionSetQuestion asq = (ActionSetQuestion) reloadedValue;
		return asq;
	}

}
