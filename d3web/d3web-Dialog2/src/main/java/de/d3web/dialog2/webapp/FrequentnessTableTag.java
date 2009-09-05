package de.d3web.dialog2.webapp;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentELTag;

import de.d3web.dialog2.component.html.UIFrequentnessTable;

public class FrequentnessTableTag extends UIComponentELTag {

    public ValueExpression value;

    @Override
    public String getComponentType() {
	return UIFrequentnessTable.COMPONENT_TYPE;
    }

    @Override
    public String getRendererType() {
	return UIFrequentnessTable.COMPONENT_TYPE;
    }

    @Override
    public void release() {
	super.release();
    }

    @Override
    public void setProperties(UIComponent component) {
	super.setProperties(component);
	component.setValueExpression("value", value);
    }

    public void setValue(ValueExpression value) {
	this.value = value;
    }

}
