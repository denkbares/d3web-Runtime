package de.d3web.dialog2.render;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.apache.log4j.Logger;

import de.d3web.dialog2.component.html.UIFrequentnessTable;
import de.d3web.dialog2.frequentness.DataGroup;
import de.d3web.dialog2.frequentness.DataWithFrequentness;
import de.d3web.dialog2.util.DialogUtils;

public class FrequentnessTableRenderer extends Renderer {

    public static Logger logger = Logger
	    .getLogger(FrequentnessTableRenderer.class);

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
	    throws IOException {
	Object value = ((UIFrequentnessTable) component).getValue();
	List<DataGroup> datagroups = (List<DataGroup>) value;
	if (datagroups.size() > 0) {
	    renderDataTable(context.getResponseWriter(), component, datagroups);
	}

    }

    private void renderDataTable(ResponseWriter writer, UIComponent component,
	    List<DataGroup> datagroups) throws IOException {
	DialogRenderUtils.renderTableWithClass(writer, component, "panelBox",
		2, 0);
	writer.startElement("tr", component);
	writer.startElement("th", component);
	writer.writeText(DialogUtils.getMessageFor("freq.firstcol"), "value");
	writer.endElement("th");
	writer.startElement("th", component);
	writer.writeText(DialogUtils.getMessageFor("freq.secondcol"), "value");
	writer.endElement("th");
	writer.startElement("th", component);
	writer.writeText(DialogUtils.getMessageFor("freq.thirdcol"), "value");
	writer.endElement("th");
	writer.endElement("tr");
	for (DataGroup group : datagroups) {
	    writer.startElement("tr", component);
	    writer.startElement("td", component);
	    writer.writeAttribute("colspan", "3", "colspan");
	    writer.writeAttribute("class", "subHL", "class");
	    writer.writeText(group.getText(), "value");
	    writer.endElement("td");
	    writer.endElement("tr");
	    for (DataWithFrequentness freqData : group
		    .getDataWithFrequentnessList()) {
		writer.startElement("tr", component);
		writer.startElement("td", component);
		writer.writeText(freqData.getText(), "value");
		writer.endElement("td");
		writer.startElement("td", component);
		writer.writeText(freqData.getAbsoluteFrequency(), "value");
		writer.endElement("td");
		writer.startElement("td", component);
		writer.writeAttribute("class", "rel", "class");
		double relFreq = freqData.getRelativeFrequency() * 100;
		DecimalFormat fmt = new DecimalFormat();
		fmt.setMinimumFractionDigits(2);
		fmt.setMaximumFractionDigits(2);
		writer.writeText("" + fmt.format(relFreq) + " %", "value");
		writer.endElement("td");
		writer.endElement("tr");
	    }
	}
	writer.endElement("table");
    }
}
