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
 * Created on 13.10.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.d3web.kernel.domainModel.formula;



/**
 * This class represents a primitive (atomar) date-element 
 * of a formula
 *
 * Creation date: (14.08.2000 15:41:25)
 * @author Tobias Vogele
 */
public abstract class FormulaDatePrimitive implements FormulaDateElement {

	/**
	 * Value of the primitive FormulaElement
	 */
	protected Object value = null;

	/**
	 * creates a new FormulaPrimitive
	 */
	public FormulaDatePrimitive() {
	}

	/**
	 * creates a new FormulaDatePrimitive
	 * @param value value that the primitive FormulaDateElement will have
	 */
	public FormulaDatePrimitive(Object value) {
		setValue(value);
	}

	/**
	 * @return value of this primitive FormulaDateElement
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Creation date: (20.06.2001 15:31:53)
	 * @see FormulaDateElement
	 */
	public abstract String getXMLString();

	public void setValue(Object value) {
		this.value = value;
	}
}
