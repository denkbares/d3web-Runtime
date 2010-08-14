/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

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
