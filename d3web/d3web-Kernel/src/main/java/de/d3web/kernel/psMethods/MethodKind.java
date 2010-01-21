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

package de.d3web.kernel.psMethods;


/**
 * Helper class to provide explict information about
 * the knowledge to be stored in the ps-method knowledge
 * maps.
 * Creation date: (07.09.00 13:40:08)
 * @author Joachim Baumeister
 */
public class MethodKind implements java.io.Serializable {
	private static final long serialVersionUID = 7807082872089949082L;

	private final String kind;
	
	public final static MethodKind FORWARD = new MethodKind("FORWARD");
	public final static MethodKind BACKWARD = new MethodKind("BACKWARD");

	/**
	 * Insert the method's description here.
	 * Creation date: (07.09.00 13:40:46)
	 * @param theKind java.lang.String
	 */
	public MethodKind(String kind) {
		this.kind = kind;
	}

	/**
	 * @return true if the String-value of these Objects are equal.
	 */
	public boolean equals(Object obj) {
		return kind.equals(obj.toString());
	}

	/**
	 * @return a string representation of this Object
	 */
	public String toString() {
		return kind;
	}
	
	
	/**
	 * This method is called immediately after an object of this class is deserialized.
	 * To avoid that several instances of a unique object are created, this method returns
	 * the current unique instance that is equal to the object that was deserialized.
	 * @author georg
	 */
	private Object readResolve() {
		MethodKind[] methodKinds = new MethodKind[] {
			MethodKind.FORWARD,
			MethodKind.BACKWARD,
		};
		for (MethodKind m : methodKinds) {
			if (m.kind.equals(this.kind)) {
				return m;
			}
		}
		return this;
	}
	
}
