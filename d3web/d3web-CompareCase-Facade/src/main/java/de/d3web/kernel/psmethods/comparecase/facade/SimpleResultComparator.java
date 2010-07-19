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

package de.d3web.kernel.psmethods.comparecase.facade;

import java.util.Comparator;

/**
 * Compares SimpleResult objects by similarity
 * @author bruemmer
 */
public class SimpleResultComparator implements Comparator {

	private static SimpleResultComparator instance = null;

	private SimpleResultComparator() {
	}

	public static SimpleResultComparator getInstance() {
		if (instance == null) {
			instance = new SimpleResultComparator();
		}
		return instance;
	}

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object arg0, Object arg1) {
		try {
			SimpleResult simRes0 = (SimpleResult) arg0;
			SimpleResult simRes1 = (SimpleResult) arg1;

			if (simRes0.getSimilarity() > simRes1.getSimilarity()) {
				return -1;
			} else if (simRes0.getSimilarity() < simRes1.getSimilarity()) {
				return 1;
			} else
				return 0;

		} catch (Exception e) {
			return 0;
		}
	}

}
