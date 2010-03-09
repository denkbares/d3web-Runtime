/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.persistence.tests;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.io.BasicPersistenceHandler;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.DummyProgressListener;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.session.CaseFactory;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.interviewmanager.DialogController;
import de.d3web.core.session.interviewmanager.OQDialogController;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.session.values.AnswerNum;
import de.d3web.core.session.values.AnswerUnknown;
import de.d3web.plugin.test.InitPluginManager;
/**
 * Testclass for the Loading and executing a kfz-kb from an xml file
 * Creation date: (08.09.2000 15:41:53)
 * @author bates
 */
public class TestKfz extends TestCase {
	private static KnowledgeBase kb = null;
	private static URL kbURL = TestKfz.class.getClassLoader().getResource("Kfz2K.xml");

/**
 * Creates a new test-object with name ´name´
 * @param name Name of the test
 */
public TestKfz(String name) {
	super(name);
}

/**
 * use it, if you only want to run TestKfz (console)
 * Creation date: (08.09.2000 16:06:00)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	junit.textui.TestRunner.run(TestKfz.suite());
}

/**
 * Method for instantiating neccessary objects
 * Creation date: (08.09.2000 16:15:09)
 */
protected void setUp() {
	try {
		InitPluginManager.init();
	}
	catch (IOException e1) {
		assertTrue("Error initialising plugin framework", false);
	}
	BasicPersistenceHandler ph = new BasicPersistenceHandler();
	kb = new KnowledgeBase();
	try {
		ph.read(kb, kbURL.openStream(), new DummyProgressListener());
	} catch (IOException e) {
		e.printStackTrace();
	}
}

