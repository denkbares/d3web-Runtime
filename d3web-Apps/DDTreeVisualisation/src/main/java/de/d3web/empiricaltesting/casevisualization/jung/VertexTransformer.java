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

package de.d3web.empiricaltesting.casevisualization.jung;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Transformer;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.empiricaltesting.CaseUtils;
import de.d3web.empiricaltesting.ConfigLoader;
import de.d3web.empiricaltesting.Finding;
import de.d3web.empiricaltesting.RatedSolution;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.Rating;
import de.d3web.empiricaltesting.ScoreRating;
import de.d3web.empiricaltesting.StateRating;
import de.d3web.scoring.HeuristicRating;

/**
 * This class transforms Rated-Test-Cases which are the vertices of our graph
 * into a nice formatted String for rendering.
 * 
 * @author Sebastian Furth
 * 
 */
public class VertexTransformer implements Transformer<RatedTestCase, String> {

	/**
	 * Graph which's vertices are transformed. This reference is necessary for
	 * getting the questions which are asked next.
	 */
	private CaseTree<RatedTestCase, EdgeFinding> graph;

	/**
	 * Provides some methods that are useful during the transformation process.
	 */
	private CaseUtils bh = CaseUtils.getInstance();

	/**
	 * Formatter to format Scores.
	 */
	private static NumberFormat formater = new DecimalFormat("#########");

	/**
	 * Creates an instance of VertexTransformer backed on the committed CaseTree
	 * object.
	 * 
	 * @param graph CaseTree which's elements are transformed.
	 */
	public VertexTransformer(CaseTree<RatedTestCase, EdgeFinding> graph) {
		this.graph = graph;
	}

	/**
	 * Transforms RatedTestCase to nice formatted String which can be used for
	 * rendering.
	 * 
	 * @param rtc RatedTestCase which is transformed.
	 * @return String representing transformed RTC.
	 */
	@Override
	public String transform(RatedTestCase rtc) {

		Map<String, String> cfg = getConfigs(rtc);

		StringBuilder result = new StringBuilder();
		result.append("<html>");
		result.append("<table ");
		result.append("border=\"0\" ");
		result.append("cellpadding=\"1\" ");
		result.append("cellspacing=\"2\">");
		result.append(transformFinding(rtc, cfg));
		result.append(transformSolutions(rtc, cfg));
		result.append(transformNextQuestion(rtc, cfg));
		result.append("</table>");
		result.append("</html>");

		return result.toString();
	}

	/**
	 * Loads necessary information from config file and stores is it in HashMap.
	 * 
	 * @param rtc Current RatedTestCase.
	 * @return HashMap containing config information.
	 */
	private Map<String, String> getConfigs(RatedTestCase rtc) {

		ConfigLoader config = ConfigLoader.getInstance();

		Map<String, String> configs = new HashMap<String, String>();
		String nodeColor = config.getProperty("nodeColorNewCase");
		String correctColor = config.getProperty("nodeColorNewCase");
		String colspan = "3";
		String colorSuggested = nodeColor;
		String colorEstablished = nodeColor;

		if (rtc.wasTestedBefore()
				&& config.getProperty("renderOldCasesLikeNewCases").equals("false")) {
			nodeColor = config.getProperty("nodeColorOldCase");
			colorSuggested = nodeColor;
			colorEstablished = nodeColor;
			correctColor = nodeColor;
		}

		configs.put("colorSuggested", colorSuggested);
		configs.put("colorEstablished", colorEstablished);
		configs.put("onlySymbolicStates",
				config.getProperty("compareOnlySymbolicStates"));
		configs.put("nodeColor", nodeColor);
		configs.put("colspan", colspan);
		configs.put("correctColor", correctColor);

		return configs;
	}

	/**
	 * Transforms Finding to nice formatted String in preparation for rendering.
	 * 
	 * @param rtc Current RatedTestCase
	 * @param cfg HashMap containing config information
	 * 
	 * @return String representing the Finding.
	 */
	private String transformFinding(RatedTestCase rtc,
			Map<String, String> cfg) {

		StringBuilder result = new StringBuilder();
		for (Finding f : rtc.getFindings()) {
			result.append("<tr>");
			result.append("<td colspan=\"");
			result.append(cfg.get("colspan"));
			result.append("\" bgcolor=\"");
			result.append(cfg.get("nodeColor"));
			result.append("\">");
			result.append("<center>");
			result.append(bh.pretty(f.toString()));
			result.append("</center>");
			result.append("</td>");
			result.append("</tr>");
		}

		return result.toString();
	}

