package de.d3web.kernel.psMethods.setCovering.unitTests;

import java.util.Arrays;
import java.util.LinkedList;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.CaseFactory;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.psMethods.setCovering.ObservableFinding;
import de.d3web.kernel.psMethods.setCovering.PSMethodSetCovering;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.kernel.psMethods.setCovering.SCNodeFactory;
import de.d3web.kernel.psMethods.setCovering.SCProbability;
import de.d3web.kernel.psMethods.setCovering.SCRelationFactory;
import de.d3web.kernel.psMethods.setCovering.strategies.hypothesesGeneration.DefaultBestDiagnosesSelectionStrategy;
import de.d3web.kernel.psMethods.setCovering.strategies.hypothesesGeneration.DefaultBestFindingSelectionStrategy;
import de.d3web.kernel.psMethods.shared.QuestionWeightValue;
import de.d3web.kernel.psMethods.shared.Weight;
import de.d3web.kernel.utilities.Utils;

/**
 * This is a TestCase for strategies used by hypotheses generation algorithms
 * 
 * @author bruemmer
 * 
 * 
 */
public class HypothesesGenerationStrategiesTest extends TestCase {

	private KnowledgeBase kb = null;
	private XPSCase theCase = null;

	private SCDiagnosis d1, d2, d3 = null;
	private PredictedFinding f1 = null;
	// private SCRelation r1, r2, r3, r4, r5 = null;
	private Question q1 = null;
	private AnswerNum ansNum5 = null;

	public HypothesesGenerationStrategiesTest(String name) {
		super(name);
	}

	public static TestSuite suite() {
		return new TestSuite(HypothesesGenerationStrategiesTest.class);
	}

	public static void main(String[] args) {
		TestRunner.run(HypothesesGenerationStrategiesTest.class);
	}

	public void setUp() {
		kb = new KnowledgeBase();
		Diagnosis diag1 = new Diagnosis();
		diag1.setId("d1");
		diag1.setText("d1-text");
		diag1.setKnowledgeBase(kb);

		Diagnosis diag2 = new Diagnosis();
		diag2.setId("d2");
		diag2.setText("d2-text");
		diag2.setKnowledgeBase(kb);

		Diagnosis diag3 = new Diagnosis();
		diag3.setId("d3");
		diag3.setText("d3-text");
		diag3.setKnowledgeBase(kb);

		d1 = SCNodeFactory.createSCDiagnosis(diag1);
		d2 = SCNodeFactory.createSCDiagnosis(diag2);
		d3 = SCNodeFactory.createSCDiagnosis(diag3);

		q1 = new QuestionNum();
		q1.setId("f1");
		q1.setText("f1-text");
		q1.setKnowledgeBase(kb);
		ansNum5 = new AnswerNum();
		ansNum5.setValue(new Double(5));
		f1 = SCNodeFactory.createFindingEquals(q1, new Object[]{ansNum5});

		SCRelationFactory.createSCRelation(d1, d2, Arrays.asList(new Object[]{new SCProbability(
				"N2", 0.015)}));
		SCRelationFactory.createSCRelation(d2, d3, Arrays.asList(new Object[]{new SCProbability(
				"P1", 0.075)}));
		SCRelationFactory.createSCRelation(d2, f1, Arrays.asList(new Object[]{new SCProbability(
				"P2", 0.15)}));
		SCRelationFactory.createSCRelation(d1, d3, Arrays.asList(new Object[]{new SCProbability(
				"P3", 0.3)}));
		SCRelationFactory.createSCRelation(d3, f1, Arrays.asList(new Object[]{new SCProbability(
				"N1", 0.025)}));

		theCase = CaseFactory.createXPSCase(kb);
		theCase.getUsedPSMethods().add(PSMethodSetCovering.getInstance());
		theCase.setUsedPSMethods(Utils.createList(new Object[]{PSMethodSetCovering.getInstance()}));
		PSMethodSetCovering.getInstance().init(theCase);
	}

	public void testDefaultBestDiagnosesSelectionStrategy() {

		theCase.setValue(q1, new Object[]{ansNum5});

		DefaultBestDiagnosesSelectionStrategy strat = DefaultBestDiagnosesSelectionStrategy
				.getInstance();
		ObservableFinding obsF = SCNodeFactory.createObservableFinding(q1, new Object[]{ansNum5});

		assertEquals("diagnoses selection strategy wrong (0)", Utils.createList(new Object[]{d1}),
				strat.selectBestKDiagnosesFor(theCase, obsF, 1));

		assertEquals("diagnoses selection strategy wrong (1)", Utils
				.createList(new Object[]{d1, d2}), strat.selectBestKDiagnosesFor(theCase, obsF, 2));

		assertEquals("diagnoses selection strategy wrong (2)", Utils.createList(new Object[]{d1,
				d2, d3}), strat.selectBestKDiagnosesFor(theCase, obsF, 3));

		assertEquals("diagnoses selection strategy wrong (3)", Utils.createList(new Object[]{d1,
				d2, d3}), strat.selectBestKDiagnosesFor(theCase, obsF, 1000));

		assertEquals("diagnoses selection strategy wrong (4)", new LinkedList(), strat
				.selectBestKDiagnosesFor(theCase, obsF, 0));

	}

	public void testDefaultMaxFindingSelectionStrategy() {
		AnswerNum ans = new AnswerNum();
		ans.setValue(new Double(5));

		QuestionNum qnum1 = new QuestionNum();
		qnum1.setId("qnum1");
		qnum1.setKnowledgeBase(kb);

		QuestionNum qnum2 = new QuestionNum();
		qnum2.setId("qnum2");
		qnum2.setKnowledgeBase(kb);

		QuestionNum qnum3 = new QuestionNum();
		qnum3.setId("qnum3");
		qnum3.setKnowledgeBase(kb);

		QuestionNum qnum4 = new QuestionNum();
		qnum4.setId("qnum4");
		qnum4.setKnowledgeBase(kb);

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

		ObservableFinding find1 = SCNodeFactory.createObservableFinding(qnum1, new Object[]{ans});
		ObservableFinding find2 = SCNodeFactory.createObservableFinding(qnum2, new Object[]{ans});
		ObservableFinding find3 = SCNodeFactory.createObservableFinding(qnum3, new Object[]{ans});
		ObservableFinding find4 = SCNodeFactory.createObservableFinding(qnum4, new Object[]{ans});

		DefaultBestFindingSelectionStrategy strat = DefaultBestFindingSelectionStrategy
				.getInstance();

		theCase.setValue(qnum1, new Object[]{ans});
		theCase.setValue(qnum2, new Object[]{ans});
		theCase.setValue(qnum3, new Object[]{ans});
		theCase.setValue(qnum4, new Object[]{ans});

		assertEquals("finding max selection incorrect", find4, strat.selectMaxFinding(Utils
				.createList(new Object[]{find3, find2, find1, find4}), theCase));
	}

}
