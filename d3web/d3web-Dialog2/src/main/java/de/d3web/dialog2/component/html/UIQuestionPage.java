package de.d3web.dialog2.component.html;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

public class UIQuestionPage extends UIInput {

    public static final String COMPONENT_TYPE = "de.d3web.dialog2.QuestionPage";

    private static final String DEFAULT_RENDERER_TYPE = "de.d3web.dialog2.QuestionPage";

    private String minanswrap;

    private Map<String, String> errorIDsToSubmittedValues;

    public UIQuestionPage() {
	errorIDsToSubmittedValues = new HashMap<String, String>();
	setRendererType(DEFAULT_RENDERER_TYPE);
    }

    public Map<String, String> getErrorIDsToSubmittedValues() {
	return errorIDsToSubmittedValues;
    }

    public String getMinanswrap() {
	return ComponentUtils.getStringValue(this, minanswrap, "minanswrap");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void restoreState(FacesContext context, Object state) {
	Object values[] = (Object[]) state;
	super.restoreState(context, values[0]);
	minanswrap = (String) values[1];
	errorIDsToSubmittedValues = (Map<String, String>) values[2];
    }

    @Override
    public Object saveState(FacesContext context) {
	Object values[] = new Object[3];
	values[0] = super.saveState(context);
	values[1] = minanswrap;
	values[2] = errorIDsToSubmittedValues;
	return values;
    }

    public void setMinanswrap(String minanswrap) {
	this.minanswrap = minanswrap;
    }
}
