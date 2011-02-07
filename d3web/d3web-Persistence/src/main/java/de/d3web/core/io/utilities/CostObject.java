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

package de.d3web.core.io.utilities;

/**
 * Realizes a static representation of costs of an NamedObject. This is needed
 * because the costs must be set after parsing the NamedObject.<br>
 * Creation date: (03.07.2001 15:40:43)
 * 
 * @author Norman Brümmer
 */
public class CostObject {

	private String id;
	private Double value;
	private String verbalization;
	private String unit;

	/**
	 * Creates a new CostObject with the given object ID and its value
	 */
	public CostObject(String id, Double value) {
		super();
		this.id = id;
		this.value = value;
	}

	/**
	 * Creates a new CostObject with the given object ID, the costs
	 * verbalization and its unit
	 */
	public CostObject(String id, String verbalization, String unit) {
		super();
		this.id = id;
		this.verbalization = verbalization;
		this.unit = unit;
	}

	/**
	 * Creation date: (03.07.2001 15:41:11)
	 * 
	 * @return the id of the knowledge base object for which the costs are
	 *         encapsulated here
	 */
	public String getId() {
		return id;
	}

	/**
	 * Creation date: (02.08.2001 15:24:35)
	 * 
	 * @return the unit of the incapsulated costs
	 */
	public java.lang.String getUnit() {
		return unit;
	}

	/**
	 * Creation date: (03.07.2001 15:41:25)
	 * 
	 * @return the value of the encapsulated costs
	 */
	public Double getValue() {
		return value;
	}

	/**
	 * Creation date: (03.07.2001 15:41:25)
	 * 
	 * @return the verbalization of the encapsulated costs
	 */
	public String getVerbalization() {
		return verbalization;
	}

	public String toString() {
		if (id != null && value != null) return "(" + id + ")[" + value.doubleValue() + "]";
		else return super.toString();
	}
}