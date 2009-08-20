/*
 * Created on 13.10.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.d3web.kernel.domainModel.formula;



/**
 * This class represents a primitive (atomar) date-element 
 * of a formula
 *
 * Creation date: (14.08.2000 15:41:25)
 * @author Tobias Vogele
 */
public abstract class FormulaDatePrimitive implements FormulaDateElement {

	/**
	 * Value of the primitive FormulaElement
	 */
	protected Object value = null;

	/**
	 * creates a new FormulaPrimitive
	 */
	public FormulaDatePrimitive() {
	}

	/**
	 * creates a new FormulaDatePrimitive
	 * @param value value that the primitive FormulaDateElement will have
	 */
	public FormulaDatePrimitive(Object value) {
		setValue(value);
	}

	/**
	 * @return value of this primitive FormulaDateElement
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Creation date: (20.06.2001 15:31:53)
	 * @see FormulaDateElement
	 */
	public abstract String getXMLString();

	public void setValue(Object value) {
		this.value = value;
	}
}
