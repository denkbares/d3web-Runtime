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

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Diagnosis;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.empiricalTesting.Finding;
import de.d3web.empiricalTesting.RatedSolution;
import de.d3web.empiricalTesting.RatedTestCase;
import de.d3web.empiricalTesting.ScoreRating;
import de.d3web.empiricalTesting.SequentialTestCase;
import de.d3web.empiricalTesting.caseVisualization.BotHelper;

public class SequentialTestCasePersistence {
	private final static String S_TEST_CASES = "SeqTestCaseRepository";
	private final static String S_TEST_CASE = "STestCase";
	private final static String RATED_TEST_CASE = "RatedTestCase";
	private final static String NAME = "Name";
	private final static String FINDINGS = "Findings";
	private final static String FINDING = "Finding";
	private final static String QUESTION = "Question";
	private final static String PARENT = "Parent";
	private final static String ANSWER = "Answer";
	private static final String SOLUTIONS = "Solutions";
	private static final String SOLUTION = "Solution";
	private static final String RATING = "Rating";

	private static SequentialTestCasePersistence instance = new SequentialTestCasePersistence();
	private boolean verbose = false;
	private KnowledgeBase knowledge;
	private BotHelper bh = BotHelper.getInstance();

	private SequentialTestCasePersistence() {
		super();
	}

	public static SequentialTestCasePersistence getInstance() {
		return instance;
	}

	public Document generate(List<SequentialTestCase> theTestCases) {
		Element rootElement = new Element(S_TEST_CASES);
		rootElement.setAttribute("CreationDate", new SimpleDateFormat(
				"yyyy-MM-dd_HHmm").format(new Date()));
		rootElement.setAttribute("NumberOfCases", "" + theTestCases.size());
		rootElement.setAttribute("UsedSolutions", ""
				+ computeUsedSolutions(theTestCases).size() + "");
		rootElement.setAttribute("UsedFindings", ""
				+ computeUsedFindings(theTestCases).size() + "");

		Document doc = new Document();
		doc.addContent(rootElement);
		for (SequentialTestCase sequentialTestCase : theTestCases) {
			Element elem = generate(sequentialTestCase);
			rootElement.addContent(elem);
		}
		return doc;
	}

	private Collection<Diagnosis> computeUsedSolutions(
			List<SequentialTestCase> theTestCases) {
		Set<Diagnosis> solutions = new HashSet<Diagnosis>();
		for (SequentialTestCase sequentialTestCase : theTestCases) {
			for (RatedTestCase rCase : sequentialTestCase.getCases()) {
				for (RatedSolution rSolution : rCase.getExpectedSolutions()) {
					solutions.add(rSolution.getSolution());
				}
			}
		}
		return solutions;
	}

	private Collection<Finding> computeUsedFindings(
			List<SequentialTestCase> theTestCases) {
		Set<Finding> findings = new HashSet<Finding>();
		for (SequentialTestCase sequentialTestCase : theTestCases) {
			for (RatedTestCase rCase : sequentialTestCase.getCases()) {
				findings.addAll(rCase.getFindings());
			}
		}
		return findings;
	}

	public Element generate(SequentialTestCase sequentialTestCase) {
		Element rootElement = new Element(S_TEST_CASE);
		rootElement.setAttribute(NAME, sequentialTestCase.getName());
		for (RatedTestCase ratedTestCase : sequentialTestCase.getCases()) {
			rootElement.addContent(createElement(ratedTestCase));
		}
		return rootElement;
	}

	public void saveTo(List<SequentialTestCase> theTestCases, Writer writer)
			throws IOException {
		Document doc = generate(theTestCases);
		XMLOutputter outp = new XMLOutputter(Format.getPrettyFormat());
		outp.output(doc, writer);
	}

	public List<SequentialTestCase> loadFrom(Reader reader,
			KnowledgeBase knowledge) throws Exception {
		setKnowledge(knowledge);
		List<SequentialTestCase> cases = new ArrayList<SequentialTestCase>();

		SAXBuilder parser = new SAXBuilder();
		Document doc = parser.build(reader);

		Element root = doc.getRootElement();
		List<Element> chilren = root.getChildren();
		for (Element element : chilren) {
			readSequentialTestCase(element, cases);
		}

		return cases;
	}

