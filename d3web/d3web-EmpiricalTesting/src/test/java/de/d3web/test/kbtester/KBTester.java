/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.d3web.test.kbtester;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestCase;
import de.d3web.empiricaltesting.TestPersistence;
import de.d3web.empiricaltesting.caseAnalysis.functions.TestCaseAnalysis;
import de.d3web.empiricaltesting.caseAnalysis.functions.TestCaseAnalysisReport;
import de.d3web.plugin.test.InitPluginManager;

/**
 * This test offers the ability to test the problem solving mechanism of the
 * d3web-kernel using the EmpiricalTesting Project.
 * 
 * The KnowledgeBases and TestCases can be configured in
 * resources/cases.properties
 * 
 * @author Sebastian Furth
 * 
 */
public class KBTester {

	private List<TestCase> testsuites;

	/**
	 * Runs all loaded TestSuites. In each TestSuite the derived solutions are
	 * checked for actuality and correctness and the total precision and recall
	 * will be calculated. If specified the total precision and recall for the
	 * interview will also be tested.
	 */
	@Test
	public void testKB() {
		for (TestCase t : testsuites) {
			testPrecisionAndRecall(t);
			if (t.interviewAgendsIsRelevant()) testPrecisionAndRecallInterview(t);
		}
	}

	/**
	 * Tests the Interview-Precision and Interview-Calculation.
	 * 
	 * @param t the underlying TestSuite
	 */
	private void testPrecisionAndRecallInterview(TestCase t) {
		TestCaseAnalysis analysis = new TestCaseAnalysis();
		TestCaseAnalysisReport result = analysis.runAndAnalyze(t);
		StringBuilder precisionFailureMsg = new StringBuilder();
		precisionFailureMsg.append("\nInterview-Precision differ in test suite ");
		precisionFailureMsg.append(t.getName() + " should be 1.0 but is ");

		StringBuilder recallFailureMsg = new StringBuilder();
		recallFailureMsg.append("\nInterview-Recall differ in test suite ");
		recallFailureMsg.append(t.getName() + " should be 1.0 but is ");
		recallFailureMsg.append(result.interviewRecall(t.getKb()) + "\n");
		assertEquals(recallFailureMsg.toString(), 1.0, result.interviewRecall(t.getKb()), 0.0);

	}

	/**
	 * Tests the Precision and Recall of the derived solutions.
	 * 
	 * @param t the underlying TestSuite
	 */
	private void testPrecisionAndRecall(TestCase t) {
		TestCaseAnalysis analysis = new TestCaseAnalysis();
		TestCaseAnalysisReport result = analysis.runAndAnalyze(t);

		StringBuilder recallFailureMsg = new StringBuilder();
		recallFailureMsg.append("\nRecall differ in test suite ");
		recallFailureMsg.append(t.getName() + " should be 1.0 but is ");
		recallFailureMsg.append(result.recall() + "\n");
		assertEquals(recallFailureMsg.toString(), 1.0, result.recall(), 0.0);

	}

	/**
	 * Creates the TestSuites necessary for this JUNIT-Test. Therefore the
	 * configuration file (cases.properties) is read. The configuration file
	 * contains a list of KnowledgeBases with corresponding SequentialTestCases
	 * and a declaration whether the InterviewCalculator should be used or not
	 * for this case.
	 * 
	 * @throws IOException
	 */
	@Before
	public void initialize() throws IOException {
		testsuites = new ArrayList<TestCase>();
		String userdir = System.getProperty("user.dir") + "/src/test/resources/";
		FileReader f;
		f = new FileReader(userdir + "cases.properties");
		BufferedReader r = new BufferedReader(f);
		String s;
		while ((s = r.readLine()) != null) {
			if (s.startsWith("#")) {
				continue;
			}
			else {
				String[] config = s.split(";");
				String kbpath = userdir + config[0];
				String casespath = userdir + config[1];

				KnowledgeBase kb = loadKnowledgeBase(kbpath);
				TestCase t = new TestCase();
				t.setName(config[1]);
				t.setKb(kb);
				if (config[2].equals("true")) {
					t.setInterviewAgendsIsRelevant(true);
				}

				List<SequentialTestCase> cases = TestPersistence.getInstance().loadCases(
						new File(casespath).toURI().toURL(), kb);
				t.setRepository(cases);

				testsuites.add(t);
			}
		}
		f.close();
	}

	/**
	 * Loads the KnowledgeBase.
	 * 
	 * @param kbPath URL-formatted String representing the path to the
	 *        KnowledgeBase
	 * @return the loaded KnowledgeBase
	 * @throws IOException
	 */
	private static KnowledgeBase loadKnowledgeBase(String kbPath)
			throws IOException {
		InitPluginManager.init();
		PersistenceManager pm = PersistenceManager.getInstance();
		KnowledgeBase kb = pm.load(new File(kbPath));

		return kb;
	}
}
