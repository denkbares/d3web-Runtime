package de.d3web.dialog2.webapp;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentELTag;

import de.d3web.dialog2.component.html.UIDiagWithInfo;

public class DiagWithInfoTag extends UIComponentELTag {

    private ValueExpression value;

    private ValueExpression styleClass;

    @Override
    public String getComponentType() {
	return UIDiagWithInfo.COMPONENT_TYPE;
    }

    @Override
    public String getRendererType() {
	return null;
    }

    @Override
    public void release() {
	super.release();
	value = null;
	styleClass = null;
    }

    @Override
    public void setProperties(UIComponent component) {
	super.setProperties(component);
	component.setValueExpression("value", value);
	component.setValueExpression("styleClass", styleClass);
    }

    public void setStyleClass(ValueExpression styleClass) {
	this.styleClass = styleClass;
    }

    public void setValue(ValueExpression newVal) {
	value = newVal;
    }
}
