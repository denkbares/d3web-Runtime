package de.d3web.dialog2.webapp;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentELTag;

import de.d3web.dialog2.component.html.UIAnswerFrequentnessBox;

public class AnswerFrequentnessBoxTag extends UIComponentELTag {

    @Override
    public String getComponentType() {
	return UIAnswerFrequentnessBox.COMPONENT_TYPE;
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
