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

package de.d3web.shared.comparators;

import de.d3web.core.session.values.Choice;

/**
 * Helper Class for managing a pair of answers and the depending value (for
 * similarity calculation) Creation date: (07.08.2001 10:54:15)
 * 
 * @author: Norman Brümmer
 */
public class PairRelation {

	private Choice ans1 = null;
	private Choice ans2 = null;

	private double value = 1;

	public PairRelation(Choice ans1, Choice ans2, double value) {
		this.ans1 = ans1;
		this.ans2 = ans2;
		this.value = value;
	}

	public Choice getAnswer1() {
		return ans1;
	}

	public Choice getAnswer2() {
		return ans2;
	}

	public double getValue() {
		return value;
	}

	public boolean containsAnswer(Choice a) {
		return ans1.equals(a) || ans2.equals(a);
	}

	public boolean equals(Object o) {

		if (o == null) {
			return false;
		}

		if (o instanceof PairRelation) {
			PairRelation rel = (PairRelation) o;
			if ((rel.getAnswer1() == null) || (rel.getAnswer2() == null)) {
				return false;
			}
			if ((ans1 == null) || (ans2 == null)) {
				return false;
			}
			return (rel.getAnswer1().equals(ans1) && (rel.getAnswer2()
					.equals(ans2)));
		}
		return false;
	}
}