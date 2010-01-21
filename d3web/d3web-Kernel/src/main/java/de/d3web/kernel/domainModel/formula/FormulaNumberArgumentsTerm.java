/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

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
	
	private static final long serialVersionUID = 3935587662481357454L;

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
	public Collection<Object> getTerminalObjects() {
		Collection<Object> ret = new LinkedList<Object>(getArg1().getTerminalObjects());
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
