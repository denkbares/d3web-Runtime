package de.d3web.kernel.domainModel.formula;
import de.d3web.kernel.XPSCase;
/**
 * Maximum-Term.
 * Creation date: (14.08.2000 16:41:32)
 * @author Norman Brümmer
 */
public class Max extends FormulaNumberArgumentsTerm implements FormulaNumberElement{

	/** 
	 * Creates a new FormulaTerm with null-arguments.
	 */
	public Max() {
		this(null, null);
	}
	
	/**
	 *	Creates a new FormulaTerm max(arg1, arg2)
	 *	@param arg1 first argument of the term
	 *	@param arg2 second argument of the term 
	 **/
	public Max(FormulaNumberElement arg1, FormulaNumberElement arg2) {
		setArg1(arg1);
		setArg2(arg2);
		setSymbol("max");
	}

	/**
	 * @return the maximum of the evaluated arguments
	 */
	public Double eval(XPSCase theCase) {
		if (super.eval(theCase) == null)
			return null;
		else
			return new Double(
				Math.max(
					getEvaluatedArg1().doubleValue(),
					getEvaluatedArg2().doubleValue()));
	}
}