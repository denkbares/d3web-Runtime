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

import javax.el.ValueExpression;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

public class UIFrequentnessTable extends UIInput {

	public static final String COMPONENT_TYPE = "de.d3web.dialog2.FrequentnessTable";

	private static final String DEFAULT_RENDERER_TYPE = "de.d3web.dialog2.FrequentnessTable";

	private Object _value;

	public UIFrequentnessTable() {
		setRendererType(DEFAULT_RENDERER_TYPE);
	}

	@Override
	public Object getValue() {
		if (_value != null) {
			return _value;
		}
		ValueExpression expression = getValueExpression("value");
		if (expression != null) {
			return expression.getValue(getFacesContext().getELContext());
		}
		return null;
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

	@Override
	public void setValue(Object value) {
		this._value = value;
	}

}
