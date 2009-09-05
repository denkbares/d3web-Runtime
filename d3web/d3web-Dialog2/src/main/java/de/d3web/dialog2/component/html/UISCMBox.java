package de.d3web.dialog2.component.html;

import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

public class UISCMBox extends UIOutput {

    public static final String COMPONENT_TYPE = "de.d3web.dialog2.SCMBox";

    private static final String DEFAULT_RENDERER_TYPE = "de.d3web.dialog2.SCMBox";

    public UISCMBox() {
	setRendererType(DEFAULT_RENDERER_TYPE);
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