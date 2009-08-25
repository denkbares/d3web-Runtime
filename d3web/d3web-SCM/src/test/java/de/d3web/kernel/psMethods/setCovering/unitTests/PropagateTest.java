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
import de.d3web.kernel.psMethods.setCovering.ObservableFinding;
import de.d3web.kernel.psMethods.setCovering.PSMethodSetCovering;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.kernel.psMethods.setCovering.SCNodeFactory;
import de.d3web.kernel.psMethods.setCovering.SCProbability;
import de.d3web.kernel.psMethods.setCovering.SCRelationFactory;
import de.d3web.kernel.psMethods.setCovering.pools.SetPool;
import de.d3web.kernel.utilities.Utils;

/**
 * This TestCase tests everything that happens when executing
 * PSMethodSetcovering.propagate(XPSCase).
 * 
 * @author bruemmer
 */
public class PropagateTest extends TestCase {

	private KnowledgeBase kb = null;

	private QuestionNum q1, q2, q3, q4 = null;
	private Diagnosis diag1, diag2, diag3, diag4 = null;

	private PredictedFinding f1, f2, f3, f4 = null;
	private SCDiagnosis d1, d2, d3, d4 = null;

	private AnswerNum ans1, ans2, ans3, ans4 = null;

	public PropagateTest(String name) {
		super(name);
	}

	public static TestSuite suite() {
		return new TestSuite(PropagateTest.class);
	}

	public static void main(String[] args) {
		TestRunner.run(PropagateTest.class);
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

	/**
	 * Test the whole propagation-mechanism. A new XPSCase will be created and
	 * some Questions become answered. This test will check, if the explanation
	 * quotient and the explanation possibility have been computed correctly,
	 * etc.
	 */
	public void testPropagation() {
		XPSCase theCase = CaseFactory.createXPSCase(kb);
		theCase.setUsedPSMethods(Utils.createList(new Object[]{PSMethodSetCovering.getInstance()}));
		PSMethodSetCovering.getInstance().init(theCase);

		// q1 will be answered, but not as predicted (resulting finding != f1)
		theCase.setValue(q1, new Object[]{ans2});

		// q2 will be answered as predicted (resulting finding == f2)
		theCase.setValue(q2, new Object[]{ans2});

		// q4 will be answered, but not as predicted (resulting finding != f4)
		theCase.setValue(q4, new Object[]{ans2});

		// create findings for differently answered questions
		ObservableFinding f1x = SCNodeFactory.createObservableFinding(q1, new Object[]{ans2});
		ObservableFinding f4x = SCNodeFactory.createObservableFinding(q4, new Object[]{ans2});

		// *** check transitive covered findings ***

		Set transCovFind_d1 = SetPool.getInstance().getFilledSet(new Object[]{f1, f2, f3});
		Set transCovFind_d2 = SetPool.getInstance().getFilledSet(new Object[]{f4});
		Set transCovFind_d3 = SetPool.getInstance().getFilledSet(new Object[]{f1});
		Set transCovFind_d4 = SetPool.getInstance().getFilledSet(new Object[]{f2, f3});

		assertEquals("transitive covered findings wrong for d1", transCovFind_d1, d1
				.getTransitivePredictedFindings());
		assertEquals("transitive covered findings wrong for d2", transCovFind_d2, d2
				.getTransitivePredictedFindings());
		assertEquals("transitive covered findings wrong for d3", transCovFind_d3, d3
				.getTransitivePredictedFindings());
		assertEquals("transitive covered findings wrong for d4", transCovFind_d4, d4
				.getTransitivePredictedFindings());

		// *** check transitive observed findings ***

		Set transObsFind_d1 = SetPool.getInstance().getFilledSet(new Object[]{f1x, f2});
		Set transObsFind_d2 = SetPool.getInstance().getFilledSet(new Object[]{f4x});
		Set transObsFind_d3 = SetPool.getInstance().getFilledSet(new Object[]{f1x});
		Set transObsFind_d4 = SetPool.getInstance().getFilledSet(new Object[]{f2});

		assertEquals("transitive observed findings wrong for d1", transObsFind_d1, d1
				.getObservedFindings(theCase));

		assertEquals("transitive observed findings wrong for d2", transObsFind_d2, d2
				.getObservedFindings(theCase));

		assertEquals("transitive observed findings wrong for d3", transObsFind_d3, d3
				.getObservedFindings(theCase));

		assertEquals("transitive observed findings wrong for d4", transObsFind_d4, d4
				.getObservedFindings(theCase));

		// *** check transitive parametric predicted findings ***

		Set transParPredFind_d1 = SetPool.getInstance().getFilledSet(new Object[]{f1, f2});
		Set transParPredFind_d2 = SetPool.getInstance().getFilledSet(new Object[]{f4});
		Set transParPredFind_d3 = SetPool.getInstance().getFilledSet(new Object[]{f1});
		Set transParPredFind_d4 = SetPool.getInstance().getFilledSet(new Object[]{f2});

		assertEquals("transitive parametrically predicted findings wrong for d1",
				transParPredFind_d1, d1.getTransitiveParametricPredictedFindings(theCase));

		assertEquals("transitive parametrically predicted findings wrong for d2",
				transParPredFind_d2, d2.getTransitiveParametricPredictedFindings(theCase));

		assertEquals("transitive parametrically predicted findings wrong for d3",
				transParPredFind_d3, d3.getTransitiveParametricPredictedFindings(theCase));

		assertEquals("transitive parametrically predicted findings wrong for d4",
				transParPredFind_d4, d4.getTransitiveParametricPredictedFindings(theCase));

		// *** check negatively observed findings ***

		Set transNegPredFind_d1 = SetPool.getInstance().getFilledSet(new Object[]{f1});
		Set transNegPredFind_d2 = SetPool.getInstance().getFilledSet(new Object[]{f4});
		Set transNegPredFind_d3 = SetPool.getInstance().getFilledSet(new Object[]{f1});
		Set transNegPredFind_d4 = SetPool.getInstance().getEmptySet();

		assertEquals("transitive negatively predicted findings wrong for d1", transNegPredFind_d1,
				d1.getTransitiveNegativePredictedFindings(theCase));

		assertEquals("transitive negatively predicted findings wrong for d2", transNegPredFind_d2,
				d2.getTransitiveNegativePredictedFindings(theCase));

		assertEquals("transitive negatively predicted findings wrong for d3", transNegPredFind_d3,
				d3.getTransitiveNegativePredictedFindings(theCase));

		assertEquals("transitive negatively predicted findings wrong for d4", transNegPredFind_d4,
				d4.getTransitiveNegativePredictedFindings(theCase));

	}
}