	/**
	 * Transforms solutions to nice formatted String in preparation for
	 * rendering.
	 * 
	 * @param rtc Current RatedTestCase
	 * @param cfg HashMap containing config information
	 * 
	 * @return String representing the Solutions.
	 */
	private String transformSolutions(RatedTestCase rtc,
			Map<String, String> cfg) {

		Map<Solution, RatedSolution> expSolutions =
				getSolutionsInHashMap(rtc.getExpectedSolutions());
		Map<Solution, RatedSolution> derSolutions =
				getSolutionsInHashMap(rtc.getDerivedSolutions());
		Set<Solution> solutions = new HashSet<Solution>();

		solutions.addAll(expSolutions.keySet());
		solutions.addAll(derSolutions.keySet());

		StringBuilder result = new StringBuilder();
		result.append(transformSolutionsHeader(cfg));

		for (Solution d : solutions) {
			RatedSolution expected = expSolutions.get(d);
			RatedSolution derived = derSolutions.get(d);

			if (expected != null && derived != null && expected.equals(derived)) {
				result.append(transformCorrectSolution(derived, cfg));
			}
			else {
				result.append(transformIncorrectSolutions(expected, derived, cfg));
			}
		}

		return result.toString();

	}

	/**
	 * Returns all elements (RatedSolution) of a List in a HashMap.
	 * 
	 * @param solutions List<RatedSolution>
	 * 
	 * @return HashMap<Diagnosis, RatedSolution>
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
	 * @param cfg HashMap<String, String> containing configuration information
	 * 
	 * @return String representing the header of the solutions part of the table
	 */
	private String transformSolutionsHeader(Map<String, String> cfg) {

		StringBuilder result = new StringBuilder();

		result.append("<tr>");
		result.append("<th bgcolor=\"");
		result.append(cfg.get("nodeColor"));
		result.append("\">");
		result.append("Solution");
		result.append("</th>");
		result.append("<th bgcolor=\"");
		result.append(cfg.get("nodeColor"));
		result.append("\">");
		result.append("exp.");
		result.append("</th>");
		result.append("<th bgcolor=\"");
		result.append(cfg.get("nodeColor"));
		result.append("\">");
		result.append("der.");
		result.append("</th>");
		result.append(createCorrectionColumn(cfg));
		result.append("</tr>");

		return result.toString();

	}

