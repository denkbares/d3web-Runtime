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