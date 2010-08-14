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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.dialog2.WebDialog;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.indication.inference.PSMethodUserSelected;

public class CorrectionPageRenderer extends Renderer {

	@Override
	public void decode(FacesContext context, UIComponent component) {
		Map<String, String[]> requestMap = context.getExternalContext()
				.getRequestParameterValuesMap();

		String[] selectedDiagsHeur = requestMap.get("userseldiagsheuristic");
		String[] selectedDiagsOther = requestMap.get("userseldiagsother");

		List<String> userSelDiagIDs = new ArrayList<String>();
		if (selectedDiagsHeur != null && selectedDiagsHeur.length > 0) {
			for (String id : selectedDiagsHeur) {
				userSelDiagIDs.add(id);
			}
		}
		if (selectedDiagsOther != null && selectedDiagsOther.length > 0) {
			for (String id : selectedDiagsOther) {
				userSelDiagIDs.add(id);
			}
		}
		DialogUtils.getSaveCaseBean().setUserSelectedDiags(userSelDiagIDs);
		// also save userselected diags in case...
		WebDialog dia = DialogUtils.getDialog();

		Session session = dia.getSession();
		List<Solution> allDiags = session.getKnowledgeBase().getSolutions();
		for (Solution diag : allDiags) {
			if (diagIsUserSelected(dia, userSelDiagIDs, diag)) {
				// set as user selected
				session.getBlackboard().addValueFact(
						new DefaultFact(diag, new Rating(
								Rating.State.ESTABLISHED), this,
								PSMethodUserSelected.getInstance()));
			}
			else {
				// delete user selected diagnosis
				session.getBlackboard().removeValueFact(diag, this);
			}
		}
	}

	private boolean diagIsUserSelected(WebDialog dia,
			List<String> userSelDiagIDs, Solution diag) {
		for (String userSelDiagID : userSelDiagIDs) {
			if (userSelDiagID.equals(diag.getId())) {
				// diag is user selected..
				return true;
			}
		}
		return false;
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component)
			throws IOException {
		ResponseWriter writer = context.getResponseWriter();

		Session session = DialogUtils.getDialog().getSession();

		DialogRenderUtils.renderTableWithClass(writer, component, "panelBox",
				2, 0);
		writer.startElement("tr", component);
		writer.startElement("th", component);
		writer.writeAttribute("colspan", "3", "colspan");

		writer.startElement("h2", component);
		writer.writeAttribute("class", "panelBoxCenteredHeadline", "class");
		writer.writeText(DialogUtils.getMessageFor("correctcase.title"),
				"value");
		writer.endElement("h2");

		// get established and suggested diagnoses
		List<Solution> diagListEstablished = session.getBlackboard().getSolutions(State.ESTABLISHED);
		List<Solution> diagListSuggested = session.getBlackboard().getSolutions(State.SUGGESTED);
		// filter duplicate diagoses (some are userselected established and
		// heuristic suggested)
		List<Solution> diagListSuggestedFiltered = new ArrayList<Solution>();
		for (Solution d : diagListSuggested) {
			String diagID = d.getId();
			boolean alreadyAdded = false;
			for (Solution dFiltered : diagListEstablished) {
				if (dFiltered.getId().equals(diagID)) {
					// diag is already added -> we dont have to add it again
					alreadyAdded = true;
					break;
				}
			}
			if (!alreadyAdded) {
				diagListSuggestedFiltered.add(d);
			}
		}

		if (diagListEstablished.isEmpty()
				&& diagListSuggestedFiltered.isEmpty()) {
			// no diags found...
			writer.endElement("th");
			writer.endElement("tr");
			writer.startElement("tr", component);
			writer.startElement("td", component);
			writer.writeAttribute("colspan", "3", "colspan");
			writer.writeText(DialogUtils.getMessageFor("correctcase.nodiags"),
					"value");
			writer.endElement("td");
			writer.endElement("tr");
		}
		else {
			writer.startElement("div", component);
			writer.writeAttribute("class", "smaller", "class");
			writer.writeText(DialogUtils.getMessageFor("correctcase.advice"),
					"value");
			writer.endElement("div");

			writer.endElement("th");
			writer.endElement("tr");

			renderCorrectionTableSubHeadline(writer, component);

			if (!diagListEstablished.isEmpty()) {
				DialogRenderUtils.sortDiagnosisList(diagListEstablished,
						session);
				renderDiags(writer, component, diagListEstablished, true, true);
			}
			if (!diagListSuggestedFiltered.isEmpty()) {
				DialogRenderUtils.sortDiagnosisList(diagListSuggestedFiltered,
						session);
				renderDiags(writer, component, diagListSuggestedFiltered, true,
						false);
			}

			renderMarkAllLink(writer, component, true);
		}
		writer.endElement("table");

		List<Solution> remainingDiags = getRemainingDiags(session,
				diagListEstablished, diagListSuggestedFiltered);

		if (!remainingDiags.isEmpty()) {

			DialogRenderUtils.renderTableWithClass(writer, component,
					"panelBox", 2, 0);
			writer.startElement("tr", component);
			writer.startElement("th", component);
			writer.writeAttribute("colspan", "3", "colspan");

			writer.startElement("h2", component);
			writer.writeAttribute("class", "panelBoxCenteredHeadline", "class");
			writer.writeText(DialogUtils
					.getMessageFor("correctcase.title_other"), "value");
			writer.endElement("h2");

			writer.startElement("div", component);
			writer.writeAttribute("class", "smaller", "class");
			writer.writeText(DialogUtils
					.getMessageFor("correctcase.advice_other"), "value");
			writer.endElement("div");

			writer.endElement("th");
			writer.endElement("tr");

			renderCorrectionTableSubHeadline(writer, component);

			renderDiags(writer, component, remainingDiags, false, false);

			renderMarkAllLink(writer, component, false);

			writer.endElement("table");
		}
	}

