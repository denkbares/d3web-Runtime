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

package de.d3web.empiricalTesting.joba.testcases;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.d3web.empiricalTesting.Finding;
import de.d3web.empiricalTesting.SequentialTestCase;
import de.d3web.empiricalTesting.caseVisualization.BotHelper;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;
import de.d3web.persistence.xml.PersistenceManager;

/**
 * 
 * @author joba
 * @deprecated Please use InterviewBotRunner
 */
public class DDBot2Runner {

	final static int FROM = 1;
	final static int TO = 1;

	private KnowledgeBase knowledge;
	private String caseNamePraefix;

	boolean cutQuationnaireSibling = false;
	List<QContainer> ingnoreQuestionnaires;
	List<QContainer> registeredContainers;

	public DDBot2Runner(KnowledgeBase knowledge) {
		this.knowledge = knowledge;
		cutQuationnaireSibling = false;
		ingnoreQuestionnaires = new ArrayList<QContainer>(1);
	}

	public static void main(String[] args) throws Exception {
		String workspace = "D:/Studium/d3web/d3web-EmpiricalTesting/resources/";
		// String workspace =
		// "/Users/joba/Documents/Projekte/Digitalys/DDNets/dots20080629/";
		// String kfilepath = workspace + "demo.d3web.jar";

		PersistenceManager mgr = PersistenceManager.getInstance();
		KnowledgeBase k = mgr.load(new File(workspace + "dano.jar").toURI()
				.toURL());

		// KnowledgeBase k = D3webHelper.loadKnowledge(kfilepath);
		DDBot2Runner botRunner = new DDBot2Runner(k);
		botRunner.setCaseNamePraefix("STC");
		// botRunner.setCaseNamePraefix("Digitalys_STC");

//		botRunner.setCutQuationnaireSibling(true);
//		botRunner.addIngnoreQuestionnaire(botRunner.knowledge.searchQContainers("Q1"));
		// botRunner.setRegisteredContainers(toQContainers(botRunner.knowledge,
		// new String[] {"LS_BS", "LS_SB", "LS_BB", "LS_AP", "LSVergiftung",
		// "LS_Neur",
		// "LS_AS", "LS_KN", "LSKreislauf", "LS_Psy"}));

		// Example: compare two nets and print differences in a dot
		botRunner.testDriveDDNetComparison(workspace, "some_bigger_exp.xml",
				"some_bigger_exp2.xml", "some_bigger_exp.dot");

		// Example: a single run
		// botRunner.runDDNetFor(1, workspace, "DDTree_");

		// for (int i = 1; i < 11; i++) {
		// botRunner.runDDNetFor(i, workspace, "Demo_");
		// }

		// Example: run ALL cases and store it to a file
		// List<SequentialTestCase> cases =
		// botRunner.generateSequentialTestCases(1,1);
		// botRunner.storeCases(cases, workspace+"Demo_all_cases_", true);

		System.out.println("ILDE.");
	}

	private void testDriveDDNetComparison(String workspace, String newDDNet,
			String oldDDNet, String outFile) throws Exception {

//		// List<SequentialTestCase> newCases = loadSequentialTestCase(new
//		// File(workspace+newDDNet));
//		List<SequentialTestCase> newCases = TestPersistence
//				.loadCases(new URL("file://" + workspace + newDDNet), knowledge);
//
//		// List<SequentialTestCase> oldCases = loadSequentialTestCase(new
//		// File(workspace+oldDDNet));
//		List<SequentialTestCase> oldCases = TestPersistence
//				.loadCases(new URL("file://" + workspace + oldDDNet), knowledge);
//		TestCaseComparator comp = new TestCaseComparator(newCases, oldCases);
//
//		DDNetBuilder ddnetBuilder = new DDNetBuilder();
//		ddnetBuilder.setCutQuationnaireSibling(true);
//		ddnetBuilder.addAllIngnoreQuestionnaire(getIngnoreQuestionnaire());
//		ddnetBuilder.setRegisteredContainers(getRegisteredContainers());
//		HashMap<String, DDNode> nodes = ddnetBuilder.generateDDNet(
//				comp.onlyInCase1, comp.intersectingCases);

//		String dotString = new DDNetDOTGenerator().generateDOT(nodes, true);
//		writeStringToFile(dotString, new File(workspace + outFile));

	}

	private void writeStringToFile(String dotString, File file)
			throws IOException {
		OutputStreamWriter dotwriter = new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8");
		dotwriter.write(dotString);
		dotwriter.close();
	}

	private List<SequentialTestCase> loadSequentialTestCase(File file)
			throws Exception {
		SequentialTestCasePersistence persistence = SequentialTestCasePersistence
				.getInstance();
		Reader reader = new FileReader(file);
		return persistence.loadFrom(reader, knowledge);
	}

	private static List<QContainer> toQContainers(KnowledgeBase knowledge,
			String[] strings) throws Exception {
		List<QContainer> containers = new ArrayList<QContainer>(strings.length);
		for (String qcid : strings) {
			QContainer qc = knowledge.searchQContainers(qcid);
			if (qc != null) {
				containers.add(qc);
			} else
				throw new Exception("Error: QContainer with ID " + qcid
						+ " not found.");
		}
		return containers;
	}

