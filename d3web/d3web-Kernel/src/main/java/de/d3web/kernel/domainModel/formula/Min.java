package de.d3web.kernel.domainModel.formula;

import de.d3web.kernel.XPSCase;

/**
 * Minimum-Term.
 * Creation date: (14.08.2000 16:41:32)
 * @author Norman Brümmer
 */
public class Min extends FormulaNumberArgumentsTerm implements FormulaNumberElement{


	/** 
	 * Creates a new FormulaTerm with null-arguments.
	 */
	public Min() {
		this(null, null);
	}
	
	
	/**
	 *	Creates a new FormulaTerm min(arg1, arg2)
	 *	@param arg1 first argument of the term
	 *	@param arg2 second argument of the term 
	 **/
	public Min(FormulaNumberElement arg1, FormulaNumberElement arg2) {
		setArg1(arg1);
		setArg2(arg2);
		setSymbol("min");
	}

	/**
	 * @return the minimum of the evaluated arguments
	 */
	public Double eval(XPSCase theCase) {
		if (super.eval(theCase) == null)
			return null;
		else
			return new Double(
				Math.min(
					getEvaluatedArg1().doubleValue(),
					getEvaluatedArg2().doubleValue()));
	}
}