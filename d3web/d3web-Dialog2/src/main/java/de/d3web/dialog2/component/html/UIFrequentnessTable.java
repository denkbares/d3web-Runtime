package de.d3web.dialog2.component.html;

import javax.el.ValueExpression;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

public class UIFrequentnessTable extends UIInput {

    public static final String COMPONENT_TYPE = "de.d3web.dialog2.FrequentnessTable";

    private static final String DEFAULT_RENDERER_TYPE = "de.d3web.dialog2.FrequentnessTable";

    private Object _value;

    public UIFrequentnessTable() {
	setRendererType(DEFAULT_RENDERER_TYPE);
    }

    @Override
    public Object getValue() {
	if (_value != null) {
	    return _value;
	}
	ValueExpression expression = getValueExpression("value");
	if (expression != null) {
	    return expression.getValue(getFacesContext().getELContext());
	}
	return null;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
	Object values[] = (Object[]) state;
	super.restoreState(context, values[0]);
    }

    @Override
    public Object saveState(FacesContext context) {
	Object values[] = new Object[1];
	values[0] = super.saveState(context);
	return ((values));
    }

    @Override
    public void setValue(Object value) {
	this._value = value;
    }

}
