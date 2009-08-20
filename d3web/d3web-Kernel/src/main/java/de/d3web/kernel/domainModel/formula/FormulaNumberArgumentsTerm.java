package de.d3web.kernel.domainModel.formula;

import java.util.Collection;
import java.util.LinkedList;

import de.d3web.kernel.XPSCase;
/**
 * Complex FormulaElement.
 * Creation date: (14.08.2000 15:41:43)
 * @author Norman Brümmer
 */
public abstract class FormulaNumberArgumentsTerm implements FormulaElement {
	/** first argument of the term*/
	private FormulaNumberElement arg1 = null;

	/** second argument of the term*/
	private FormulaNumberElement arg2 = null;

	/**
	 * Here the evaluation value will be stored
	 * while trying to evaluate the term.
	 * It warrents, that the evaluation will be done 
	 * only once.
	 */
	private Double evaluatedArg1 = null;

	/**
	 * Look above.
	 */
	private Double evaluatedArg2 = null;

	private String symbol = null;

	public FormulaNumberArgumentsTerm() {
	}

	/**
	 * Creates a new term with its two arguments
	 * @param arg1 first argument
	 * @param arg2 second argument
	 */
	public FormulaNumberArgumentsTerm(FormulaNumberElement arg1, FormulaNumberElement arg2) {

		setArg1(arg1);
		setArg2(arg2);
	}

	/**
	 * Checks if term contains null (rek.)
	 * Creation date: (14.08.2000 17:05:38)
	 * @return null, if one argument is "null", a "0"-Double else.
	 */
	public Double eval(XPSCase theCase) {
		if (getArg1() == null || getArg2() == null) {
			return null;
		}

		evaluatedArg1 = (getArg1().eval(theCase));
		evaluatedArg2 = (getArg2().eval(theCase));

		if ((getEvaluatedArg1() == null) || (getEvaluatedArg2() == null))
			return null;
		else
			return new Double(0);

	}

	/**
	 * Creation date: (14.08.2000 15:46:45)
	 * @return first argument of the term
	 */
	public FormulaNumberElement getArg1() {
		if (arg1 == null)
			return new FormulaNumber(null);
		else
			return arg1;
	}

	/**
	 * Creation date: (14.08.2000 15:46:45)
	 * @return second argument of the term
	 */
	public FormulaNumberElement getArg2() {
		if (arg2 == null)
			return new FormulaNumber(null);
		else
			return arg2;
	}

	/**
	 * Creation date: (15.08.2000 08:26:48)
	 * @return the evaluated value of the first argument
	 */
	public Double getEvaluatedArg1() {
		return evaluatedArg1;
	}

	/**
	 * Creation date: (15.08.2000 08:26:48)
	 * @return the evaluated value of the second argument
	 */
	public Double getEvaluatedArg2() {
		return evaluatedArg2;
	}

	public String getSymbol() {
		return symbol;
	}

	/**
	 * @see FormulaElement
	 */
	public String getXMLString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<FormulaTerm type=\"" + getSymbol() + "\">\n");
		sb.append("<arg1>\n");
		sb.append(getArg1().getXMLString());
		sb.append("</arg1>\n");
		sb.append("<arg2>\n");
		sb.append(getArg2().getXMLString());
		sb.append("</arg2>\n");
		sb.append("</FormulaTerm>\n");
		return sb.toString();
	}

	public void setArg1(FormulaNumberElement arg1) {
		this.arg1 = arg1;
	}

	public void setArg2(FormulaNumberElement arg2) {
		this.arg2 = arg2;
	}

	/**
	 * Sets the arithmetic symbol of the expression
	 *
	 * Creation date: (15.08.2000 09:44:57)
	 * @param newSymbol new arithmetic symbol
	 */
	protected void setSymbol(java.lang.String symbol) {
		this.symbol = symbol;
	}

	/**
	 * @see FormulaElement
	 */
	public Collection getTerminalObjects() {
		Collection ret = new LinkedList(getArg1().getTerminalObjects());
		ret.addAll(getArg2().getTerminalObjects());

		return ret;
	}

	public String toString() {

		return "("
			+ getArg1().toString()
			+ " "
			+ getSymbol()
			+ " "
			+ getArg2().toString()
			+ ")";

	}
}
