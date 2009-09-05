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
