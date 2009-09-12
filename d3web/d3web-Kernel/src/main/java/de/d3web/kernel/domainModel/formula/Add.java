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

import de.d3web.kernel.XPSCase;
/**
 * Addition term
 * Creation date: (14.08.2000 16:33:00)
 * @author Norman Brümmer
 */
public class Add extends FormulaNumberArgumentsTerm implements FormulaNumberElement{

	/** 
	 * Creates a new FormulaTerm with null-arguments.
	 */
	public Add() {
		this(null, null);
	}
	
	/**
	 *	Creates a new FormulaTerm (arg1 + arg2)
	 *	@param _arg1 first argument of the term
	 *	@param _arg2 second argument of the term 
	 **/
	public Add(FormulaNumberElement arg1, FormulaNumberElement arg2) {
		setArg1(arg1);
		setArg2(arg2);
		setSymbol("+");
	}

	/**
	 * Adds the evaluation values of both arguments
	 * @return added evaluated arguments
	 */
	public Double eval(XPSCase theCase) {
		super.eval(theCase);
		Double arg1 = getEvaluatedArg1();
		Double arg2 = getEvaluatedArg2();

		double arg1value = (arg1 == null) ? (0.0) : arg1.doubleValue();
		double arg2value = (arg2 == null) ? (0.0) : arg2.doubleValue();
		return new Double(arg1value + arg2value);
	}
}
