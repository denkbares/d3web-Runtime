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

package de.d3web.kernel.psMethods.setCovering.unitTests;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.CaseFactory;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.psMethods.setCovering.ObservableFinding;
import de.d3web.kernel.psMethods.setCovering.PSMethodSetCovering;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.kernel.psMethods.setCovering.SCNodeFactory;
import de.d3web.kernel.psMethods.setCovering.SCProbability;
import de.d3web.kernel.psMethods.setCovering.SCRelation;
import de.d3web.kernel.psMethods.setCovering.SCRelationFactory;
import de.d3web.kernel.psMethods.setCovering.pools.SetPool;
import de.d3web.kernel.psMethods.setCovering.strategies.transitivePropagation.DefaultStrengthCalculationStrategy;
import de.d3web.kernel.psMethods.setCovering.strategies.transitivePropagation.DefaultStrengthSelectionStrategy;
import de.d3web.kernel.psMethods.setCovering.strategies.transitivePropagation.StrengthCalculationStrategy;
import de.d3web.kernel.psMethods.setCovering.strategies.transitivePropagation.StrengthSelectionStrategy;
import de.d3web.kernel.psMethods.setCovering.utils.SortedList;
import de.d3web.kernel.psMethods.setCovering.utils.TransitiveClosure;
import de.d3web.kernel.psMethods.setCovering.utils.comparators.DiagnosisByStrengthComparator;
import de.d3web.kernel.psMethods.setCovering.utils.comparators.FindingByWeightComparator;
import de.d3web.kernel.psMethods.shared.QuestionWeightValue;
import de.d3web.kernel.psMethods.shared.Weight;
import de.d3web.kernel.utilities.Utils;

/**
 * This suite tests all sorted collections that occur in this project.
 * 
 * @author bruemmer
 * 
 * 
 * 
 */
public class SortedCollectionsTest extends TestCase {

	Diagnosis diag1, diag2, diag3, diag4 = null;
	SCDiagnosis d1, d2, d3, d4 = null;

	QuestionNum q = null;
	AnswerNum ans1 = null;
	PredictedFinding f = null;
	SCRelation r1, r2, r3, r4 = null;

	XPSCase theCase = null;

	public SortedCollectionsTest(String name) {
		super(name);
	}

	public static TestSuite suite() {
		return new TestSuite(SortedCollectionsTest.class);
	}

	public static void main(String[] args) {
		TestRunner.run(SortedCollectionsTest.class);
	}

	public void setUp() {
		KnowledgeBase kb = new KnowledgeBase();

		diag1 = new Diagnosis();
		diag1.setId("d1");
		diag1.setText("diag1");
		diag1.setKnowledgeBase(kb);
		d1 = SCNodeFactory.createSCDiagnosis(diag1);

		diag2 = new Diagnosis();
		diag2.setId("d2");
		diag2.setText("diag2");
		diag2.setKnowledgeBase(kb);
		d2 = SCNodeFactory.createSCDiagnosis(diag2);

		diag3 = new Diagnosis();
		diag3.setId("d3");
		diag3.setText("diag3");
		diag3.setKnowledgeBase(kb);
		d3 = SCNodeFactory.createSCDiagnosis(diag3);

		diag4 = new Diagnosis();
		diag4.setId("d4");
		diag4.setText("diag4");
		diag4.setKnowledgeBase(kb);
		d4 = SCNodeFactory.createSCDiagnosis(diag4);

		q = new QuestionNum();
		q.setId("q");
		q.setText("qnum");
		q.setKnowledgeBase(kb);
		ans1 = new AnswerNum();
		ans1.setValue(new Double(1));
		f = SCNodeFactory.createFindingEquals(q, new Object[]{ans1});

		r1 = SCRelationFactory.createSCRelation(d1, f, Utils
				.createList(new Object[]{SCProbability.N2}));
		r2 = SCRelationFactory.createSCRelation(d2, f, Utils
				.createList(new Object[]{SCProbability.N1}));
		r3 = SCRelationFactory.createSCRelation(d3, f, Utils
				.createList(new Object[]{SCProbability.P1}));
		r4 = SCRelationFactory.createSCRelation(d4, f, Utils
				.createList(new Object[]{SCProbability.P3}));

		theCase = CaseFactory.createXPSCase(kb);
		theCase.getUsedPSMethods().add(PSMethodSetCovering.getInstance());
		PSMethodSetCovering.getInstance().init(theCase);

		TransitiveClosure closure = PSMethodSetCovering.getInstance().getTransitiveClosure(kb);
		StrengthCalculationStrategy calcStrat = DefaultStrengthCalculationStrategy.getInstance();
		StrengthSelectionStrategy selStrat = DefaultStrengthSelectionStrategy.getInstance();

		d1.initialize(closure, calcStrat, selStrat);
		d2.initialize(closure, calcStrat, selStrat);
		d3.initialize(closure, calcStrat, selStrat);
		d4.initialize(closure, calcStrat, selStrat);
	}

