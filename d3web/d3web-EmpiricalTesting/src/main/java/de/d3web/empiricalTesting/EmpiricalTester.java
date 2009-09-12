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

package de.d3web.empiricalTesting;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import de.d3web.empiricalTesting.ddTrees.DDBuilder;
import de.d3web.empiricalTesting.joba.testcases.DDBot2Runner;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.persistence.xml.PersistenceManager;
import de.d3web.persistence.xml.XCLModelPersistenceHandler;

public class EmpiricalTester {

	/**
	 * Here you can specify the KnowlegeBase and the TestCase.
	 * Furthermore you can chose which demos you want to start.
	 * simply comment them out if you don't want to use them.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		String workspace = "d:/eigene projekte/temp/";
		String kbPath = workspace + "car_xcl_temp.jar";
		String casesPath = workspace + "car_xcl_cases.xml";
//		String dotPath = workspace + "pflanzen.dot";
//		String generatedCasesPath = kbPath + "_generated.xml";
		
		demoComputePrecisionAndRecall(kbPath, casesPath);
//		demoBuildDDTree(kbPath, casesPath, dotPath);
//		demoBotTestCases(kbPath, generatedCasesPath);
	}
	
	/**
	 * Demo that computes the Precision and Recall for the
	 * derived solutions and the interview.
	 * @param kbPath URL-formatted String representing the path to the KnowledgeBase
	 * @return the loaded KnowledgeBase
	 * @throws Exception
	 */
	public static void demoComputePrecisionAndRecall(String kbPath,
			String casesPath) throws Exception {
		
		KnowledgeBase kb = loadKnowledgeBase(kbPath);

		TestSuite TS = new TestSuite();
		TS.setKb(kb);
		TS.loadRepository(casesPath);

		System.out.println("DerivedSolutions-Precision: " + TS.totalPrecision());
		System.out.println("DerivedSolutions-Recall: " + TS.totalRecall());
		System.out.println("Interview-Precision: " + TS.totalPrecisionInterview());
		System.out.println("Interview-Recall: " + TS.totalRecallInterview());

		System.out.println("Consistent test suite: " + TS.isConsistent());
		for (RatedTestCase r : TS.getRepository().get(0).getCases()) {
			System.out.println("Derived solutions are up-to-date: "
					+ r.getDerivedSolutionsAreUpToDate() + " and correct: "
					+ r.isCorrect());
		}
	}

	/**
	 * Loads the KnowledgeBase.
	 * @param kbPath URL-formatted String representing the path to the KnowledgeBase
	 * @return the loaded KnowledgeBase
	 * @throws MalformedURLException
	 */
	private static KnowledgeBase loadKnowledgeBase(String kbPath)
			throws MalformedURLException {
		PersistenceManager pm = PersistenceManager.getInstance();
		pm.addPersistenceHandler(new XCLModelPersistenceHandler());
		KnowledgeBase kb = pm.load(new File(kbPath).toURI().toURL());
		return kb;
	}
	
	public static void demoBotTestCases(String kbPath, String casesPath) throws Exception {
		KnowledgeBase k = loadKnowledgeBase(kbPath);
		DDBot2Runner botRunner = new DDBot2Runner(k);
		botRunner.setCaseNamePraefix("STC");
		
		// Example: run ALL cases and store it to a file
		 List<SequentialTestCase> cases =
		 botRunner.generateSequentialTestCases(1,1);
		 botRunner.storeCases(cases, casesPath, true);
		
		// Example: compare two nets and print differences in a dot
//		botRunner.testDriveDDNetComparison(workspace, "some_bigger_exp.xml",
//				"some_bigger_exp2.xml", "some_bigger_exp.dot");
		 
	}

	public static void demoBuildDDTree(String kbPath, String casesPath,
			String dotPath) throws MalformedURLException {
		KnowledgeBase kb = loadKnowledgeBase(kbPath);
		TestSuite TS = new TestSuite();
		TS.setKb(kb);
		TS.loadRepository(casesPath);

		DDBuilder builder = new DDBuilder();
		builder.printDOT(TS, dotPath);
	}


}
