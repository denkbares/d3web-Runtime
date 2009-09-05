package de.d3web.dialog2.webapp;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentELTag;

import de.d3web.dialog2.component.html.UICorrectionPage;

public class CorrectionPageTag extends UIComponentELTag {

    private ValueExpression value;

    @Override
    public String getComponentType() {
	return UICorrectionPage.COMPONENT_TYPE;
    }

    @Override
    public String getRendererType() {
	return UICorrectionPage.COMPONENT_TYPE;
    }

    @Override
    public void release() {
	super.release();
	value = null;
    }

    @Override
    public void setProperties(UIComponent component) {
	super.setProperties(component);
	component.setValueExpression("value", value);
    }

    public void setValue(ValueExpression newVal) {
	value = newVal;
    }
}
