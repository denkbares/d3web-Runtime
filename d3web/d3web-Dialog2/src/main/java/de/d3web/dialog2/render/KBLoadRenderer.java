package de.d3web.dialog2.render;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import de.d3web.dialog2.basics.knowledge.KBDescriptorLoader;
import de.d3web.dialog2.basics.knowledge.KnowledgeBaseDescriptor;
import de.d3web.dialog2.controller.KBLoadController;
import de.d3web.dialog2.util.DialogUtils;

public class KBLoadRenderer extends Renderer {

    public static final String KB_LOAD_ID = "kb_loadid";

    @Override
    public void decode(FacesContext context, UIComponent component) {

	Map<String, String[]> requestMap = context.getExternalContext()
		.getRequestParameterValuesMap();

	if (requestMap.containsKey(KB_LOAD_ID)) {
	    String[] val = requestMap.get(KB_LOAD_ID);
	    DialogUtils.getKBLoadBean().setKbID(val[0]);
	}
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
	    throws IOException {
	ResponseWriter writer = context.getResponseWriter();

	KBLoadController kbLoadBean = DialogUtils.getKBLoadBean();

	DialogRenderUtils.renderTableWithClass(writer, component, "panelBox");

	Iterator<KnowledgeBaseDescriptor> iter = KBDescriptorLoader
		.getInstance().getKnowledgeBaseDescriptors().iterator();
	int counter = 0;
	while (iter.hasNext()) {
	    KnowledgeBaseDescriptor desc = iter.next();

	    writer.startElement("tr", component);
	    writer.startElement("td", component);
	    writer.writeAttribute("class", "radio", "class");

	    writer.startElement("input", component);
	    writer.writeAttribute("type", "radio", "type");
	    writer.writeAttribute("id", desc.getId(), "id");
	    writer.writeAttribute("value", desc.getId(), "value");
	    writer.writeAttribute("name", KB_LOAD_ID, "name");
	    if (kbLoadBean.getKbID() != null
		    && kbLoadBean.getKbID().equals(desc.getId())) {
		writer.writeAttribute("checked", "checked", "checked");
	    }
	    writer.endElement("input");

	    writer.endElement("td");
	    writer.startElement("td", component);

	    writer.startElement("label", component);
	    writer.writeAttribute("for", desc.getId(), "for");
	    writer.writeText(desc.getName(), "value");
	    writer.endElement("label");

	    writer.endElement("td");
	    writer.endElement("tr");
	    counter++;
	}
	if (counter == 0) {
	    // no KB available..
	    writer.startElement("tr", component);
	    writer.startElement("td", component);
	    writer.writeText(DialogUtils
		    .getMessageFor("kbselect.nokbavailable"), "value");
	    // render "upload kb" link
	    UIComponent facet = component.getFacet("uploadKBLink");
	    if (facet != null) {
		DialogRenderUtils.renderChild(
			FacesContext.getCurrentInstance(), facet);
	    }

	    writer.endElement("td");
	    writer.endElement("tr");
	}

	writer.endElement("table");

	// render validation errors (if available)
	Iterator<FacesMessage> it = FacesContext.getCurrentInstance()
		.getMessages(KB_LOAD_ID + "_MSG");
	while (it.hasNext()) {
	    FacesMessage msg = it.next();
	    writer.startElement("p", component);
	    writer.writeAttribute("id", "kbload_error", "id");
	    writer.writeAttribute("class", "validationerror", "class");
	    writer.writeText(msg.getSummary(), "value");
	    writer.endElement("p");
	}

	// if KBs available -> render "load"-button
	if (counter > 0) {
	    UIComponent facet = component.getFacet("loadKBbutton");
	    if (facet != null) {
		DialogRenderUtils.renderChild(
			FacesContext.getCurrentInstance(), facet);
	    }
	}
    }
}
