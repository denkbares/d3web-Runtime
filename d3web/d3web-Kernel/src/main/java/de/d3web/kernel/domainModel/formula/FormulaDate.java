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
 * Created on 11.11.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.d3web.kernel.domainModel.formula;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.answers.AnswerDate;

/**
 * Primitive formula date element in date format
 * Creation date: (14.08.2000 15:49:14)
 * @author Norman Brümmer
 */
public class FormulaDate extends FormulaDatePrimitive {

	/** 
	 * Creates a new FormulaNubmer with 0 as value.
	 */
	public FormulaDate() {
		this(new Date());
	}	
	
	/**
	 * 	Creates a new FormulaNumber object
	 * 	@param value value of this FormulaElement
	 */
	public FormulaDate(Date value) {
		super(value);
	}

	/**
	 * Creation date: (14.08.2000 15:51:57)
	 * @return Double-value of this FormulaElement
	 */
	public Date eval(XPSCase theCase) {
		return (Date) getValue();
	}

	/**
	 *	@return XML representation of this FormulaNumber-object
	 **/
	public java.lang.String getXMLString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<FormulaDatePrimitive type='FormulaDate'>\n");
		sb.append("<Value>" + AnswerDate.format.format(getValue())	 + "</Value>\n");
		sb.append("</FormulaDatePrimitive>\n");
		return sb.toString();
	}

	/**
	 *	@return String representation of this FormulaNumber-object
	 **/
	public String toString() {
		if (getValue() == null)
			return "null";
		else
			return "<"+AnswerDate.format.format(getValue())+">";
	}

	/**
	 * @see FormulaElement
	 */
	public Collection getTerminalObjects() {
		return new LinkedList();
	}

}