	/**
	 * Transforms a correct (expected = derived) solution to a nice formatted
	 * String representation in preparation for rendering.
	 * 
	 * @param derived RatedSolution the derived solution which will be
	 *        transformed
	 * 
	 * @param cfg Map containing configuration information
	 * 
	 * @return String representing the transformed RatedSolution
	 */
	private String transformCorrectSolution(RatedSolution derived,
			Map<String, String> cfg) {

		StringBuilder result = new StringBuilder();

		result.append("<tr>");
		result.append("<td bgcolor=\"");
		result.append(cfg.get("correctColor"));
		// result.append(getColor(derived, cfg));
		result.append("\" >");
		result.append(bh.pretty(derived.getSolution().getName()));
		result.append("</td>");
		result.append("<td colspan=\"2\" bgcolor=\"");
		result.append(cfg.get("correctColor"));
		// result.append(getColor(derived, cfg));
		result.append("\" >");
		result.append("<center>");
		result.append(transformState(derived, cfg));
		result.append("</center>");
		result.append("</td>");
		result.append(createCorrectionColumn(cfg));
		result.append("</tr>");

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
	 * 
	 * @param cfg HashMap containing configuration information
	 * 
	 * @return String representing the transformed RatedSolutions
	 */
	private String transformIncorrectSolutions(RatedSolution expected,
			RatedSolution derived, Map<String, String> cfg) {

		StringBuilder result = new StringBuilder();
		String solName = (expected == null ?
				derived.getSolution().getName() :
				expected.getSolution().getName());

		result.append("<tr>");
		result.append("<td bgcolor=\"");
		result.append(getColor(expected, cfg));
		result.append("\">");
		result.append(bh.pretty(solName));
		result.append("</td>");
		result.append("<td bgcolor=\"");
		result.append(getColor(expected, cfg));
		result.append("\" >");
		result.append("<center>");
		result.append(expected == null ? "N/A" : transformState(expected, cfg));
		result.append("</center>");
		result.append("</td>");
		result.append("<td bgcolor=\"");
		result.append(getColor(derived, cfg));
		result.append("\" >");
		result.append("<center>");
		result.append(derived == null ? "N/A" : transformState(derived, cfg));
		result.append("</center>");
		result.append("</td>");
		result.append(createCorrectionColumn(cfg));
		result.append("</tr>");

		return result.toString();
	}

	/**
	 * Transforms the state of a RatedSolution to a nice formatted String in
	 * preparation for rendering.
	 * 
	 * @param rs RatedSolution representing the currently processed
	 *        RatedSolution
	 * 
	 * @param cfg HashMap containing config information
	 * 
	 * @return String representing the score of the currently processed
	 *         Solution.
	 */
	private String transformState(RatedSolution rs, Map<String,
			String> cfg) {

		StringBuilder result = new StringBuilder();

		if (cfg.get("onlySymbolicStates").equals("true")) {
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
	 * Returns the backgroundcolor which is appropriate for the currently
	 * RatedSolution.
	 * 
	 * @param rs RatedSolution which's backgroundcolor we are looking for.
	 * 
	 * @param cfg HashMap containing config information
	 * 
	 * @return String representing the color
	 */
	private String getColor(RatedSolution rs, Map<String, String> cfg) {

		if (rs == null) {
			return cfg.get("nodeColor");
		}

		Rating score = rs.getRating();
		de.d3web.core.knowledge.terminology.Rating state = getState(score);

		if (state.equals(new de.d3web.core.knowledge.terminology.Rating(State.ESTABLISHED))) {
			return cfg.get("colorEstablished");
		}
		else {
			return cfg.get("colorSuggested");
		}
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
	 * Checks if it is necessary to render a column for user correction.
	 * 
	 * @param cfg HashMap containing the required information
	 * @return String which is empty if column is not required.
	 */
	private String createCorrectionColumn(Map<String, String> cfg) {

		if (cfg.get("colspan").equals("4")) {
			return "<td width=\"50\" bgcolor=\"#FFFFFF\"></td>";
		}

		return "";
	}

	/**
	 * Creates a string representing a list of the questions which will be asked
	 * next.
	 * 
	 * @param rtc The current RatedTestCase.
	 * @param cfg HashMap containing config information.
	 * 
	 * @return String representing a list of answers which will be asked next.
	 */
	private String transformNextQuestion(RatedTestCase rtc,
			Map<String, String> cfg) {

		List<Question> nextQuestions = getNextQuestions(rtc);

		StringBuilder result = new StringBuilder();

		for (Question q : nextQuestions) {
			result.append("<tr>");
			result.append("<td colspan=\"");
			result.append(cfg.get("colspan"));
			result.append("\" bgcolor=\"");
			result.append(cfg.get("nodeColor"));
			result.append("\">");
			result.append("<center>");
			result.append(bh.pretty(q.getName()));
			result.append("</center>");
			result.append("</td>");
			result.append("</tr>");
		}

		return result.toString();

	}

	/**
	 * Creates a list of questions which will be asked next.
	 * 
	 * @param rtc Current RatedTestCase.
	 * 
	 * @return List<Question>
	 */
	private List<Question> getNextQuestions(RatedTestCase rtc) {

		List<Question> questions = new ArrayList<Question>();
		Collection<RatedTestCase> children = graph.getSuccessors(rtc);

		if (children != null) {

			for (RatedTestCase r : graph.getChildren(rtc)) {
				for (Finding f : r.getFindings()) {
					if (!questions.contains(f.getQuestion())) {
						questions.add(f.getQuestion());
					}
				}
			}

			return questions;
		}

		return new ArrayList<Question>();

	}

}