	public void testDiagnosesSortingByStrengthForFinding() {
		theCase.setValue(q, new Object[]{ans1});
		ObservableFinding obsF = SCNodeFactory.createObservableFinding(q, new Object[]{ans1});
		SortedList sl = new SortedList(new DiagnosisByStrengthComparator(theCase, obsF), SetPool
				.getInstance().getFilledSet(new Object[]{d3, d2, d1, d4}));

		assertEquals("diagnosis sorting by strength for finding wrong (0)", Utils
				.createList(new Object[]{d4, d3, d2, d1}), sl);
	}

	public void testFindingSortingByWeight() {
		AnswerNum ans = new AnswerNum();
		ans.setValue(new Double(5));

		QuestionNum qnum1 = new QuestionNum();
		qnum1.setId("qnum1");

		QuestionNum qnum2 = new QuestionNum();
		qnum2.setId("qnum2");

		QuestionNum qnum3 = new QuestionNum();
		qnum3.setId("qnum3");

		QuestionNum qnum4 = new QuestionNum();
		qnum4.setId("qnum4");

		Weight w1 = new Weight();
		QuestionWeightValue qww1 = new QuestionWeightValue();
		qww1.setQuestion(qnum1);
		qww1.setValue(Weight.G3);
		w1.setQuestionWeightValue(qww1);

		Weight w2 = new Weight();
		QuestionWeightValue qww2 = new QuestionWeightValue();
		qww2.setQuestion(qnum2);
		qww2.setValue(Weight.G5);
		w2.setQuestionWeightValue(qww2);

		Weight w3 = new Weight();
		QuestionWeightValue qww3 = new QuestionWeightValue();
		qww3.setQuestion(qnum3);
		qww3.setValue(Weight.G4);
		w3.setQuestionWeightValue(qww3);

		Weight w4 = new Weight();
		QuestionWeightValue qww4 = new QuestionWeightValue();
		qww4.setQuestion(qnum4);
		qww4.setValue(Weight.G6);
		w4.setQuestionWeightValue(qww4);

		ObservableFinding f1 = SCNodeFactory.createObservableFinding(qnum1, new Object[]{ans});
		ObservableFinding f2 = SCNodeFactory.createObservableFinding(qnum2, new Object[]{ans});
		ObservableFinding f3 = SCNodeFactory.createObservableFinding(qnum3, new Object[]{ans});
		ObservableFinding f4 = SCNodeFactory.createObservableFinding(qnum4, new Object[]{ans});

		SortedList sl = new SortedList(new FindingByWeightComparator(theCase), Utils
				.createList(new Object[]{f1, f2, f3, f4}));

		assertEquals("sorting by weight failed for findings", Utils.createList(new Object[]{f4, f2,
				f3, f1}), sl);
	}

}
