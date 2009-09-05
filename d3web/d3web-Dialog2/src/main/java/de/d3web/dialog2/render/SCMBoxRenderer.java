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

public class SCMBoxRenderer extends Renderer {

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
	    renderSCMBox(context.getResponseWriter(), component, theCase);
	}
    }

    protected boolean hasNonZeroSolution(Collection<KnowledgeSlice> solutions,
	    XPSCase theCase) {
	for (KnowledgeSlice d : solutions) {
	    if (d instanceof XCLModel) {
		XCLModel model = (XCLModel) d;
		if (model.computeXCLScore(theCase) > 0
			|| model.getState(theCase).equals(
				DiagnosisState.ESTABLISHED)) {
		    return true;
		}
	    }
	}
	return false;
    }

    private void renderSCMBox(ResponseWriter writer, UIComponent component,
	    XPSCase theCase) throws IOException {

	DialogRenderUtils.renderTableWithClass(writer, component, "panelBox");
	writer.writeAttribute("id", component.getClientId(FacesContext
		.getCurrentInstance()), "id");
	writer.startElement("tr", component);
	writer.startElement("th", component);
	writer.writeText(DialogUtils.getMessageFor("scm.title"), "value");
	writer.endElement("th");
	writer.endElement("tr");

	Collection<KnowledgeSlice> sortedDiags = theCase.getKnowledgeBase()
		.getAllKnowledgeSlicesFor(PSMethodXCL.class);
	double minValue = DialogUtils.getDialogSettings()
		.getScm_display_min_percentage();
	if (minValue == 0) {
	    // do not show 0 results
	    minValue = 0.001;
	}

	for (KnowledgeSlice d : sortedDiags) {
	    if (d instanceof XCLModel) {
		XCLModel model = (XCLModel) d;
		Diagnosis origDiag = model.getSolution();

		if (model.computeXCLScore(theCase) >= minValue) {
		    int score = (int) (Math.round(model
			    .computeXCLScore(theCase) * 100));

		    DiagnosisState state = model.getState(theCase);

		    writer.startElement("tr", component);
		    writer.startElement("td", component);

		    writer.startElement("a", component);
		    writer.writeAttribute("id", "openSCM_" + origDiag.getId(),
			    "id");
		    writer
			    .writeAttribute("onclick", "openSCM('"
				    + origDiag.getId() + "'); return false;",
				    "onclick");
		    writer.writeAttribute("href", "#", "href");
		    writer.writeAttribute("title", DialogUtils
			    .getMessageWithParamsFor("scm.tooltip",
				    new Object[] { origDiag.getText() }),
			    "title");
		    writer.writeText(origDiag.getText() + " (" + score + " % "
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
