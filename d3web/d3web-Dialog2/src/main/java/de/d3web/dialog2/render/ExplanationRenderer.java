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
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import de.d3web.dialog2.component.html.UIExplanation;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.psMethods.MethodKind;
import de.d3web.kernel.psMethods.heuristic.ActionHeuristicPS;
import de.d3web.kernel.psMethods.heuristic.PSMethodHeuristic;

public class ExplanationRenderer extends Renderer {

    public static void renderDiagStatusAndScore(FacesContext context,
	    XPSCase theCase, Diagnosis diag) throws IOException {
	ResponseWriter writer = context.getResponseWriter();
	writer.writeText(" (= "
		+ ExplanationRendererUtils.getStateTranslation(diag.getState(
			theCase, PSMethodHeuristic.class)) + "; "
		+ diag.getScore(theCase, PSMethodHeuristic.class) + " "
		+ DialogUtils.getMessageFor("explain.diag_scoreunit") + "):",
		"value");
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
	    throws IOException {
	ResponseWriter writer = context.getResponseWriter();

	String expl = (String) ((UIOutput) component).getValue();
	String toExplain = ((UIExplanation) component).getDiag();

	XPSCase theCase = DialogUtils.getDialog().getTheCase();

	boolean explainReason = false;
	boolean explainDerivation = false;
	boolean explainConcreteDerivation = false;

	if (expl.equals("explainReason")) {
	    explainReason = true;
	} else if (expl.equals("explainDerivation")) {
	    explainDerivation = true;
	} else if (expl.equals("explainConcreteDerivation")) {
	    explainConcreteDerivation = true;
	}

	// diagnosis ...
	if (explainReason || explainDerivation || explainConcreteDerivation) {

	    Diagnosis diag = theCase.getKnowledgeBase().searchDiagnosis(
		    toExplain);

	    writer.startElement("h3", component);
	    writer.writeAttribute("id", expl + "_" + toExplain + "_headline",
		    "id");

	    // headline
	    if (explainReason) {
		writer.writeText(DialogUtils
			.getMessageFor("explain.reason_beginning")
			+ " ", "value");
	    } else if (explainDerivation) {
		writer.writeText(DialogUtils
			.getMessageFor("explain.derivation_beginning")
			+ " ", "value");
	    } else {
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
		List<? extends KnowledgeSlice> knowledgeList = diag
			.getKnowledge(PSMethodHeuristic.class,
				MethodKind.BACKWARD);

		if (knowledgeList == null) {
		    writer.startElement("p", component);
		    writer.writeText(DialogUtils
			    .getMessageFor("explain.no_knowledge_available"),
			    "value");
		    writer.endElement("p");
		    return;
		}

		// sort by score
		Comparator<KnowledgeSlice> explComp = new Comparator<KnowledgeSlice>() {
		    public int compare(KnowledgeSlice a, KnowledgeSlice b) {
			RuleComplex ra = (RuleComplex) a;
			RuleComplex rb = (RuleComplex) b;
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
			    } else if (a_score > b_score) {
				return -1;
			    } else {
				return 0;
			    }
			} else {
			    return 0;
			}
		    }
		};
		Collections.sort(knowledgeList, explComp);

		DialogRenderUtils.renderTableWithClass(writer, component,
			"explanationtable");
		for (Iterator<? extends KnowledgeSlice> iter = knowledgeList
			.iterator(); iter.hasNext();) {
		    RuleComplex rc = (RuleComplex) iter.next();

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
	} else {
	    // QASet ...
	}
    }

}
