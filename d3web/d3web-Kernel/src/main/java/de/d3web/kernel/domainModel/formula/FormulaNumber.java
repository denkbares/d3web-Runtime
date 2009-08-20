package de.d3web.kernel.domainModel.formula;

import java.util.Collection;
import java.util.LinkedList;

import de.d3web.kernel.XPSCase;
/**
 * Primitive formula element in number format
 * Creation date: (14.08.2000 15:49:14)
 * @author Norman Brümmer
 */
public class FormulaNumber extends FormulaNumberPrimitive {

	/** 
	 * Creates a new FormulaNubmer with 0 as value.
	 */
	public FormulaNumber() {
		this(new Double(0));
	}	
	
	/**
	 * 	Creates a new FormulaNumber object
	 * 	@param value value of this FormulaElement
	 */
	public FormulaNumber(Double value) {
		super(value);
	}

	/**
	 * Creation date: (14.08.2000 15:51:57)
	 * @return Double-value of this FormulaElement
	 */
	public Double eval(XPSCase theCase) {
		return (Double) getValue();
	}

	/**
	 *	@return XML representation of this FormulaNumber-object
	 **/
	public java.lang.String getXMLString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<FormulaPrimitive type='FormulaNumber'>\n");
		sb.append("<Value>" + getValue() + "</Value>\n");
		sb.append("</FormulaPrimitive>\n");
		return sb.toString();
	}

	/**
	 *	@return String representation of this FormulaNumber-object
	 **/
	public String toString() {
		if (getValue() == null)
			return "null";
		else
			return trim(getValue());
	}

	/**
	 * Method for formatting the double-value of this FormulaElement
	 * Used by toString ()
	 * 
	 * Creation date: (15.08.2000 08:31:02)
	 * @return formatted String-representation of this FormulaElement
	 */
	private String trim(Object trimValue) {
		final int digits = 3;
		String text = trimValue.toString();
		int dot = text.indexOf("."); 
		if (dot != -1){
			text = text.substring(0, Math.min(text.length(), dot + 1 + digits));
		}
		return text;
	}

	/**
	 * @see FormulaElement
	 */
	public Collection getTerminalObjects() {
		return new LinkedList();
	}

}
