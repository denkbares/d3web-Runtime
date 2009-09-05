package de.d3web.dialog2.webapp;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentELTag;

import de.d3web.dialog2.component.html.UISCMPage;

public class SCMPageTag extends UIComponentELTag {

    private ValueExpression diag;

    @Override
    public String getComponentType() {
	return UISCMPage.COMPONENT_TYPE;
    }

    @Override
    public String getRendererType() {
	return UISCMPage.COMPONENT_TYPE;
    }

    @Override
    public void release() {
	super.release();
	diag = null;
    }

    public void setDiag(ValueExpression diag) {
	this.diag = diag;
    }

    @Override
    public void setProperties(UIComponent component) {
	super.setProperties(component);
	component.setValueExpression("diag", diag);
    }
}