	private void readSequentialTestCase(Element element,
			List<SequentialTestCase> cases) throws Exception {
		SequentialTestCase testcase = new SequentialTestCase();
		testcase.setName(element.getAttributeValue(NAME));
		List<Element> ratedCaseElements = element.getChildren(RATED_TEST_CASE);
		readRatedCaseElement(ratedCaseElements, testcase);

		cases.add(testcase);
	}

	private void readRatedCaseElement(List<Element> ratedCasesElements,
			SequentialTestCase testcase) throws Exception {
		for (Element ratedCaseElement : ratedCasesElements) {
			RatedTestCase ratedTestCase = new RatedTestCase();
			ratedTestCase.setName(ratedCaseElement.getAttributeValue(NAME));
			List<Element> findingsElements = ratedCaseElement
					.getChildren(FINDINGS);
			readFindingsElement(findingsElements, ratedTestCase);
			List<Element> solutionsElement = ratedCaseElement
					.getChildren(SOLUTIONS);
			readSolutionsElement(solutionsElement, ratedTestCase);

			testcase.add(ratedTestCase);
		}

	}

	private void readSolutionsElement(List<Element> solutionsElements,
			RatedTestCase ratedTestCase) throws Exception {
		for (Element element : solutionsElements) {
			List<Element> sols = element.getChildren(SOLUTION);
			for (Element e : sols) {
				String solutionID = e.getAttributeValue(NAME);
				String ratingVal = e.getAttributeValue(RATING);

				// Diagnosis solution =
				// getKnowledge().searchDiagnosis(solutionID);
				Diagnosis solution = bh.getDiagnosisByIDorText(solutionID,
						getKnowledge());

				ScoreRating rating = new ScoreRating(new Double(ratingVal)
						.doubleValue());
				ratedTestCase.addExpected(new RatedSolution(solution, rating));
			}
		}
	}

	private void readFindingsElement(List<Element> findingsElements,
			RatedTestCase ratedTestCase) throws Exception {
		for (Element e : findingsElements) {
			Element element = e.getChild(FINDING);
			String questionID = element.getAttributeValue(QUESTION);
			String answerID = element.getAttributeValue(ANSWER);
			String questionnaireText = element.getAttributeValue(PARENT);

			// Question q = bh.findQuestion(questionID, getKnowledge());
			Question q = bh.getQuestionByIDorText(questionID, questionnaireText, getKnowledge());

			// if (q == null)
			// throw new Exception("Question with ID " + questionID +
			// " not found.");

			AnswerChoice a = bh.findAnswer((QuestionChoice) q, answerID);
			if (a == null)
				throw new Exception("Answer with ID " + answerID
						+ " not found.");

			ratedTestCase.add(new Finding(q, a));
		}

	}

	private Element createElement(RatedTestCase ratedTestCase) {
		Element elem = new Element(RATED_TEST_CASE);
		if (ratedTestCase.getName() != null && ratedTestCase.getName() != "") {
			elem.setAttribute(NAME, ratedTestCase.getName());
		}
		elem.addContent(createFindingList(ratedTestCase.getFindings()));
		elem
				.addContent(createSolutionList(ratedTestCase
						.getExpectedSolutions()));
		return elem;
	}

	private Element createSolutionList(List<RatedSolution> solutions) {
		Element elem = new Element(SOLUTIONS);
		for (RatedSolution solution : solutions) {
			if (verbose) {
				elem.addContent(new Comment(solution.toString()));
			}
			Element sol = new Element(SOLUTION);
			sol.setAttribute(NAME, solution.getSolution().getId());
			sol.setAttribute(RATING, solution.getRating().toString());
			elem.addContent(sol);
		}
		return elem;
	}

	private Element createFindingList(List<Finding> findings) {
		Element elem = new Element(FINDINGS);
		for (Finding finding : findings) {
			Element findi = new Element(FINDING);
			findi.setAttribute(QUESTION, finding.getQuestion().getId());
			if (finding.getAnswer() instanceof AnswerChoice)
				findi.setAttribute(ANSWER, finding.getAnswer().getId());
			else
				findi.setAttribute(ANSWER, finding.getAnswer().toString());
			if (verbose) {
				elem.addContent(new Comment(finding.toString()));
			}
			elem.addContent(findi);
		}
		return elem;
	}

	public synchronized boolean isVerbose() {
		return verbose;
	}

	public synchronized void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public KnowledgeBase getKnowledge() {
		return knowledge;
	}

	public void setKnowledge(KnowledgeBase knowledge) {
		this.knowledge = knowledge;
	}

}
