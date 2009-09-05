package de.d3web.dialog2.component.html;

import java.io.IOException;

import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

public class UIQContainerName extends HtmlOutputText {

    public static final String COMPONENT_TYPE = "de.d3web.dialog2.QContainerName";
    private static final String DEFAULT_RENDERER_TYPE = null;

    private String qcontainerId;

    public UIQContainerName() {
	setRendererType(DEFAULT_RENDERER_TYPE);
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
	ResponseWriter writer = context.getResponseWriter();
	writer.startElement("span", this);
	if (getStyleClass() != null) {
	    writer.writeAttribute("class", getStyleClass(), "class");
	}

	if (getQcontainerId() != null) {
	    writer.writeAttribute("id", "tree_cont_" + getQcontainerId(), "id");
	}

	if (this.getChildCount() == 0) {
	    writer.writeText(getValue(), "value");
	}
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
	ResponseWriter writer = context.getResponseWriter();
	writer.endElement("span");
    }

    public String getQcontainerId() {
	return ComponentUtils
		.getStringValue(this, qcontainerId, "qcontainerId");
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
	Object values[] = (Object[]) state;
	super.restoreState(context, values[0]);
	qcontainerId = (String) values[1];
    }

    @Override
    public Object saveState(FacesContext context) {
	Object values[] = new Object[2];
	values[0] = super.saveState(context);
	values[1] = qcontainerId;
	return ((values));
    }

    public void setQcontainerId(String qcontainerId) {
	this.qcontainerId = qcontainerId;
    }

}