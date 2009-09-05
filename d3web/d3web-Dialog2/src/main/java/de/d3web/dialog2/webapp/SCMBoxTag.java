package de.d3web.dialog2.webapp;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentELTag;

import de.d3web.dialog2.component.html.UISCMBox;

public class SCMBoxTag extends UIComponentELTag {

    @Override
    public String getComponentType() {
	return UISCMBox.COMPONENT_TYPE;
    }

    @Override
    public String getRendererType() {
	return UISCMBox.COMPONENT_TYPE;
    }

    @Override
    public void release() {
	super.release();
    }

    @Override
    public void setProperties(UIComponent component) {
	super.setProperties(component);
    }
}
