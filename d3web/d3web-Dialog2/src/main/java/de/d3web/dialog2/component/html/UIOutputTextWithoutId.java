package de.d3web.dialog2.component.html;

import java.io.IOException;

import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

public class UIOutputTextWithoutId extends HtmlOutputText {

    public static final String COMPONENT_TYPE = "de.d3web.dialog2.OutputTextWithoutId";

    private static final String DEFAULT_RENDERER_TYPE = null;

    private String value;

    public UIOutputTextWithoutId() {
	setRendererType(DEFAULT_RENDERER_TYPE);
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
	ResponseWriter writer = FacesContext.getCurrentInstance()
		.getResponseWriter();
	writer.writeText(this.getValue(), "value");
    }

    @Override
    public String getValue() {
	return ComponentUtils.getStringValue(this, value, "value");
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
	Object values[] = (Object[]) state;
	super.restoreState(context, values[0]);
	value = (String) values[1];
    }

    @Override
    public Object saveState(FacesContext context) {
	Object values[] = new Object[2];
	values[0] = super.saveState(context);
	values[1] = value;
	return ((values));
    }

    public void setValue(String value) {
	this.value = value;
    }

}