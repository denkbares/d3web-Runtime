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
package de.d3web.xcl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.xcl.inference.PSMethodXCL;

/**
 * Tests contra relations in combination with xcl (sprintgroup)
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 27.03.2012
 */
public class ContraRelationsTest {

	@Test
	public void test() throws IOException {
		InitPluginManager.init();
		KnowledgeBase kb = PersistenceManager.getInstance().load(
				new File("src/test/resources/xcl JUnit.d3web"));
		Session session = SessionFactory.createSession(kb);
		PSMethodXCL xcl = session.getPSMethodInstance(PSMethodXCL.class);
		Question q1 = kb.getManager().searchQuestion("Frage1");
		Question q2 = kb.getManager().searchQuestion("Frage2");
		Question q3 = kb.getManager().searchQuestion("Frage3");
		Question q4 = kb.getManager().searchQuestion("Frage4");
		Assert.assertEquals(5, xcl.getUndiscriminatedSolutions(session).size());
		assertPositive(session, xcl, q1);
		assertPositive(session, xcl, q2);
		assertPositive(session, xcl, q3);
		assertZero(session, xcl, q4);
		Solution solution4 = kb.getManager().searchSolution(
				"Lösung4");
		InferenceTrace inferenceTrace4 = solution4.getKnowledgeStore().getKnowledge(
				XCLModel.KNOWLEDGE_KIND).getInferenceTrace(session);
		Solution solution2 = kb.getManager().searchSolution(
				"Lösung2");
		InferenceTrace inferenceTrace2 = solution2.getKnowledgeStore().getKnowledge(
				XCLModel.KNOWLEDGE_KIND).getInferenceTrace(session);
		Assert.assertEquals(0.0, inferenceTrace4.getSupport());
		Assert.assertEquals(0.0, inferenceTrace2.getSupport());
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(q1, new ChoiceValue(new ChoiceID("a1"))));
		Assert.assertEquals(3, xcl.getUndiscriminatedSolutions(session).size());
		assertZero(session, xcl, q1);
		assertPositive(session, xcl, q2);
		assertZero(session, xcl, q3);
		assertZero(session, xcl, q4);
		double supportBeforeFullfillingContraIndication4 = inferenceTrace4.getSupport();
		Assert.assertTrue(supportBeforeFullfillingContraIndication4 > 0.0);
		double supportBeforeFullfillingContraIndication2 = inferenceTrace2.getSupport();
		Assert.assertTrue(supportBeforeFullfillingContraIndication2 > 0.0);
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(q2, new ChoiceValue(new ChoiceID("a1"))));
		Assert.assertEquals(2, xcl.getUndiscriminatedSolutions(session).size());
		Assert.assertTrue(xcl.getUndiscriminatedSolutions(session).contains(
				kb.getManager().searchSolution(
						"Lösung1")));
		Assert.assertEquals(supportBeforeFullfillingContraIndication4, inferenceTrace4.getSupport());
		Assert.assertEquals(supportBeforeFullfillingContraIndication2, inferenceTrace2.getSupport());
		Assert.assertTrue(xcl.getUndiscriminatedSolutions(session).contains(
				solution4));
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(q2, new ChoiceValue(new ChoiceID("a2"))));
		Assert.assertEquals(2, xcl.getUndiscriminatedSolutions(session).size());
		Assert.assertTrue(xcl.getUndiscriminatedSolutions(session).contains(
				solution2));
		Assert.assertTrue(xcl.getUndiscriminatedSolutions(session).contains(
				solution4));
	}

	private void assertZero(Session session, PSMethodXCL xcl, Question q4) {
		Assert.assertEquals(0.0, xcl.getInformationGain(Arrays.asList(q4),
				xcl.getUndiscriminatedSolutions(session), session));
	}

	private void assertPositive(Session session, PSMethodXCL xcl, Question q1) {
		Assert.assertTrue(xcl.getInformationGain(Arrays.asList(q1),
				xcl.getUndiscriminatedSolutions(session), session) > 0);
	}

}
