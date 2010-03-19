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

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

public class UIQuestionPage extends UIInput {

	public static final String COMPONENT_TYPE = "de.d3web.dialog2.QuestionPage";

	private static final String DEFAULT_RENDERER_TYPE = "de.d3web.dialog2.QuestionPage";

	private String minanswrap;

	private Map<String, String> errorIDsToSubmittedValues;

	public UIQuestionPage() {
		errorIDsToSubmittedValues = new HashMap<String, String>();
		setRendererType(DEFAULT_RENDERER_TYPE);
	}

	public Map<String, String> getErrorIDsToSubmittedValues() {
		return errorIDsToSubmittedValues;
	}

	public String getMinanswrap() {
		return ComponentUtils.getStringValue(this, minanswrap, "minanswrap");
	}

	@Override
	@SuppressWarnings("unchecked")
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		minanswrap = (String) values[1];
		errorIDsToSubmittedValues = (Map<String, String>) values[2];
	}

	@Override
	public Object saveState(FacesContext context) {
		Object values[] = new Object[3];
		values[0] = super.saveState(context);
		values[1] = minanswrap;
		values[2] = errorIDsToSubmittedValues;
		return values;
	}

	public void setMinanswrap(String minanswrap) {
		this.minanswrap = minanswrap;
	}
}
