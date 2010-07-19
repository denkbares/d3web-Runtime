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

package de.d3web.kernel.psmethods.comparecase.comparators;

/**
 * @author bates
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public class CompareMode {

	private int[] value;
	private boolean isIgnoreMutualUnknowns = false;

	public static final CompareMode NO_FILL_UNKNOWN = new CompareMode(new int[] { 0, 0 });
	public static final CompareMode COMPARE_CASE_FILL_UNKNOWN = new CompareMode(new int[] { 0, 1 });
	public static final CompareMode BOTH_FILL_UNKNOWN = new CompareMode(new int[] { 1, 1 });
	public static final CompareMode CURRENT_CASE_FILL_UNKNOWN = new CompareMode(new int[] { 1, 0 });
	public static final CompareMode JUNIT_TEST = new CompareMode(new int[] { Integer.MAX_VALUE, Integer.MAX_VALUE });

	private CompareMode(int[] value) {
		this.value = value;
	}

	public int[] getValue() {
		return value;
	}

	public boolean covers(CompareMode other) {
		try {
			return (value[0] >= other.getValue()[0]) && (value[1] >= other.getValue()[1]);
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * Returns the isIgnoreMutualUnknowns.
	 * @return boolean
	 */
	public boolean isIgnoreMutualUnknowns() {
		return isIgnoreMutualUnknowns;
	}

	/**
	 * Sets the isIgnoreMutualUnknowns.
	 * @param isIgnoreMutualUnknowns The isIgnoreMutualUnknowns to set
	 */
	public void setIsIgnoreMutualUnknowns(boolean isIgnoreBooleanUnknowns) {
		this.isIgnoreMutualUnknowns = isIgnoreBooleanUnknowns;
	}

	public boolean equals(Object o) {
		try {
			CompareMode other = (CompareMode) o;
			return other.covers(this) && this.covers(other);
		} catch (Exception e) {
			return false;
		}
	}

}