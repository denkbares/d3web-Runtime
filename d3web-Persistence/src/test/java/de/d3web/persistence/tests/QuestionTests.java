package de.d3web.persistence.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author merz
 *
 * !!! propertytest missing !!!
 */
public class QuestionTests extends TestCase {

	/**
	 * Constructor for QuestionTests.
	 * @param arg0
	 */
	public QuestionTests(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(QuestionTests.suite());
	}
	
	public static Test suite() {
		de.d3web.kernel.domainModel.D3WebCase.TRACE = true;
		TestSuite suite = new junit.framework.TestSuite("Suite for question-tests");
		
		suite.addTest(QuestionNumTest.suite());
		suite.addTest(QuestionChoiceTest.suite());
		suite.addTest(QuestionTextTest.suite());
		suite.addTest(QuestionDateTest.suite());
		
		return suite;
	}
}
