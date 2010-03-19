/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

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