	public void storeCases(List<SequentialTestCase> cases, String filename,
			boolean addTimestamp) throws IOException {
		if (addTimestamp) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HHmm");
			filename += df.format(new Date()) + ".xml";

		}
		System.out.println(cases.size());

		Writer writer = new OutputStreamWriter(new FileOutputStream(new File(
				filename)));
		writeCases(writer, cases);
		writer.close();
	}

	public void runDDNetFor(int number, String workspace, String filenamePraefix)
			throws Exception {
		// Generate the test cases
		List<SequentialTestCase> cases = generateSequentialTestCases(number,
				number);

		// Write the Sequentialized Test Cases to a file
		Writer writer = new OutputStreamWriter(new FileOutputStream(new File(
				workspace + filenamePraefix + number + ".xml")));
		writeCases(writer, cases);
		writer.close();

		// Generate a DDNet from the Sequentialized Test Case data
//		DDNetBuilder ddnetBuilder = new DDNetBuilder();
//		ddnetBuilder.setCutQuationnaireSibling(isCutQuationnaireSibling());
//		ddnetBuilder.addAllIngnoreQuestionnaire(getIngnoreQuestionnaire());
//		ddnetBuilder.setRegisteredContainers(getRegisteredContainers());
//		HashMap<String, DDNode> nodes = ddnetBuilder.generateDDNet(cases);
//		String dotString = new DDNetDOTGenerator().generateDOT(nodes, true);

//		writeStringToFile(dotString, new File(workspace + filenamePraefix
//				+ number + ".dot"));

	}

	/**
	 * This praefix will be used for labeling the sequentialized test cases.
	 * 
	 * @param praefix
	 *            praefix label of a sequentialized test case
	 */
	public void setCaseNamePraefix(String praefix) {
		caseNamePraefix = praefix;
	}

	public List<SequentialTestCase> generateSequentialTestCases()
			throws Exception {
		return generateSequentialTestCases(FROM, TO);
	}

	public List<SequentialTestCase> generateSequentialTestCases(int begin,
			int end) throws Exception {
		int counter = 0;
		List<SequentialTestCase> cases = new ArrayList<SequentialTestCase>();
		for (int i = begin; i <= end; i++) {
			DDBot2 bot2 = new DDBot2();
			bot2.setCaseNamePraefix(getCaseNamePraefix());
			bot2.setCaseCounter(counter);
			List<Finding> initFindings = new ArrayList<Finding>();
			try {
				Finding findi = toFinding(knowledge, "Q1", i);
				initFindings.add(findi);
			} catch (Exception e) {
				System.out.println("> " + e.toString());
			}
			bot2.traverse(knowledge, initFindings);
			cases.addAll(bot2.storedCases);
			counter += bot2.storedCases.size();
			System.out.println("new: " + bot2.storedCases.size());
		}
		return cases;
	}

	public void writeCases(Writer writer, List<SequentialTestCase> cases)
			throws IOException {
		SequentialTestCasePersistence persistence = SequentialTestCasePersistence
				.getInstance();
		persistence.setVerbose(true);
		persistence.saveTo(cases, writer);
	}

	private Finding toFinding(KnowledgeBase k, String questionID, int answerNo)
			throws Exception {
		QuestionChoice q = (QuestionChoice) k.searchQuestions(questionID);
		AnswerChoice a = BotHelper.getInstance().findAnswer(q,
				questionID + "a" + answerNo);
		return new Finding(q, a);
	}

	public synchronized String getCaseNamePraefix() {
		return caseNamePraefix;
	}

	public boolean isCutQuationnaireSibling() {
		return cutQuationnaireSibling;
	}

	public List<QContainer> getIngnoreQuestionnaire() {
		return ingnoreQuestionnaires;
	}

	public void setCutQuationnaireSibling(boolean cutQuationnaireSibling) {
		this.cutQuationnaireSibling = cutQuationnaireSibling;
	}

	public boolean addIngnoreQuestionnaire(QContainer ingnoreQuestionnaire) {
		return this.ingnoreQuestionnaires.add(ingnoreQuestionnaire);
	}

	/**
	 * These containers are used to compare if the next question of the
	 * interview switches to another of these registered containers. If so, then
	 * the traversal of the tree will be stopped (if
	 * cutQuationnaireSibling-option is set TRUE)
	 * 
	 * @param registeredContainers
	 */
	public List<QContainer> getRegisteredContainers() {
		return registeredContainers;
	}

	/**
	 * These containers are used to compare if the next question of the
	 * interview switches to another of these registered containers. If so, then
	 * the traversal of the tree will be stopped (if
	 * cutQuationnaireSibling-option is set TRUE)
	 * 
	 * @param registeredContainers
	 */
	public void setRegisteredContainers(List<QContainer> registeredContainers) {
		this.registeredContainers = registeredContainers;
	}
}
