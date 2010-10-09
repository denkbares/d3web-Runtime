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

package de.d3web.empiricaltesting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import de.d3web.casegeneration.InterviewBot;
import de.d3web.casegeneration.StateRatingStrategy;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.ConsoleProgressListener;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.empiricaltesting.casevisualization.dot.DDBuilder;
import de.d3web.empiricaltesting.casevisualization.jung.JUNGCaseVisualizer;
import de.d3web.empiricaltesting.writer.TestSuiteKnOfficeWriter;
import de.d3web.empiricaltesting.writer.TestSuiteXMLWriter;
import de.d3web.plugin.test.InitPluginManager;

public class EmpiricalTester {

	// Input variables
	static String workspace = "D:/Projekte/Temp/EmpiricalTesting/";
	static String kbFile = "KnowledgeBases/dano.jar";
	static String caseFile = "dano.xml";

	// Output file for DDBuilder (DOT-File for GraphViz)
	static String dotFile = "dano.dot";
	// Output file for JUNGCaseVisualizer (PDF-File)
	static String pdfFile = "dano.pdf";
	// Output files for InterviewBot
	static String xmlFile = "dano";
	static String txtFile = "dano";

	private EmpiricalTester() {
	}

	/**
	 * Driver for the Empirical Testing project
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		InitPluginManager.init();
		// try {
		// demoComputePrecisionAndRecall();
		// demoBotTestCases();
		// demoCaseVisualization();
		// demoBuildDDTree();
		// }
		// catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	/**
	 * Demo that computes the Precision and Recall for the derived solutions and
	 * the interview.
	 * 
	 * @throws Exception
	 */
	public static void demoComputePrecisionAndRecall() throws Exception {

		KnowledgeBase kb = loadKnowledgeBase(workspace + kbFile);
		TestSuite testSuite = new TestSuite();
		testSuite.setKb(kb);
		testSuite.loadRepository(workspace + caseFile);

		System.out.println("DerivedSolutions-Precision (Non-Sequential): "
				+ testSuite.totalNonSequentialPrecision());
		System.out.println("DerivedSolutions-Recall (Non-Sequential): "
				+ testSuite.totalNonSequentialRecall());
		System.out.println("DerivedSolutions-Precision: " + testSuite.totalPrecision());
		System.out.println("DerivedSolutions-Recall: " + testSuite.totalRecall());
		System.out.println("Interview-Precision: " + testSuite.totalPrecisionInterview());
		System.out.println("Interview-Recall: " + testSuite.totalRecallInterview());
		showDifferences(testSuite);
	}

	private static void showDifferences(TestSuite t) {

		for (SequentialTestCase stc : t.getRepository()) {

			for (RatedTestCase rtc : stc.getCases()) {
				if (!rtc.isCorrect()) {
					System.out.println("SequentialTestCase: " + stc.getName());
					System.out.println("RatedTestCase: " + rtc.getName());
					System.out.println("Findings: ");
					for (Finding f : rtc.getFindings()) {
						System.out.println(f.toString());
					}
					System.out.println("Expected: ");
					Collections.sort(rtc.getExpectedSolutions(),
							new RatedSolution.RatingComparatorByName());
					for (RatedSolution rs : rtc.getExpectedSolutions()) {
						System.out.println("\t" + rs.toString());
					}
					System.out.println("\nDerived: ");
					Collections.sort(rtc.getDerivedSolutions(),
							new RatedSolution.RatingComparatorByName());
					for (RatedSolution rs : rtc.getDerivedSolutions()) {
						System.out.println("\t" + rs.toString());
					}
					System.out.println("-----------------------------------------");
				}
			}

		}

	}

	/**
	 * Demo that generates sequential test cases simulating an interview with
	 * the d3web interview manager by exhaustively traversing all possible
	 * answers of the currently active question.
	 * 
	 * The generated sequential test cases are stored in the xml file and the
	 * txt file which are specified above.
	 * 
	 * @throws IOException
	 */
	public static void demoBotTestCases() throws
			Exception {

		KnowledgeBase kb = loadKnowledgeBase(workspace + kbFile);

		InterviewBot bot = new InterviewBot.Builder(kb).
				ratingStrategy(new StateRatingStrategy()).
				maxCases(50).
				build();
		List<SequentialTestCase> cases = bot.generate();

		writeCasesTXT(txtFile, cases);
		writeCasesXML(xmlFile, cases);
	}

	/**
	 * Demo that generates a visualization of the specified test suite using
	 * JUNGCaseVisualizer. The visualization is saved in a PDF file at the
	 * location specified above.
	 * 
	 * @throws IOException
	 */
	public static void demoCaseVisualization() throws IOException {

		KnowledgeBase kb = loadKnowledgeBase(workspace + kbFile);
		TestSuite testSuite = new TestSuite();
		testSuite.setKb(kb);
		testSuite.loadRepository(workspace + caseFile);
		testSuite.deriveAllSolutions();

		JUNGCaseVisualizer.getInstance().writeToFile(testSuite, workspace + pdfFile);

	}

	/**
	 * Demo that generates a visualization of the specified test suite using
	 * DDBuilder. The visualization is saved in DOT format to the file specified
	 * above.
	 * 
	 * @throws IOException
	 */
	public static void demoBuildDDTree() throws IOException {

		KnowledgeBase kb = loadKnowledgeBase(workspace + kbFile);
		TestSuite testSuite = new TestSuite();
		testSuite.setKb(kb);
		testSuite.loadRepository(workspace + caseFile);
		testSuite.deriveAllSolutions();

		DDBuilder.getInstance().writeToFile(testSuite, workspace + dotFile);
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
		PersistenceManager pm = PersistenceManager.getInstance();
		KnowledgeBase kb = pm.load(new File(kbPath), new ConsoleProgressListener());
		return kb;
	}

	/**
	 * Saves the generated sequential test cases to an XML file.
	 * 
	 * @param filename String part of the filename of the generated output file
	 * @param cases List<SequentialTestCase> the cases
	 * @throws FileNotFoundException
	 */
	private static void writeCasesXML(String filename, List<SequentialTestCase> cases) throws FileNotFoundException {
		TestSuiteXMLWriter conv = new TestSuiteXMLWriter();
		long casesK = cases.size();
		conv.write(cases, workspace + filename + "_" + casesK + "_cases.xml");
	}

	/**
	 * Saves the generated sequential test cases in KnOffice format to an TXT
	 * file.
	 * 
	 * @param filename String part of the filename of the generated output file
	 * @param cases List<SequentialTestCase> the cases
	 * @throws FileNotFoundException
	 */
	private static void writeCasesTXT(String filename, List<SequentialTestCase> cases) throws FileNotFoundException {
		TestSuiteKnOfficeWriter conv = new TestSuiteKnOfficeWriter();
		long casesK = cases.size();
		conv.write(cases, workspace + filename + "_" + casesK + "_cases.txt");
	}

	// public static void demoBotTestCases(String kbPath, String casesPath)
	// throws Exception {
	// KnowledgeBase k = loadKnowledgeBase(kbPath);
	// DDBot2Runner botRunner = new DDBot2Runner(k);
	// botRunner.setCaseNamePraefix("STC");
	//
	// // Example: run ALL cases and store it to a file
	// List<SequentialTestCase> cases =
	// botRunner.generateSequentialTestCases(1,1);
	// botRunner.storeCases(cases, casesPath, true);
	//
	// // Example: compare two nets and print differences in a dot
	// botRunner.testDriveDDNetComparison(workspace, "some_bigger_exp.xml",
	// "some_bigger_exp2.xml", "some_bigger_exp.dot");
	//
	// }

}
