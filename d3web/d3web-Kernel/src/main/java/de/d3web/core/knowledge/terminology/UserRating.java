/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.d3web.core.knowledge.terminology;

import de.d3web.core.session.Value;
import de.d3web.core.utilities.HashCodeUtils;

/**
 * This class represents the rating of a solution which has been confirmed or
 * rejected by the user.
 * 
 * @author Reinhard Hatko
 * @created 22.11.2010
 */
public class UserRating extends Rating {

	/**
	 * Possible user evaluations of a rating
	 * 
	 * @author Reinhard Hatko
	 */
	public enum Evaluation {
		CONFIRMED, REJECTED
	}

	private final Evaluation evaluation;

	public UserRating(State state, Evaluation evaluation) {
		super(state);
		this.evaluation = evaluation;

		if (this.evaluation == null) {
			throw new NullPointerException();
		}
	}

	public UserRating(String stateName, String evaluationName) {
		this(State.valueOf(stateName.toUpperCase()),
				Evaluation.valueOf(evaluationName.toUpperCase()));
	}

	@Override
	public int compareTo(Value other) {
		if (other instanceof UserRating) {
			if (getState().equals(((UserRating) other).getState())) return getEvaluation().ordinal()
					- ((UserRating) other).getEvaluation().ordinal();
			else {
				return getState().ordinal() - ((UserRating) other).getState().ordinal();
			}

		}
		else {
			return super.compareTo(other);
		}

	}

	public Evaluation getEvaluation() {
		return evaluation;
	}
	
	@Override
	public boolean equals(Object other) {
		
		if (this == other) return true;
		
		if (other == null) return false;
		
		if (other.getClass() != this.getClass()) return false;

		UserRating rating = (UserRating) other;

		return rating.getEvaluation().equals(getEvaluation())
				&& rating.getState().equals(getState());
	}

	@Override
	public int hashCode() {
		return HashCodeUtils.hash(super.hashCode(), this.evaluation);

	}

	public boolean hasEvaluation(Evaluation evaluation) {
		return this.evaluation.equals(evaluation);
	}
	
	
	@Override
	public String getName() {
		return super.getName() + " and " + this.evaluation;
	}

}
