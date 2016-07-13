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
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.empiricaltesting.CaseUtils;
import de.d3web.empiricaltesting.Finding;
import de.d3web.empiricaltesting.RatedSolution;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.Rating;
import de.d3web.empiricaltesting.ScoreRating;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestCase;
import de.d3web.empiricaltesting.casevisualization.CaseVisualizer;
import de.d3web.empiricaltesting.casevisualization.ConfigLoader;
import de.d3web.empiricaltesting.casevisualization.ConfigLoader.EdgeShowAnswers;
import de.d3web.empiricaltesting.casevisualization.Label;
import de.d3web.empiricaltesting.casevisualization.util.Util;

@SuppressWarnings("deprecation") // not important enough to refactor
public final class DDBuilder implements CaseVisualizer {

	private static final String CORRECTION_CELL = "<TD BGCOLOR=\"#FFFFFF\" WIDTH=\"50\"></TD>";
	private static final String HEADER = "digraph g { \ngraph [ \n  rankdir = \"TD\" \n"
			+ "]; \n" + "node [\n" + " fontname=Helvetica\n"
			+ " fontsize = \"16\"\n" + "  shape = none\n" + "];\n"
			+ "edge [ \n" + "];\n";
	private static final String FOOTER = "\n}\n";

	private static final NumberFormat formater = new DecimalFormat("#########");

	private ConfigLoader config = ConfigLoader.getInstance();
	private Label label = null;

	private Set<String> createdEdges;
	private final List<DDNode> nodes = new LinkedList<>();

	public DDBuilder() {
	}

	@Override
	public void setLabel(Label label) {
		this.label = label;
	}

	/**
	 * Streams the graph to an OutputStream (useful for web requests!)
	 *
	 * @param cases     List<SequentialTestCase> cases
	 * @param outStream OutputStream
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
	 *              by this class.
	 * @param file  String which specifies where the created <b>DOT file</b> will
	 *              be stored.
	 */
	@Override
	public void writeToFile(List<SequentialTestCase> cases, File file) throws IOException {
		writeToFile(cases, file.getPath());
	}

	private void writeToFile(List<SequentialTestCase> cases, String filepath) throws IOException {
		filepath = checkDotFilePath(filepath, "");
		try (FileOutputStream fileOutputStream = new FileOutputStream(filepath)) {
			writeToStream(cases, fileOutputStream);
		}
	}

	/**
	 * Saves the graph visualization to a <b>DOT file</b> which will be created
	 * at the committed filepath.
	 *
	 * @param testSuite TestSuite which's cases will be visualized by this
	 *                  class.
	 * @param dotFile   String which specifies where the created <b>DOT file</b> will
	 *                  be stored.
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
						Util.getPartiallyAnsweredSuite(answerOfFirstQuestion,
								testSuite.getRepository());
				if (!partitioned.getRepository().isEmpty()) {
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
		String ret;

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

		createdEdges = new HashSet<>();
		Map<RatedTestCase, DDNode> nodeMap = new HashMap<>();

		// first we generate the complete net
		for (SequentialTestCase stc : cases) {
			List<RatedTestCase> ratedCases = stc.getCases();
			DDNode prec = null;

			for (int i = 0; i < ratedCases.size(); i++) {
				RatedTestCase ratedTestCase = ratedCases.get(i);
				DDNode node = getDDNode(ratedTestCase, nodeMap, prec);
				prec = node;
			}
		}

		// afterwards, we rework the tree for splitting nodes into sub-graphs
		boolean seperateQuestionSolutionBlocks =
				getConfig().getProperty("seperateQuestionSolutionBlocks").equals("true");
		if (seperateQuestionSolutionBlocks) {
			splitMixedFindingSolutionNodes();
			splitMixedDecisiveNodes();
		}
	}

	/**
	 * Splits all nodes that are mixing up findings and solutions into two
	 * connected nodes
	 *
	 * @created 20.07.2011
	 */
	private void splitMixedFindingSolutionNodes() {
		List<DDNode> allNodes = new ArrayList<>(this.nodes);
		for (DDNode node : allNodes) {
			if (node.getFindings().isEmpty()) continue;
			if (node.getDerivedSolutions().isEmpty() && node.getExpectedSolutions().isEmpty()) continue;

			DDNode questionNode = DDNode.createFindingNode(
					node.getCaseName(), node.getFindings(), node.isTestedBefore());
			DDNode solutionNode = DDNode.createSolutionNode(
					node.getCaseName(),
					node.getExpectedSolutions(), node.getDerivedSolutions(), node.isTestedBefore());
			questionNode.addChild(solutionNode);
			replaceNode(node, questionNode, solutionNode);
			nodes.add(questionNode);
			nodes.add(solutionNode);
		}
	}

