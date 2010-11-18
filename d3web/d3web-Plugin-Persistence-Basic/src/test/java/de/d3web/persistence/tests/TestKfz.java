/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.io.BasicPersistenceHandler;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.DummyProgressListener;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.interviewmanager.EmptyForm;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Test class for the Loading and executing a kfz-kb from an XML file Creation
 * date: (08.09.2000 15:41:53)
 * 
 * @author bates
 */
public class TestKfz {

	private static KnowledgeBase kb = null;
	private static URL kbURL = TestKfz.class.getClassLoader().getResource("Kfz2K.xml");

	/**
	 * Method for instantiating necessary objects Creation date: (08.09.2000
	 * 16:15:09)
	 * 
	 * @throws IOException
	 */
	@Before
	public void setUp() throws IOException {
		InitPluginManager.init();
		BasicPersistenceHandler ph = new BasicPersistenceHandler();
		kb = new KnowledgeBase();
		ph.read(kb, kbURL.openStream(), new DummyProgressListener());
	}

	/**
	 * Assures that every type of question can be set and retrieved. Creation
	 * date: (08.09.2000 16:11:48)
	 * 
	 * TODO Ochlast: No assert(), should be refactored
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCount() throws IOException {
		File file = new File("target/kbs");
		if (!file.isDirectory()) {
			file.mkdir();
		}
		PersistenceManager.getInstance().save(kb, new File("target/kbs/test2.jar"));
		Session session = SessionFactory.createSession(kb);

		QuestionNum Mf5 = (QuestionNum) kb.searchQuestion("Mf5");
		QuestionMC Mf7 = (QuestionMC) kb.searchQuestion("Mf7");
		KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance(session.getKnowledgeBase());

		Choice Mf7a1 = kbm.findChoice(Mf7, "Mf7a1");
		Choice Mf7a2 = kbm.findChoice(Mf7, "Mf7a2");
		Choice[] choices = new Choice[] {
				Mf7a1, Mf7a2 };
		List<Choice> values = new ArrayList<Choice>(choices.length);
		for (Choice choice : choices) {
			values.add(choice);
		}
		MultipleChoiceValue mcv = MultipleChoiceValue.fromChoices(values);
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, Mf7,
						mcv, PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));
		Value value = session.getBlackboard().getValue(Mf5);
		if (value == null) {
			System.out.println("(1) --> NULL!!!!");
		}
		else {
			System.out.println("(1) --> Mf55 : " + value.getValue());
		}

		System.out.println("---");
	}

	/**
	 * Assures that every type of question can be set and retrieved. Creation
	 * date: (08.09.2000 16:11:48)
	 */
	@Test
	public void testFormulaSchema() {
		Session session = SessionFactory.createSession(kb);

		/*---------------------------------------------- */

		QuestionNum Mf5 = (QuestionNum) kb.searchQuestion("Mf5");
		QuestionNum Mf6 = (QuestionNum) kb.searchQuestion("Mf6");
		QuestionOC Msi4 = (QuestionOC) kb.searchQuestion("Msi4");

		NumValue Mf5Value = new NumValue(new Double(10));
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, Mf5,
						Mf5Value, PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		NumValue Mf6Value = new NumValue(new Double(10));
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, Mf6,
						Mf6Value, PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance(session.getKnowledgeBase());
		Choice ratingNormal = kbm.findChoice(Msi4, "Msi4a1");
		ChoiceValue ratingNormalValue = new ChoiceValue(ratingNormal);

		System.out.println("(1) --> Msi4: " + session.getBlackboard().getValue(Msi4));

		assertEquals("Error with formula (1)", ratingNormalValue, session.getBlackboard().getValue(
				Msi4));

		// This is exactly the border ((Mf6-Mf5)/Mf5)*100 = 10
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, Mf6,
						new NumValue(new Double(11)), PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));
		Choice ratingHigh = kbm.findChoice(Msi4, "Msi4a2");
		ChoiceValue ratingHighValue = new ChoiceValue(ratingHigh);
		System.out.println("(2) --> Msi4: " + session.getBlackboard().getValue(Msi4));
		assertEquals("Error with formula (2)", ratingHighValue, session.getBlackboard().getValue(
				Msi4));

		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, Mf6,
						new NumValue(new Double(15)), PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));
		System.out.println("(4) --> Msi4: " + session.getBlackboard().getValue(Msi4));
		Choice ratingVeryHigh = kbm.findChoice(Msi4, "Msi4a3");
		ChoiceValue ratingVeryHighValue = new ChoiceValue(ratingVeryHigh);

		assertEquals("Error with formula (4)", ratingVeryHighValue,
				session.getBlackboard().getValue(Msi4));

		// user sets the value to 19.5 (user overrides all other values)
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, Msi4,
						new NumValue(new Double(19.5)), PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));
		System.out.println("(3) --> Msi4: " + session.getBlackboard().getValue(Msi4));
		assertEquals("Error with formula (3)", ratingHighValue, session.getBlackboard().getValue(
				Msi4));
	}

	/**
	 * Assures that every type of question can be set and retrieved. Creation
	 * date: (08.09.2000 16:11:48)
	 */
	@Test
	public void testNumericExpression() {
		Session session = SessionFactory.createSession(kb);

		/*----------------------------------------------
		 */

		QuestionNum Mf58 = (QuestionNum) kb.searchQuestion("Mf58");
		QuestionNum Mf6 = (QuestionNum) kb.searchQuestion("Mf6");
		QuestionOC Mf4 = (QuestionOC) kb.searchQuestion("Mf4");
		KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance(session.getKnowledgeBase());
		Choice Mf4a1 = kbm.findChoice(Mf4, "Mf4a1");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, Mf4,
						new ChoiceValue(Mf4a1), PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));
		//
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, Mf6,
						new NumValue(new Double(10)), PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));
		Value Mf58Value = session.getBlackboard().getValue(Mf58);
		if (Mf58Value == null) {
			System.out.println("(1) --> NULL!!!!");
		}
		else {
			System.out.println("(1) --> Mf58: " + Mf58Value.getValue());
		}
		// assertTrue("Error with formula (1)",
		// ratingNormal == Msi4.getValue(session).get(0));

		System.out.println("---");
	}

	/**
	 * Assures that every type of question can be set and retrieved. Creation
	 * date: (08.09.2000 16:11:48)
	 */
	@Test
	public void testSetValue() {
		Session session = SessionFactory.createSession(kb);

		QuestionOC questionOC = (QuestionOC) kb.searchQuestion("Mf2");

		assertTrue(
				"Error: isDone should be false (1)",
				UndefinedValue.getInstance().equals(session.getBlackboard().getValue(questionOC)));

		Choice answerChoice = KnowledgeBaseManagement.createInstance(kb).findChoice(questionOC,
				"Mf2a1");
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, questionOC,
						new ChoiceValue(answerChoice), PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		assertEquals(
				"Error while setting/getting known OC-Value (2)",
				new ChoiceValue(answerChoice), session.getBlackboard().getValue(questionOC));
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, questionOC,
						Unknown.getInstance(), PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));

		assertEquals(
				"Error while setting/getting unknown OC-Value (3)",
				Unknown.getInstance(), session.getBlackboard().getValue(questionOC));

		assertTrue(
				"Error: should have value (4)",
				!session.getBlackboard().getValue(questionOC).equals(UndefinedValue.getInstance()));

		/*----------------------------------------------
		 */

		/*
		 * QuestionNum questionNum = (QuestionNum) kb.searchQuestions("Mf58");
		 * 
		 * assertTrue("Error: isDone should be false (5)", false ==
		 * questionNum.isDone(session));
		 * 
		 * AnswerNum answerNum = (AnswerNum) questionNum.getAnswer(new
		 * Double(1973)); values = new Object[]{answerNum};
		 * 
		 * session.setValue(questionNum, values, context);
		 * 
		 * 
		 * assertTrue("Error while setting/getting known Num-Value (6)",
		 * answerNum == questionNum.getValue(session).get(0));
		 * 
		 * 
		 * 
		 * AnswerUnknown answerNumUnknown = questionNum.getUnknownAlternative();
		 * values = new Object[]{answerNumUnknown};
		 * 
		 * session.setValue(questionNum, values, context);
		 * 
		 * 
		 * assertTrue("Error while setting/getting unknown Num-Value (7)",
		 * answerNumUnknown == questionNum.getValue(session).get(0));
		 * 
		 * assertTrue("Error: isDone should be true (8)", true ==
		 * questionNum.isDone(session));
		 */

	}

	/**
	 * Assures that every type of question can be set and retrieved. Creation
	 * date: (08.09.2000 16:11:48)
	 * 
	 */
	@Test
	public void testCase() {
		Session session = SessionFactory.createSession(kb);

		while (session.getInterview().nextForm() != EmptyForm.getInstance()) {

			QASet qaSet = (QASet) session.getInterview().nextForm().getInterviewObject();
			assertNotNull(qaSet);
			assertTrue(
					"Keine Frage, sondern ein " + qaSet.getClass() + "-Objekt",
					qaSet instanceof Question);

			Question q1 = (Question) qaSet;

			System.out.println("    Frage: " + q1);
			System.out.println("  FrageId: " + q1.getId());
			System.out.println("FrageText: " + q1.getName());
			if (q1 instanceof QuestionChoice) {
				System.out.println("Antworten: "
						+ ((QuestionChoice) q1).getAllAlternatives());
			}
			session.getBlackboard().addValueFact(
					FactFactory.createFact(session, q1,
							Unknown.getInstance(), PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));
		}

	}
}