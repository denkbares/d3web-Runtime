/*
 * ExplainDiagnosisReasons.java
 *
 * Created on 26. März 2002, 09:18
 */

package de.d3web.explain.test;

import java.util.Collection;
import java.util.LinkedList;

import junit.framework.Test;
import junit.framework.TestSuite;
import de.d3web.explain.ExplanationFactory;
import de.d3web.explain.eNodes.ENode;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.CaseFactory;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;
import de.d3web.kernel.psMethods.PSMethodInit;
import de.d3web.kernel.psMethods.contraIndication.PSMethodContraIndication;
import de.d3web.kernel.psMethods.heuristic.PSMethodHeuristic;
import de.d3web.kernel.psMethods.nextQASet.PSMethodNextQASet;
import de.d3web.kernel.psMethods.questionSetter.PSMethodQuestionSetter;
import de.d3web.kernel.psMethods.suppressAnswer.PSMethodSuppressAnswer;
import de.d3web.kernel.psMethods.userSelected.PSMethodUserSelected;

/**
 *
 * @author  betz
 */
public class ExplainDiagnosisReasons extends AbstractExplainTest {

	KnowledgeBase testKb = new KfzWb();
	XPSCase theCase = null;
	private ExplanationFactory eFac = null;

	/** Creates a new instance of ExplainQASetReasons */
	public ExplainDiagnosisReasons(String name) {
		super(name);
	}

	public static void main(String[] args) {
		de.d3web.kernel.domainModel.D3WebCase.TRACE = false;
		junit.textui.TestRunner.main(
			new String[] { "de.d3web.explain.test.ExplainDiagnosisReasons" });
	}

	public static Test suite() {
		return new TestSuite(ExplainDiagnosisReasons.class);
	}

	protected void setUp() {
		theCase = CaseFactory.createXPSCase(testKb);
		/* Let me have some explanations of this test first:
		 * We do have the following assumptions:
		 * InitQASets: Q56, Q16
		 * Children of Q56: Mf2, Mf3, Mf4, Mf5, Mf6, Mf7, Mf8, Mf9
		 * Consequences of setting Mf8 to Mf8a2: Activation of Mf10
		 *       + adding Diagnosis P8 the score P5.
		 * Consequences of P8 = suggested: Activate Q17
		 * Consequences of P8 = established: Acitvate Q17
		 */

		eFac = new ExplanationFactory(theCase);
	}

	public void testSimple() {
		Collection explainContext = new LinkedList();
		explainContext.add(PSMethodInit.class);
		explainContext.add(PSMethodHeuristic.class);
		explainContext.add(PSMethodUserSelected.class);
		explainContext.add(PSMethodContraIndication.class);
		explainContext.add(PSMethodNextQASet.class);
		explainContext.add(PSMethodQuestionSetter.class);
		explainContext.add(PSMethodSuppressAnswer.class);

		// set MF8a2 since it will give P8 the score P5 (and activate Mf10)
		QuestionChoice Mf8 = (QuestionChoice) findQ("Mf8", testKb);
		theCase.setValue(Mf8, new Object[] { Mf8.getAnswer(theCase,"Mf8a2")});

		//  explain a diagnosis
		ENode expl = eFac.explain(findD("P8", testKb), explainContext);
		System.err.println("-- >testSimple --");
		log(expl.toString());
		System.err.println("-- <testSimple --");
	}

	public void testTwoRules() {
		Collection explainContext = new LinkedList();
		explainContext.add(PSMethodInit.class);
		explainContext.add(PSMethodHeuristic.class);
		explainContext.add(PSMethodUserSelected.class);
		explainContext.add(PSMethodContraIndication.class);
		explainContext.add(PSMethodNextQASet.class);
		explainContext.add(PSMethodQuestionSetter.class);
		explainContext.add(PSMethodSuppressAnswer.class);

		// set MF8a2 since it will give P8 the score P5 (and activate Mf10)
		QuestionChoice Mf13 = (QuestionChoice) findQ("Mf13", testKb);
		theCase.setValue(Mf13, new Object[] { Mf13.getAnswer(theCase,"Mf13a1")});

		QuestionChoice Mf8 = (QuestionChoice) findQ("Mf8", testKb);
		theCase.setValue(Mf8, new Object[] { Mf8.getAnswer(theCase,"Mf8a2")});

		//  explain a diagnosis
		ENode expl = eFac.explain(findD("P8", testKb), explainContext);

		System.err.println("-- >testTwoRules--");
		log(expl.toString());
		System.err.println("-- <testTwoRules --");
	}

	public void _testAPriori() {
		// soll irgendwann mal die Erklärung von APriori-Regeln aufnehmen
	}

	public void _testNecessary() {
		// sollte die Erklärung von Pp-Regeln aufnehmen.
	}

}