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

package de.d3web.empiricaltesting.casevisualization.dot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Value;
import de.d3web.empiricaltesting.CaseUtils;
import de.d3web.empiricaltesting.ConfigLoader;
import de.d3web.empiricaltesting.Finding;
import de.d3web.empiricaltesting.RatedSolution;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.Rating;
import de.d3web.empiricaltesting.ScoreRating;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.StateRating;
import de.d3web.empiricaltesting.TestCase;
import de.d3web.empiricaltesting.casevisualization.CaseVisualizer;
import de.d3web.scoring.HeuristicRating;

public final class DDBuilder implements CaseVisualizer {

	private static final String CORRECTION_CELL = "<TD BGCOLOR=\"#FFFFFF\" WIDTH=\"50\"></TD>";
	private final static String HEADER = "digraph g { \ngraph [ \n  rankdir = \"TD\" \n"
			+ "]; \n" + "node [\n" + " fontname=Helvetica\n"
			+ " fontsize = \"16\"\n" + "  shape = none\n" + "];\n"
			+ "edge [ \n" + "];\n";
	private final static String FOOTER = "\n}\n";

	private static NumberFormat formater = new DecimalFormat("#########");

	private ConfigLoader config = ConfigLoader.getInstance();

	private Set<String> createdEdges;
	private List<DDNode> nodes = new LinkedList<DDNode>();

	public DDBuilder() {
	}

	/**
	 * Streams the graph to an OutputStream (useful for web requests!)
	 * 
	 * @param cases List<SequentialTestCase> cases
	 * @param out OutputStream
	 */
	@Override
	public void writeToStream(List<SequentialTestCase> cases, OutputStream outStream) throws IOException {
		generateDDNet(cases);
		write(outStream);
	}

	/**
	 * Saves the graph visualization to a <b>DOT file</b> which will be created
	 * at the committed filepath.
	 * 
	 * @param cases List<SequentialTestCase> which's elements will be visualized
	 *        by this class.
	 * @param file String which specifies where the created <b>DOT file</b> will
	 *        be stored.
	 */
	@Override
	public void writeToFile(List<SequentialTestCase> cases, File file) throws IOException {
		writeToFile(cases, file.getPath());
	}

	private void writeToFile(List<SequentialTestCase> cases, String filepath) throws IOException {
		filepath = checkDotFilePath(filepath, "");
		FileOutputStream fileOutputStream = new FileOutputStream(filepath);
		try {
			writeToStream(cases, fileOutputStream);
		}
		finally {
			fileOutputStream.close();
		}
	}

	/**
	 * Saves the graph visualization to a <b>DOT file</b> which will be created
	 * at the committed filepath.
	 * 
	 * @param testSuite TestSuite which's cases will be visualized by this
	 *        class.
	 * @param file String which specifies where the created <b>DOT file</b> will
	 *        be stored.
	 */
	@Override
	public void writeToFile(TestCase testSuite, File dotFile) throws IOException {
		writeToFile(testSuite, dotFile.getPath());
	}

	private void writeToFile(TestCase testSuite, String dotFilePath) throws IOException {
		String partitionTree = getConfig().getProperty("partitionTree");
		if (partitionTree.equals("true")) {

			// Die erste Frage ermitteln
			QuestionChoice firstQuestion = (QuestionChoice) testSuite.getRepository().get(0).
					getCases().get(0).getFindings().get(0).getQuestion();
			// Die Antwortalternativen
			List<Choice> firstAnswers = firstQuestion.getAllAlternatives();
			for (Choice answerOfFirstQuestion : firstAnswers) {
				TestCase partitioned =
						CaseUtils.getInstance().getPartiallyAnsweredSuite(answerOfFirstQuestion,
								testSuite.getRepository());
				if (partitioned.getRepository().size() > 0) {
					String printFilePath =
							checkDotFilePath(dotFilePath, answerOfFirstQuestion.getName());
					writeToFile(partitioned.getRepository(), printFilePath);
				}
			}
		}
		else {
			dotFilePath = checkDotFilePath(dotFilePath, "");
			writeToFile(testSuite.getRepository(), dotFilePath);
		}

	}

