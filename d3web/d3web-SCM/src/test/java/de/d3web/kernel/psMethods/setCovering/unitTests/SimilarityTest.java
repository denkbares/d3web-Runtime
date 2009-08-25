package de.d3web.kernel.psMethods.setCovering.unitTests;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerFactory;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.domainModel.qasets.QuestionOC;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCNodeFactory;
import de.d3web.kernel.psMethods.shared.comparators.num.QuestionComparatorNumDivision;
import de.d3web.kernel.psMethods.shared.comparators.num.QuestionComparatorNumDivisionDenominator;

/**
 * These tests check, if the similarity-computation will be triggered correctly
 * 
 * @author bates
 * 
 */
public class SimilarityTest extends TestCase {

	private final double EPSILON = 0.001;

	private PredictedFinding finding1 = null;
	private PredictedFinding finding2 = null;

	private Question q1 = null;
	private Question q2 = null;

	private AnswerNum ansNum1 = null;
	private AnswerNum ansNum2 = null;

	public SimilarityTest(String name) {
		super(name);
	}

	public static TestSuite suite() {
		return new TestSuite(SimilarityTest.class);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SimilarityTest.suite());
	}

	public void setUp() {
		q1 = new QuestionNum();
		q1.setId("Q1");
		ansNum1 = new AnswerNum();
		ansNum1.setValue(new Double(1));

		q2 = new QuestionNum();
		q2.setId("Q2");
		ansNum2 = new AnswerNum();
		ansNum2.setValue(new Double(2));

		finding1 = SCNodeFactory.createFindingEquals(q1, new Object[]{ansNum1});
		finding2 = SCNodeFactory.createFindingEquals(q2, new Object[]{ansNum2});
	}

	/**
	 * Test if the default-comparison (inidividual) is taken, if no
	 * QuestionComparator is defined.
	 */
	public void testDefaultSimilarityComputation() {
		PredictedFinding finding1b = SCNodeFactory.createFindingEquals(q1, new Object[]{ansNum2});
		assertEquals("default-similarity calculation wrong (0)", 0, finding1
				.calculateSimilarity(finding1b), 0.0001);
		assertEquals("default-similarity calculation wrong (1)", 0, finding1
				.calculateSimilarity(finding2), 0.0001);
		assertEquals("default-similarity calculation wrong (2)", 1, finding1
				.calculateSimilarity(finding1), 0.0001);
	}

	/**
	 * Uses QuestionComparators and tests, if they are taken for comparison.
	 */
	public void testSimilarityComputationWithComparator() {
		PredictedFinding finding1b = SCNodeFactory.createFindingEquals(q1, new Object[]{ansNum2});
		PredictedFinding finding2b = SCNodeFactory.createFindingEquals(q2, new Object[]{ansNum1});
		QuestionComparatorNumDivision qcomp1 = new QuestionComparatorNumDivision();
		qcomp1.setQuestion((Question) finding1.getNamedObject());

		QuestionComparatorNumDivisionDenominator qcomp2 = new QuestionComparatorNumDivisionDenominator();
		qcomp2.setDenominator(10);
		qcomp2.setQuestion((Question) finding2.getNamedObject());

		assertEquals("similarity-computation not triggered (1)", 0.5, finding1
				.calculateSimilarity(finding1b), 0.001);
		assertEquals("similarity-computation not triggered (2)", 0.9, finding2b
				.calculateSimilarity(finding2), 0.001);
	}

	public void testSimilarityComputationWithNonTerminalCondition() {
		AnswerChoice ans1 = AnswerFactory.createAnswerChoice("1", "1");
		AnswerChoice ans2 = AnswerFactory.createAnswerChoice("2", "2");
		AnswerChoice ans3 = AnswerFactory.createAnswerChoice("3", "3");
		AnswerChoice ans4 = AnswerFactory.createAnswerChoice("4", "4");

		QuestionOC question = new QuestionOC();
		question.setId("question");
		ans1.setQuestion(question);
		ans2.setQuestion(question);
		ans3.setQuestion(question);
		ans4.setQuestion(question);

		PredictedFinding pf1OR2 = SCNodeFactory.createFindingOR(question, new Object[]{ans1, ans2});
		PredictedFinding pf1OR3 = SCNodeFactory.createFindingOR(question, new Object[]{ans1, ans3});
		PredictedFinding pf3OR4 = SCNodeFactory.createFindingOR(question, new Object[]{ans3, ans4});

		PredictedFinding pf1AND2 = SCNodeFactory.createFindingAND(question,
				new Object[]{ans1, ans3});

		PredictedFinding pf1 = SCNodeFactory.createFindingEquals(question, ans1);
		PredictedFinding pf3 = SCNodeFactory.createFindingEquals(question, ans3);

		assertEquals("sim wrong(0)", 1.0, pf1OR2.calculateSimilarity(pf1OR2), EPSILON);
		assertEquals("sim wrong(1)", 1.0, pf1OR2.calculateSimilarity(pf1), EPSILON);
		assertEquals("sim wrong(2)", 0.0, pf1OR2.calculateSimilarity(pf3), EPSILON);
		assertEquals("sim wrong(3)", 1.0, pf1OR2.calculateSimilarity(pf1OR3), EPSILON);
		assertEquals("sim wrong(4)", 0.0, pf1AND2.calculateSimilarity(pf1), EPSILON);
		assertEquals("sim wrong(5)", 1.0, pf1AND2.calculateSimilarity(pf1AND2), EPSILON);
		assertEquals("sim wrong(6)", 0.0, pf1OR2.calculateSimilarity(pf3OR4), EPSILON);
	}

}
