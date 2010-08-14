package de.d3web.empiricaltesting.casevisualization;

import java.io.ByteArrayOutputStream;
import java.util.List;

import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestSuite;

public interface CaseVisualizer {

	public void writeToFile(TestSuite testsuite, String filepath);

	public void writeToFile(List<SequentialTestCase> cases, String filepath);

	public ByteArrayOutputStream getByteArrayOutputStream(List<SequentialTestCase> cases);

}
