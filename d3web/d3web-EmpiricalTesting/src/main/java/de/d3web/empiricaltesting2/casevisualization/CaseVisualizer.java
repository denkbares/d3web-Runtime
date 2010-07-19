package de.d3web.empiricaltesting2.casevisualization;

import java.io.ByteArrayOutputStream;
import java.util.List;

import de.d3web.empiricaltesting2.SequentialTestCase;
import de.d3web.empiricaltesting2.TestSuite;

public interface CaseVisualizer {
	
	public void writeToFile(TestSuite testsuite, String filepath);
	
	public void writeToFile(List<SequentialTestCase> cases, String filepath);
	
	public ByteArrayOutputStream getByteArrayOutputStream(List<SequentialTestCase> cases);

}
