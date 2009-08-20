/*
 * Created on 13.10.2003
 * 
 * To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
package de.d3web.kernel.domainModel.formula;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.answers.AnswerDate;
import de.d3web.kernel.domainModel.answers.EvaluatableAnswerDateValue;
import de.d3web.kernel.domainModel.qasets.QuestionDate;

/**
 * Delegate-Pattern: Wraps a QuestionNum to use it as FormulaElement. Creation
 * date: (25.07.2001 15:51:18)
 * 
 * @author Christian Betz
 */
public class QDateWrapper extends FormulaDatePrimitive {

	/** 
	 * Creates a new FormulaTerm with null-arguments.
	 */
	public QDateWrapper() {
		this(null);
	}
	
	
	/**
	 * QNumWrapper constructor comment.
	 */
	public QDateWrapper(QuestionDate q) {
		super();
		setQuestion(q);
	}

	/**
	 * @param theCase
	 *            current case
	 * @return evaluated AnswerNumValue (Double) of the wrapped QuestionNum
	 */
	public Date eval(XPSCase theCase) {
		if (getQuestion().getValue(theCase) == null
			|| getQuestion().getValue(theCase).isEmpty()) {
			return null;
		}
		AnswerDate ans = (AnswerDate) (getQuestion().getValue(theCase).get(0));
		EvaluatableAnswerDateValue ret =(EvaluatableAnswerDateValue)ans.getValue(theCase); 
		return ret.eval(theCase);
			
	}

	/**
	 * Creation date: (25.07.2001 15:52:27)
	 * 
	 * @return the wrapped QuestionNum
	 */
	public QuestionDate getQuestion() {
		return (QuestionDate)value;
	}

	/**
	 * @see FormulaElement
	 */
	public java.lang.String getXMLString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<FormulaDatePrimitive type='QDateWrapper'>\n");
		sb.append("<Value>" + getQuestion().getId() + "</Value>");
		sb.append("</FormulaDatePrimitive>\n");
		return sb.toString();
	}

	/**
	 * Sets the QuestionNum that will be wrapped
	 */
	private void setQuestion(QuestionDate newQuestion) {
		value = newQuestion;
	}

	/**
	 * @see FormulaElement
	 */
	public Collection getTerminalObjects() {
		return Collections.singletonList(value);
	}

	public String toString() {
		return value == null ? "question:null" : value.toString();
	}
	
	public void setValue(Object o) {
		setQuestion((QuestionDate)o);
	}	
}
