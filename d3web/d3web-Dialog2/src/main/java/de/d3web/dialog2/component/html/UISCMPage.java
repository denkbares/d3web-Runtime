package de.d3web.dialog2.component.html;

import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

public class UISCMPage extends UIOutput {

    public static final String COMPONENT_TYPE = "de.d3web.dialog2.SCMPage";

    private static final String DEFAULT_RENDERER_TYPE = "de.d3web.dialog2.SCMPage";

    private String diag;

    public UISCMPage() {
	setRendererType(DEFAULT_RENDERER_TYPE);
    }

    public String getDiag() {
	return ComponentUtils.getStringValue(this, diag, "diag");
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
	Object values[] = (Object[]) state;
	super.restoreState(context, values[0]);
	diag = (String) values[1];
    }

    @Override
    public Object saveState(FacesContext context) {
	Object values[] = new Object[2];
	values[0] = super.saveState(context);
	values[1] = diag;
	return ((values));
    }

    public void setDiag(String diag) {
	this.diag = diag;
    }
}
