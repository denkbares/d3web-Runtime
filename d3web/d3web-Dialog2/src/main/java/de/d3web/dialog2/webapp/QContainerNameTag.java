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

package de.d3web.dialog2.webapp;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentELTag;

import de.d3web.dialog2.component.html.UIQContainerName;

public class QContainerNameTag extends UIComponentELTag {

    private ValueExpression value;

    private ValueExpression qcontainerId;

    private ValueExpression styleClass;

    @Override
    public String getComponentType() {
	return UIQContainerName.COMPONENT_TYPE;
    }

    @Override
    public String getRendererType() {
	return null;
    }

    @Override
    public void release() {
	super.release();
	value = null;
	qcontainerId = null;
	styleClass = null;
    }

    @Override
    public void setProperties(UIComponent component) {
	super.setProperties(component);
	component.setValueExpression("value", value);
	component.setValueExpression("qcontainerId", qcontainerId);
	component.setValueExpression("styleClass", styleClass);
    }

    public void setQcontainerId(ValueExpression qcontainerId) {
	this.qcontainerId = qcontainerId;
    }

    public void setStyleClass(ValueExpression styleClass) {
	this.styleClass = styleClass;
    }

    public void setValue(ValueExpression value) {
	this.value = value;
    }
}
