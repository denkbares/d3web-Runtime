package de.d3web.kernel.psMethods.setCovering.unitTests;

import java.util.Arrays;
import java.util.Set;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.CaseFactory;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.psMethods.setCovering.PSMethodSetCovering;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.kernel.psMethods.setCovering.SCProbability;
import de.d3web.kernel.psMethods.setCovering.SCRelationFactory;
import de.d3web.kernel.psMethods.setCovering.algorithms.EliminationHypothesesGenerationAlgorithm;
import de.d3web.kernel.psMethods.setCovering.algorithms.FullEliminationStrategy;
import de.d3web.kernel.psMethods.setCovering.algorithms.PartialEliminationStrategy;
import de.d3web.kernel.psMethods.setCovering.pools.HypothesisPool;
import de.d3web.kernel.psMethods.setCovering.pools.SetPool;
import de.d3web.kernel.psMethods.setCovering.strategies.hypothesesGeneration.DefaultBestDiagnosesSelectionStrategy;
import de.d3web.kernel.psMethods.setCovering.strategies.hypothesesGeneration.DefaultBestFindingSelectionStrategy;
import de.d3web.kernel.psMethods.setCovering.unitTests.utils.TestUtils;
import de.d3web.kernel.utilities.Utils;

/**
 * Tests all hypotheses-generation algorithms
 * 
 * @author bruemmer
 * 
 */
public class HypothesesGenerationTest extends TestCase {

	private KnowledgeBase kb = null;
	private XPSCase theCase = null;

	private SCDiagnosis d1, d2, d3, d4, d5 = null;
	private PredictedFinding f1, f2, f3, f4 = null;

	public HypothesesGenerationTest(String name) {
		super(name);
	}

	public static TestSuite suite() {
		return new TestSuite(HypothesesGenerationTest.class);
	}

	public static void main(String[] args) {
		TestRunner.run(HypothesesGenerationTest.class);
	}

	public void setUp() {

		kb = new KnowledgeBase();

		d1 = TestUtils.createSCDiagnosis("d1", kb);
		d2 = TestUtils.createSCDiagnosis("d2", kb);
		d3 = TestUtils.createSCDiagnosis("d3", kb);
		d4 = TestUtils.createSCDiagnosis("d4", kb);
		d5 = TestUtils.createSCDiagnosis("d5", kb);

		f1 = TestUtils.createFindingNum("f1", 1, kb, 1);
		f2 = TestUtils.createFindingNum("f2", 2, kb, 2);
		f3 = TestUtils.createFindingNum("f3", 3, kb, 3);
		f4 = TestUtils.createFindingNum("f4", 4, kb, 4);

		SCRelationFactory.createSCRelation(d1, d3, Arrays.asList(new Object[]{new SCProbability(
				"P1", 0.075)}));
		SCRelationFactory.createSCRelation(d1, d4, Arrays.asList(new Object[]{new SCProbability(
				"N1", 0.025)}));
		SCRelationFactory.createSCRelation(d2, d4, Arrays.asList(new Object[]{new SCProbability(
				"P2", 0.15)}));
		SCRelationFactory.createSCRelation(d2, d5, Arrays.asList(new Object[]{new SCProbability(
				"N2", 0.015)}));
		SCRelationFactory.createSCRelation(d3, f1, Arrays.asList(new Object[]{new SCProbability(
				"P2", 0.15)}));
		SCRelationFactory.createSCRelation(d4, f2, Arrays.asList(new Object[]{new SCProbability(
				"N2", 0.015)}));
		SCRelationFactory.createSCRelation(d4, f3, Arrays.asList(new Object[]{new SCProbability(
				"P3", 0.3)}));
		SCRelationFactory.createSCRelation(d5, f3, Arrays.asList(new Object[]{new SCProbability(
				"P1", 0.075)}));
		SCRelationFactory.createSCRelation(d5, f4, Arrays.asList(new Object[]{new SCProbability(
				"N1", 0.025)}));

		theCase = CaseFactory.createXPSCase(kb);
		theCase.setUsedPSMethods(Utils.createList(new Object[]{PSMethodSetCovering.getInstance()}));
		PSMethodSetCovering.getInstance().init(theCase);
	}

