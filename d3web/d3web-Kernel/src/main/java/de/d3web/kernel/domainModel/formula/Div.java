package de.d3web.kernel.domainModel.formula;

import de.d3web.kernel.XPSCase;
/**
 * Division term
 * Creation date: (14.08.2000 16:42:22)
 * @author Norman Brümmer
 */
public class Div extends FormulaNumberArgumentsTerm implements FormulaNumberElement{

	/** 
	 * Creates a new FormulaTerm with null-arguments.
	 */
	public Div() {
		this(null, null);
	}	
	
	/**
	 *	Creates a new FormulaTerm (arg1 / arg2)
	 *	@param _arg1 first argument of the term
	 *	@param _arg2 second argument of the term 
	 **/
	public Div(FormulaNumberElement arg1, FormulaNumberElement arg2) {

		setArg1(arg1);
		setArg2(arg2);
		setSymbol("/");
	}

	/**
	 * Divides the evaluation values of both arguments
	 * @return divided evaluated arguments
	 */
	public Double eval(XPSCase theCase) {
		if (super.eval(theCase) == null)
			return null;
		else
			return new Double(
				getEvaluatedArg1().doubleValue()
					/ getEvaluatedArg2().doubleValue());
	}
}
