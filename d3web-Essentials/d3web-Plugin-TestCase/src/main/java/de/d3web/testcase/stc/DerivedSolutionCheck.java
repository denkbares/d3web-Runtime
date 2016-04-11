/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.testcase.stc;

import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.testcase.model.Check;

/**
 * Checks if a solution has the specified {@link Rating} in an session
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 24.01.2012
 */
public class DerivedSolutionCheck implements Check {

	private final Solution solution;
	private final Rating rating;

	public DerivedSolutionCheck(Solution solution, Rating rating) {
		this.solution = solution;
		this.rating = rating;
	}

	@Override
	public boolean check(Session session) {
		return rating.equals(session.getBlackboard().getValue(solution));
	}

	@Override
	public String getCondition() {
		return solution.getName() + " = " + rating.toString();
	}

	public Solution getSolution() {
		return solution;
	}

	public Rating getRating() {
		return rating;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DerivedSolutionCheck that = (DerivedSolutionCheck) o;

		if (!solution.equals(that.solution)) return false;
		return rating.equals(that.rating);

	}

	@Override
	public int hashCode() {
		int result = solution.hashCode();
		result = 31 * result + rating.hashCode();
		return result;
	}
}