	/**
	 * Splits all nodes that are mixing up decisive and non-decisive findings.
	 *
	 * @created 20.07.2011
	 */
	private void splitMixedDecisiveNodes() {
		List<DDNode> allNodes = new ArrayList<>(this.nodes);
		for (DDNode node : allNodes) {
			// check for decisive questions
			// and continue if we do not have such ones
			List<Question> decisiveQuestions = node.getDecisiveQuestions();
			if (decisiveQuestions.isEmpty()) continue;
			// check for common question
			// and continue if we do not have such ones
			List<Finding> commonFindings = node.getNonDecisiveFindings();
			if (commonFindings.isEmpty()) continue;
			DDNode commonNode = DDNode.createFindingNode(
					// must be null,
					// because it is the common part of all children
					null,
					commonFindings,
					node.isTestedBefore());
			this.nodes.add(commonNode);
			for (DDNode child : node.getChildNodes()) {
				// only add decisive findings and solutions
				List<Finding> decisiveFindings = new LinkedList<>(child.getFindings());
				decisiveFindings.removeAll(commonNode.getFindings());
				DDNode specificNode = DDNode.createNode(
						child.getCaseName(),
						decisiveFindings,
						child.getDerivedSolutions(),
						child.getExpectedSolutions(),
						child.isTestedBefore());
				commonNode.addChild(specificNode);
				replaceNode(child, commonNode, specificNode);
				this.nodes.add(specificNode);
			}
		}
	}

	private void replaceNode(DDNode old, DDNode subgraphRoot, DDNode subgraphLeaf) {
		// replace old item from all its parents
		for (DDNode parent : old.getParentNodes()) {
			parent.removeChild(old);
			parent.addChild(subgraphRoot);
		}
		// replace old item in all its children
		for (DDNode child : old.getChildNodes()) {
			old.removeChild(child);
			subgraphLeaf.addChild(child);
		}
		// and remove old node from our node list
		this.nodes.remove(old);
	}

	private DDNode getDDNode(RatedTestCase ratedTestCase, Map<RatedTestCase, DDNode> nodeMap, DDNode precessor) {
		DDNode node = nodeMap.get(ratedTestCase);
		if (node == null) {
			node = DDNode.createCompleteNode(ratedTestCase);
			nodes.add(node);
			nodeMap.put(ratedTestCase, node);
			if (precessor != null) {
				precessor.addChild(node);
			}
		}
		return node;
	}

	public String render() {
		StringBuffer b = new StringBuffer(HEADER);
		renderLabel(b);
		for (DDNode node : nodes) {
			renderNode(b, node, computeOutgoing(node));
			for (DDEdge edge : node.getOutgoing()) {
				renderEdge(b, edge);
			}
		}
		b.append(FOOTER);
		return b.toString();
	}