	private static String checkDotFilePath(String dotFile, String addOn) {
		String ret = "";

		if (dotFile.equals("")) {
			// empty
			ret = "dotFile" + addOn + ".dot";
		}
		else if (dotFile.endsWith("/")) {
			// just a path
			ret = dotFile + addOn + ".dot";
		}
		else if (dotFile.endsWith(".dot")) {
			// full filepath specified
			if (addOn.equals("")) {
				ret = dotFile.substring(0, dotFile.length() - 4) + ".dot";
			}
			else {
				ret = dotFile.substring(0, dotFile.length() - 4) + "_" + addOn + ".dot";
			}
		}
		else {
			// we suppose, its a Path without trailing "/"
			ret = dotFile + "/" + addOn + ".dot";
		}
		return ret;
	}

	private void generateDDNet(List<SequentialTestCase> cases) {

		createdEdges = new HashSet<String>();
		Map<RatedTestCase, DDNode> nodeMap = new HashMap<RatedTestCase, DDNode>();

		for (SequentialTestCase stc : cases) {

			List<RatedTestCase> ratedCases = stc.getCases();
			DDNode prec = null;

			for (int i = 0; i < ratedCases.size(); i++) {
				RatedTestCase ratedTestCase = ratedCases.get(i);
				DDNode node = getDDNode(ratedTestCase, nodeMap, prec);
				prec = node;
			}
		}
	}

	private DDNode getDDNode(RatedTestCase ratedTestCase, Map<RatedTestCase, DDNode> nodeMap, DDNode precessor) {
		DDNode node = nodeMap.get(ratedTestCase);
		if (node == null) {
			boolean seperateQuestionSolutionBlocks =
					getConfig().getProperty("seperateQuestionSolutionBlocks").equals("true")
							&& !ratedTestCase.getFindings().isEmpty()
							&& (!ratedTestCase.getDerivedSolutions().isEmpty() || !ratedTestCase.getExpectedSolutions().isEmpty());
			if (seperateQuestionSolutionBlocks) {
				// we generate at least a block for the questions and the
				// solutions
				// if the questions are a mixture of decisive and non-decisive
				// questions,
				// we create two question nodes
				// TODO: implement split of decisive and non-decisive questions
				DDNode questionNode = DDNode.createFindingNode(ratedTestCase);
				DDNode solutionNode = DDNode.createSolutionNode(ratedTestCase);
				questionNode.addChild(solutionNode);
				nodes.add(questionNode);
				nodes.add(solutionNode);
				nodeMap.put(ratedTestCase, solutionNode);
				if (precessor != null) {
					precessor.addChild(questionNode);
				}
				node = solutionNode;
			}
			else {
				node = DDNode.createCompleteNode(ratedTestCase);
				nodes.add(node);
				nodeMap.put(ratedTestCase, node);
				if (precessor != null) {
					precessor.addChild(node);
				}
			}
		}
		return node;
	}

	public String render() {
		StringBuffer b = new StringBuffer(HEADER);
		for (DDNode node : nodes) {
			renderNode(b, node, computeOutgoing(node));
			for (DDEdge edge : node.getOutgoing()) {
				renderEdge(b, edge);
			}
		}
		b.append(FOOTER);
		return b.toString();
	}

	private void write(OutputStream out) throws IOException {
		OutputStreamWriter dotwriter =
				new OutputStreamWriter(out, "UTF-8");
		dotwriter.write(render());
		dotwriter.close();
	}

	private List<Question> computeOutgoing(DDNode node) {
		List<Question> outgoingQuestions = new ArrayList<Question>(1);
		for (DDEdge edge : node.getOutgoing()) {
			outgoingQuestions = extractNewQuestions(
					edge.getEnd().getFindings(), outgoingQuestions);
		}
		return outgoingQuestions;
	}

	private List<Question> extractNewQuestions(List<Finding> findings,
			List<Question> outgoingQuestions) {
		for (Finding finding : findings) {
			if (!outgoingQuestions.contains(finding.getQuestion())) outgoingQuestions.add(finding.getQuestion());
		}
		return outgoingQuestions;
	}

