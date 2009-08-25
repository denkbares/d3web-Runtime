package de.d3web.kernel.verbalizer.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for de.d3web.kernel.verbalizer.test");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestConditionVerbalizer.class);
		suite.addTestSuite(TestRuleActionVerbalizer.class);
		//$JUnit-END$
		return suite;
	}

}
