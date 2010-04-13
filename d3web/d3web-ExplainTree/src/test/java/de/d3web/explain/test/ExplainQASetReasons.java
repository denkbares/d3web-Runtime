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
import de.d3web.abstraction.inference.PSMethodQuestionSetter;
import de.d3web.core.inference.PSMethodInit;
import de.d3web.core.inference.condition.CondDState;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.DiagnosisState;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.values.Choice;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.explain.ExplanationFactory;
import de.d3web.explain.eNodes.ECondition;
import de.d3web.explain.eNodes.ENode;
import de.d3web.explain.eNodes.EReason;
import de.d3web.explain.eNodes.reasons.EPSMethodReason;
import de.d3web.explain.eNodes.reasons.ERuleReason;
import de.d3web.explain.eNodes.values.QState;
import de.d3web.indication.inference.PSMethodContraIndication;
import de.d3web.indication.inference.PSMethodNextQASet;
import de.d3web.indication.inference.PSMethodSuppressAnswer;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.scoring.DiagnosisScore;
import de.d3web.scoring.Score;
import de.d3web.scoring.inference.PSMethodHeuristic;

/**
 *
 * @author  betz
 */
public class ExplainQASetReasons extends AbstractExplainTest {

	KnowledgeBase testKb = new KfzWb();
	Session theCase = null;
	private ExplanationFactory eFac = null;

	/** Creates a new instance of ExplainQASetReasons */
	public ExplainQASetReasons(String name) {
		super(name);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.main(
			new String[] { "de.d3web.explain.test.ExplainQASetReasonsTest" });
	}

	public static Test suite() {
		return new TestSuite(ExplainQASetReasons.class);
	}

	@Override
	protected void setUp() {
		theCase = SessionFactory.createSession(testKb);
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
		Solution P8 = findD("P8", testKb);
		DiagnosisScore score = new DiagnosisScore();
		score = score.add(Score.P7);
		theCase.setValue(P8, score, PSMethodUserSelected.class);

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
		Solution P8 = findD("P8", testKb);
		DiagnosisScore score = new DiagnosisScore();
		score = score.add(Score.P4);
		theCase.setValue(P8, score, PSMethodUserSelected.class);

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
		theCase.setValue(Mf8, new ChoiceValue((Choice) Mf8.getAnswer(theCase,
				"Mf8a2")));


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