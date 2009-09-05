package de.d3web.dialog2.component.html;

import java.io.IOException;

import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import de.d3web.dialog2.render.DialogRenderUtils;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;

public class UIDiagWithInfo extends HtmlOutputText {

    public static final String COMPONENT_TYPE = "de.d3web.dialog2.DiagWithInfo";

    private static final String DEFAULT_RENDERER_TYPE = null;

    public UIDiagWithInfo() {
	setRendererType(DEFAULT_RENDERER_TYPE);
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
	ResponseWriter writer = FacesContext.getCurrentInstance()
		.getResponseWriter();
	XPSCase theCase = DialogUtils.getDialog().getTheCase();

	Diagnosis diag = theCase.getKnowledgeBase().searchDiagnosis(
		(String) getValue());

	DialogRenderUtils.renderDiagnosesLink(writer, this, diag, theCase,
		getStyleClass(), null, false);
	writer.write(" ");
	DialogRenderUtils
		.renderMMInfoPopupLink(writer, this, diag, false, null);
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
	Object values[] = (Object[]) state;
	super.restoreState(context, values[0]);
    }

    @Override
    public Object saveState(FacesContext context) {
	Object values[] = new Object[1];
	values[0] = super.saveState(context);
	return ((values));
    }

}