	private List<Solution> getRemainingDiags(Session session,
			List<Solution> diagListEstablished,
			List<Solution> diagListSuggested) {
		List<Solution> diagList = session.getKnowledgeBase().getSolutions();
		Solution root = session.getKnowledgeBase().getRootSolution();
		List<Solution> retList = new ArrayList<Solution>();
		for (int i = 0; i < diagList.size(); i++) {
			Solution actual = diagList.get(i);
			if (!actual.equals(root)
					&& !(diagListEstablished.contains(actual) || diagListSuggested
							.contains(actual))) {
				retList.add(actual);
			}
		}
		return retList;
	}

	private void renderCorrectionTableSubHeadline(ResponseWriter writer,
			UIComponent component) throws IOException {
		writer.startElement("tr", component);
		writer.startElement("td", component);
		renderTableHeadline(writer, component, DialogUtils
				.getMessageFor("correctcase.name"));
		writer.endElement("td");
		writer.startElement("td", component);
		writer.writeAttribute("class", "centered", "class");
		renderTableHeadline(writer, component, DialogUtils
				.getMessageFor("correctcase.info"));
		writer.endElement("td");
		writer.startElement("td", component);
		writer.writeAttribute("class", "centered", "class");
		renderTableHeadline(writer, component, DialogUtils
				.getMessageFor("correctcase.choice"));
		writer.endElement("td");
		writer.endElement("tr");

	}

	private void renderDiags(ResponseWriter writer, UIComponent component,
			List<Solution> diagList, boolean heuristic, boolean checked)
			throws IOException {
		for (Iterator<Solution> iter = diagList.iterator(); iter.hasNext();) {
			Solution diag = iter.next();
			writer.startElement("tr", component);

			writer.startElement("td", component);
			writer.writeText(diag.getName(), "value");
			writer.endElement("td");

			writer.startElement("td", component);
			writer.writeAttribute("class", "centered", "class");
			DialogRenderUtils.renderMMInfoPopupLink(writer, component, diag,
					false, null);
			writer.endElement("td");

			writer.startElement("td", component);
			writer.writeAttribute("class", "centered", "class");

			writer.startElement("input", component);
			writer.writeAttribute("type", "checkbox", "type");
			if (heuristic) {
				writer.writeAttribute("name", "userseldiagsheuristic", "name");
			}
			else {
				writer.writeAttribute("name", "userseldiagsother", "name");
			}

			writer.writeAttribute("id", "corr_" + diag.getId(), "id");
			writer.writeAttribute("value", diag.getId(), "value");
			if (checked) {
				writer.writeAttribute("checked", "checked", "checked");
			}
			writer.endElement("input");
			writer.endElement("td");
			writer.endElement("tr");
		}
	}

	private void renderMarkAllLink(ResponseWriter writer,
			UIComponent component, boolean heuristic) throws IOException {

		writer.startElement("tr", component);
		writer.startElement("th", component);
		writer.writeAttribute("colspan", "3", "colspan");
		writer.writeAttribute("align", "right", "align");

		writer.startElement("a", component);
		writer.writeAttribute("href", "#", "href");
		writer.writeAttribute("title", DialogUtils
				.getMessageFor("correctcase.markall"), "title");
		if (heuristic) {
			writer.writeAttribute("id", "markallheur", "id");
			writer
					.writeAttribute(
							"onclick",
							"toggleUserSelectedDiags('userseldiagsheuristic'); return false;",
							"onclick");
		}
		else {
			writer.writeAttribute("id", "markallother", "id");
			writer
					.writeAttribute(
							"onclick",
							"toggleUserSelectedDiags('userseldiagsother'); return false;",
							"onclick");
		}

		writer.writeText(DialogUtils.getMessageFor("correctcase.markall"),
				"value");

		writer.endElement("a");
		writer.endElement("th");
		writer.endElement("tr");
	}

	private void renderTableHeadline(ResponseWriter writer,
			UIComponent component, String text) throws IOException {
		writer.startElement("span", component);
		writer.writeAttribute("style", "font-weight: bold;", "style");
		writer.writeText(text, "value");
		writer.endElement("span");
	}
}
