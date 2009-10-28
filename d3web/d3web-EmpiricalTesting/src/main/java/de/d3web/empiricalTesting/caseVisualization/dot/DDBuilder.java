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

package de.d3web.empiricalTesting.caseVisualization.dot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.d3web.empiricalTesting.ConfigLoader;
import de.d3web.empiricalTesting.Finding;
import de.d3web.empiricalTesting.RatedSolution;
import de.d3web.empiricalTesting.RatedTestCase;
import de.d3web.empiricalTesting.Rating;
import de.d3web.empiricalTesting.ScoreRating;
import de.d3web.empiricalTesting.SequentialTestCase;
import de.d3web.empiricalTesting.StateRating;
import de.d3web.empiricalTesting.TestSuite;
import de.d3web.empiricalTesting.caseVisualization.BotHelper;
import de.d3web.empiricalTesting.caseVisualization.CaseVisualizer;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;

public class DDBuilder implements CaseVisualizer {
	final String HEADER = "digraph g { \ngraph [ \n  rankdir = \"TD\" \n"
			+ "]; \n" + "node [\n" + " fontname=Helvetica\n"
			+ " fontsize = \"16\"\n" + "  shape = none\n" + "];\n"
			+ "edge [ \n" + "];\n";
	final String FOOTER = "\n}\n";
	
	private static NumberFormat formater = new DecimalFormat("#########");

	private static DDBuilder instance = new DDBuilder();
	
	BotHelper bh = BotHelper.getInstance();
	ConfigLoader config = ConfigLoader.getInstance();
	
	private Set<String> createdEdges;
	private HashMap<String, DDNode> nodes;
	
	public enum caseType {
		old_case, new_case, incorrect
	};
	
	private DDBuilder(){}
	
