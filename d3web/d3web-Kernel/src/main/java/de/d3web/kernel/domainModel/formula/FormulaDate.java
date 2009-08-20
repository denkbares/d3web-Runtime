/*
 * Created on 11.11.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.d3web.kernel.domainModel.formula;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.answers.AnswerDate;

/**
 * Primitive formula date element in date format
 * Creation date: (14.08.2000 15:49:14)
 * @author Norman Brümmer
 */
public class FormulaDate extends FormulaDatePrimitive {

	/** 
	 * Creates a new FormulaNubmer with 0 as value.
	 */
	public FormulaDate() {
		this(new Date());
	}	
	
	/**
	 * 	Creates a new FormulaNumber object
	 * 	@param value value of this FormulaElement
	 */
	public FormulaDate(Date value) {
		super(value);
	}

	/**
	 * Creation date: (14.08.2000 15:51:57)
	 * @return Double-value of this FormulaElement
	 */
	public Date eval(XPSCase theCase) {
		return (Date) getValue();
	}

	/**
	 *	@return XML representation of this FormulaNumber-object
	 **/
	public java.lang.String getXMLString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<FormulaDatePrimitive type='FormulaDate'>\n");
		sb.append("<Value>" + AnswerDate.format.format(getValue())	 + "</Value>\n");
		sb.append("</FormulaDatePrimitive>\n");
		return sb.toString();
	}

	/**
	 *	@return String representation of this FormulaNumber-object
	 **/
	public String toString() {
		if (getValue() == null)
			return "null";
		else
			return "<"+AnswerDate.format.format(getValue())+">";
	}

	/**
	 * @see FormulaElement
	 */
	public Collection getTerminalObjects() {
		return new LinkedList();
	}

}
