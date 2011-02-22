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

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.empiricaltesting.ConfigLoader;
import de.d3web.empiricaltesting.Finding;
import de.d3web.empiricaltesting.RatedSolution;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.Rating;
import de.d3web.empiricaltesting.ScoreRating;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.StateRating;
import de.d3web.empiricaltesting.TestCase;
import de.d3web.empiricaltesting.casevisualization.BotHelper;
import de.d3web.empiricaltesting.casevisualization.CaseVisualizer;
import de.d3web.scoring.HeuristicRating;

public final class DDBuilder implements CaseVisualizer {

	private final static String HEADER = "digraph g { \ngraph [ \n  rankdir = \"TD\" \n"
			+ "]; \n" + "node [\n" + " fontname=Helvetica\n"
			+ " fontsize = \"16\"\n" + "  shape = none\n" + "];\n"
			+ "edge [ \n" + "];\n";
	private final static String FOOTER = "\n}\n";

	private static NumberFormat formater = new DecimalFormat("#########");

	private static DDBuilder instance = new DDBuilder();

	private BotHelper bh = BotHelper.getInstance();
	private ConfigLoader config = ConfigLoader.getInstance();

	private Set<String> createdEdges;
	private Map<String, DDNode> nodes;

	public enum caseType {
		old_case, new_case, incorrect
	};

	private DDBuilder() {
	}

	public static DDBuilder getInstance() {
		return instance;
	}

