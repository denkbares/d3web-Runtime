/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.d3web.abstraction.formula;

import java.util.Collection;
import java.util.LinkedList;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;

/**
 * Complex FormulaElement. Creation date: (14.08.2000 15:41:43)
 * 
 * @author Norman Brümmer
 */
public class Operator implements FormulaNumberElement {

	public enum Operation {
		Add, Div, Max, Min, Mult, Sub;
	}

	/** first argument of the term */
	private FormulaNumberElement arg1 = null;

	/** second argument of the term */
	private FormulaNumberElement arg2 = null;

	private final Operation operator;

	/**
	 * Creates a new term with its two arguments
	 * 
	 * @param arg1 first argument
	 * @param arg2 second argument
	 */
	public Operator(FormulaNumberElement arg1, FormulaNumberElement arg2, Operation operator) {
		this.operator = operator;
		setArg1(arg1);
		setArg2(arg2);
	}

	/**
	 * Checks if term contains null (rek.) Creation date: (14.08.2000 17:05:38)
	 * 
	 * @return null, if one argument is "null", a "0"-Double else.
	 */
	public Double eval(Session session) {
		if (getArg1() == null || getArg2() == null) {
			return null;
		}

		Double evaluatedArg1 = (getArg1().eval(session));
		Double evaluatedArg2 = (getArg2().eval(session));
		if ((evaluatedArg1 == null) || (evaluatedArg2 == null)) {
			return null;
		}
		switch (operator) {
		case Add:
			return new Double(evaluatedArg1.doubleValue() + evaluatedArg2.doubleValue());
		case Div:
			return new Double(evaluatedArg1.doubleValue() / evaluatedArg2.doubleValue());
		case Max:
			return new Double(Math.max(evaluatedArg1.doubleValue(), evaluatedArg2.doubleValue()));
		case Min:
			return new Double(Math.min(evaluatedArg1.doubleValue(), evaluatedArg2.doubleValue()));
		case Mult:
			return new Double(evaluatedArg1.doubleValue() * evaluatedArg2.doubleValue());
		case Sub:
			return new Double(evaluatedArg1.doubleValue() - evaluatedArg2.doubleValue());
		default:
			return null;
		}
	}

	/**
	 * Creation date: (14.08.2000 15:46:45)
	 * 
	 * @return first argument of the term
	 */
	public FormulaNumberElement getArg1() {
		if (arg1 == null) return new FormulaNumber(null);
		else return arg1;
	}

	/**
	 * Creation date: (14.08.2000 15:46:45)
	 * 
	 * @return second argument of the term
	 */
	public FormulaNumberElement getArg2() {
		if (arg2 == null) return new FormulaNumber(null);
		else return arg2;
	}

	public String getSymbol() {
		switch (operator) {
		case Add:
			return "+";
		case Div:
			return "/";
		case Max:
			return "max";
		case Min:
			return "min";
		case Mult:
			return "*";
		case Sub:
			return "-";
		default:
			return null;
		}
	}

	public void setArg1(FormulaNumberElement arg1) {
		this.arg1 = arg1;
	}

	public void setArg2(FormulaNumberElement arg2) {
		this.arg2 = arg2;
	}

	/**
	 * @see FormulaElement
	 */
	public Collection<? extends TerminologyObject> getTerminalObjects() {
		Collection<TerminologyObject> ret = new LinkedList<TerminologyObject>(
				getArg1().getTerminalObjects());
		ret.addAll(getArg2().getTerminalObjects());

		return ret;
	}

	@Override
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
