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
import java.util.List;

import de.d3web.casegeneration.InterviewBot;
import de.d3web.casegeneration.StateRatingStrategy;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.ConsoleProgressListener;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.empiricaltesting.writer.TestSuiteKnOfficeWriter;
import de.d3web.empiricaltesting.writer.TestSuiteXMLWriter;
import de.d3web.plugin.test.InitPluginManager;

public final class EmpiricalTester {

	// Input variables
	private static String workspace = "D:/Projekte/Temp/EmpiricalTesting/";
	private static String kbFile = "KnowledgeBases/dano.jar";
	// private static String caseFile = "dano.xml";

	// Output file for DDBuilder (DOT-File for GraphViz)
	// private static String dotFile = "dano.dot";
	// Output file for JUNGCaseVisualizer (PDF-File)
	// private static String pdfFile = "dano.pdf";
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

}
