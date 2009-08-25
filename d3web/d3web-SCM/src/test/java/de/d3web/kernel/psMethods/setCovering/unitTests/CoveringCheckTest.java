package de.d3web.kernel.psMethods.setCovering.unitTests;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.CaseFactory;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.NumericalInterval;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerFactory;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.domainModel.qasets.QuestionMC;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.psMethods.setCovering.PSMethodSetCovering;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCNodeFactory;

/**
 * This TestCase tests the covers()-Method of all types of implemented Findings
 * 
 * @author bruemmer
 */
public class CoveringCheckTest extends TestCase {

	PredictedFinding fnum1, fnum2, fmc1, fmc2, fmc3 = null;
	QuestionNum qnum1 = null;
	QuestionMC qmc = null;
	AnswerChoice ac1, ac2, ac3, ac4 = null;

	XPSCase theCase = null;

	public CoveringCheckTest(String name) {
		super(name);
	}

	public static TestSuite suite() {
		return new TestSuite(CoveringCheckTest.class);
	}

	public static void main(String[] args) {
		TestRunner.run(CoveringCheckTest.class);
	}

	public void setUp() {

		// knowledge base
		KnowledgeBase kb = new KnowledgeBase();

		// numerical findings

		AnswerNum ansNum1 = new AnswerNum();
		ansNum1.setValue(new Double(1));

		qnum1 = new QuestionNum();
		qnum1.setId("qnum1");
		qnum1.setKnowledgeBase(kb);

		NumericalInterval interval1 = new NumericalInterval(0, 1, true, true);
		NumericalInterval interval2 = new NumericalInterval(0, 1, false, false);

		fnum1 = SCNodeFactory.createFindingNum(qnum1, interval1);
		fnum2 = SCNodeFactory.createFindingNum(qnum1, interval2);

		// multiple choice findings

		ac1 = AnswerFactory.createAnswerChoice("a1", "ac1");
		ac2 = AnswerFactory.createAnswerChoice("a2", "ac2");
		ac3 = AnswerFactory.createAnswerChoice("a3", "ac3");
		ac4 = AnswerFactory.createAnswerChoice("a4", "ac4");

		qmc = new QuestionMC();
		qmc.setId("qmc");
		qmc.setKnowledgeBase(kb);

		fmc1 = SCNodeFactory.createFindingOR(qmc, new Object[]{ac1, ac2, ac3, ac4});
		fmc2 = SCNodeFactory.createFindingOR(qmc, new Object[]{ac2, ac3});
		fmc3 = SCNodeFactory.createFindingAND(qmc, new Object[]{ac2, ac3});

		theCase = CaseFactory.createXPSCase(kb);
		PSMethodSetCovering.getInstance().init(theCase);

	}

	public void testNum() {

		theCase.setValue(qnum1, new Object[]{qnum1.getAnswer(theCase, new Double(1))});
		assertTrue("numerical covering check not correct (0)", !fnum1.covers(theCase));
		assertTrue("numerical covering check not correct (1)", fnum2.covers(theCase));

		theCase.setValue(qnum1, new Object[]{qnum1.getAnswer(theCase, new Double(0.5))});
		assertTrue("numerical covering check not correct (2)", fnum1.covers(theCase));

	}

	public void testOR() {
		theCase.setValue(qmc, new Object[]{ac1, ac2, ac3});
		assertTrue("multiple choice covering check not correct (0)", fmc1.covers(theCase));
		assertTrue("multiple choice covering check not correct (1)", fmc2.covers(theCase));

		theCase.setValue(qmc, new Object[]{ac1, ac4});
		assertTrue("multiple choice covering check not correct (2)", !fmc2.covers(theCase));
	}

	public void testAnd() {
		theCase.setValue(qmc, new Object[]{ac1, ac2, ac3});
		assertTrue("multiple choice covering check not correct (0)", fmc3.covers(theCase));

		theCase.setValue(qmc, new Object[]{ac1, ac2});
		assertTrue("multiple choice covering check not correct (1)", !fmc3.covers(theCase));

		theCase.setValue(qmc, new Object[]{ac3});
		assertTrue("multiple choice covering check not correct (2)", !fmc3.covers(theCase));
	}
}
