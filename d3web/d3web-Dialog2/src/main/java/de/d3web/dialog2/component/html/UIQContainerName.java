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