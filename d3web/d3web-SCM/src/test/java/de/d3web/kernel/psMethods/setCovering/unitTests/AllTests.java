package de.d3web.kernel.psMethods.setCovering.unitTests;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author bates TestClass-container for all SCM-TestCases
 */
public class AllTests extends TestCase {

	public static void main(String[] args) {
		junit.swingui.TestRunner
				.main(new String[]{"de.d3web.kernel.psMethods.setCovering.unitTests.AllTests"});
	}

	public static TestSuite suite() {
		TestSuite suite = new TestSuite("Suite for all tests for SCM");
		suite.addTest(SCRelationFactoryTest.suite());
		suite.addTest(PoolTest.suite());
		suite.addTest(SCObjectsTest.suite());
		suite.addTest(SimilarityTest.suite());
		suite.addTest(SCMPersistenceTest.suite());
		suite.addTest(TransitiveClosureTest.suite());
		suite.addTest(TransitivePropagationTest.suite());
		suite.addTest(PropagateTest.suite());
		suite.addTest(HypothesisTest.suite());
		suite.addTest(SortedCollectionsTest.suite());
		suite.addTest(HypothesesGenerationStrategiesTest.suite());
		suite.addTest(HypothesesGenerationTest.suite());
		suite.addTest(CoveringCheckTest.suite());
		suite.addTest(RemoveKnowledgeSliceTest.suite());
		
		return suite;
	}
}
