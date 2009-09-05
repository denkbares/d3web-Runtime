package de.d3web.dialog2.component.html;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

public class ComponentUtils {

    public static String getStringValue(UIComponent component, String value,
	    String name) {
	if (value != null) {
	    return value;
	}
	ValueExpression ve = component.getValueExpression(name);
	if (ve != null) {
	    String val = (String) ve.getValue(FacesContext.getCurrentInstance()
		    .getELContext());
	    if (val != null) {
		return val;
	    }
	}
	return null;
    }

    private ComponentUtils() {
    }
}
