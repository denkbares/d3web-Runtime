package de.d3web.dialog2.webapp;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentELTag;

import de.d3web.dialog2.component.html.UIMMInfoPage;

public class MMInfoPageTag extends UIComponentELTag {

    private ValueExpression value;

    private ValueExpression diag;

    @Override
    public String getComponentType() {
	return UIMMInfoPage.COMPONENT_TYPE;
    }

    @Override
    public String getRendererType() {
	return UIMMInfoPage.COMPONENT_TYPE;
    }

    @Override
    public void release() {
	super.release();
	value = null;
	diag = null;
    }

    public void setDiag(ValueExpression diag) {
	this.diag = diag;
    }

    @Override
    public void setProperties(UIComponent component) {
	super.setProperties(component);
	component.setValueExpression("value", value);
	component.setValueExpression("diag", diag);
    }

    public void setValue(ValueExpression newVal) {
	value = newVal;
    }
}
