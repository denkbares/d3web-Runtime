package de.d3web.kernel.shared.tests;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;
/**
 * Container class for all tests.
 * The suite accessable by this class contains 
 * all test suites registered here.
 * If main() has been started, all registered tests will be executed.
 * Creation date: (05.09.2000 15:59:01)
 * @author: norman
 */

public class TestAll
{
	  
public static void main(String[] args) {
	TestRunner.main(new String [] { "de.d3web.kernel.shared.tests.TestAll" });
}

/**
 * @return a test suite containing all registered tests
 * Creation date: (05.09.2000 16:01:23)
 */
public static Test suite() {
	TestSuite suite = new TestSuite("Suite for all tests");
	
	suite.addTest(AbnormalityNumTest.suite());
	suite.addTest(RemoveKnowledgeSliceTest.suite());
	return suite;
}
}