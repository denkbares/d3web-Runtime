package de.d3web.dialog2.webapp;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentELTag;

import de.d3web.dialog2.component.html.UIProcessedQContainersBox;

public class ProcessedQContainersBoxTag extends UIComponentELTag {

    @Override
    public String getComponentType() {
	return UIProcessedQContainersBox.COMPONENT_TYPE;
    }

    @Override
    public String getRendererType() {
	return UIProcessedQContainersBox.COMPONENT_TYPE;
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
