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

package de.d3web.persistence.xml;
/**
 * This class encapsulates the output-relevant attributes of a Cost-object.
 * Creation date: (08.06.2001 14:57:03)
 * @author Michael Scharvogel
 */
public class MockCostObject {
	private String ID = null;
	private String verbalization = null;
	private String unit = null;

	/**
	 * Default-constructor for MockCostObject
	 */
	public MockCostObject() {
	}

	/**
	 * Creates a new MockCostObject with the given parameters
	 */
	public MockCostObject(String ID, String verbalization, String unit) {
		this.ID = ID;
		this.verbalization = verbalization;
		this.unit = unit;
	}

	/**
	 * Creation date: (08.06.2001 16:18:31)
	 * @return the ID of the corresponding Cost-object
	 */
	public java.lang.String getID() {
		return ID;
	}

	/**
	 * Creation date: (08.06.2001 16:18:31)
	 * @return the verbalization of the corresponding Cost-object
	 */
	public java.lang.String getVerbalization() {
		return verbalization;
	}

	/**
	 * sets the ID-attribute
	 * Creation date: (08.06.2001 16:18:31)
	 */
	public void setID(String newID) {
		ID = newID;
	}

	/**
	 * sets the verbalization-attribute
	 * Creation date: (08.06.2001 16:18:31)
	 */
	public void setVerbalization(String newVerbalization) {
		verbalization = newVerbalization;
	}

	/**
	 * @return the unit of the corresponding Cost-object
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * Sets the unit-attribute
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

}