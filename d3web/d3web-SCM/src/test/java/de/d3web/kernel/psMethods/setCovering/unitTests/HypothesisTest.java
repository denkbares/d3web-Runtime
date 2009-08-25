package de.d3web.kernel.psMethods.setCovering.unitTests;

import java.util.Set;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.CaseFactory;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.psMethods.setCovering.Hypothesis;
import de.d3web.kernel.psMethods.setCovering.PSMethodSetCovering;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.kernel.psMethods.setCovering.SCNodeFactory;
import de.d3web.kernel.psMethods.setCovering.SCProbability;
import de.d3web.kernel.psMethods.setCovering.SCRelationFactory;
import de.d3web.kernel.psMethods.setCovering.pools.HypothesisPool;
import de.d3web.kernel.psMethods.setCovering.pools.SetPool;
import de.d3web.kernel.psMethods.setCovering.utils.SortedList;
import de.d3web.kernel.psMethods.setCovering.utils.comparators.HypothesisComparator;
import de.d3web.kernel.utilities.Utils;

/**
 * This test checks all basic features of a hypothesis.
 * 
 * @author bruemmer
 * 
 * 
 */
public class HypothesisTest extends TestCase {

	/*
	 * KnowledgeBase:
	 * 
	 * 
	 * d1 d2 / \ | / \ | d3 d4 | | /\ | | / \ | | / \ | f1 f2 f3 f4
	 * 
	 * 
	 * 
	 */

	private static final double EPSILON = 0.001;

	private KnowledgeBase kb = null;

	private QuestionNum q1, q2, q3, q4 = null;
	private Diagnosis diag1, diag2, diag3, diag4 = null;

	private PredictedFinding f1, f2, f3, f4 = null;
	private SCDiagnosis d1, d2, d3, d4 = null;

	private AnswerNum ans1, ans2, ans3, ans4 = null;

	public HypothesisTest(String name) {
		super(name);
	}

	public static TestSuite suite() {
		return new TestSuite(HypothesisTest.class);
	}

	public static void main(String[] args) {
		TestRunner.run(HypothesisTest.class);
	}

	public void setUp() {

		// KnowledgeBase
		kb = new KnowledgeBase();

		// Findings
		q1 = new QuestionNum();
		q1.setId("q1");
		q1.setText("qnum1");
		q1.setKnowledgeBase(kb);
		ans1 = new AnswerNum();
		ans1.setValue(new Double(1));
		f1 = SCNodeFactory.createFindingEquals(q1, new Object[]{ans1});

		q2 = new QuestionNum();
		q2.setId("q2");
		q2.setText("qnum2");
		q2.setKnowledgeBase(kb);
		ans2 = new AnswerNum();
		ans2.setValue(new Double(2));
		f2 = SCNodeFactory.createFindingEquals(q2, new Object[]{ans2});

		q3 = new QuestionNum();
		q3.setId("q3");
		q3.setText("qnum3");
		q3.setKnowledgeBase(kb);
		ans3 = new AnswerNum();
		ans3.setValue(new Double(3));
		f3 = SCNodeFactory.createFindingEquals(q3, new Object[]{ans3});

		q4 = new QuestionNum();
		q4.setId("q4");
		q4.setText("qnum4");
		q4.setKnowledgeBase(kb);
		ans4 = new AnswerNum();
		ans4.setValue(new Double(4));
		f4 = SCNodeFactory.createFindingEquals(q4, new Object[]{ans4});

		// SCDiagnoses

		diag1 = new Diagnosis();
		diag1.setId("d1");
		diag1.setText("diag1");
		diag1.setKnowledgeBase(kb);
		d1 = new SCDiagnosis();
		d1.setNamedObject(diag1);

		diag2 = new Diagnosis();
		diag2.setId("d2");
		diag2.setText("diag2");
		diag2.setKnowledgeBase(kb);
		d2 = new SCDiagnosis();
		d2.setNamedObject(diag2);

		diag3 = new Diagnosis();
		diag3.setId("d3");
		diag3.setText("diag3");
		diag3.setKnowledgeBase(kb);
		d3 = new SCDiagnosis();
		d3.setNamedObject(diag3);

		diag4 = new Diagnosis();
		diag4.setId("d4");
		diag4.setText("diag4");
		diag4.setKnowledgeBase(kb);
		d4 = new SCDiagnosis();
		d4.setNamedObject(diag4);

		// Relations

		SCRelationFactory
				.createSCRelation(d1, d3, Utils.createList(new Object[]{SCProbability.P2}));

		SCRelationFactory
				.createSCRelation(d1, d4, Utils.createList(new Object[]{SCProbability.P3}));

		SCRelationFactory
				.createSCRelation(d3, f1, Utils.createList(new Object[]{SCProbability.P1}));

		SCRelationFactory
				.createSCRelation(d4, f2, Utils.createList(new Object[]{SCProbability.N1}));

		SCRelationFactory
				.createSCRelation(d4, f3, Utils.createList(new Object[]{SCProbability.P2}));

		SCRelationFactory
				.createSCRelation(d2, f4, Utils.createList(new Object[]{SCProbability.P1}));
	}