	public void renderLabel(StringBuffer result) {
		if (this.label == null) return;
		result.append("label0");
		result.append(" [\n  label=<\n");
		result.append("   <TABLE>\n");
		String colorHeading = getConfig().getProperty("nodeColorLabelHeading");
		String colorEntries = getConfig().getProperty("nodeColorLabelEntries");
		// render about
		renderTableLine(result, colorHeading, false, "About");
		List<String> aboutKeys = this.label.getAboutKeys();
		for (String key : aboutKeys) {
			String value = this.label.getAboutValue(key);
			renderTableLine(result, colorEntries, false, encodeHTML(key), encodeHTML(value));
		}
		// render seed
		renderTableLine(result, colorHeading, false, "Seed");
		List<Fact> seedEntries = this.label.getSeedEntries();
		for (Fact fact : seedEntries) {
			String name = CaseUtils.getPrompt(fact.getTerminologyObject());
			String value = CaseUtils.getPrompt(fact.getTerminologyObject(), fact.getValue());
			renderTableLine(result, colorEntries, false, encodeHTML(name), encodeHTML(value));
		}
		result.append("   </TABLE>>\n");
		result.append("];\n");
	}

	private void write(OutputStream out) throws IOException {
		OutputStreamWriter dotwriter =
				new OutputStreamWriter(out, "UTF-8");
		dotwriter.write(render());
		dotwriter.close();
	}

	private List<Question> computeOutgoing(DDNode node) {
		List<Question> outgoingQuestions = new ArrayList<>(1);
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
		if (!createdEdges.contains(arcName)) {
			createdEdges.add(arcName);

			b.append("\"").append(name0).append("\" -> \"").append(name1).append("\" [");
			b.append("label = \"");
			EdgeShowAnswers showAnswers =
					EdgeShowAnswers.valueOf(getConfig().getProperty("edgeShowAnswers"));
			if (!showAnswers.equals(EdgeShowAnswers.none)) {
				List<Finding> findings = edge.getEnd().getFindings();
				for (Finding f : findings) {
					if (showAnswers.equals(EdgeShowAnswers.decisive)
							&& !hasMultipleOutgoingValues(f.getQuestion(), edge.getBegin())) {
						continue;
					}
					b.append(renderEdgeLabel(f));
					b.append("\\l");
				}
			}
			b.append("\"");

			boolean bolOldCasesLikeNewCases =
					getConfig().getProperty("renderOldCasesLikeNewCases").equals("true");

			if (edge.isTestedBefore() && !bolOldCasesLikeNewCases) {
				b.append(" color = \"").append(getConfig().getProperty("edgeColorOldCase")).append("\"");
				b.append(" penwidth = ").append(getConfig().getProperty("edgeWidthOldCase"));
			}
			else {
				b.append(" color = \"").append(getConfig().getProperty("edgeColorNewCase")).append("\"");
				b.append(" penwidth = ").append(getConfig().getProperty("edgeWidthNewCase"));
			}

			b.append("]\n");
		}
	}

