package de.d3web.kernel.psMethods.setCovering.unitTests;

import java.util.Set;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.kernel.psMethods.setCovering.SCNodeFactory;
import de.d3web.kernel.psMethods.setCovering.SCProbability;
import de.d3web.kernel.psMethods.setCovering.SCRelationFactory;
import de.d3web.kernel.psMethods.setCovering.pools.SetPool;
import de.d3web.kernel.psMethods.setCovering.strategies.transitivePropagation.DefaultStrengthCalculationStrategy;
import de.d3web.kernel.psMethods.setCovering.strategies.transitivePropagation.DefaultStrengthSelectionStrategy;
import de.d3web.kernel.psMethods.setCovering.strategies.transitivePropagation.StrengthCalculationStrategy;
import de.d3web.kernel.psMethods.setCovering.strategies.transitivePropagation.StrengthSelectionStrategy;
import de.d3web.kernel.psMethods.setCovering.utils.TransitiveClosure;
import de.d3web.kernel.utilities.Utils;

/**
 * This is a TestCase for everything that has to do with the initially
 * transitive propagation
 * 
 * @author bruemmer
 * 
 */
public class TransitivePropagationTest extends TestCase {

	private SCDiagnosis d1, d2, d3 = null;
	private PredictedFinding f1 = null;
	// private SCRelation r1, r2, r3, r4, r5 = null;

	private TransitiveClosure transitiveClosure = null;

	public TransitivePropagationTest(String name) {
		super(name);
	}

	public static TestSuite suite() {
		return new TestSuite(TransitivePropagationTest.class);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TransitivePropagationTest.suite());
	}

	public void setUp() {
		Diagnosis diag1 = new Diagnosis();
		diag1.setId("d1");
		diag1.setText("d1-text");

		Diagnosis diag2 = new Diagnosis();
		diag2.setId("d2");
		diag2.setText("d2-text");

		Diagnosis diag3 = new Diagnosis();
		diag3.setId("d3");
		diag3.setText("d3-text");

		d1 = SCNodeFactory.createSCDiagnosis(diag1);
		d2 = SCNodeFactory.createSCDiagnosis(diag2);
		d3 = SCNodeFactory.createSCDiagnosis(diag3);

		QuestionNum q1 = new QuestionNum();
		q1.setId("f1");
		q1.setText("f1-text");
		f1 = SCNodeFactory.createFindingEquals(q1, new Object[]{q1.getUnknownAlternative()});

		SCRelationFactory
				.createSCRelation(d1, d2, Utils.createList(new Object[]{SCProbability.N2}));
		SCRelationFactory
				.createSCRelation(d2, d3, Utils.createList(new Object[]{SCProbability.P1}));
		SCRelationFactory
				.createSCRelation(d2, f1, Utils.createList(new Object[]{SCProbability.P2}));
		SCRelationFactory
				.createSCRelation(d1, d3, Utils.createList(new Object[]{SCProbability.P3}));
		SCRelationFactory
				.createSCRelation(d3, f1, Utils.createList(new Object[]{SCProbability.N1}));

		transitiveClosure = new TransitiveClosure(SetPool.getInstance().getFilledSet(
				new Object[]{d1, d2, d3, f1}));
	}

	public void testDefaultStrengthCalculationStrategy() {

		Set tempStrengths = null;

		StrengthCalculationStrategy strategy = DefaultStrengthCalculationStrategy.getInstance();

		tempStrengths = SetPool.getInstance().getFilledSet(
				new Object[]{SCProbability.ZERO.getValue(), SCProbability.P2.getValue()});
		assertEquals("strength calculation for d2 -> f1 incorrect", tempStrengths, strategy
				.calculateTransitiveStrengths(transitiveClosure, d2, f1));

		tempStrengths = SetPool.getInstance().getFilledSet(new Object[]{new Double(0.025)});
		assertEquals("strength calculation for d3 -> f1 incorrect", tempStrengths, strategy
				.calculateTransitiveStrengths(transitiveClosure, d3, f1));

	}

	public void testDefaultStrengthSelectionStrategy() {
		StrengthCalculationStrategy calcStrategy = DefaultStrengthCalculationStrategy.getInstance();
		StrengthSelectionStrategy selStrategy = DefaultStrengthSelectionStrategy.getInstance();

		Set tempStrengths = calcStrategy.calculateTransitiveStrengths(transitiveClosure, d2, f1);
		assertEquals("strength selection wrong for d2 -> f1", new Double(0.15), selStrategy
				.selectStrength(tempStrengths));

		tempStrengths = calcStrategy.calculateTransitiveStrengths(transitiveClosure, d1, f1);
		assertEquals("strength selection wrong for d1 -> f1", new Double(0.1625), selStrategy
				.selectStrength(tempStrengths));

		tempStrengths = calcStrategy.calculateTransitiveStrengths(transitiveClosure, d3, f1);
		assertEquals("strength selection wrong for d3 -> f1", new Double(0.025), selStrategy
				.selectStrength(tempStrengths));

	}

	public void testTransitiveCoveredFindings() {
		// **************************************
		// add some more findings and relations

		QuestionNum q2 = new QuestionNum();
		q2.setId("f2");
		q2.setText("f2-text");
		PredictedFinding f2 = SCNodeFactory.createFindingEquals(q2, new Object[]{q2
				.getUnknownAlternative()});

		QuestionNum q3 = new QuestionNum();
		q3.setId("f3");
		q3.setText("f3-text");
		PredictedFinding f3 = SCNodeFactory.createFindingEquals(q3, new Object[]{q3
				.getUnknownAlternative()});

		SCRelationFactory.createSCRelation(d3, f2, null);
		SCRelationFactory.createSCRelation(d2, f3, null);

		transitiveClosure.initialize(SetPool.getInstance().getFilledSet(
				new Object[]{d1, d2, d3, f1, f2, f3}));
		transitiveClosure.calculateClosure();

		// **************************************

		StrengthCalculationStrategy calcStrategy = DefaultStrengthCalculationStrategy.getInstance();
		StrengthSelectionStrategy selStrategy = DefaultStrengthSelectionStrategy.getInstance();

		d1.initialize(transitiveClosure, calcStrategy, selStrategy);
		d2.initialize(transitiveClosure, calcStrategy, selStrategy);
		d3.initialize(transitiveClosure, calcStrategy, selStrategy);

		Set cov1 = SetPool.getInstance().getFilledSet(new Object[]{f1, f2, f3});
		Set cov2 = SetPool.getInstance().getFilledSet(new Object[]{f1, f2, f3});
		Set cov3 = SetPool.getInstance().getFilledSet(new Object[]{f1, f2});

		assertEquals("covered findings of d1 not correctly calculated", cov1, d1
				.getTransitivePredictedFindings());
		assertEquals("covered findings of d2 not correctly calculated", cov2, d2
				.getTransitivePredictedFindings());
		assertEquals("covered findings of d3 not correctly calculated", cov3, d3
				.getTransitivePredictedFindings());

	}

}