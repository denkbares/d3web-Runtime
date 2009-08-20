package de.d3web.persistence.tests;
import junit.framework.Test;
import junit.framework.TestSuite;
import de.d3web.kernel.domainModel.D3WebCase;

/**
 * Container class for all persistence tests.
 * The suite accessable by this class contains 
 * all test suites registered here.
 * If main() has been started, all registered tests will be executed.
 * Creation date: (05.09.2000 15:59:01)
 * @author mike / merz
 */

public class TestAll
{
	  
/**
 * Starts all registered tests.
 * Creation date: (05.09.2000 16:00:30)
 * @param args java.lang.String[]
 */
public static void main(String[] args) 
{
	D3WebCase.TRACE = false;
	junit.swingui.TestRunner.main(new String [] { "de.d3web.persistence.tests.TestAll"});
}



/**
 * @return a test suite containing all registered tests
 * Creation date: (05.09.2000 16:01:23)
 */
public static Test suite()
{
	D3WebCase.TRACE = true;
	TestSuite suite = new TestSuite("Suite for all tests");

	suite.addTest(ActionTest.suite());
	suite.addTest(ConditionTest.suite());
	suite.addTest(DiagnosisTest.suite());
	suite.addTest(QContainerTest.suite());
	suite.addTest(QuestionTests.suite());
	suite.addTest(CostTest.suite());
	suite.addTest(DCMarkupTest.suite());
	suite.addTest(PropertyTest.suite());
	suite.addTest(PriorityGroupTest.suite());
	suite.addTest(RuleComplexTest.suite());
	suite.addTest(KnowledgeBaseExportTest.suite());
	suite.addTest(TestKfz.suite());
	
	return suite;
}
}