package de.d3web.dialog2.webapp;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentELTag;

import de.d3web.dialog2.component.html.UIQuestionPage;

public class QuestionPageTag extends UIComponentELTag {

    private ValueExpression minanswrap = null;

    @Override
    public String getComponentType() {
	return UIQuestionPage.COMPONENT_TYPE;
    }

    @Override
    public String getRendererType() {
	return UIQuestionPage.COMPONENT_TYPE;
    }

    @Override
    public void release() {
	super.release();
	minanswrap = null;
    }

    public void setMinanswrap(ValueExpression minanswrap) {
	this.minanswrap = minanswrap;
    }

    @Override
    public void setProperties(UIComponent component) {
	super.setProperties(component);
	component.setValueExpression("minanswrap", minanswrap);
    }
}