	public void renderEdge(StringBuffer b, DDEdge edge) {
		String name0 = edge.getBegin().getID();
		String name1 = edge.getEnd().getID();
		String arcName = name0 + "-" + name1;
		if (createdEdges.contains(arcName)) return;
		else {
			createdEdges.add(arcName);

			b.append("\"" + name0 + "\" -> \"" + name1 + "\" [");
			b.append("label = \"");
			boolean onlyDecisiveAnswers =
					Boolean.valueOf(getConfig().getProperty("onlyDecisiveAnswers"));
			List<Finding> findings = edge.getEnd().getFindings();
			for (Finding f : findings) {
				if (onlyDecisiveAnswers
						&& !hasMultipleOutgoingValues(f.getQuestion(), edge.getBegin())) {
					continue;
				}
				b.append(renderEdgeLabel(f));
				b.append("\\l");
			}
			b.append("\"");

			boolean bolOldCasesLikeNewCases =
					getConfig().getProperty("renderOldCasesLikeNewCases").equals("true");

			if (edge.isTestedBefore() && !bolOldCasesLikeNewCases) {
				b.append(" color = \"" +
							getConfig().getProperty("edgeColorOldCase") + "\"");
				b.append(" penwidth = " +
							getConfig().getProperty("edgeWidthOldCase"));
			}
			else {
				b.append(" color = \"" +
						getConfig().getProperty("edgeColorNewCase") + "\"");
				b.append(" penwidth = " +
						getConfig().getProperty("edgeWidthNewCase"));
			}

			b.append("]\n");
		}
	}

	/**
	 * Returns if the specified {@link Question} has multiple values within the
	 * outgoing paths of a specified source {@link DDNode}.
	 * 
	 * @created 02.05.2011
	 * @param question the question to be checked
	 * @param outgoingNode the node where the different paths starts
	 * @return if there are multiple different values for this question
	 */
	static boolean hasMultipleOutgoingValues(Question question, DDNode sourceNode) {
		Set<Value> values = new HashSet<Value>();
		List<DDEdge> outgoing = sourceNode.getOutgoing();
		for (DDEdge edge : outgoing) {
			List<Finding> findings = edge.getEnd().getFindings();
			for (Finding finding : findings) {
				if (question.equals(finding.getQuestion())) {
					values.add(finding.getValue());
				}
			}
		}
		return values.size() >= 2;
	}

