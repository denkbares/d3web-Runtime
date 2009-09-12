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

package de.d3web.kernel.psMethods.setCovering;

/**
 * This class describes the covering-scores (as knowledge) for SCRelations
 * 
 * @author bates
 */
public final class SCScore implements SCKnowledge {

	private int value;

	public SCScore(int value) {
		this.value = value;
	}

	public static final SCScore P1 = new SCScore(1);
	public static final SCScore P2 = new SCScore(2);
	public static final SCScore P3 = new SCScore(3);
	public static final SCScore P4 = new SCScore(4);
	public static final SCScore P5 = new SCScore(5);
	public static final SCScore P6 = new SCScore(6);
	public static final SCScore P7 = new SCScore(7);

	/**
	 * @return the value as Integer-Object
	 */
	public Object getValue() {
		return new Integer(this.value);
	}

	public String getSymbol() {
		return getValue().toString();
	}

	/**
	 * @see SCKnowledge#verbalize()
	 */
	public String verbalize() {
		return "score";
	}
}
