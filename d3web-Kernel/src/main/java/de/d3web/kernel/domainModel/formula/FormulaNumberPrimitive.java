package de.d3web.kernel.domainModel.formula;

/**
 * This class represents a primitive (atomar) element 
 * of a formula (e.g. number)
 *
 * Creation date: (14.08.2000 15:41:25)
 * @author Norman Brümmer
 */
public abstract class FormulaNumberPrimitive implements FormulaNumberElement {

	/**
	 * Value of the primitive FormulaElement
	 */
	protected Object value = null;

	/**
	 * creates a new FormulaPrimitive
	 */
	public FormulaNumberPrimitive() {
	}

	/**
	 * creates a new FormulaPrimitive
	 * @param value value that the primitive FormulaElement will have
	 */
	public FormulaNumberPrimitive(Object value) {
		setValue(value);
	}

	/**
	 * @return value of this primitive FormulaElement
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Creation date: (20.06.2001 15:31:53)
	 * @see FormulaElement
	 */
	public abstract String getXMLString();

	public void setValue(Object value) {
		this.value = value;
	}
}