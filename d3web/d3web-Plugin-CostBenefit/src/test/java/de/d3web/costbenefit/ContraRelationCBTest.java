/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.costbenefit;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.interviewmanager.EmptyForm;
import de.d3web.core.session.interviewmanager.Form;
import de.d3web.core.session.interviewmanager.Interview;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.plugin.test.InitPluginManager;

/**
 * This test is based on de.d3web.xcl.ContraRelationsTest an tests the bath
 * calculation of it's {@link KnowledgeBase} (PSMethodCostBenefit is activated
 * manually in the KB)
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 19.04.2012
 */
public class ContraRelationCBTest {

	@Test
	public void test() throws IOException {
		InitPluginManager.init();
		KnowledgeBase kb = PersistenceManager.getInstance().load(
				new File("src/test/resources/xcl JUnit.d3web"));
		Session session = SessionFactory.createSession(kb);
		Interview interview = session.getInterview();
		Form nextForm = interview.nextForm();
		Assert.assertEquals("Start..", nextForm.getInterviewObject().getName());
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(nextForm.getInterviewObject(), new ChoiceValue(
						new Choice("los"))));
		nextForm = interview.nextForm();
		Assert.assertEquals("Frage1", nextForm.getInterviewObject().getName());
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(nextForm.getInterviewObject(), new ChoiceValue(
						new Choice("a1"))));
		nextForm = interview.nextForm();
		Assert.assertEquals("Frage2", nextForm.getInterviewObject().getName());
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(nextForm.getInterviewObject(), new ChoiceValue(
						new Choice("a1"))));
		nextForm = interview.nextForm();
		Assert.assertEquals(EmptyForm.getInstance(), nextForm);
	}
}
