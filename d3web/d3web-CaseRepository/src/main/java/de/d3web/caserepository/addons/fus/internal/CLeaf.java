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
 * Created on 21.10.2003
 */
package de.d3web.caserepository.addons.fus.internal;

import java.util.Set;
import java.util.logging.Logger;

import de.d3web.core.knowledge.terminology.Solution;

/**
 * 21.10.2003 15:48:17
 * 
 * @author hoernlein
 */
public class CLeaf extends AbstractCNode {

	public final static Type INCLUDED = new Type("included");
	public final static Type EXCLUDED = new Type("excluded");

	public static class Type {

		private String name;

		private Type() { /* hide empty constructor */
		}

		private Type(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	private Solution d;
	private Type type;

	private CLeaf() { /* hide empty constructor */
	}

	public CLeaf(Solution d, Type type) {
		this.d = d;
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.Train.FUS.AbstractCNode#matches(java.util.Set)
	 */
	public boolean matches(Set<Solution> diagnoses) {
		if (type == INCLUDED) {
			return diagnoses.contains(d);
		}
		else if (type == EXCLUDED) {
			return !diagnoses.contains(d);
		}
		else {
			Logger.getLogger(this.getClass().getName()).severe(
					"not implemented for type '" + type.getName() + "'");
			return false;
		}
	}

	/**
	 * @return
	 */
	public Solution geSolution() {
		return d;
	}

	/**
	 * @return
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type The type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	public Object clone() {
		CLeaf temp = new CLeaf(d, type);
		return temp;
	}

}
