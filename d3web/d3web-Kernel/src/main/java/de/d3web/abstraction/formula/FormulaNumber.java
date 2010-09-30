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
 * Primitive formula element in number format Creation date: (14.08.2000
 * 15:49:14)
 * 
 * @author Norman Brümmer
 */
public class FormulaNumber implements FormulaNumberElement {

	private final Double value;

	/**
	 * Creates a new FormulaNubmer with 0 as value.
	 */
	public FormulaNumber() {
		this(new Double(0));
	}

	/**
	 * Creates a new FormulaNumber object
	 * 
	 * @param value value of this FormulaElement
	 */
	public FormulaNumber(Double value) {
		this.value = value;
	}

	/**
	 * Creation date: (14.08.2000 15:51:57)
	 * 
	 * @return Double-value of this FormulaElement
	 */
	@Override
	public Double eval(Session session) {
		return value;
	}

	/**
	 * @return String representation of this FormulaNumber-object
	 **/
	@Override
	public String toString() {
		if (value == null) {
			return "null";
		}
		else {
			return trim(value);
		}
	}

	/**
	 * Method for formatting the double-value of this FormulaElement Used by
	 * toString ()
	 * 
	 * Creation date: (15.08.2000 08:31:02)
	 * 
	 * @return formatted String-representation of this FormulaElement
	 */
	private String trim(Object trimValue) {
		final int digits = 3;
		String text = trimValue.toString();
		int dot = text.indexOf(".");
		if (dot != -1) {
			text = text.substring(0, Math.min(text.length(), dot + 1 + digits));
		}
		return text;
	}

	/**
	 * @see FormulaElement
	 */
	@Override
	public Collection<? extends TerminologyObject> getTerminalObjects() {
		return new LinkedList<TerminologyObject>();
	}

	public Double getValue() {
		return value;
	}

}