	/**
	 * Creation date: (05.09.2000 15:57:24)
	 * @return a test suite created with this test class
	 */
	public static Test suite() {
		return new TestSuite(TestKfz.class);
	}

/**
 * Assures that every type of question can be set and retrieved.
 * Creation date: (08.09.2000 16:11:48)
 * @throws IOException 
 */
public void testCount() throws IOException {
	File file = new File("target/kbs");
	if (!file.isDirectory()) {
		file.mkdir();
	}
	PersistenceManager.getInstance().save(kb, new File("target/kbs/test2.jar"));
	XPSCase theCase = CaseFactory.createXPSCase(kb);
	Class<? extends PSMethod> context = de.d3web.scoring.inference.PSMethodHeuristic.class;
	Object[] values;

	/*----------------------------------------------
	 	*/

	QuestionNum Mf5 = (QuestionNum) kb.searchQuestion("Mf5");
	QuestionMC Mf7 = (QuestionMC) kb.searchQuestion("Mf7");
	AnswerChoice Mf7a1 = (AnswerChoice) Mf7.getAnswer(theCase, "Mf7a1");
	AnswerChoice Mf7a2 = (AnswerChoice) Mf7.getAnswer(theCase, "Mf7a2");
	values = new Object[] { Mf7a1, Mf7a2 };
	theCase.setValue(Mf7, values, context);
	//

	List<?> answerList = Mf5.getValue(theCase);
	if (answerList == null) {
		System.out.println("(1) --> NULL!!!!");
	} else if (answerList.isEmpty()) {
		System.out.println("(1) --> EMPTY !!!!");
	} else {
		System.out.println("(1) --> Mf55: " + ((AnswerNum) answerList.get(0)).getValue(theCase));
	}
	//
	//
	//assertTrue("Error with formula (1)",
	//ratingNormal == Msi4.getValue(theCase).get(0));

	System.out.println("---");
}

/**
 * Assures that every type of question can be set and retrieved.
 * Creation date: (08.09.2000 16:11:48)
 */
public void testFormulaSchema() {
	XPSCase theCase = CaseFactory.createXPSCase(kb);
	Class<? extends PSMethod> context = de.d3web.scoring.inference.PSMethodHeuristic.class;
	Object[] values;

	/*---------------------------------------------- */

	QuestionNum Mf5 = (QuestionNum) kb.searchQuestion("Mf5");
	QuestionNum Mf6 = (QuestionNum) kb.searchQuestion("Mf6");
	QuestionOC Msi4 = (QuestionOC) kb.searchQuestion("Msi4");

	//
	AnswerNum Mf5Answer = Mf5.getAnswer(theCase, new Double(10));
	values = new Object[] { Mf5Answer };
	theCase.setValue(Mf5, values, context);
	//
	AnswerNum normalAnswer = Mf6.getAnswer(theCase, new Double(10));
	values = new Object[] { normalAnswer };
	theCase.setValue(Mf6, values, context);
	//
	AnswerChoice ratingNormal = (AnswerChoice) Msi4.getAnswer(theCase, "Msi4a1");

	// Object o = Msi4.getValue(theCase);

	System.out.println(
		"(1) --> Msi4: "
			+ ((AnswerChoice) Msi4.getValue(theCase).get(0)).verbalizeValue(theCase));
	//
	assertTrue(
		"Error with formula (1)",
		ratingNormal == Msi4.getValue(theCase).get(0));

	// This is exactly the border ((Mf6-Mf5)/Mf5)*100 = 10
	AnswerNum highAnswer = Mf6.getAnswer(theCase, new Double(11));
	values = new Object[] { highAnswer };
	theCase.setValue(Mf6, values, context);
	//
	AnswerChoice ratingHigh = (AnswerChoice) Msi4.getAnswer(theCase, "Msi4a2");
	System.out.println(
		"(2) --> Msi4: "
			+ ((AnswerChoice) Msi4.getValue(theCase).get(0)).verbalizeValue(theCase));
	//
	assertEquals(
		"Error with formula (2)",
		ratingHigh, Msi4.getValue(theCase).get(0));

	// 10+9.5 < 20 so answer is "leicht erhöht" as expected
	highAnswer = Mf6.getAnswer(theCase, new Double(9.5));
	values = new Object[] { highAnswer };
	theCase.setValue(Msi4, values, context);
	//
	System.out.println(
		"(3) --> Msi4: "
			+ ((AnswerChoice) Msi4.getValue(theCase).get(0)).verbalizeValue(theCase));
	//
	assertEquals(
		"Error with formula (3)",
		ratingHigh, Msi4.getValue(theCase).get(0));

	//
	AnswerNum veryHighAnswer = Mf6.getAnswer(theCase, new Double(15));
	values = new Object[] { veryHighAnswer };
	theCase.setValue(Mf6, values, context);
	System.out.println(
		"(4) --> Msi4: "
			+ ((AnswerChoice) Msi4.getValue(theCase).get(0)).verbalizeValue(theCase));
	//
	AnswerChoice ratingVeryHigh = (AnswerChoice) Msi4.getAnswer(theCase, "Msi4a3");
	//
	assertTrue(
		"Error with formula (4)",
		ratingVeryHigh == Msi4.getValue(theCase).get(0));

	//		

}

	/**
	 * Assures that every type of question can be set and retrieved.
	 * Creation date: (08.09.2000 16:11:48)
	 */
	public void testNumericExpression() {
		XPSCase theCase = CaseFactory.createXPSCase(kb);
		Class<? extends PSMethod> context = de.d3web.scoring.inference.PSMethodHeuristic.class;
		Object[] values;

		/*----------------------------------------------
		 	*/

		QuestionNum Mf58 = (QuestionNum) kb.searchQuestion("Mf58");
		QuestionNum Mf6 = (QuestionNum) kb.searchQuestion("Mf6");
		QuestionOC Mf4 = (QuestionOC) kb.searchQuestion("Mf4");
		AnswerChoice Mf4a1 = (AnswerChoice) Mf4.getAnswer(theCase, "Mf4a1");
		values = new Object[] { Mf4a1 };
		theCase.setValue(Mf4, values, context);
		//
		AnswerNum normalAnswer = Mf6.getAnswer(theCase, new Double(10));
		values = new Object[] { normalAnswer };
		theCase.setValue(Mf6, values, context);
		List<?> answerList = Mf58.getValue(theCase);
		if (answerList == null) {
			System.out.println("(1) --> NULL!!!!");
		} else if (answerList.isEmpty()) {
			System.out.println("(1) --> EMPTY !!!!");
		} else {
			System.out.println(
				"(1) --> Mf58: " + ((AnswerNum) answerList.get(0)).getValue(theCase));
		}
		//
		//
		//assertTrue("Error with formula (1)",
		//ratingNormal == Msi4.getValue(theCase).get(0));

		System.out.println("---");
	}

