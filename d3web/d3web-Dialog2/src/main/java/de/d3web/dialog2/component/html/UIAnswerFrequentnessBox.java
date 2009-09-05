package de.d3web.dialog2.component.html;

import java.io.IOException;

import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.log4j.Logger;

import de.d3web.dialog2.render.DialogRenderUtils;
import de.d3web.dialog2.util.DialogUtils;

public class UIAnswerFrequentnessBox extends UIOutput {

    public static final String COMPONENT_TYPE = "de.d3web.dialog2.AnswerFrequentnessBox";

    private static final String DEFAULT_RENDERER_TYPE = null;

    public static Logger logger = Logger
	    .getLogger(UIAnswerFrequentnessBox.class);

    public UIAnswerFrequentnessBox() {
	setRendererType(DEFAULT_RENDERER_TYPE);
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
	ResponseWriter writer = context.getResponseWriter();

	DialogRenderUtils.renderTableWithClass(writer, this, "panelBox");
	writer.writeAttribute("id", this.getClientId(context), "id");
	writer.startElement("tr", this);
	writer.startElement("th", this);
	writer.writeText(DialogUtils.getMessageFor("freq.title"), "value");
	writer.endElement("th");
	writer.endElement("tr");

	writer.startElement("tr", this);
	writer.startElement("td", this);

	writer.startElement("a", this);
	writer.writeAttribute("onclick", "openFreqPopup(); return false;",
		"onclick");
	writer.writeAttribute("href", "#", "href");
	writer.writeAttribute("id", "start_freq", "id");
	writer.writeAttribute("title", DialogUtils
		.getMessageFor("freq.subtitle.desc"), "title");
	writer.writeText(DialogUtils.getMessageFor("freq.subtitle"), "value");
	writer.endElement("a");

	writer.endElement("td");
	writer.endElement("tr");

	writer.endElement("table");
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