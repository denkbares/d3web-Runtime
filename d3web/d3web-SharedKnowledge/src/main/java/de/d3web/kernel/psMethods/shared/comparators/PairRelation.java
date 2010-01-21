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

package de.d3web.kernel.psMethods.shared.comparators;

import de.d3web.kernel.domainModel.answers.AnswerChoice;

/**
 * Helper Class for managing a pair of answers and the depending value (for
 * similarity calculation) Creation date: (07.08.2001 10:54:15)
 * 
 * @author: Norman Brümmer
 */
public class PairRelation implements java.io.Serializable {

	private static final long serialVersionUID = -5038955289180555039L;
	private AnswerChoice ans1 = null;
	private AnswerChoice ans2 = null;

	private double value = 1;

	public PairRelation(AnswerChoice ans1, AnswerChoice ans2, double value) {
		this.ans1 = ans1;
		this.ans2 = ans2;
		this.value = value;
	}

	public AnswerChoice getAnswer1() {
		return ans1;
	}

	public AnswerChoice getAnswer2() {
		return ans2;
	}

	public double getValue() {
		return value;
	}

	public boolean containsAnswer(AnswerChoice a) {
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