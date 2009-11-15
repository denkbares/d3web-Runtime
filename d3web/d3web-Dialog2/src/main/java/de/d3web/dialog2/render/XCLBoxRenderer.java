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
import java.util.Collection;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import de.d3web.dialog2.util.DialogUtils;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.psMethods.xclPattern.PSMethodXCL;
import de.d3web.kernel.psMethods.xclPattern.XCLModel;

public class XCLBoxRenderer extends Renderer {

    public boolean checkDisplayability(XPSCase theCase) {
	if (theCase != null) {
	    Collection<KnowledgeSlice> solutions = theCase.getKnowledgeBase()
		    .getAllKnowledgeSlicesFor(PSMethodXCL.class);

	    if (solutions != null && hasNonZeroSolution(solutions, theCase)) {
		return !solutions.isEmpty();
	    }
	}
	return false;
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
	    throws IOException {
	XPSCase theCase = DialogUtils.getDialog().getTheCase();
	if (checkDisplayability(theCase)) {
	    renderXCLBox(context.getResponseWriter(), component, theCase);
	}
    }

    protected boolean hasNonZeroSolution(Collection<KnowledgeSlice> solutions,
	    XPSCase theCase) {
	for (KnowledgeSlice d : solutions) {
	    if (d instanceof XCLModel) {
		XCLModel model = (XCLModel) d;
		if (model.getInferenceTrace(theCase).getScore() > 0
			|| model.getState(theCase).equals(
				DiagnosisState.ESTABLISHED)) {
		    return true;
		}
	    }
	}
	return false;
    }

    private void renderXCLBox(ResponseWriter writer, UIComponent component,
	    XPSCase theCase) throws IOException {

	DialogRenderUtils.renderTableWithClass(writer, component, "panelBox");
	writer.writeAttribute("id", component.getClientId(FacesContext
		.getCurrentInstance()), "id");
	writer.startElement("tr", component);
	writer.startElement("th", component);
	writer.writeText(DialogUtils.getMessageFor("xcl.title"), "value");
	writer.endElement("th");
	writer.endElement("tr");

	Collection<KnowledgeSlice> sortedDiags = theCase.getKnowledgeBase()
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
		Diagnosis origDiag = model.getSolution();
		DiagnosisState state = model.getState(theCase);

		double score = model.getInferenceTrace(theCase).getScore();
		if (score >= minValue || state.equals(DiagnosisState.ESTABLISHED)) {
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
				    new Object[] { origDiag.getText() }),
			    "title");
		    writer.writeText(origDiag.getText() + " (" + percent + " % "
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