	/**
	 * Returns if the specified {@link Question} has multiple values within the
	 * outgoing paths of a specified source {@link DDNode}.
	 *
	 * @param question   the question to be checked
	 * @param sourceNode the node where the different paths starts
	 * @return if there are multiple different values for this question
	 * @created 02.05.2011
	 */
	static boolean hasMultipleOutgoingValues(Question question, DDNode sourceNode) {
		Set<Value> values = new HashSet<>();
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
	 * @param text the text to be encoded
	 * @return the encoded text
	 * @created 02.05.2011
	 */
	public String encodeHTML(String text) {
		if (text == null) return "";
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
	 * @param finding the finding to be rendered as a label
	 * @return the rendered finding
	 * @created 02.05.2011
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
		renderTableLine(
				result, nodeColor, false,
				(node.getCaseNote() == null) ? "???" : node.getCaseNote());
		result.append("   </TABLE>>\n");
		result.append("];\n");
	}

	/**
	 * Renders the case name as the header of a node into the result buffer.
	 * This method must render a set of HTML table rows into the buffer
	 * (including the opening and closing &lt;TR&gt;...&lt;/TR&gt; tags), as
	 * specified in the dot language for HTML nodes.
	 *
	 * @param result the result buffer to render into
	 * @param node   the node to render its case name
	 * @created 03.05.2011
	 */
	public void renderNodeCaseNameRow(StringBuffer result, DDNode node) {
		if (Boolean.valueOf(config.getProperty("showTestCaseName"))) {
			String color = config.getProperty("testCaseNameColor");
			renderTableLine(result, color, false, node.getCaseName());
		}
	}

	public void renderNode(StringBuffer result, DDNode node, List<Question> nextQuestions) {

		// prepare list of all solutions
		List<RatedSolution> allRatedSolutions = new LinkedList<>();
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
				Set<Solution> printedSolutions = new HashSet<>();
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
	 * @param result        the result buffer to render into
	 * @param node          the node to render its findings as answers
	 * @param nextQuestions the next questions of that node
	 * @created 02.05.2011
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
	 * @param result the result buffer to render into
	 * @param node   the node to render its findings as answers
	 * @created 02.05.2011
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
			String valuePrompt = null;
			if (UndefinedValue.isNotUndefinedValue(finding.getValue())) {
				valuePrompt = encodeHTML(finding.getValuePrompt());
			}
			renderTableLine(
					result, null, false,
					encodeHTML(finding.getQuestionPrompt()),
					valuePrompt);
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
	 */
	private Map<Solution, RatedSolution> getSolutionsInHashMap(
			List<RatedSolution> solutions) {

		Map<Solution, RatedSolution> result =
				new HashMap<>();

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
			String text = cells[i];
			if (text == null) {
				String emptyColor = getConfig().getProperty("cellColorNull");
				result.append(" BGCOLOR=\"").append(emptyColor).append("\"");
				text = "";
			}
			result.append(">");
			result.append(text);
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
	 * @param result           the result buffer to render into
	 * @param node             the node to render its findings as answers
	 * @param correctionColumn is a correction column required
	 * @created 02.05.2011
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
	 * @param result         the result buffer to render into
	 * @param node           the node to render its findings as answers
	 * @param derived        RatedSolution the derived solution which will be
	 *                       transformed
	 * @param color          String containing color information
	 * @param symbolicStates boolean containing information whether states are
	 *                       shown as symbolic states or scores
	 * @created 02.05.2011
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
	 * @param result           the result buffer to render into
	 * @param node             the node to render its findings as answers
	 * @param derived          RatedSolution the derived solution which will be rendered
	 * @param expected         RatedSolution the expected solution which will be
	 *                         rendered
	 * @param expectedColor    String containing color information for expected
	 *                         solution
	 * @param derivedColor     String containing color information for derived
	 *                         solution
	 * @param correctionColumn decide whether correction cell should be shown
	 * @param symbolicStates   boolean containing information whether states are
	 *                         shown as symbolic states or scores
	 * @created 02.05.2011
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
	 * @param rs             RatedSolution representing the currently processed
	 *                       RatedSolution
	 * @param symbolicStates boolean containing information whether states are
	 *                       shown as symbolic states or scores
	 * @return String representing the score of the currently processed
	 * Solution.
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
	 *              solution
	 * @return String representing the state of the currently processed
	 * solution.
	 */
	public String renderSymbolicState(Rating score) {
		de.d3web.core.knowledge.terminology.Rating state = CaseUtils.getState(score);
		return state.getName();
	}

	/**
	 * Transforms the state of a RatedSolution to a nice formatted String in
	 * preparation for rendering. This Method transforms the scores to numbers.
	 *
	 * @param score Score representing the currently processed score of the
	 *              solution
	 * @return String representing the score of the currently processed
	 * Solution.
	 */
	public String renderScore(Rating score) {
		if (score instanceof ScoreRating) {
			return formater.format(score.getRating());
		}
		return renderSymbolicState(score);
	}

	public void setConfig(ConfigLoader config) {
		this.config = config;
	}

	public ConfigLoader getConfig() {
		return config;
	}

}