	/**
	 * Assures that every type of question can be set and retrieved.
	 * Creation date: (08.09.2000 16:11:48)
	 */
	public void testSetValue() {
		XPSCase theCase = CaseFactory.createXPSCase(kb);
		Class<? extends PSMethod> context = de.d3web.scoring.inference.PSMethodHeuristic.class;
		Object[] values;

		QuestionOC questionOC = (QuestionOC) kb.searchQuestion("Mf2");

		assertTrue(
			"Error: isDone should be false (1)",
			false == questionOC.isDone(theCase));

		AnswerChoice answerChoice = (AnswerChoice) questionOC.getAnswer(theCase, "Mf2a1");
		values = new Object[] { answerChoice };

		theCase.setValue(questionOC, values, context);

		assertTrue(
			"Error while setting/getting known OC-Value (2)",
			answerChoice == questionOC.getValue(theCase).get(0));

		AnswerUnknown answerChoiceUnknown = questionOC.getUnknownAlternative();
		values = new Object[] { answerChoiceUnknown };

		theCase.setValue(questionOC, values, context);

		assertTrue(
			"Error while setting/getting unknown OC-Value (3)",
			answerChoiceUnknown == questionOC.getValue(theCase).get(0));

		assertTrue(
			"Error: isDone shouldn't be false (4)",
			true == questionOC.isDone(theCase));

		/*----------------------------------------------
			*/

		/* 	QuestionNum questionNum = (QuestionNum) kb.searchQuestions("Mf58"); 
		
			assertTrue("Error: isDone should be false (5)",
				false == questionNum.isDone(theCase));
		
			AnswerNum answerNum = (AnswerNum) questionNum.getAnswer(new Double(1973));
			values = new Object[]{answerNum};
			
			theCase.setValue(questionNum, values, context);
			
			
			assertTrue("Error while setting/getting known Num-Value (6)",
				answerNum == questionNum.getValue(theCase).get(0));
			
			
			
			AnswerUnknown answerNumUnknown = questionNum.getUnknownAlternative();
			values = new Object[]{answerNumUnknown};
			
			theCase.setValue(questionNum, values, context);
			
			
			assertTrue("Error while setting/getting unknown Num-Value (7)",
				answerNumUnknown == questionNum.getValue(theCase).get(0));
			
			assertTrue("Error: isDone should be true (8)",
				true == questionNum.isDone(theCase));
			*/

	}



/**
 * Assures that every type of question can be set and retrieved.
 * Creation date: (08.09.2000 16:11:48)
 */
public void testCase() {
	XPSCase theCase = CaseFactory.createXPSCase(kb, OQDialogController.class);

	while (((DialogController) theCase.getQASetManager()).hasNewestQASet()) {

		((DialogController) theCase.getQASetManager()).moveToNewestQASet();
		QASet qaSet = null;
		try {
			qaSet = ((DialogController)theCase.getQASetManager()).getCurrentQASet();
		} catch (Exception e) {
			assertTrue("Ups, eine Exception", false);
		}
		assertNotNull(qaSet);
		assertTrue(
			"Keine Frage, sondern ein " + qaSet.getClass() + "-Objekt",
			qaSet instanceof Question);

		Question q1 = (Question) qaSet;

		System.out.println("    Frage: " + q1);
		System.out.println("  FrageId: " + q1.getId());
		System.out.println("FrageText: " + q1.getText());
		if (q1 instanceof QuestionChoice) {
			System.out.println("Antworten: " + ((QuestionChoice) q1).getAllAlternatives());
		}

		theCase.setValue(q1, new Object[] { q1.getUnknownAlternative()});
	}

}
}