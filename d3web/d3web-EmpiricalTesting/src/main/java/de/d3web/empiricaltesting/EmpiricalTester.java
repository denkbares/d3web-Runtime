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

public final class EmpiricalTester {

	// Input variables
	private static String workspace = "D:/Projekte/Temp/EmpiricalTesting/";
	private static String kbFile = "KnowledgeBases/dano.jar";
	private static String caseFile = "dano.xml";

	// Output file for DDBuilder (DOT-File for GraphViz)
	private static String dotFile = "dano.dot";
	// Output file for JUNGCaseVisualizer (PDF-File)
	private static String pdfFile = "dano.pdf";
	// Output files for InterviewBot
	private static String xmlFile = "dano";
	private static String txtFile = "dano";

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
	public static String demoComputePrecisionAndRecall() throws Exception {
		StringBuffer buffy = new StringBuffer();
		KnowledgeBase kb = loadKnowledgeBase(workspace + kbFile);
		TestCase testSuite = new TestCase();
		testSuite.setKb(kb);
		testSuite.loadRepository(workspace + caseFile);

		buffy.append("DerivedSolutions-Precision (Non-Sequential): "
				+ testSuite.totalNonSequentialPrecision() + "\n");
		buffy.append("DerivedSolutions-Recall (Non-Sequential): "
				+ testSuite.totalNonSequentialRecall() + "\n");
		buffy.append("DerivedSolutions-Precision: " + testSuite.totalPrecision() + "\n");
		buffy.append("DerivedSolutions-Recall: " + testSuite.totalRecall() + "\n");
		buffy.append("Interview-Precision: " + testSuite.totalPrecisionInterview() + "\n");
		buffy.append("Interview-Recall: " + testSuite.totalRecallInterview() + "\n");
		showDifferences(testSuite, buffy);
		return buffy.toString();
	}

	private static void showDifferences(TestCase t, StringBuffer buffy) {

		for (SequentialTestCase stc : t.getRepository()) {

			for (RatedTestCase rtc : stc.getCases()) {
				if (!rtc.isCorrect()) {
					buffy.append("SequentialTestCase: " + stc.getName() + "\n");
					buffy.append("RatedTestCase: " + rtc.getName() + "\n");
					buffy.append("Findings: " + "\n");
					for (Finding f : rtc.getFindings()) {
						buffy.append(f.toString());
					}
					buffy.append("Expected: " + "\n");
					Collections.sort(rtc.getExpectedSolutions(),
							new RatedSolution.RatingComparatorByName());
					for (RatedSolution rs : rtc.getExpectedSolutions()) {
						buffy.append("\t" + rs.toString() + "\n");
					}
					buffy.append("\nDerived: " + "\n");
					Collections.sort(rtc.getDerivedSolutions(),
							new RatedSolution.RatingComparatorByName());
					for (RatedSolution rs : rtc.getDerivedSolutions()) {
						buffy.append("\t" + rs.toString() + "\n");
					}
					buffy.append("-----------------------------------------" + "\n");
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
		TestCase testSuite = new TestCase();
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
		TestCase testSuite = new TestCase();
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
		return PersistenceManager.getInstance().load(new File(kbPath),
				new ConsoleProgressListener());
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
