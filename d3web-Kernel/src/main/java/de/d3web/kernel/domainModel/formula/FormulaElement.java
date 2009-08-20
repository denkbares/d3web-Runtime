package de.d3web.kernel.domainModel.formula;

import java.util.Collection;
/**
 * This interface discribes a general FormulaElement	
 * Used Pattern: Composite
 * <p>
 * NOTE: All implementing classes should contain a default-constructor.
 *
 * Creation date: (14.08.2000 15:40:15)
 * @author Norman Brümmer
 */
public interface FormulaElement extends java.io.Serializable {


	/**
	 * @return a list of FormulaElements, if it is a complex type. Otherwise e.g. a Double
	 */
	public Collection getTerminalObjects();

	/**
	 * @return the String representation of this FormulaElement
	 */
	public String getXMLString();
}