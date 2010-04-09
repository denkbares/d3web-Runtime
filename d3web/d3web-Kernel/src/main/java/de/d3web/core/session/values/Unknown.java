/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.core.session.values;

import de.d3web.core.session.Value;

/**
 * This class represents the 'unknown' answer given by a user.
 * 
 * @author joba (denkbares GmbH)
 * @created 07.04.2010
 */
public class Unknown implements Value {

	public final static String UNKNOWN_ID = "MaU";
	private static final Unknown instance = new Unknown();

	private Unknown() {
	}

	public static Unknown getInstance() {
		return instance;
	}

	/**
	 * Checks if the specified value is assigned to Unknown
	 * 
	 * @param value
	 * @return true if the value is set to unknown, false otherwise
	 * @author joba
	 * @date 09.04.2010
	 */
	public static boolean assignedTo(Value value) {
		return value instanceof Unknown;
	}

	@Override
	public Object getValue() {
		// TODO: find a better implementation of unknown choices
		return "UNKNOWN";
	}

	@Override
	public int hashCode() {
		return UNKNOWN_ID.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Unknown) {
			return true;
		}
		else {
			return false;
		}

	}

	@Override
	public int compareTo(Value o) {
		if (o instanceof Unknown)
			return 0;
		else
			return -1;
	}
	
	
}
