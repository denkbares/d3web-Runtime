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

/*
 * Created on 10.10.2003
 */
package de.d3web.abstraction.formula;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import de.d3web.core.session.Session;

/**
 * A Term which has 2 Dates as Arguments.  
 * @author Tobias vogele
 */
public abstract class FormulaDateArgumentsTerm implements FormulaElement {

	private FormulaDateElement arg1;
	private FormulaDateElement arg2;

	/**
	 * Here the evaluation value will be stored
	 * while trying to evaluate the term.
	 * It warrents, that the evaluation will be done 
	 * only once.
	 */
	private Date evaluatedArg1 = null;

	/**
	 * Look above.
	 */
	private Date evaluatedArg2 = null;

	private String symbol = null;

	public FormulaDateArgumentsTerm() {
	}

	/**
	 * Creates a new term with its two arguments
	 * @param arg1 first argument
	 * @param arg2 second argument
	 */
	public FormulaDateArgumentsTerm(FormulaDateElement arg1, FormulaDateElement arg2) {
		setArg1(arg1);
		setArg2(arg2);
	}
	
	/**
	 * Evaluates the arguments.
	 * @return true, if no argument is null.
	 */
	protected boolean evaluateArguments(Session theCase) {
		if (getArg1() == null || getArg2() == null) {
			return false;
		}
		evaluatedArg1 = getArg1().eval(theCase);
		evaluatedArg2 = getArg2().eval(theCase);
		
		return getEvaluatedArg1() != null && getEvaluatedArg2() != null;
		
	}
	

	/* (non-Javadoc)
	 * @see de.d3web.kernel.domainModel.formula.FormulaElement#getTerminalObjects()
	 */
	public Collection<Object> getTerminalObjects() {
		Collection<Object> c1 = getArg1().getTerminalObjects();
		Collection<Object> c2 = getArg2().getTerminalObjects();
		Collection<Object> both = new ArrayList<Object>(c1.size() + c2.size());
		both.addAll(c1);
		both.addAll(c2);
		return both;	
	}

	/**
	 * @return
	 */
	public FormulaDateElement getArg1() {
		return arg1;
	}

	/**
	 * @param arg1
	 */
	public void setArg1(FormulaDateElement arg1) {
		this.arg1 = arg1;
	}

	/**
	 * @return
	 */
	public FormulaDateElement getArg2() {
		return arg2;
	}

	/**
	 * @param arg2
	 */
	public void setArg2(FormulaDateElement arg2) {
		this.arg2 = arg2;
	}

	/**
	 * @return
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * @param symbol
	 */
	protected void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	/**
	 * @return
	 */
	public Date getEvaluatedArg1() {
		return evaluatedArg1;
	}

	/**
	 * @return
	 */
	public Date getEvaluatedArg2() {
		return evaluatedArg2;
	}

	public String toString() {
		StringBuffer buffy = new StringBuffer();
		buffy.append("(");
		if (getArg1() != null) {
			buffy.append(getArg1());
		}
		buffy.append(" ").append(getSymbol()).append(" ");
		if (getArg2() != null) {
			buffy.append(getArg2());
		}
		buffy.append(")");
		return buffy.toString();
	}
}