	/**
	 * Streams the graph to an OutputStream (useful for web requests!)
	 * 
	 * @param cases List<SequentialTestCase> cases
	 * @param out OutputStream
	 */
	@Override
	public ByteArrayOutputStream getByteArrayOutputStream(List<SequentialTestCase> cases) {
		generateDDNet(cases);
		ByteArrayOutputStream bstream = new ByteArrayOutputStream();
		try {
			write(bstream);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return bstream;
	}

	/**
	 * Saves the graph visualization to a <b>DOT file</b> which will be created
	 * at the committed filepath.
	 * 
	 * @param cases List<SequentialTestCase> which's elements will be visualized
	 *        by this class.
	 * @param filepath String which specifies where the created <b>DOT file</b>
	 *        will be stored.
	 */
	@Override
	public void writeToFile(List<SequentialTestCase> cases, String filepath) {
		generateDDNet(cases);
		filepath = checkDotFilePath(filepath, "");
		try {
			write(new FileOutputStream(filepath));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves the graph visualization to a <b>DOT file</b> which will be created
	 * at the committed filepath.
	 * 
	 * @param testSuite TestSuite which's cases will be visualized by this
	 *        class.
	 * @param filepath String which specifies where the created <b>DOT file</b>
	 *        will be stored.
	 */
	@Override
	public void writeToFile(TestCase testSuite, String dotFile) {

		String partitionTree = config.getProperty("partitionTree");
		if (partitionTree.equals("true")) {

			// Die erste Frage ermitteln
			QuestionChoice firstQuestion = (QuestionChoice) testSuite.getRepository().get(0).
					getCases().get(0).getFindings().get(0).getQuestion();
			// Die Antwortalternativen
			List<Choice> firstAnswers = firstQuestion.getAllAlternatives();
			for (Choice answerOfFirstQuestion : firstAnswers) {
				TestCase partitioned =
						testSuite.getPartiallyAnsweredSuite(answerOfFirstQuestion);
				if (partitioned.getRepository().size() > 0) {
					generateDDNet(partitioned.getRepository());
					String printFilePath =
							checkDotFilePath(dotFile, answerOfFirstQuestion.getName());
					System.out.println(printFilePath);
					try {
						write(new FileOutputStream(printFilePath));
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		else {
			generateDDNet(testSuite.getRepository());
			dotFile = checkDotFilePath(dotFile, "");
			try {
				write(new FileOutputStream(dotFile));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
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
		nodes = new HashMap<String, DDNode>();

		for (SequentialTestCase stc : cases) {

			List<RatedTestCase> ratedCases = stc.getCases();
			DDNode prec = null;

			for (int i = 0; i < ratedCases.size(); i++) {
				RatedTestCase ratedTestCase = ratedCases.get(i);

				caseType sessionType;

				String name = ratedTestCase.getName()
						+ ratedTestCase.getFindings().get(0).toString();

				if (nodes.get(name) == null) nodes.put(name, new DDNode(ratedTestCase));

				DDNode node = nodes.get(name);

				sessionType = caseType.new_case;

				if (ratedTestCase.wasTestedBefore()) sessionType = caseType.old_case;

				if (!ratedTestCase.isCorrect()) sessionType = caseType.incorrect;

				node.setTheCaseType(sessionType);

				if (prec != null && node.getFindings().size() > 0) prec.addChild(node,
						node.getFindings().get(0), sessionType);

				prec = node;
			}
		}
	}

	@Deprecated
	public String generateDOT(boolean cutQuestionnaireSiblingIndication) {
		StringBuffer b = new StringBuffer(HEADER);
		for (DDNode node : nodes.values()) {
			createNode(b, node, computeOutgoing(node));
			for (DDEdge edge : node.getOutgoing()) {
				createEdge(b, edge);
			}
		}
		b.append(FOOTER);
		return b.toString();
	}

	public String generateDOT() {
		return generateDOT(false);
	}

	private void write(OutputStream out) throws IOException {
		OutputStreamWriter dotwriter =
				new OutputStreamWriter(out, "UTF-8");
		dotwriter.write(generateDOT());
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

	private void createEdge(StringBuffer b, DDEdge edge) {
		StringBuilder s0name = new StringBuilder();
		s0name.append(bh.removeBadChars(edge.getBegin().getTestCase().getName()));
		s0name.append("_");
		s0name.append(bh.removeBadChars(edge.getBegin().getTestCase().getFindings().get(0).getQuestion().toString()));
		s0name.append("_");
		s0name.append(bh.removeBadChars(edge.getBegin().getTestCase().getFindings().get(0).getValue().toString()));

		StringBuilder s1name = new StringBuilder();
		s1name.append(bh.removeBadChars(edge.getEnd().getTestCase().getName()));
		s1name.append("_");
		s1name.append(bh.removeBadChars(edge.getEnd().getTestCase().getFindings().get(0).getQuestion().toString()));
		s1name.append("_");
		s1name.append(bh.removeBadChars(edge.getEnd().getTestCase().getFindings().get(0).getValue().toString()));

		String arcName = s0name.toString() + "-" + s1name.toString();
		if (createdEdges.contains(arcName)) return;
		else {
			createdEdges.add(arcName);
			// String label = edge.getLabel().getAnswer().toString();
			//
			// b.append("\"" + s0name + "\" -> \"" + s1name + "\" [");
			// b.append("label = " + bh.prettyLabel(label));

			b.append("\"" + s0name + "\" -> \"" + s1name + "\" [");
			b.append("label = \"");
			for (Finding f : edge.getEnd().getFindings()) {
				b.append(bh.prettyLabel(f.getValue().toString()));
				b.append("\\l");
			}
			b.append("\"");

			boolean bolOldCasesLikeNewCases =
					config.getProperty("renderOldCasesLikeNewCases").equals("true");

			switch (edge.getTheCasetype()) {
			case new_case:
				b.append(" color = \"" +
						config.getProperty("edgeColorNewCase") + "\"");
				b.append(" penwidth = " +
						config.getProperty("edgeWidthNewCase"));
				break;
			case old_case:

				if (bolOldCasesLikeNewCases) {
					b.append(" color = \"" +
							config.getProperty("edgeColorNewCase") + "\"");
					b.append(" penwidth = " +
							config.getProperty("edgeWidthNewCase"));
				}
				else {
					b.append(" color = \"" +
							config.getProperty("edgeColorOldCase") + "\"");
					b.append(" penwidth = " +
							config.getProperty("edgeWidthOldCase"));
				}

				break;
			case incorrect:
				b.append(" color = \"" +
						config.getProperty("edgeColorIncorrectCase") + "\"");
				b.append(" penwidth = " +
						config.getProperty("edgeWidthIncorrectCase"));
				break;
			}

			b.append("]\n");
		}
	}

	private void createNode(StringBuffer b, DDNode node, List<Question> nextQuestions) {

		b.append(bh.removeBadChars(node.getTestCase().getName()));
		b.append("_");
		b.append(bh.removeBadChars(node.getTestCase().getFindings().get(0).getQuestion().toString()));
		b.append("_");
		b.append(bh.removeBadChars(node.getTestCase().getFindings().get(0).getValue().toString()));
		b.append(" [\n  label=<\n");
		b.append("   <TABLE>\n");

		// Colspan
		int intColSpan = 3;

		String strSolutionColorCorrect =
				config.getProperty("nodeColorNewCase");

		String strSolutionColorSuggested =
				config.getProperty("solutionColorSuggested");
		String strSolutionColorEstablished =
				config.getProperty("solutionColorEstablished");

		String nodeColor = "";
		boolean bolOldCasesLikeNewCases =
				config.getProperty("renderOldCasesLikeNewCases").equals("true");

		boolean bolCompareOnlySymbolicStates =
				config.getProperty("compareOnlySymbolicStates").equals("true");

		switch (node.getTheCaseType()) {
		case new_case:
			nodeColor = config.getProperty("nodeColorNewCase");
			strSolutionColorSuggested = nodeColor;
			strSolutionColorEstablished = nodeColor;
			break;
		case old_case:

			if (bolOldCasesLikeNewCases) nodeColor = config.getProperty("nodeColorNewCase");
			else nodeColor = config.getProperty("nodeColorOldCase");

			strSolutionColorCorrect = nodeColor;
			strSolutionColorSuggested = nodeColor;
			strSolutionColorEstablished = nodeColor;
			break;
		case incorrect:
			nodeColor = config.getProperty("nodeColorIncorrectCase");
			if (config.getProperty("printCorrectionColumn").equals("true")) intColSpan = 4;
			break;
		}

		// print question answered for deriving the current solutions

		String nodeName;
		for (Finding f : node.getTestCase().getFindings()) {
			nodeName = f.toString();
			b.append("    <TR><TD COLSPAN=\"" + intColSpan + "\" BGCOLOR=\"" +
					nodeColor + "\">" + bh.pretty(nodeName) + "</TD> </TR>\n");
		}

		// Finding currentFinding = node.getTestCase().getFindings().get(0);
		// String nodeName = currentFinding.toString();
		//
		// b.append("    <TR><TD COLSPAN=\""+intColSpan+"\" BGCOLOR=\"" +
		// nodeColor + "\">" + bh.pretty(nodeName) + "</TD> </TR>\n");

		// Put all RatedSolutions in Maps
		Map<Solution, RatedSolution> expSolutions =
				getSolutionsInHashMap(node.getTestCase().getExpectedSolutions());
		Map<Solution, RatedSolution> derSolutions =
				getSolutionsInHashMap(node.getTestCase().getDerivedSolutions());
		HashSet<Solution> solutions = new HashSet<Solution>();

		solutions.addAll(expSolutions.keySet());
		solutions.addAll(derSolutions.keySet());

		// Print Solutions
		b.append(transformSolutionsHeader(nodeColor, intColSpan));

		for (Solution d : solutions) {
			RatedSolution expected = expSolutions.get(d);
			RatedSolution derived = derSolutions.get(d);

			if (expected != null && derived != null && expected.equals(derived)) {
				b.append(transformCorrectSolution(derived, strSolutionColorCorrect,
						intColSpan, bolCompareOnlySymbolicStates));
			}
			else {
				String expColor = getColor(expected, strSolutionColorSuggested,
						strSolutionColorEstablished, nodeColor);
				String derColor = getColor(derived, strSolutionColorSuggested,
						strSolutionColorEstablished, nodeColor);
				b.append(transformIncorrectSolutions(expected, derived, intColSpan,
						expColor, derColor, nodeColor, bolCompareOnlySymbolicStates));
			}
		}

		// print questions to be asked next (mostly only one)
		for (Question question : nextQuestions) {
			b.append("    <TR><TD COLSPAN=\"" + intColSpan + "\" BGCOLOR=\"" +
					nodeColor + "\">" + bh.pretty(question.getName()) + "</TD> </TR>\n");
		}

		b.append("   </TABLE>>\n");
		b.append("];\n");
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

	/**
	 * Generates the header for the solutions in the table (node).
	 * 
	 * @param color String containing coloration information
	 * @parm colspan int containing colspan information
	 * 
	 * @return String representing the header of the solutions part of the table
	 */
	private String transformSolutionsHeader(String color, int colspan) {

		StringBuilder result = new StringBuilder();

		result.append("    <TR>");
		result.append("<TD BGCOLOR=\"");
		result.append(color);
		result.append("\">");
		result.append("Solution");
		result.append("</TD>");
		result.append("<TD BGCOLOR=\"");
		result.append(color);
		result.append("\">");
		result.append("exp.");
		result.append("</TD>");
		result.append("<TD BGCOLOR=\"");
		result.append(color);
		result.append("\">");
		result.append("der.");
		result.append("</TD>");
		result.append(createCorrectionColumn(colspan));
		result.append("</TR>\n");

		return result.toString();
	}

	/**
	 * Checks if it is necessary to render a column for user correction.
	 * 
	 * @parm colspan int containing colspan information
	 * 
	 * @return String which is empty if column is not required.
	 */
	private String createCorrectionColumn(int colspan) {

		if (colspan == 4) {
			return "<TD BGCOLOR=\"#FFFFFF\" WIDTH=\"50\"></TD>";
		}

		return "";
	}

	/**
	 * Transforms a correct (expected = derived) solution to a nice formatted
	 * String representation in preparation for rendering.
	 * 
	 * @param derived RatedSolution the derived solution which will be
	 *        transformed
	 * @param color String containing coloration information
	 * @parm colspan int containing colspan information
	 * @parm symbolicstates boolean containing information whether states are
	 *       shown as symbolic states or scores
	 * 
	 * @return String representing the transformed RatedSolution
	 */
	private String transformCorrectSolution(RatedSolution derived,
			String color, int colspan, boolean symbolicstates) {

		StringBuilder result = new StringBuilder();

		result.append("    <TR>");
		result.append("<TD BGCOLOR=\"");
		result.append(color);
		result.append("\">");
		result.append(bh.pretty(derived.getSolution().getName()));
		result.append("</TD>");
		result.append("<TD COLSPAN=\"2\" ALIGN=\"CENTER\" BGCOLOR=\"");
		result.append(color);
		result.append("\">");
		result.append(transformState(derived, symbolicstates));
		result.append("</TD>");
		result.append(createCorrectionColumn(colspan));
		result.append("</TR>\n");

		return result.toString();
	}

	/**
	 * Transforms two incorrect (expected != derived) solutions to a nice
	 * formatted String representation in preparation for rendering.
	 * 
	 * @param expected RatedSolution the expected solution which will be
	 *        transformed
	 * 
	 * @param derived RatedSolution the derived solution which will be
	 *        transformed
	 * @parm colspan int containing colspan information
	 * @param expectedcolor String containing coloration information for
	 *        expected solution
	 * @param derivedcolor String containing coloration information for derived
	 *        solution
	 * @param nodecolor String containing coloration information for the node
	 * @parm symbolicstates boolean containing information whether states are
	 *       shown as symbolic states or scores
	 * 
	 * @return String representing the transformed RatedSolutions
	 */
	private String transformIncorrectSolutions(RatedSolution expected,
			RatedSolution derived, int colSpan, String expectedColor,
			String derivedColor, String nodeColor, boolean symbolicstates) {

		StringBuilder result = new StringBuilder();
		String solName = (expected == null ?
				derived.getSolution().getName() :
				expected.getSolution().getName());

		result.append("    <TR>");
		result.append("<TD BGCOLOR=\"");
		result.append(nodeColor);
		result.append("\">");
		result.append(bh.pretty(solName));
		result.append("</TD>");
		result.append("<TD ALIGN=\"CENTER\" BGCOLOR=\"");
		result.append(expectedColor);
		result.append("\">");
		result.append(expected == null ? "N/A" : transformState(expected, symbolicstates));
		result.append("</TD>");
		result.append("<TD ALIGN=\"CENTER\" BGCOLOR=\"");
		result.append(derivedColor);
		result.append("\">");
		result.append(derived == null ? "N/A" : transformState(derived, symbolicstates));
		result.append("</TD>");
		result.append(createCorrectionColumn(colSpan));
		result.append("</TR>\n");

		return result.toString();
	}

	/**
	 * Transforms the state of a RatedSolution to a nice formatted String in
	 * preparation for rendering.
	 * 
	 * @param rs RatedSolution representing the currently processed
	 *        RatedSolution
	 * 
	 * @parm symbolicstates boolean containing information whether states are
	 *       shown as symbolic states or scores
	 * 
	 * @return String representing the score of the currently processed
	 *         Solution.
	 */
	private String transformState(RatedSolution rs, boolean symbolicstates) {

		StringBuilder result = new StringBuilder();

		if (symbolicstates) {
			result.append(transformSymbolicStates(rs.getRating()));

		}
		else {
			result.append(transformScores(rs.getRating()));

		}

		return result.toString();
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
	private String transformSymbolicStates(Rating score) {

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
	private String transformScores(Rating score) {

		if (score instanceof ScoreRating) {
			return formater.format(score.getRating());
		}

		return score.getRating().toString();

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

	/**
	 * Returns the backgroundcolor which is appropriate for the currently
	 * RatedSolution.
	 * 
	 * @param rs RatedSolution which's backgroundcolor we are looking for.
	 * 
	 * @param suggested String containing coloration information for solutions
	 *        which are suggested
	 * @param established String containing coloration information for solutions
	 *        which are established
	 * 
	 * @return String representing the color
	 */
	private String getColor(RatedSolution rs, String suggested,
			String established, String nodeColor) {

		if (rs == null) {
			return nodeColor;
		}

		Rating score = rs.getRating();
		de.d3web.core.knowledge.terminology.Rating state = getState(score);

		if (state.equals(new de.d3web.core.knowledge.terminology.Rating(State.ESTABLISHED))) {
			return established;
		}
		else {
			return suggested;
		}
	}

}
