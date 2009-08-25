/*
 * ExplainQASetReasons.java
 *
 * Created on 26. März 2002, 09:18
 */

package de.d3web.explain.test;

import java.util.Collection;
import java.util.LinkedList;

import junit.framework.Test;
import junit.framework.TestSuite;
import de.d3web.explain.ExplanationFactory;
import de.d3web.explain.eNodes.ECondition;
import de.d3web.explain.eNodes.ENode;
import de.d3web.explain.eNodes.EReason;
import de.d3web.explain.eNodes.reasons.EPSMethodReason;
import de.d3web.explain.eNodes.reasons.ERuleReason;
import de.d3web.explain.eNodes.values.QState;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.CaseFactory;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisScore;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.Score;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;
import de.d3web.kernel.domainModel.ruleCondition.CondDState;
import de.d3web.kernel.domainModel.ruleCondition.CondEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondOr;
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
public class ExplainQASetReasons extends AbstractExplainTest {

	KnowledgeBase testKb = new KfzWb();
	XPSCase theCase = null;
	private ExplanationFactory eFac = null;

	/** Creates a new instance of ExplainQASetReasons */
	public ExplainQASetReasons(String name) {
		super(name);
	}

	public static void main(String[] args) {
		de.d3web.kernel.domainModel.D3WebCase.TRACE = false;
		junit.textui.TestRunner.main(
			new String[] { "de.d3web.explain.test.ExplainQASetReasonsTest" });
	}

