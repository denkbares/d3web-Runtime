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
import de.d3web.core.session.Value;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.UndefinedValue;

/**
 * Complex FormulaElement. Creation date: (14.08.2000 15:41:43)
 *
 * @author Norman Brümmer
 */
public class Operator implements FormulaNumberElement {

	public enum Operation {
		Add, Div, Max, Min, Mult, Sub
	}

	/**
	 * first argument of the term
	 */
	private final FormulaNumberElement arg1;

	/**
	 * second argument of the term
	 */
	private final FormulaNumberElement arg2;

	private final Operation operator;

	/**
	 * Creates a new term with its two arguments
	 *
	 * @param arg1 first argument
	 * @param arg2 second argument
	 */
	public Operator(FormulaNumberElement arg1, FormulaNumberElement arg2, Operation operator) {
		if (arg1 == null || arg2 == null) throw new NullPointerException(
				"The arguments must not be null.");
		if (operator == null) throw new NullPointerException(
				"The operator must not be null.");
		this.operator = operator;
		this.arg1 = arg1;
		this.arg2 = arg2;
	}

	@Override
	public Value eval(Session session) {
		Object value = (getArg1().eval(session)).getValue();
		Object value2 = (getArg2().eval(session)).getValue();
		if (!(value instanceof Number) || !(value2 instanceof Number)) {
			return UndefinedValue.getInstance();
		}

		Number evaluatedArg1 = (Number) value;
		Number evaluatedArg2 = (Number) value2;
		switch (operator) {
			case Add:
				return new NumValue(evaluatedArg1.doubleValue()
						+ evaluatedArg2.doubleValue());
			case Div:
				return new NumValue(evaluatedArg1.doubleValue()
						/ evaluatedArg2.doubleValue());
			case Max:
				return new NumValue(Math.max(evaluatedArg1.doubleValue(),
						evaluatedArg2.doubleValue()));
			case Min:
				return new NumValue(Math.min(evaluatedArg1.doubleValue(),
						evaluatedArg2.doubleValue()));
			case Mult:
				return new NumValue(evaluatedArg1.doubleValue()
						* evaluatedArg2.doubleValue());
			default:
				// only Sub is left
				return new NumValue(evaluatedArg1.doubleValue()
						- evaluatedArg2.doubleValue());
		}
	}

	/**
	 * Creation date: (14.08.2000 15:46:45)
	 *
	 * @return first argument of the term
	 */
	public FormulaNumberElement getArg1() {
		return arg1;
	}

	/**
	 * Creation date: (14.08.2000 15:46:45)
	 *
	 * @return second argument of the term
	 */
	public FormulaNumberElement getArg2() {
		return arg2;
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
		}
		throw new IllegalStateException("missing switch-case: " + operator);
	}

	/**
	 * @see FormulaElement
	 */
	@Override
	public Collection<? extends TerminologyObject> getTerminalObjects() {
		Collection<TerminologyObject> ret = new LinkedList<>(
				getArg1().getTerminalObjects());
		ret.addAll(getArg2().getTerminalObjects());

		return ret;
	}

	@Override
	public String toString() {

		return "("
				+ getArg1()
				+ " "
				+ getSymbol()
				+ " "
				+ getArg2()
				+ ")";

	}
}
