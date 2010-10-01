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

/*
 * Created on 11.11.2003
 * 
 * To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
package de.d3web.abstraction.formula;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.DateValue;

/**
 * Primitive formula date element in date format Creation date: (14.08.2000
 * 15:49:14)
 * 
 * @author Norman Brümmer
 */
public class FormulaDate implements FormulaDateElement {

	private final Date value;

	/**
	 * The Format, in which the dates are saved and loaded. The Format is for
	 * example 2003-10-20-13-51-23
	 */
	public static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	/**
	 * Creates a new FormulaNubmer with 0 as value.
	 */
	public FormulaDate() {
		this(new Date());
	}

	/**
	 * Creates a new FormulaNumber object
	 * 
	 * @param value value of this FormulaElement
	 */
	public FormulaDate(Date value) {
		this.value = value;
	}

	/**
	 * Creation date: (14.08.2000 15:51:57)
	 * 
	 * @return Double-value of this FormulaElement
	 */
	@Override
	public Value eval(Session session) {
		return new DateValue(value);
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
			return "<" + format.format(value) + ">";
		}
	}

	/**
	 * @see FormulaElement
	 */
	@Override
	public Collection<? extends TerminologyObject> getTerminalObjects() {
		return new LinkedList<TerminologyObject>();
	}

	/**
	 * 
	 * @created 24.06.2010
	 * @return
	 */
	public Date getValue() {
		return value;
	}

}
