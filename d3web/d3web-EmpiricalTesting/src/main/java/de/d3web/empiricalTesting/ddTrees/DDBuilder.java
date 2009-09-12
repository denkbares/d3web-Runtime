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

package de.d3web.empiricalTesting.ddTrees;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.CaseFactory;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;

public class DDBuilder {
	final String HEADER = "digraph g { \ngraph [ \n  rankdir = \"TD\" \n"
			+ "]; \n" + "node [\n" + " fontname=Helvetica\n"
			+ " fontsize = \"16\"\n" + "  shape = none\n" + "];\n"
			+ "edge [ \n" + "];\n";
	final String FOOTER = "\n}\n";
	
	private static NumberFormat formater = new DecimalFormat("#########");

	BotHelper bh = BotHelper.getInstance();
	ConfigLoader config = ConfigLoader.getInstance();
	
	private Set<String> createdEdges;
	private HashMap<String, DDNode> nodes;
	
	public enum caseType {
		old_case, new_case, incorrect
	};	
	
	public DDBuilder(){
//		createdEdges = new HashSet<String>();
//		nodes = new HashMap<String, DDNode>();
	}

	public void printDOT(TestSuite TS, String dotFile){
		
		String partitionTree = config.getProperty("partitionTree");
		if(partitionTree.equals("true")){
		
			//Die erste Frage ermitteln
			QuestionChoice firstQuestion = (QuestionChoice)TS.getRepository().get(0).
								getCases().get(0).getFindings().get(0).getQuestion();
			//Die Antwortalternativen 
			List<AnswerChoice> firstAnswers = firstQuestion.getAllAlternatives();
			for(Answer answerOfFirstQuestion : firstAnswers){
				TestSuite partitioned = TS.getPartiallyAnsweredSuite((AnswerChoice)answerOfFirstQuestion);
				if(partitioned.getRepository().size()>0){
					generateDDNet(partitioned);				
					String printFilePath = checkDotFilePath(dotFile, answerOfFirstQuestion.getId());
					System.out.println(printFilePath);
					try {
						writeToFile(new File(printFilePath));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}else{
			generateDDNet(TS);
			dotFile = checkDotFilePath(dotFile, "");
			try {
				writeToFile(new File(dotFile));
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
	
	private void generateDDNet(TestSuite TS) {
	
		createdEdges = new HashSet<String>();
		nodes = new HashMap<String, DDNode>();

		for (SequentialTestCase stc : TS.getRepository()) {

			List<RatedTestCase> ratedCases = stc.getCases();
			DDNode prec = null;

			for (int i = 0; i < ratedCases.size(); i++) {
				RatedTestCase ratedTestCase = ratedCases.get(i);

				caseType theCaseType;

				String name = ratedTestCase.getName();
				if (nodes.get(name) == null)
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
	
	public void writeToFile(File file) throws IOException {
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
		String s0name = edge.getBegin().getTestCase().getName();
		String s1name = edge.getEnd().getTestCase().getName();
		String arcName = s0name + "-" + s1name;
		if (createdEdges.contains(arcName))
			return;
		else {
			createdEdges.add(arcName);
			String label = edge.getLabel().getAnswer().toString();
			
			b.append("\"" + s0name + "\" -> \"" + s1name + "\" [");
			b.append("label = " + bh.prettyLabel(label));
			
			boolean bolOldCasesLikeNewCases = config.getProperty("renderOldCasesLikeNewCases").equals("true");
			
			switch(edge.getTheCasetype()){
				case new_case:
					b.append(" color = " + config.getProperty("edgeColorNewCase"));
					b.append(" penwidth = " + config.getProperty("edgeWidthNewCase"));
					break;
				case old_case:
					
					if(bolOldCasesLikeNewCases){
						b.append(" color = " + config.getProperty("edgeColorNewCase"));
						b.append(" penwidth = " + config.getProperty("edgeWidthNewCase"));						
					}else{
						b.append(" color = " + config.getProperty("edgeColorOldCase"));
						b.append(" penwidth = " + config.getProperty("edgeWidthOldCase"));						
					}

					break;
				case incorrect:
					b.append(" color = " + config.getProperty("edgeColorIncorrectCase"));
					b.append(" penwidth = " + config.getProperty("edgeWidthIncorrectCase"));
					break;
			}
			
			b.append("]\n");
		}
	}

	private void createNode(StringBuffer b, DDNode node, List<Question> nextQuestions) {
		
		b.append(node.getTestCase().getName() + " [\n  label=<\n");
		b.append("   <TABLE>\n");

		//Colspan
		int intColSpan = 2;
		
		String strSolutionColorSuggested = config.getProperty("solutionColorSuggested");
		String strSolutionColorEstablished = config.getProperty("solutionColorEstablished");
		
		String nodeColor = "";
		boolean bolOldCasesLikeNewCases = config.getProperty("renderOldCasesLikeNewCases").equals("true");
		
		boolean bolCompareOnlySymbolicStates = config.getProperty("compareOnlySymbolicStates").equals("true");
		
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
				
				strSolutionColorSuggested = nodeColor;
				strSolutionColorEstablished = nodeColor;				
				break;
			case incorrect:
				nodeColor = config.getProperty("nodeColorIncorrectCase");
				if(config.getProperty("printCorrectionColumn").equals("true"))
					intColSpan = 3;				
				break;
		}

		// print question answered for deriving the current solutions
		Finding currentFinding = node.getTestCase().getFindings().get(0);
		String nodeName = currentFinding.toString();

		b.append("    <TR><TD COLSPAN=\""+intColSpan+"\" BGCOLOR=\"" + nodeColor + "\">"
				+ bh.pretty(nodeName) + "</TD> </TR>\n");

		//Sort Expected and Derived Solutions by name
		Collections.sort(node.getTestCase().getDerivedSolutions(), 
				new RatedSolution.RatingComparatorByName());
		Collections.sort(node.getTestCase().getExpectedSolutions(), 
				new RatedSolution.RatingComparatorByName());		
		
		// print solutions trace
		for (int i=0; i<node.getTestCase().getExpectedSolutions().size(); i++){
			
			RatedSolution rsolExp = node.getTestCase().getExpectedSolutions().get(i);	
			Rating scoreExp = rsolExp.getRating();
			DiagnosisState stateExp = null;
			if (scoreExp instanceof ScoreRating) {
				stateExp = DiagnosisState.getState(((ScoreRating)scoreExp).getRating());
			} else if (scoreExp instanceof StateRating) {
				stateExp = ((StateRating)scoreExp).getRating();
			}
			
			
			RatedSolution rsolDer = node.getTestCase().getDerivedSolutions().get(i);			
			Rating scoreDer = rsolDer.getRating();
			DiagnosisState stateDer = null;
			if (scoreExp instanceof ScoreRating) {
				stateDer = DiagnosisState.getState(((ScoreRating)scoreExp).getRating());
			} else if (scoreExp instanceof StateRating) {
				stateDer = ((StateRating)scoreExp).getRating();
			}
			
			
			String color = "";
			if(stateDer.equals(DiagnosisState.ESTABLISHED))
				color = strSolutionColorEstablished;
			else
				color = strSolutionColorSuggested;
			
			String rsolDerText = rsolDer.getSolution().getText();
			String rsolExpText = rsolExp.getSolution().getText();			
			
			if(rsolDerText.equals(rsolExpText))
				b.append("  <TR><TD BGCOLOR=\"" + color + "\" ALIGN=\"left\">"
						+ bh.pretty(rsolDerText) + "</TD>");
			else
				b.append("  <TR><TD BGCOLOR=\"" + color + "\" ALIGN=\"left\">"
						+ bh.pretty(rsolDerText) + " (exp. " + 
						bh.pretty(rsolExpText) + ")</TD>");
			
			String text = "";
			if(bolCompareOnlySymbolicStates){
				DiagnosisState diagDer = stateDer;
				DiagnosisState diagExp = stateExp;
				if(diagDer.equals(diagExp))
					text = diagDer.toString();
				else
					text = diagDer.toString().substring(0,5) + " (exp. " +
						diagExp.toString().substring(0,5) + ")";
			}else{
				if(scoreDer.equals(scoreExp)) {
					if (scoreDer instanceof ScoreRating) {
						text = formater.format(scoreDer.getRating());
					} else {
						text = stateDer.getName();
					}
				} else {
					if (scoreDer instanceof ScoreRating) {
						text = formater.format(scoreDer.getRating()) + 
						" (exp. " + formater.format(scoreExp.getRating()) +")";	
					} else {
						text = stateDer.getName() + 
						" (exp. " + stateExp.getName() +")";	
					}
				}
			}
					
			b.append(" <TD ALIGN=\"right\" BGCOLOR=\"" + color + "\" >" + text + "</TD>");			
			
			//print cell for reviewer´s correction
			if(intColSpan>2) b.append("<TD WIDTH=\"50\"> </TD>");
			
			b.append("</TR>\n");			
		}

		// print questions to be asked next (mostly only one)
		for (Question question : nextQuestions) {
			b.append("    <TR><TD COLSPAN=\""+intColSpan+"\" BGCOLOR=\"" + nodeColor + "\">"
					+ bh.pretty(question.getText()) + "</TD> </TR>\n");
		}

		b.append("   </TABLE>>\n");
		b.append("];\n");
	}
}
