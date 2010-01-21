/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.test.KBTester;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.kpers.PersistenceManager;
import de.d3web.empiricalTesting.RatedTestCase;
import de.d3web.empiricalTesting.SequentialTestCase;
import de.d3web.empiricalTesting.TestSuite;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.plugin.test.InitPluginManager;


/**
 * This test offers the ability to test the problem
 * solving mechanism of the d3web-kernel using the
 * EmpiricalTesting Project. 
 * 
 * The KnowledgeBases and TestCases can be configured
 * in resources/cases.properties
 * 
 * @author Sebastian Furth
 *
 */
public class KBTester {
	
	private List<TestSuite> testsuites;
	
	/**
	 * Runs all loaded TestSuites.
	 * In each TestSuite the derived solutions are checked for
	 * actuality and correctness and the total precision and
	 * recall will be calculated. If specified the total
	 * precision and recall for the interview will also be tested.
	 */
	@Test
	public void testKB() {	
		for (TestSuite t : testsuites) {
			testCorrectnessAndActuallity(t);
			testPrecisionAndRecall(t);
			if (t.getUseInterviewCalculator())
				testPrecisionAndRecallInterview(t);
		}
	}
	
	/**
	 * Tests the Interview-Precision and Interview-Calculation.
	 * @param t the underlying TestSuite
	 */
	private void testPrecisionAndRecallInterview(TestSuite t) {
		
		StringBuilder precisionFailureMsg = new StringBuilder();
		precisionFailureMsg.append("\nInterview-Precision differ in test suite ");
		precisionFailureMsg.append(t.getName() + " should be 1.0 but is ");
		precisionFailureMsg.append(t.totalPrecisionInterview() + "\n");
		assertEquals(precisionFailureMsg.toString(), 1.0 , t.totalPrecisionInterview(), 0.0);
		
		StringBuilder recallFailureMsg = new StringBuilder();
		recallFailureMsg.append("\nInterview-Recall differ in test suite ");
		recallFailureMsg.append(t.getName() + " should be 1.0 but is ");
		recallFailureMsg.append(t.totalRecallInterview() + "\n");
		assertEquals(recallFailureMsg.toString(), 1.0 , t.totalRecallInterview(), 0.0);
		
	}

	/**
	 * Tests the Precision and Recall of the derived solutions.
	 * @param t the underlying TestSuite
	 */
	private void testPrecisionAndRecall(TestSuite t) {
		
		StringBuilder precisionFailureMsg = new StringBuilder();
		precisionFailureMsg.append("\nPrecision differ in test suite ");
		precisionFailureMsg.append(t.getName() + " should be 1.0 but is ");
		precisionFailureMsg.append(t.totalPrecision() + "\n");
		assertEquals(precisionFailureMsg.toString(), 1.0 , t.totalPrecision(), 0.0);
		
		StringBuilder recallFailureMsg = new StringBuilder();
		recallFailureMsg.append("\nRecall differ in test suite ");
		recallFailureMsg.append(t.getName() + " should be 1.0 but is ");
		recallFailureMsg.append(t.totalRecall() + "\n");
		assertEquals(recallFailureMsg.toString(), 1.0 , t.totalRecall(), 0.0);
		
	}

	/**
	 * Tests the actuality and correctness of the derived solutions.
	 * @param t the underlying TestSuite
	 */
	private void testCorrectnessAndActuallity(TestSuite t) {

			for (SequentialTestCase stc : t.getRepository()) {
				for (RatedTestCase rtc : stc.getCases()) {
					
					StringBuilder notUpToDateErrorMsg = new StringBuilder();
					notUpToDateErrorMsg.append("\nDerived Solutions aren't up to date\n in test suite ");
					notUpToDateErrorMsg.append(t.getName() + "\n in STC ");
					notUpToDateErrorMsg.append(stc.getName() + "\n in RTC ");
					notUpToDateErrorMsg.append(rtc.toString() + "!\n");				
					assertEquals(notUpToDateErrorMsg.toString(), true, rtc.getDerivedSolutionsAreUpToDate());
					
					StringBuilder notCorrectErrorMsg = new StringBuilder();
					notCorrectErrorMsg.append("\nDerived Solutions aren't up to date\n in test suite ");
					notCorrectErrorMsg.append(t.getName() + "\n in STC ");
					notCorrectErrorMsg.append(stc.getName() + "\n in RTC ");
					notCorrectErrorMsg.append(rtc.toString() + "!\n");	
					assertEquals(notCorrectErrorMsg.toString(), true, rtc.isCorrect());
				}
			}
	}
	
	/**
	 * Creates the TestSuites necessary for this JUNIT-Test.
	 * Therefore the configuration file (cases.properties) is read.
	 * The configuration file contains a list of KnowledgeBases with
	 * corresponding SequentialTestCases and a declaration whether
	 * the InterviewCalculator should be used or not for this case.
	 * @throws IOException 
	 */
	@Before
	public void initialize() throws IOException {
		testsuites = new ArrayList<TestSuite>();
		String userdir = System.getProperty("user.dir") + "/src/test/resources/";
		FileReader f;
		f = new FileReader(userdir + "cases.properties");
		BufferedReader r = new BufferedReader(f);
		String s;
		while((s = r.readLine()) != null) {
			if (s.startsWith("#")) {
				continue;
			} else {
				String[] config = s.split(";");
				String kbpath = userdir + config[0];
				String casespath = userdir + config[1];
				
				KnowledgeBase kb = loadKnowledgeBase(kbpath);
				TestSuite t = new TestSuite();
				t.setName(config[1]);
				t.setKb(kb);
				t.loadRepository(casespath);
				if (config[2].equals("true")) {
					t.setUseInterviewCalculator(true);
				}
				testsuites.add(t);
			}
		}
		f.close();
	}
	
	/**
	 * Loads the KnowledgeBase.
	 * @param kbPath URL-formatted String representing the path to the KnowledgeBase
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