	public static DDBuilder getInstance() {
		return instance;
	}
	
	
	/**
	 * Saves the graph visualization to a <b>DOT file</b> which
	 * will be created at the committed filepath.
	 * 
	 * @param cases List<SequentialTestCase> which's elements
	 *              will be visualized by this class. 
	 * @param filepath String which specifies where the 
	 *                 created <b>DOT file</b> will be stored.
	 */
	@Override
	public void writeToFile(List<SequentialTestCase> cases, String filepath) {
		generateDDNet(cases);
		filepath = checkDotFilePath(filepath, "");
		try {
			write(new File(filepath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * Saves the graph visualization to a <b>DOT file</b> which
	 * will be created at the committed filepath.
	 * 
	 * @param testsuite TestSuite which's cases will be 
	 * 					visualized by this class. 
	 * @param filepath String which specifies where the 
	 *                 created <b>DOT file</b> will be stored.
	 */
	@Override
	public void writeToFile(TestSuite TS, String dotFile){
		
		String partitionTree = config.getProperty("partitionTree");
		if(partitionTree.equals("true")){
		
			//Die erste Frage ermitteln
			QuestionChoice firstQuestion = (QuestionChoice)TS.getRepository().get(0).
								getCases().get(0).getFindings().get(0).getQuestion();
			//Die Antwortalternativen 
			List<AnswerChoice> firstAnswers = firstQuestion.getAllAlternatives();
			for(Answer answerOfFirstQuestion : firstAnswers){
				TestSuite partitioned = 
					TS.getPartiallyAnsweredSuite((AnswerChoice)answerOfFirstQuestion);
				if(partitioned.getRepository().size()>0){
					generateDDNet(partitioned.getRepository());				
					String printFilePath = 
						checkDotFilePath(dotFile, answerOfFirstQuestion.getId());
					System.out.println(printFilePath);
					try {
						write(new File(printFilePath));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}else{
			generateDDNet(TS.getRepository());
			dotFile = checkDotFilePath(dotFile, "");
			try {
				write(new File(dotFile));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private static String checkDotFilePath(String dotFile, String addOn){
		String ret = "";
		
		if(dotFile.equals(""))//empty
			ret = "dotFile" + addOn + ".dot";
		else if(dotFile.endsWith("/"))//just a path
			ret = dotFile + addOn + ".dot";
		else if(dotFile.endsWith(".dot"))//full filepath specified
			if(addOn.equals(""))
				ret = dotFile.substring(0, dotFile.length() - 4) + ".dot";
			else
				ret = dotFile.substring(0, dotFile.length() - 4) + "_" + addOn + ".dot";
		else//we suppose, its a Path without trailing "/"
			ret = dotFile + "/" + addOn + ".dot";		
		
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
				
				caseType theCaseType;

				String name = ratedTestCase.getName() + ratedTestCase.getFindings().get(0).toString();
				
				if (nodes.get(name) == null )
					nodes.put(name, new DDNode(ratedTestCase));				
				
				DDNode node = nodes.get(name);

				theCaseType = caseType.new_case;

				if (ratedTestCase.wasTestedBefore())
					theCaseType = caseType.old_case;

				if (!ratedTestCase.isCorrect())
					theCaseType = caseType.incorrect;

				node.setTheCaseType(theCaseType);

				if (prec != null && node.getFindings().size()>0)
					prec.addChild(node, node.getFindings().get(0), theCaseType);

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
	
	private void write(File file) throws IOException {
		OutputStreamWriter dotwriter = new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8");
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
			if (!outgoingQuestions.contains(finding.getQuestion()))
				outgoingQuestions.add(finding.getQuestion());
		}
		return outgoingQuestions;
	}

	private void createEdge(StringBuffer b, DDEdge edge) {
		StringBuilder s0name = new StringBuilder();
		s0name.append(bh.removeBadChars(edge.getBegin().getTestCase().getName()));
		s0name.append("_");
		s0name.append(bh.removeBadChars(edge.getBegin().getTestCase().getFindings().get(0).getQuestion().toString()));
		s0name.append("_");
		s0name.append(bh.removeBadChars(edge.getBegin().getTestCase().getFindings().get(0).getAnswer().toString()));
		
		StringBuilder s1name = new StringBuilder();
		s1name.append(bh.removeBadChars(edge.getEnd().getTestCase().getName()));
		s1name.append("_");
		s1name.append(bh.removeBadChars(edge.getEnd().getTestCase().getFindings().get(0).getQuestion().toString()));
		s1name.append("_");
		s1name.append(bh.removeBadChars(edge.getEnd().getTestCase().getFindings().get(0).getAnswer().toString()));
		
		String arcName = s0name.toString() + "-" + s1name.toString();
		if (createdEdges.contains(arcName))
			return;
		else {
			createdEdges.add(arcName);
			String label = edge.getLabel().getAnswer().toString();
			
			b.append("\"" + s0name + "\" -> \"" + s1name + "\" [");
			b.append("label = " + bh.prettyLabel(label));
			
			boolean bolOldCasesLikeNewCases = 
				config.getProperty("renderOldCasesLikeNewCases").equals("true");
			
			switch(edge.getTheCasetype()){
				case new_case:
					b.append(" color = \"" + 
							config.getProperty("edgeColorNewCase") + "\"");
					b.append(" penwidth = " + 
							config.getProperty("edgeWidthNewCase"));
					break;
				case old_case:
					
					if(bolOldCasesLikeNewCases){
						b.append(" color = \"" + 
								config.getProperty("edgeColorNewCase") + "\"");
						b.append(" penwidth = " + 
								config.getProperty("edgeWidthNewCase"));						
					}else{
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
		b.append(bh.removeBadChars(node.getTestCase().getFindings().get(0).getAnswer().toString()));
		b.append(" [\n  label=<\n");
		b.append("   <TABLE>\n");

		//Colspan
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
		
		switch(node.getTheCaseType()){
			case new_case:
				nodeColor = config.getProperty("nodeColorNewCase");
				strSolutionColorSuggested = nodeColor;
				strSolutionColorEstablished = nodeColor;
				break;
			case old_case:
				
				if(bolOldCasesLikeNewCases)
					nodeColor = config.getProperty("nodeColorNewCase");
				else
					nodeColor = config.getProperty("nodeColorOldCase");
				
				strSolutionColorCorrect = nodeColor;
				strSolutionColorSuggested = nodeColor;
				strSolutionColorEstablished = nodeColor;				
				break;
			case incorrect:
				nodeColor = config.getProperty("nodeColorIncorrectCase");
				if(config.getProperty("printCorrectionColumn").equals("true"))
					intColSpan = 4;				
				break;
		}

		// print question answered for deriving the current solutions
		Finding currentFinding = node.getTestCase().getFindings().get(0);
		String nodeName = currentFinding.toString();

		b.append("    <TR><TD COLSPAN=\""+intColSpan+"\" BGCOLOR=\"" + 
				nodeColor + "\">" + bh.pretty(nodeName) + "</TD> </TR>\n");

		// Put all RatedSolutions in HashMaps
		HashMap<Diagnosis, RatedSolution> expSolutions = 
			getSolutionsInHashMap(node.getTestCase().getExpectedSolutions());
		HashMap<Diagnosis, RatedSolution> derSolutions = 
			getSolutionsInHashMap(node.getTestCase().getDerivedSolutions());
		HashSet<Diagnosis> solutions = new HashSet<Diagnosis>();
				
		solutions.addAll(expSolutions.keySet());
		solutions.addAll(derSolutions.keySet());
		
		// Print Solutions
		b.append(transformSolutionsHeader(nodeColor, intColSpan));
		
		for (Diagnosis d : solutions) {
			RatedSolution expected = expSolutions.get(d);
			RatedSolution derived = derSolutions.get(d);
			
			if (expected.equals(derived)) {
				b.append(transformCorrectSolution(derived, strSolutionColorCorrect, 
						intColSpan, bolCompareOnlySymbolicStates));
			} else {
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
			b.append("    <TR><TD COLSPAN=\""+intColSpan+"\" BGCOLOR=\"" + 
				nodeColor + "\">" + bh.pretty(question.getText()) + "</TD> </TR>\n");
		}

		b.append("   </TABLE>>\n");
		b.append("];\n");
	}
	
	/**
	 * Returns all elements (RatedSolution) of a List
	 * in a HashMap.
	 * 
	 * @param solutions List<RatedSolution>
	 * 
	 * @return HashMap<Diagnosis, RatedSolution>
	 */
	private HashMap<Diagnosis, RatedSolution> getSolutionsInHashMap(
			List<RatedSolution> solutions) {
		
		HashMap<Diagnosis, RatedSolution> result = 
			new HashMap<Diagnosis, RatedSolution>();
		
		for (RatedSolution rs : solutions) {
			result.put(rs.getSolution(), rs);
		}
		
		return result;
	}
	
	
	/**
	 * Generates the header for the solutions
	 * in the table (node).
	 * 
	 * @param cfg HashMap<String, String> containing 
	 *            configuration information
	 *            
	 * @return String representing the header of the 
	 *                solutions part of the table
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
	 * Checks if it is necessary to render a column for
	 * user correction.
	 * @param cfg HashMap containing the required information
	 * @return String which is empty if column is not required.
	 */
	private String createCorrectionColumn(int colspan) {
		
		if (colspan == 4) {
			return "<TD BGCOLOR=\"#FFFFFF\" WIDTH=\"50\"></TD>";
		}
		
		return "";
	}
	
	/**
	 * Transforms a correct (expected = derived) solution
	 * to a nice formatted String representation in 
	 * preparation for rendering.
	 * 
	 * @param derived RatedSolution the derived solution
	 *                which will be transformed
	 *                
	 * @param co HashMap containing configuration information
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
		result.append(bh.pretty(derived.getSolution().getText()));
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
	 * Transforms two incorrect (expected != derived) 
	 * solutions to a nice formatted String representation 
	 * in preparation for rendering.
	 * 
	 * @param expected RatedSolution the expected solution
	 *                which will be transformed
	 *                
	 * @param derived RatedSolution the derived solution
	 *                which will be transformed
	 *                
	 * @param cfg HashMap containing configuration information
	 * 
	 * @return String representing the transformed RatedSolutions
	 */
	private String transformIncorrectSolutions(RatedSolution expected,
			RatedSolution derived, int colSpan, String expectedColor, 
			String derivedColor, String nodeColor, boolean symbolicstates) {
		

		StringBuilder result = new StringBuilder();
		String solName = (expected == null ? 
						  derived.getSolution().getText() : 
						  expected.getSolution().getText());
		
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
	 * Transforms the state of a RatedSolution 
	 * to a nice formatted String in preparation 
	 * for rendering.
	 * 
	 * @param rs RatedSolution representing the currently 
	 *           processed RatedSolution

	 * @param cfg HashMap containing config information
	 * 
	 * @return String representing the score of the
	 *                currently processed Solution.
	 */
	private String transformState(RatedSolution rs, boolean symbolicstates) {
							
		StringBuilder result = new StringBuilder();
		
		if (symbolicstates) {					
			result.append(transformSymbolicStates(rs.getRating()));
									
		} else {
			result.append(transformScores(rs.getRating()));					
			
		}
				
		return result.toString();
	}
	
	/**
	 * Transforms the state of a RatedSolution 
	 * to nice a formatted String in preparation 
	 * for rendering. This Method transforms the 
	 * scores to symbolic states like "established" or
	 * "suggested".
	 * 
	 * @param score Score representing the currently 
	 *              processed score of the solution
	 * 
	 * @return String representing the state of the
	 *                currently processed solution.
	 */
	private String transformSymbolicStates(Rating score) {
		
		DiagnosisState state = getState(score);
			
		return state.getName();
		
	}
	
	
	/**
	 * Transforms the state of a RatedSolution
	 * to a nice formatted String in preparation 
	 * for rendering. This Method transforms the 
	 * scores to numbers.
	 * 
	 * @param score Score representing the currently 
	 *              processed score of the solution
	 * 
	 * @return String representing the score of the
	 *                currently processed Solution.
	 */
	private String transformScores(Rating score) {
		
		if (score instanceof ScoreRating) {
			return formater.format(score.getRating());
		} 
			
		return score.getRating().toString();
		
	}
	
	/**
	 * Returns a state corresponding to the
	 * committed score.
	 * @param score Rating representing the score
	 *              of a RatedSolution.
	 * @return DiagnosisState corresponding to
	 *         the committed scored.
	 */
	private DiagnosisState getState(Rating score) {
		
		if (score instanceof ScoreRating) {
			return DiagnosisState.getState(((ScoreRating)score).getRating());
		} else if (score instanceof StateRating) {
			return ((StateRating)score).getRating();
		}
		
		return null;
	}
	
	/**
	 * Returns the backgroundcolor which is
	 * appropriate for the currently RatedSolution.
	 * 
	 * @param rs RatedSolution which's backgroundcolor
	 *           we are looking for.
	 *           
	 * @param cfg HashMap containing config information
	 * 
	 * @return String representing the color
	 */
	private String getColor(RatedSolution rs, String suggested, 
			String established, String nodeColor) {
		
		if (rs == null) {
			return nodeColor;
		}
		
		Rating score = rs.getRating();
		DiagnosisState state = getState(score);
		
		if(state.equals(DiagnosisState.ESTABLISHED)) {
			return established;	
		} else {
			return suggested;
		}
	}
	
}
