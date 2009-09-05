package de.d3web.dialog2.webapp;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentELTag;

import de.d3web.dialog2.component.html.UICompareCaseBox;

public class CompareCaseBoxTag extends UIComponentELTag {

    @Override
    public String getComponentType() {
	return UICompareCaseBox.COMPONENT_TYPE;
    }

    @Override
    public String getRendererType() {
	return null;
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