	public void testFullEliminationAlgorithm() {
		AnswerNum ansNum1 = new AnswerNum();
		ansNum1.setValue(new Double(1));

		AnswerNum ansNum3 = new AnswerNum();
		ansNum3.setValue(new Double(3));

		AnswerNum ansNum100 = new AnswerNum();
		ansNum100.setValue(new Double(100));

		// entering observations
		theCase.setValue((Question) f1.getNamedObject(), new Object[]{ansNum1});
		theCase.setValue((Question) f3.getNamedObject(), new Object[]{ansNum3});
		theCase.setValue((Question) f4.getNamedObject(), new Object[]{ansNum100});

		EliminationHypothesesGenerationAlgorithm alg = EliminationHypothesesGenerationAlgorithm
				.getInstance();
		alg.setEliminationStrategy(FullEliminationStrategy.getInstance());

		alg.generateHypotheses(theCase, DefaultBestFindingSelectionStrategy.getInstance(),
				DefaultBestDiagnosesSelectionStrategy.getInstance(), 20, 0, 5);

		Set generatedHypotheses = PSMethodSetCovering.getInstance().getGlobalHypothesesSet(theCase);

		// defining compare-hypotheses

		Set hypotheses = SetPool.getInstance().getEmptySet();
		hypotheses.add(HypothesisPool.getInstance().getHypothesis(new Object[]{d4}));
		hypotheses.add(HypothesisPool.getInstance().getHypothesis(new Object[]{d2}));
		hypotheses.add(HypothesisPool.getInstance().getHypothesis(new Object[]{d1}));
		hypotheses.add(HypothesisPool.getInstance().getHypothesis(new Object[]{d5}));
		hypotheses.add(HypothesisPool.getInstance().getHypothesis(new Object[]{d4, d3}));
		hypotheses.add(HypothesisPool.getInstance().getHypothesis(new Object[]{d2, d3}));
		hypotheses.add(HypothesisPool.getInstance().getHypothesis(new Object[]{d2, d1}));
		hypotheses.add(HypothesisPool.getInstance().getHypothesis(new Object[]{d5, d3}));
		hypotheses.add(HypothesisPool.getInstance().getHypothesis(new Object[]{d5, d1}));

		assertEquals("full elimination algorithm wrong.", hypotheses, generatedHypotheses);

	}

	public void testPartialEliminationAlgorithm() {
		AnswerNum ansNum1 = new AnswerNum();
		ansNum1.setValue(new Double(1));

		AnswerNum ansNum3 = new AnswerNum();
		ansNum3.setValue(new Double(3));

		AnswerNum ansNum100 = new AnswerNum();
		ansNum100.setValue(new Double(100));

		// entering observations
		theCase.setValue((Question) f1.getNamedObject(), new Object[]{ansNum1});
		theCase.setValue((Question) f3.getNamedObject(), new Object[]{ansNum3});
		theCase.setValue((Question) f4.getNamedObject(), new Object[]{ansNum100});

		EliminationHypothesesGenerationAlgorithm alg = EliminationHypothesesGenerationAlgorithm
				.getInstance();
		alg.setEliminationStrategy(PartialEliminationStrategy.getInstance());

		alg.generateHypotheses(theCase, DefaultBestFindingSelectionStrategy.getInstance(),
				DefaultBestDiagnosesSelectionStrategy.getInstance(), 20, 0, 5);

		Set generatedHypotheses = PSMethodSetCovering.getInstance().getGlobalHypothesesSet(theCase);

		// defining compare-hypotheses

		Set hypotheses = SetPool.getInstance().getEmptySet();
		hypotheses.add(HypothesisPool.getInstance().getHypothesis(new Object[]{d4}));
		hypotheses.add(HypothesisPool.getInstance().getHypothesis(new Object[]{d2}));
		hypotheses.add(HypothesisPool.getInstance().getHypothesis(new Object[]{d1}));
		hypotheses.add(HypothesisPool.getInstance().getHypothesis(new Object[]{d5}));
		hypotheses.add(HypothesisPool.getInstance().getHypothesis(new Object[]{d2, d1}));
		hypotheses.add(HypothesisPool.getInstance().getHypothesis(new Object[]{d5, d4}));
		hypotheses.add(HypothesisPool.getInstance().getHypothesis(new Object[]{d5, d1}));

		assertEquals("partial elimination algorithm wrong.", hypotheses, generatedHypotheses);

	}

}
