package de.d3web.empiricalTesting.caseVisualization;

import java.util.List;

import de.d3web.empiricalTesting.SequentialTestCase;
import de.d3web.empiricalTesting.TestSuite;

public interface CaseVisualizer {
	
	public void writeToFile(TestSuite testsuite, String filepath);
	
	public void writeToFile(List<SequentialTestCase> cases, String filepath);

}
