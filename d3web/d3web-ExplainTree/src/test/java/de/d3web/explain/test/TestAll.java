package de.d3web.explain.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Container class for all tests.
 * The suite accessable by this class contains
 * all test suites registered here.
 * If main() has been started, all registered tests will be executed.
 * @author: CBB
 */

public class TestAll {
    
    /**
     * Starts all registered tests.
     * Creation date: (05.09.2000 16:00:30)
     * @param args java.lang.String[]
     */
    public static void main(String[] args) {
        de.d3web.kernel.domainModel.D3WebCase.TRACE = false;
        junit.swingui.TestRunner.main(new String [] { "de.d3web.explain.test.TestAll"});
    }
    
    /**
     * @return a test suite containing all registered tests
     * Creation date: (05.09.2000 16:01:23)
     */
    public static Test suite() {
        TestSuite suite = new junit.framework.TestSuite("Suite for all tests");
        // suite.addTest(RuleSymptomTest.suite());
        suite.addTest(ExplainQASetReasons.suite());
        suite.addTest(ExplainDiagnosisReasons.suite());
        return suite;
    }
}