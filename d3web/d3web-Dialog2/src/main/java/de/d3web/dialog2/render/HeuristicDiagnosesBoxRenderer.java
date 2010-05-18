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

package de.d3web.dialog2.render;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.Session;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.scoring.HeuristicRating;

public class HeuristicDiagnosesBoxRenderer extends Renderer {

	private static void renderDiagnoses(ResponseWriter writer, UIComponent component,
			List<Solution> diagList, Session theCase, String headline, boolean showScore) throws IOException {
		writer.startElement("tr", component);
		writer.startElement("th", component);
		writer.writeAttribute("colspan", "2", "colspan");
		writer.writeAttribute("style", "font-weight: normal;", "style");
		writer.writeText(headline, "value");
		writer.endElement("th");
		writer.endElement("tr");

		for (Iterator<Solution> iter = diagList.iterator(); iter.hasNext();) {
			Solution diag = iter.next();
			Rating state = theCase.getBlackboard().getState(diag);
			Integer score = 0;
			if (state instanceof HeuristicRating) {
				HeuristicRating hr = (HeuristicRating) state;
				score = new Double(hr.getScore()).intValue();
			}
			if (score != 0) {
				writer.startElement("tr", component);
				writer.startElement("td", component);

				DialogRenderUtils.renderDiagnosesLink(writer, component, diag, theCase,
						"underline", score
						.toString(), showScore);
				writer.endElement("td");

				DialogRenderUtils.renderMMInfoPopupLink(writer, component, diag, true,
						null);
				writer.endElement("tr");

				Object diagExplanationObj = diag.getProperties().getProperty(
						Property.EXPLANATION);
				if (diagExplanationObj != null && diagExplanationObj instanceof String) {
					String diagExplanation = (String) diagExplanationObj;

					writer.startElement("tr", component);
					writer.startElement("td", component);

					writer.write(diagExplanation);
					writer.endElement("td");
					writer.startElement("td", component);
					writer.endElement("td");
					writer.endElement("tr");
				}
			}
		}
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

		Session theCase = DialogUtils.getDialog().getSession();

		if (heuristicDiagnosesAvailable(theCase)) {
			DialogRenderUtils.renderTableWithClass(writer, component, "panelBox");
			writer.writeAttribute("id", component.getClientId(context), "id");
			writer.startElement("tr", component);
			writer.startElement("th", component);
			writer.writeText(DialogUtils.getMessageFor("solution.heuristicdiagnoses"),
					"value");
			writer.endElement("th");
			writer.endElement("tr");

			// render ESTABLISHED...
			List<Solution> diagListEstablished = theCase.getBlackboard().getSolutions(
					Rating.State.ESTABLISHED);
			if (!diagListEstablished.isEmpty()
					&& DialogUtils.getDialogSettings().isShowHeuristicEstablishedDiagnoses()) {
				DialogRenderUtils.sortDiagnosisList(diagListEstablished, theCase);
				renderDiagnoses(writer, component, diagListEstablished, theCase,
						DialogUtils
						.getMessageFor("solution.established"), false);
			}

			// render SUGGESTED...
			List<Solution> diagListSuggested = theCase.getBlackboard().getSolutions(State.SUGGESTED);
			if (!diagListSuggested.isEmpty()
					&& DialogUtils.getDialogSettings().isShowHeuristicSuggestedDiagnoses()) {
				DialogRenderUtils.sortDiagnosisList(diagListSuggested, theCase);
				renderDiagnoses(writer, component, diagListSuggested, theCase,
						DialogUtils
						.getMessageFor("solution.suggested"), false);
			}

			// render EXCLUDED...
			List<Solution> diagListExcluded = theCase.getBlackboard().getSolutions(State.EXCLUDED);
			if (!diagListExcluded.isEmpty()
					&& DialogUtils.getDialogSettings().isShowHeuristicExcludedDiagnoses()) {
				DialogRenderUtils.sortDiagnosisList(diagListExcluded, theCase);
				renderDiagnoses(writer, component, diagListExcluded, theCase, DialogUtils
						.getMessageFor("solution.excluded"), false);
			}
			writer.endElement("table");
		}
	}

	private boolean heuristicDiagnosesAvailable(Session theCase) {
		List<Solution> established = theCase.getBlackboard().getSolutions(State.ESTABLISHED);
		List<Solution> suggested = theCase.getBlackboard().getSolutions(State.SUGGESTED);
		List<Solution> excluded = theCase.getBlackboard().getSolutions(State.EXCLUDED);
		if ((established.size() != 0 && DialogUtils.getDialogSettings().isShowHeuristicEstablishedDiagnoses())
				|| (suggested.size() != 0 && DialogUtils.getDialogSettings()
						.isShowHeuristicSuggestedDiagnoses())
				|| (excluded.size() != 0 && DialogUtils.getDialogSettings()
						.isShowHeuristicExcludedDiagnoses())) {
			return true;
		}
		return false;
	}
}
