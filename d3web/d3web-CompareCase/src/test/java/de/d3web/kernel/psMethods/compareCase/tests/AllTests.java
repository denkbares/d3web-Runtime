package de.d3web.kernel.psMethods.compareCase.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestSuite for all tests that have to do with cbr
 * @author bruemmer
 */
public class AllTests {

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(AllTests.class);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for CBR");

		suite.addTestSuite(ClusteringTest.class);

		return suite;
	}
}