	public static Test suite() {
		return new TestSuite(ExplainQASetReasons.class);
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

	public void testInitQASets() {
		Collection explainContext = new LinkedList();
		explainContext.add(PSMethodInit.class);
		explainContext.add(PSMethodHeuristic.class);
		explainContext.add(PSMethodUserSelected.class);
		explainContext.add(PSMethodContraIndication.class);
		explainContext.add(PSMethodNextQASet.class);
		explainContext.add(PSMethodQuestionSetter.class);
		explainContext.add(PSMethodSuppressAnswer.class);

		// first explain a (direct) start-qaset
		ENode expl = eFac.explainActive(findQ("Q56", testKb), explainContext);
		// Test the activation-explanation of Q56: It's an init-qaset.

		assertSame("Target won't match", expl.getTarget(), findQ("Q56", testKb));
		Collection reasons = expl.getProReasons();
		assertTrue(reasons.size() == 1);
		EReason firstReason = (EReason) reasons.iterator().next();
		assertTrue("Incorrect type", firstReason instanceof EPSMethodReason);
		assertTrue(
			((EPSMethodReason) firstReason).getContext() == PSMethodInit.class);



		/* This won't work properly due to the problem mentioned in ToDo-List
		 * I think I won't need it in WebTrain
		 *
		   // then explain a (indirect) start-qaset
		   expl = eFac.explainActive(findQ("Mf2",testKb), explainContext);
		   System.out.println("expl" + expl.toString());
		*/

	}

	public void testRefinement() {

		Collection explainContext = new LinkedList();
		explainContext.add(PSMethodInit.class);
		explainContext.add(PSMethodHeuristic.class);
		explainContext.add(PSMethodUserSelected.class);
		explainContext.add(PSMethodContraIndication.class);
		explainContext.add(PSMethodNextQASet.class);
		explainContext.add(PSMethodQuestionSetter.class);
		explainContext.add(PSMethodSuppressAnswer.class);

		// set P8 to suggested since it will activate Q17
		Diagnosis P8 = findD("P8", testKb);
		DiagnosisScore score = new DiagnosisScore();
		score = score.add(Score.P7);
		theCase.setValue(P8, new Object[] { score }, PSMethodUserSelected.class);

		assertEquals(DiagnosisState.ESTABLISHED, P8.getState(theCase, PSMethodUserSelected.class));

		assertTrue(findQ("Q17", testKb).isValid(theCase));

		//  explain a followup-question not active
		ENode expl = eFac.explainActive(findQ("Q17", testKb), explainContext);

		log(expl.toString());

		assertSame(findQ("Q17", testKb), expl.getTarget());
		assertSame(expl.getValue(), QState.ACTIVE);
		assertTrue(expl.getProReasons().size()==1);
		Object firstReason = expl.getProReasons().iterator().next();
		assertTrue(firstReason instanceof ERuleReason);
		ERuleReason reason = (ERuleReason) firstReason;
		assertTrue(reason.getActiveException()==null);
		assertTrue(reason.getActiveContext()==null);
		ECondition activeCondition = reason.getActiveCondition();
		assertTrue(activeCondition!=null);
		assertTrue(activeCondition.getCondition() instanceof CondDState);



	}

	public void testClarification() {

		Collection explainContext = new LinkedList();
		explainContext.add(PSMethodInit.class);
		explainContext.add(PSMethodHeuristic.class);
		explainContext.add(PSMethodUserSelected.class);
		explainContext.add(PSMethodContraIndication.class);
		explainContext.add(PSMethodNextQASet.class);
		explainContext.add(PSMethodQuestionSetter.class);
		explainContext.add(PSMethodSuppressAnswer.class);

		// set P8 to suggested since it will activate Q17
		Diagnosis P8 = findD("P8", testKb);
		DiagnosisScore score = new DiagnosisScore();
		score = score.add(Score.P4);
		theCase.setValue(P8, new Object[] { score }, PSMethodUserSelected.class);

		assertEquals(DiagnosisState.SUGGESTED, P8.getState(theCase, PSMethodUserSelected.class));

		assertTrue(findQ("Q17", testKb).isValid(theCase));

		//  explain a followup-question not active
		ENode expl = eFac.explainActive(findQ("Q17", testKb), explainContext);

		log(expl.toString());

		assertSame(findQ("Q17", testKb), expl.getTarget());
		assertSame(expl.getValue(), QState.ACTIVE);
		assertTrue(expl.getProReasons().size()==1);
		Object firstReason = expl.getProReasons().iterator().next();
		assertTrue(firstReason instanceof ERuleReason);
		ERuleReason reason = (ERuleReason) firstReason;
		assertTrue(reason.getActiveException()==null);
		assertTrue(reason.getActiveContext()==null);
		ECondition activeCondition = reason.getActiveCondition();
		assertTrue(activeCondition!=null);
		assertTrue(activeCondition.getCondition() instanceof CondDState);




	}
	public void _testInactive() {
	}

	public void testIndication() {
		Collection explainContext = new LinkedList();
		explainContext.add(PSMethodInit.class);
		explainContext.add(PSMethodHeuristic.class);
		explainContext.add(PSMethodUserSelected.class);
		explainContext.add(PSMethodContraIndication.class);
		explainContext.add(PSMethodNextQASet.class);
		explainContext.add(PSMethodQuestionSetter.class);
		explainContext.add(PSMethodSuppressAnswer.class);

		// set MF8a2 since it will activate Mf10 (and give P8 the score P5
		QuestionChoice Mf8 = (QuestionChoice) findQ("Mf8", testKb);
		theCase.setValue(Mf8, new Object[]{Mf8.getAnswer(theCase,"Mf8a2")});


		//  explain a followup-question not active
		ENode expl = eFac.explainActive(findQ("Mf10", testKb), explainContext);

		// log(expl.toString());

		assertSame(expl.getTarget(), findQ("Mf10", testKb));
		assertSame(expl.getValue(), QState.ACTIVE);
		assertTrue(expl.getProReasons().size()==1);
		Object firstReason = expl.getProReasons().iterator().next();
		assertTrue(firstReason instanceof ERuleReason);
		ERuleReason reason = (ERuleReason) firstReason;
		assertTrue(reason.getActiveException()==null);
		assertTrue(reason.getActiveContext()==null);
		ECondition activeCondition = reason.getActiveCondition();
		assertTrue(activeCondition!=null);
		assertTrue(activeCondition.getCondition() instanceof CondOr);
		assertTrue(activeCondition.getActiveParts()!=null && !activeCondition.getActiveParts().isEmpty());
		assertTrue(((ECondition)activeCondition.getActiveParts().get(0)).getCondition() instanceof CondEqual);
		// das sollte hier genügen. Es müsste noch Mf8 überprüft werden...
	}

	public void _testContraIndication() {
	}

}