	/**
	 * Encodes the specified text by replacing all possible invalid characters
	 * by HTML entities as specified in DOT language. The returned text will be
	 * displayed as the original one, but does not contain any invalid
	 * characters.
	 * 
	 * @created 02.05.2011
	 * @param text the text to be encoded
	 * @return the encoded text
	 */
	public String encodeHTML(String text) {
		StringBuilder result = new StringBuilder(text.length() * 5);
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
					|| (c >= '0' && c <= '9')) {
				result.append(c);
			}
			else if (Character.isWhitespace(c)) {
				result.append(" ");
			}
			else {
				result.append("&#").append(String.valueOf((int) c)).append(";");
			}
		}
		return result.toString();
	}

	/**
	 * Renders the name of one finding to be displayed as an edge label. The
	 * rendered label must not contain any invalid characters. If you expect
	 * those, use #encodeHTML(String) method.
	 * 
	 * @created 02.05.2011
	 * @param finding the finding to be rendered as a label
	 * @return the rendered finding
	 */
	public String renderEdgeLabel(Finding finding) {
		// if the next questions are shown in the previous node
		// we only display the answers
		String result = encodeHTML(finding.getValuePrompt());
		boolean showNextQuestions =
				Boolean.valueOf(getConfig().getProperty("showNextQuestions"));
		if (!showNextQuestions) {
			// otherwise also the prompt
			result = encodeHTML(finding.getQuestionPrompt()) + " = " + result;
		}
		return result;
	}

	public void renderEmptyNode(StringBuffer result, DDNode node) {
		result.append(node.getID());
		result.append(" [\n  label=<\n");
		result.append("   <TABLE>\n");
		String nodeColor = getConfig().getProperty("nodeColorIncorrectCase");
		renderNodeCaseNameRow(result, node);
		renderTableLine(result, nodeColor, false, "truncated");
		result.append("   </TABLE>>\n");
		result.append("];\n");
	}

	/**
	 * Renders the case name as the header of a node into the result buffer.
	 * This method must render a set of HTML table rows into the buffer
	 * (including the opening and closing &lt;TR&gt;...&lt;/TR&gt; tags), as
	 * specified in the dot language for HTML nodes.
	 * 
	 * @created 03.05.2011
	 * @param result the result buffer to render into
	 * @param node the node to render its case name
	 */
	public void renderNodeCaseNameRow(StringBuffer result, DDNode node) {
		if (Boolean.valueOf(config.getProperty("showTestCaseName"))) {
			String color = config.getProperty("testCaseNameColor");
			renderTableLine(result, color, false, node.getCaseName());
		}
	}

	public void renderNode(StringBuffer result, DDNode node, List<Question> nextQuestions) {

		// prepare list of all solutions
		List<RatedSolution> allRatedSolutions = new LinkedList<RatedSolution>();
		allRatedSolutions.addAll(node.getExpectedSolutions());
		allRatedSolutions.addAll(node.getDerivedSolutions());

		if (node.getFindings().isEmpty() && allRatedSolutions.isEmpty()) {
			renderEmptyNode(result, node);
			return;
		}

		result.append(node.getID());
		result.append(" [\n  label=<\n");

		boolean correctionColumn = false;

		String nodeColor;
		boolean oldCasesLikeNewCases =
				Boolean.valueOf(getConfig().getProperty("renderOldCasesLikeNewCases"));

		boolean symbolicStates =
				Boolean.valueOf(getConfig().getProperty("compareOnlySymbolicStates"));

		if (node.isTestedBefore() && !oldCasesLikeNewCases) {
			nodeColor = getConfig().getProperty("nodeColorOldCase");
		}
		else {
			nodeColor = getConfig().getProperty("nodeColorNewCase");
		}

		result.append("   <TABLE BGCOLOR=\"").append(nodeColor).append("\">\n");

		renderNodeCaseNameRow(result, node);

		if (node.isQuestionNode()) {
			// print question answered for deriving the current solutions
			renderNodeAnswers(result, node);
		}

		if (node.isSolutionNode()) {
			// Put all RatedSolutions in Maps
			Map<Solution, RatedSolution> expSolutions =
					getSolutionsInHashMap(node.getExpectedSolutions());
			Map<Solution, RatedSolution> derSolutions =
					getSolutionsInHashMap(node.getDerivedSolutions());

			// Print Solutions Header
			if (!allRatedSolutions.isEmpty()) {
				renderSolutionsHeader(result, node, correctionColumn);
			}

			int maxCount = Integer.valueOf(config.getProperty("maxVisibleSolutions"));
			if (allRatedSolutions.size() > maxCount) {
				renderTableLine(result, null, false,
						"..." + allRatedSolutions.size() + " solutions left...");
			}
			else {
				// iterate over all rated solutions, but avoid duplicates
				Set<Solution> printedSolutions = new HashSet<Solution>();
				for (RatedSolution ratedSolution : allRatedSolutions) {
					// check if solution is already done
					Solution d = ratedSolution.getSolution();
					if (printedSolutions.contains(d)) continue;
					printedSolutions.add(d);

					// otherwise print it
					RatedSolution expected = expSolutions.get(d);
					RatedSolution derived = derSolutions.get(d);

					if (expected != null && derived != null && expected.equals(derived)) {
						renderCorrectSolution(result, node,
								derived, nodeColor,
								correctionColumn, symbolicStates);
					}
					else {
						renderIncorrectSolutions(result, node, expected, derived,
								nodeColor, nodeColor, correctionColumn, symbolicStates);
					}
				}
			}

			boolean showNextQuestions =
					Boolean.valueOf(getConfig().getProperty("showNextQuestions"));
			if (showNextQuestions) {
				// print questions to be asked next (mostly only one)
				renderNodeNextQuestions(result, node, nextQuestions);
			}
		}

		result.append("   </TABLE>>\n");
		result.append("];\n");
	}

	/**
	 * Renders the questions to be answered next after the specified node into
	 * the result buffer. This method must render a set of HTML table rows into
	 * the buffer (including the opening and closing &lt;TR&gt;...&lt;/TR&gt;
	 * tags), as specified in the dot language for HTML nodes.
	 * 
	 * @created 02.05.2011
	 * @param result the result buffer to render into
	 * @param node the node to render its findings as answers
	 * @param nextQuestions the next questions of that node
	 */
	public void renderNodeNextQuestions(StringBuffer result, DDNode node, List<Question> nextQuestions) {
		for (Question question : nextQuestions) {
			String name = CaseUtils.getPrompt(question);
			result.append("    <TR><TD COLSPAN=\"4\">")
					.append(encodeHTML(name))
					.append("</TD> </TR>\n");
		}
	}

	/**
	 * Renders the given answers of the specified node into the result buffer.
	 * The answers are the findings of the node's corresponding test case. This
	 * method must render a set of HTML table rows into the buffer (including
	 * the opening and closing &lt;TR&gt;...&lt;/TR&gt; tags), as specified in
	 * the dot language for HTML nodes.
	 * 
	 * @created 02.05.2011
	 * @param result the result buffer to render into
	 * @param node the node to render its findings as answers
	 */
	public void renderNodeAnswers(StringBuffer result, DDNode node) {
		List<Finding> findings = node.getFindings();
		if (findings.isEmpty()) return;

		Boolean showPrompt = Boolean.valueOf(getConfig().getProperty("showQuestionnairePrompt"));
		Boolean showName = Boolean.valueOf(getConfig().getProperty("showQuestionnaireName"));
		if (showPrompt || showName) {
			String color = getConfig().getProperty("nodeColorQuestionnaireTitle");
			QContainer parent = findQContainer(findings.get(0).getQuestion());
			String title;
			String prompt = CaseUtils.getPrompt(parent);
			String name = parent.getName();
			if (showName && showPrompt && !name.equals(prompt)) {
				title = prompt + " (" + name + ")";
			}
			else if (showPrompt) {
				title = prompt;
			}
			else {
				title = parent.getName();
			}
			renderTableLine(result, color, false, encodeHTML(title));
		}
		for (Finding finding : findings) {
			renderTableLine(
					result, null, false,
					encodeHTML(finding.getQuestionPrompt()),
					encodeHTML(finding.getValuePrompt()));
		}
	}

	private QContainer findQContainer(TerminologyObject object) {
		TerminologyObject[] parents = object.getParents();
		for (TerminologyObject parent : parents) {
			if (parent instanceof QContainer) {
				return (QContainer) parent;
			}
			QContainer result = findQContainer(parent);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns all elements (RatedSolution) of a List in a Map.
	 * 
	 * @param solutions List<RatedSolution>
	 * 
	 * @return Map<Diagnosis, RatedSolution>
	 */
	private Map<Solution, RatedSolution> getSolutionsInHashMap(
			List<RatedSolution> solutions) {

		Map<Solution, RatedSolution> result =
				new HashMap<Solution, RatedSolution>();

		for (RatedSolution rs : solutions) {
			result.put(rs.getSolution(), rs);
		}

		return result;
	}

	public void renderTableLine(StringBuffer result, String color, boolean correctionColumn, String... cells) {
		int colspan = 5 - cells.length;
		if (correctionColumn) colspan--;
		String bgColor = (color == null) ? "" : " BGCOLOR=\"" + color + "\"";
		result.append("    <TR>");
		for (int i = 0; i < cells.length; i++) {
			result.append("<TD").append(bgColor);
			if (i == 0) {
				result.append(" COLSPAN=\"").append(colspan).append("\"");
			}
			result.append(">");
			result.append(cells[i]);
			result.append("</TD>");
		}
		if (correctionColumn) {
			result.append(CORRECTION_CELL);
		}
		result.append("</TR>\n");
	}

	public void renderTableRuler(StringBuffer result) {
		renderTableLine(result, "#000000", false, "");
	}

	/**
	 * Renders the header for the solutions into the result buffer. This method
	 * must render a set of HTML table rows into the buffer (including the
	 * opening and closing &lt;TR&gt;...&lt;/TR&gt; tags), as specified in the
	 * dot language for HTML nodes.
	 * 
	 * @created 02.05.2011
	 * @param result the result buffer to render into
	 * @param node the node to render its findings as answers
	 * @param correctionColumn is a correction column required
	 * 
	 */
	public void renderSolutionsHeader(StringBuffer result, DDNode node, boolean correctionColumn) {
		String color = getConfig().getProperty("nodeColorSolutionTitle");
		renderTableLine(result, color, correctionColumn, "Solution", "exp.", "der.");
	}

	/**
	 * Renders a correct (expected = derived) solution to the specified target
	 * buffer. This method must render a set of HTML table rows into the buffer
	 * (including the opening and closing &lt;TR&gt;...&lt;/TR&gt; tags), as
	 * specified in the dot language for HTML nodes.
	 * 
	 * @created 02.05.2011
	 * @param result the result buffer to render into
	 * @param node the node to render its findings as answers
	 * @param derived RatedSolution the derived solution which will be
	 *        transformed
	 * @param color String containing color information
	 * @param symbolicStates boolean containing information whether states are
	 *        shown as symbolic states or scores
	 * 
	 * @return String representing the transformed RatedSolution
	 */
	public void renderCorrectSolution(StringBuffer result, DDNode node, RatedSolution derived,
			String color, boolean correctionColumn, boolean symbolicStates) {
		renderTableLine(result, color, correctionColumn,
				encodeHTML(CaseUtils.getPrompt(derived.getSolution())),
				renderSolutionState(derived, symbolicStates));
	}

	/**
	 * Renders two incorrect (expected != derived) solutions to the result
	 * buffer. This method must render a set of HTML table rows into the buffer
	 * (including the opening and closing &lt;TR&gt;...&lt;/TR&gt; tags), as
	 * specified in the dot language for HTML nodes.
	 * 
	 * @created 02.05.2011
	 * @param result the result buffer to render into
	 * @param node the node to render its findings as answers
	 * @param derived RatedSolution the derived solution which will be rendered
	 * @param expected RatedSolution the expected solution which will be
	 *        rendered
	 * @param expectedColor String containing color information for expected
	 *        solution
	 * @param derivedColor String containing color information for derived
	 *        solution
	 * @param correctionColumn
	 * @param symbolicStates boolean containing information whether states are
	 *        shown as symbolic states or scores
	 */
	public void renderIncorrectSolutions(StringBuffer result, DDNode node, RatedSolution expected,
			RatedSolution derived, String expectedColor,
			String derivedColor, boolean correctionColumn, boolean symbolicStates) {

		String solName = CaseUtils.getPrompt(expected == null ?
						derived.getSolution() :
						expected.getSolution());

		int colspan = correctionColumn ? 1 : 2;

		result.append("    <TR>");
		result.append("<TD COLSPAN=\"").append(colspan).append("\">");
		result.append(encodeHTML(solName));
		result.append("</TD>");
		result.append("<TD ALIGN=\"CENTER\" BGCOLOR=\"");
		result.append(expectedColor);
		result.append("\">");
		result.append(expected == null ? "N/A" : renderSolutionState(expected, symbolicStates));
		result.append("</TD>");
		result.append("<TD ALIGN=\"CENTER\" BGCOLOR=\"");
		result.append(derivedColor);
		result.append("\">");
		result.append(derived == null ? "N/A" : renderSolutionState(derived, symbolicStates));
		result.append("</TD>");
		if (correctionColumn) {
			result.append(CORRECTION_CELL);
		}
		result.append("</TR>\n");
	}

	/**
	 * Renders the state of a RatedSolution to a nice formatted String in
	 * preparation for rendering.
	 * 
	 * @param rs RatedSolution representing the currently processed
	 *        RatedSolution
	 * 
	 * @param symbolicStates boolean containing information whether states are
	 *        shown as symbolic states or scores
	 * 
	 * @return String representing the score of the currently processed
	 *         Solution.
	 */
	public String renderSolutionState(RatedSolution rs, boolean symbolicStates) {
		if (symbolicStates) {
			return renderSymbolicState(rs.getRating());
		}
		else {
			return renderScore(rs.getRating());
		}
	}

	/**
	 * Transforms the state of a RatedSolution to nice a formatted String in
	 * preparation for rendering. This Method transforms the scores to symbolic
	 * states like "established" or "suggested".
	 * 
	 * @param score Score representing the currently processed score of the
	 *        solution
	 * 
	 * @return String representing the state of the currently processed
	 *         solution.
	 */
	public String renderSymbolicState(Rating score) {
		de.d3web.core.knowledge.terminology.Rating state = getState(score);
		return state.getName();
	}

	/**
	 * Transforms the state of a RatedSolution to a nice formatted String in
	 * preparation for rendering. This Method transforms the scores to numbers.
	 * 
	 * @param score Score representing the currently processed score of the
	 *        solution
	 * 
	 * @return String representing the score of the currently processed
	 *         Solution.
	 */
	public String renderScore(Rating score) {
		if (score instanceof ScoreRating) {
			return formater.format(score.getRating());
		}
		return renderSymbolicState(score);
	}

	/**
	 * Returns a state corresponding to the committed score.
	 * 
	 * @param score Rating representing the score of a RatedSolution.
	 * @return DiagnosisState corresponding to the committed scored.
	 */
	private de.d3web.core.knowledge.terminology.Rating getState(Rating score) {

		if (score instanceof ScoreRating) {
			return new HeuristicRating(((ScoreRating) score).getRating());
		}
		else if (score instanceof StateRating) {
			return ((StateRating) score).getRating();
		}

		return null;
	}

	public void setConfig(ConfigLoader config) {
		this.config = config;
	}

	public ConfigLoader getConfig() {
		return config;
	}

}
