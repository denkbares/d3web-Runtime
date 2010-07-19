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
import java.util.Collection;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.session.Session;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.xcl.XCLModel;
import de.d3web.xcl.inference.PSMethodXCL;

public class XCLBoxRenderer extends Renderer {

	public boolean checkDisplayability(Session session) {
		if (session != null) {
			Collection<KnowledgeSlice> solutions = session.getKnowledgeBase()
					.getAllKnowledgeSlicesFor(PSMethodXCL.class);

			if (solutions != null && hasNonZeroSolution(solutions, session)) {
				return !solutions.isEmpty();
			}
		}
		return false;
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component)
			throws IOException {
		Session session = DialogUtils.getDialog().getSession();
		if (checkDisplayability(session)) {
			renderXCLBox(context.getResponseWriter(), component, session);
		}
	}

	protected boolean hasNonZeroSolution(Collection<KnowledgeSlice> solutions,
			Session session) {
		for (KnowledgeSlice d : solutions) {
			if (d instanceof XCLModel) {
				XCLModel model = (XCLModel) d;
				if (model.getInferenceTrace(session).getScore() > 0
						|| model.getState(session).hasState(State.ESTABLISHED)) {
					return true;
				}
			}
		}
		return false;
	}

	private void renderXCLBox(ResponseWriter writer, UIComponent component,
			Session session) throws IOException {

		DialogRenderUtils.renderTableWithClass(writer, component, "panelBox");
		writer.writeAttribute("id", component.getClientId(FacesContext
				.getCurrentInstance()), "id");
		writer.startElement("tr", component);
		writer.startElement("th", component);
		writer.writeText(DialogUtils.getMessageFor("xcl.title"), "value");
		writer.endElement("th");
		writer.endElement("tr");

		Collection<KnowledgeSlice> sortedDiags = session.getKnowledgeBase()
				.getAllKnowledgeSlicesFor(PSMethodXCL.class);
		double minValue = DialogUtils.getDialogSettings()
				.getXCL_display_min_percentage();
		if (minValue == 0) {
			// do not show 0 results
			minValue = 0.001;
		}

		for (KnowledgeSlice d : sortedDiags) {
			if (d instanceof XCLModel) {
				XCLModel model = (XCLModel) d;
				Solution origDiag = model.getSolution();
				Rating state = model.getState(session);

				double score = model.getInferenceTrace(session).getScore();
				if (score >= minValue || state.hasState(State.ESTABLISHED)) {
					int percent = (int) (Math.round(score * 100));

					writer.startElement("tr", component);
					writer.startElement("td", component);

					writer.startElement("a", component);
					writer.writeAttribute("id", "openXCL_" + origDiag.getId(),
							"id");
					writer
							.writeAttribute("onclick", "openXCL('"
							+ origDiag.getId() + "'); return false;",
							"onclick");
					writer.writeAttribute("href", "#", "href");
					writer.writeAttribute("title", DialogUtils
							.getMessageWithParamsFor("xcl.tooltip",
							new Object[] { origDiag.getName() }),
							"title");
					writer.writeText(origDiag.getName() + " (" + percent + " % "
							+ "(" + state.getName().substring(0, 4) + ".))",
							"value");
					writer.endElement("a");

					writer.endElement("td");

					DialogRenderUtils.renderMMInfoPopupLink(writer, component,
							origDiag, true, null);

					writer.endElement("tr");
				}
			}
		}
		writer.endElement("table");
	}
}
