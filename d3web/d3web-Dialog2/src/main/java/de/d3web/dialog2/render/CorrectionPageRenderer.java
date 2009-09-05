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

import de.d3web.dialog2.WebDialog;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.dynamicObjects.CaseDiagnosis;
import de.d3web.kernel.psMethods.userSelected.PSMethodUserSelected;

public class CorrectionPageRenderer extends Renderer {

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

	List<Diagnosis> allDiags = dia.getTheCase().getDiagnoses();
	for (Diagnosis diag : allDiags) {
	    if (diagIsUserSelected(dia, userSelDiagIDs, diag)) {
		// set as user selected
		diag.setValue(dia.getTheCase(),
			new Object[] { DiagnosisState.ESTABLISHED },
			PSMethodUserSelected.class);
	    } else {
		// delete user selected diagnosis
		((CaseDiagnosis) dia.getTheCase().getCaseObject(diag))
			.setValue(null, PSMethodUserSelected.class);
	    }
	}
    }

    private boolean diagIsUserSelected(WebDialog dia,
	    List<String> userSelDiagIDs, Diagnosis diag) {
	for (String userSelDiagID : userSelDiagIDs) {
	    if (userSelDiagID.equals(diag.getId())) {
		// diag is user selected..
		return true;
	    }
	}
	return false;
    }

    public void encodeEnd(FacesContext context, UIComponent component)
	    throws IOException {
	ResponseWriter writer = context.getResponseWriter();

	XPSCase theCase = DialogUtils.getDialog().getTheCase();

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
	List<Diagnosis> diagListEstablished = theCase
		.getDiagnoses(DiagnosisState.ESTABLISHED);
	List<Diagnosis> diagListSuggested = theCase
		.getDiagnoses(DiagnosisState.SUGGESTED);
	// filter duplicate diagoses (some are userselected established and
	// heuristic suggested)
	List<Diagnosis> diagListSuggestedFiltered = new ArrayList<Diagnosis>();
	for (Diagnosis d : diagListSuggested) {
	    String diagID = d.getId();
	    boolean alreadyAdded = false;
	    for (Diagnosis dFiltered : diagListEstablished) {
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
	} else {
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
			theCase);
		renderDiags(writer, component, diagListEstablished, true, true);
	    }
	    if (!diagListSuggestedFiltered.isEmpty()) {
		DialogRenderUtils.sortDiagnosisList(diagListSuggestedFiltered,
			theCase);
		renderDiags(writer, component, diagListSuggestedFiltered, true,
			false);
	    }

	    renderMarkAllLink(writer, component, true);
	}
	writer.endElement("table");

	List<Diagnosis> remainingDiags = getRemainingDiags(theCase,
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

    private List<Diagnosis> getRemainingDiags(XPSCase theCase,
	    List<Diagnosis> diagListEstablished,
	    List<Diagnosis> diagListSuggested) {
	List<Diagnosis> diagList = theCase.getDiagnoses();
	Diagnosis root = theCase.getKnowledgeBase().getRootDiagnosis();
	List<Diagnosis> retList = new ArrayList<Diagnosis>();
	for (int i = 0; i < diagList.size(); i++) {
	    Diagnosis actual = diagList.get(i);
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
	    List<Diagnosis> diagList, boolean heuristic, boolean checked)
	    throws IOException {
	for (Iterator<Diagnosis> iter = diagList.iterator(); iter.hasNext();) {
	    Diagnosis diag = iter.next();
	    writer.startElement("tr", component);

	    writer.startElement("td", component);
	    writer.writeText(diag.getText(), "value");
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
	    } else {
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
	} else {
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
