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

import javax.activation.UnsupportedDataTypeException;

import de.d3web.caseGeneration.HeuristicScoreRatingStrategy;
import de.d3web.caseGeneration.InterviewBot;
import de.d3web.empiricalTesting.caseConverter.CaseObjectToKnOffice;
import de.d3web.empiricalTesting.caseConverter.CaseObjectToTestSuiteXML;
import de.d3web.empiricalTesting.caseVisualization.dot.DDBuilder;
import de.d3web.empiricalTesting.caseVisualization.jung.JUNGCaseVisualizer;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.persistence.xml.PersistenceManager;
import de.d3web.persistence.xml.XCLModelPersistenceHandler;

public class EmpiricalTester {
	
	// Input variables
	static String workspace = "D:/Projekte/Temp/EmpiricalTesting/";
	static String kbFile = "dano.jar";
	static String caseFile = "dano_bot_49_cases.xml";

	// Output file for DDBuilder
	static String dotFile = "dano.dot";
	// Output file for JUNGCaseVisualizer
	static String pdfFile = "dano.pdf";
	// Output files for InterviewBot
	static String xmlFile = "dano_bot"; 
	static String txtFile = "dano_bot"; 


	/**
	 * Driver for the Empirical Testing project
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		try {
//			demoComputePrecisionAndRecall();
//			demoBotTestCases();
//			demoCaseVisualization();
//			demoBuildDDTree();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	
	/**
	 * Demo that computes the Precision and Recall for the
	 * derived solutions and the interview.
	 * @throws Exception
	 */
	public static void demoComputePrecisionAndRecall() throws Exception {
		
		KnowledgeBase kb = loadKnowledgeBase(workspace + kbFile);
		TestSuite TS = new TestSuite();
		TS.setKb(kb);
		TS.loadRepository(workspace + caseFile);

		System.out.println("DerivedSolutions-Precision: " + TS.totalPrecision());
		System.out.println("DerivedSolutions-Recall: " + TS.totalRecall());
		System.out.println("Interview-Precision: " + TS.totalPrecisionInterview());
		System.out.println("Interview-Recall: " + TS.totalRecallInterview());
	}
	
	
	/**
	 * Demo that generates sequential test cases simulating an interview 
	 * with the d3web interview manager by exhaustively traversing all 
	 * possible answers of the currently active question.
	 * 
	 * The generated sequential test cases are stored in the xml file
	 * and the txt file which are specified above.
	 *  
	 * @throws MalformedURLException
	 * @throws UnsupportedDataTypeException
	 */
	public static void demoBotTestCases() throws 
		UnsupportedDataTypeException, MalformedURLException {
		
		KnowledgeBase kb = loadKnowledgeBase(workspace + kbFile);
	
		InterviewBot bot = new InterviewBot.Builder(kb).
			maxCases(50).
			ratingStrategy(new HeuristicScoreRatingStrategy()).
			build();
		List<SequentialTestCase> cases = bot.generate();
	
		writeCasesTXT(txtFile, cases);
		writeCasesXML(xmlFile, cases);
	}

	
	/**
	 * Demo that generates a visualization of the specified
	 * test suite using JUNGCaseVisualizer. The visualization 
	 * is saved in a PDF file at the location specified above.
	 * @throws MalformedURLException
	 */
	public static void demoCaseVisualization() throws MalformedURLException {
		
		KnowledgeBase kb = loadKnowledgeBase(workspace + kbFile);
		TestSuite TS = new TestSuite();
		TS.setKb(kb);
		TS.loadRepository(workspace + caseFile);
		
		JUNGCaseVisualizer.getInstance().writeToFile(TS, workspace + pdfFile);
	}
	
	
	/**
	 * Demo that generates a visualization of the specified
	 * test suite using DDBuilder. The visualization is
	 * saved in DOT format to the file specified above.
	 * @throws MalformedURLException
	 */
	public static void demoBuildDDTree() throws MalformedURLException {
		
		KnowledgeBase kb = loadKnowledgeBase(workspace + kbFile);
		TestSuite TS = new TestSuite();
		TS.setKb(kb);
		TS.loadRepository(workspace + caseFile);

		DDBuilder.getInstance().writeToFile(TS, workspace + dotFile);
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
	
	
	/**
	 * Saves the generated sequential test cases to 
	 * an XML file.
	 * @param filename String part of the filename of the 
	 *                 generated output file
	 * @param cases List<SequentialTestCase> the cases
	 */
	private static void writeCasesXML(String filename, List<SequentialTestCase> cases) {
		CaseObjectToTestSuiteXML conv = new CaseObjectToTestSuiteXML();
		long casesK = cases.size();
		conv.write(cases, workspace+filename+"_"+casesK+"_cases.xml");
	}
	
	
	/**
	 * Saves the generated sequential test cases in KnOffice format
	 * to an TXT file.
	 * @param filename String part of the filename of the 
	 *                 generated output file
	 * @param cases List<SequentialTestCase> the cases
	 */
	private static void writeCasesTXT(String filename, List<SequentialTestCase> cases) {
		CaseObjectToKnOffice conv = new CaseObjectToKnOffice();
		long casesK = cases.size();
		conv.write(cases, workspace+filename+"_"+casesK+"_cases.txt");
	}
	
	
//	public static void demoBotTestCases(String kbPath, String casesPath) throws Exception {
//	KnowledgeBase k = loadKnowledgeBase(kbPath);
//	DDBot2Runner botRunner = new DDBot2Runner(k);
//	botRunner.setCaseNamePraefix("STC");
//	
//	// Example: run ALL cases and store it to a file
//	 List<SequentialTestCase> cases =
//	 botRunner.generateSequentialTestCases(1,1);
//	 botRunner.storeCases(cases, casesPath, true);
//	
//	// Example: compare two nets and print differences in a dot
//	botRunner.testDriveDDNetComparison(workspace, "some_bigger_exp.xml",
//			"some_bigger_exp2.xml", "some_bigger_exp.dot");
//	 
//	}

}