	public void testHypothesisCloning() {
		XPSCase theCase = CaseFactory.createXPSCase(kb);
		theCase.setUsedPSMethods(Utils.createList(new Object[]{PSMethodSetCovering.getInstance()}));
		PSMethodSetCovering.getInstance().init(theCase);

		// q1 will be answered, but not as predicted (resulting finding != f1)
		theCase.setValue(q1, new Object[]{ans2});

		// q2 will be answered as predicted (resulting finding == f2)
		theCase.setValue(q2, new Object[]{ans2});

		// q4 will be answered, but not as predicted (resulting finding != f4)
		theCase.setValue(q4, new Object[]{ans2});

		Hypothesis hyp = HypothesisPool.getInstance().getHypothesis(new Object[]{d4, d2});
		Hypothesis hypClone = (Hypothesis) hyp.clone();

		assertEquals("clone not correct (0)", hyp, hypClone);
		assertEquals("clone not correct (1)", hyp.getExplainedFindings(theCase), hypClone
				.getExplainedFindings(theCase));
		assertEquals("clone not correct (2)", hyp.getNegativePredictedFindings(theCase), hypClone
				.getNegativePredictedFindings(theCase));
	}

	public void testHypothesisFindingMaps() {
		XPSCase theCase = CaseFactory.createXPSCase(kb);
		theCase.setUsedPSMethods(Utils.createList(new Object[]{PSMethodSetCovering.getInstance()}));
		PSMethodSetCovering.getInstance().init(theCase);

		// q1 will be answered, but not as predicted (resulting finding != f1)
		theCase.setValue(q1, new Object[]{ans2});

		// q2 will be answered as predicted (resulting finding == f2)
		theCase.setValue(q2, new Object[]{ans2});

		// q4 will be answered, but not as predicted (resulting finding != f4)
		theCase.setValue(q4, new Object[]{ans2});

		Hypothesis hyp1 = HypothesisPool.getInstance().getHypothesis(new Object[]{d4});
		// adding diagnosis
		hyp1.addSCDiagnosis(d2);

		Set explained1 = SetPool.getInstance().getFilledSet(new Object[]{f2});
		Set negativeObs1 = SetPool.getInstance().getFilledSet(new Object[]{f4});
		assertEquals("Explained-map of hyp1 wrong.", explained1, hyp1.getExplainedFindings(theCase));

		assertEquals("NegObs-map of hyp1 wrong.", negativeObs1, hyp1
				.getNegativePredictedFindings(theCase));

		Hypothesis hyp2 = HypothesisPool.getInstance().getHypothesis(new Object[]{d1});
		Set explained2 = SetPool.getInstance().getFilledSet(new Object[]{f2});
		Set negativeObs2 = SetPool.getInstance().getFilledSet(new Object[]{f1});
		assertEquals("Explained-map of hyp2 wrong.", explained2, hyp2.getExplainedFindings(theCase));

		assertEquals("NegObs-map of hyp2 wrong.", negativeObs2, hyp2
				.getNegativePredictedFindings(theCase));

		Hypothesis hyp3 = HypothesisPool.getInstance().getHypothesis(new Object[]{d3, d2});
		Set explained3 = SetPool.getInstance().getEmptySet();
		Set negativeObs3 = SetPool.getInstance().getFilledSet(new Object[]{f1, f4});
		assertEquals("Explained-map of hyp3 wrong.", explained3, hyp3.getExplainedFindings(theCase));

		assertEquals("NegObs-map of hyp3 wrong.", negativeObs3, hyp3
				.getNegativePredictedFindings(theCase));
	}

	public void testHypothesisEvaluation() {
		XPSCase theCase = CaseFactory.createXPSCase(kb);
		theCase.setUsedPSMethods(Utils.createList(new Object[]{PSMethodSetCovering.getInstance()}));
		PSMethodSetCovering.getInstance().init(theCase);

		// q1 will be answered, but not as predicted (resulting finding != f1)
		theCase.setValue(q1, new Object[]{ans2});

		// q2 will be answered as predicted (resulting finding == f2)
		theCase.setValue(q2, new Object[]{ans2});

		// q4 will be answered, but not as predicted (resulting finding != f4)
		theCase.setValue(q4, new Object[]{ans2});

		Hypothesis hyp1 = HypothesisPool.getInstance().getHypothesis(new Object[]{d4});
		assertEquals("Quality of hyp1 not correct", 0.00833, hyp1.getQuality(theCase), EPSILON);
		assertEquals("FalsePredAcc of hyp1 not correct", 0,
				hyp1.getFalsePredictionAccount(theCase), EPSILON);

		Hypothesis hyp2 = HypothesisPool.getInstance().getHypothesis(new Object[]{d3, d4});
		assertEquals("Quality of hyp2 not correct", 0.00833, hyp2.getQuality(theCase), EPSILON);
		assertEquals("FalsePredAcc of hyp2 not correct", 0.02499, hyp2
				.getFalsePredictionAccount(theCase), EPSILON);

		Hypothesis hyp3 = HypothesisPool.getInstance().getHypothesis(new Object[]{d1});
		assertEquals("Quality of hyp3 not correct", 0.054, hyp3.getQuality(theCase), EPSILON);
		assertEquals("FalsePredAcc of hyp3 not correct", 0.0375, hyp3
				.getFalsePredictionAccount(theCase), EPSILON);

		Hypothesis hyp4 = HypothesisPool.getInstance().getHypothesis(new Object[]{d2, d4});
		assertEquals("Quality of hyp4 not correct", 0.00833, hyp4.getQuality(theCase), EPSILON);
		assertEquals("FalsePredAcc of hyp4 not correct", 0.02499, hyp4
				.getFalsePredictionAccount(theCase), EPSILON);

		// test sorting by quality and false prediction count

		SortedList sl = new SortedList(new HypothesisComparator(theCase), Utils
				.createList(new Object[]{hyp1, hyp2, hyp3, hyp4}));

		assertEquals("hypothesis-sorting wrong", Utils.createList(new Object[]{hyp3, hyp1, hyp2,
				hyp4}), sl);

	}

}
