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

package de.d3web.dialog2.render;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.dialog2.component.html.UIExplanation;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.HeuristicRating;
import de.d3web.scoring.inference.PSMethodHeuristic;

public class ExplanationRenderer extends Renderer {

	public static void renderDiagStatusAndScore(FacesContext context,
			Session theCase, Solution diag) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String score = "";
		Rating state = theCase.getBlackboard().getRating(diag);
		if (state instanceof HeuristicRating) {
			HeuristicRating hr = (HeuristicRating) state;
			score = "" + hr.getScore();
		}
		writer.writeText(
				" (= "
						+ ExplanationRendererUtils.getStateTranslation(theCase.getBlackboard().getRating(
								diag)) + "; "
						+ score + " "
						+ DialogUtils.getMessageFor("explain.diag_scoreunit") + "):",
				"value");
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component)
			throws IOException {
		ResponseWriter writer = context.getResponseWriter();

		String expl = (String) ((UIOutput) component).getValue();
		String toExplain = ((UIExplanation) component).getDiag();

		Session theCase = DialogUtils.getDialog().getSession();

		boolean explainReason = false;
		boolean explainDerivation = false;
		boolean explainConcreteDerivation = false;

		if (expl.equals("explainReason")) {
			explainReason = true;
		}
		else if (expl.equals("explainDerivation")) {
			explainDerivation = true;
		}
		else if (expl.equals("explainConcreteDerivation")) {
			explainConcreteDerivation = true;
		}

		// diagnosis ...
		if (explainReason || explainDerivation || explainConcreteDerivation) {

			Solution diag = theCase.getKnowledgeBase().searchSolution(
					toExplain);

			writer.startElement("h3", component);
			writer.writeAttribute("id", expl + "_" + toExplain + "_headline",
					"id");

			// headline
			if (explainReason) {
				writer.writeText(DialogUtils
						.getMessageFor("explain.reason_beginning")
						+ " ", "value");
			}
			else if (explainDerivation) {
				writer.writeText(DialogUtils
						.getMessageFor("explain.derivation_beginning")
						+ " ", "value");
			}
			else {
				writer.writeText(DialogUtils
						.getMessageFor("explain.concrete_derivation_beginning")
						+ " ", "value");
			}
			ExplanationRendererUtils.renderDiagnosisObject(writer, diag);

			// if explainReason or explainConcreteDerivation -> render status
			// and score
			if (explainReason || explainConcreteDerivation) {
				renderDiagStatusAndScore(context, theCase, diag);
			}

			writer.endElement("h3");

			// if ConcreteDerivation
			if (explainConcreteDerivation) {
				ExplanationRendererUtils.explainConcreteDerivation(writer,
						component, diag, theCase);
			}
			// if explainReason or explainDerivation ...
			else {
				KnowledgeSlice ks = diag
						.getKnowledge(PSMethodHeuristic.class,
						MethodKind.BACKWARD);

				if (ks == null) {
					writer.startElement("p", component);
					writer.writeText(DialogUtils
							.getMessageFor("explain.no_knowledge_available"),
							"value");
					writer.endElement("p");
					return;
				}
				RuleSet rs = (RuleSet) ks;
				List<Rule> knowledgeList = rs.getRules();

				// sort by score
				Comparator<Rule> explComp = new Comparator<Rule>() {
					public int compare(Rule ra, Rule rb) {
						if (ra.getAction() instanceof ActionHeuristicPS
								&& rb.getAction() instanceof ActionHeuristicPS) {
							ActionHeuristicPS a_ac = (ActionHeuristicPS) ra
									.getAction();
							ActionHeuristicPS b_ac = (ActionHeuristicPS) rb
									.getAction();
							Double a_score = a_ac.getScore().getScore();
							Double b_score = b_ac.getScore().getScore();
							if (a_score < b_score) {
								return 1;
							}
							else if (a_score > b_score) {
								return -1;
							}
							else {
								return 0;
							}
						}
						else {
							return 0;
						}
					}
				};
				Collections.sort(knowledgeList, explComp);

				DialogRenderUtils.renderTableWithClass(writer, component,
						"explanationtable");
				for (Rule rc : knowledgeList) {
					writer.startElement("tr", component);

					if (rc.getAction() instanceof ActionHeuristicPS) {
						ActionHeuristicPS ac = (ActionHeuristicPS) rc
								.getAction();
						writer.writeAttribute("id", rc.getId() + "_"
								+ ac.getScore().getSymbol(), "id");
					}

					// if fired, then change background (only if explainReason)
					if (explainReason && rc.isUsed(theCase)) {
						writer.writeAttribute("class", "fired", "class");
					}

					// 1. column: Rule ID
					writer.startElement("td", component);
					ExplanationRendererUtils.renderRuleComplexId(writer, rc);
					writer.endElement("td");

					// 2. column: Action
					writer.startElement("td", component);
					ExplanationRendererUtils
							.renderRuleComplexAction(writer, rc);
					writer.endElement("td");

					// 3. column: Conditions
					writer.startElement("td", component);

					DialogRenderUtils.renderTable(writer, component);
					writer.startElement("tr", component);
					// Conditions ...
					// if explainReason then with status
					if (explainReason) {
						ExplanationRendererUtils.renderCondition(writer,
								component, rc.getCondition(), theCase, true,
								true, DialogUtils
								.getMessageFor("explain.if_verb"),
								false, rc.getId());
					}
					// if explainDerivation then without status
					else {
						ExplanationRendererUtils.renderCondition(writer,
								component, rc.getCondition(), theCase, false,
								true, DialogUtils
								.getMessageFor("explain.if_verb"),
								false, rc.getId());
					}
					writer.endElement("tr");
					writer.endElement("table");

					writer.endElement("td");
					writer.endElement("tr");
				}
				writer.endElement("table");
			}
		}
		else {
			// QASet ...
		}
	}

}
