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
package de.d3web.core.inference.condition;

import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.indication.inference.PSMethodUserSelected;

/**
 * This condition checks whether the supplied solution has been confirmed by the
 * user (using a UserRating).
 * 
 * @author Reinhard Hatko
 * @created 22.11.2010
 */
public class CondSolutionConfirmed extends TerminalCondition {

	public CondSolutionConfirmed(Solution solution) {
		super(solution);

		if (solution == null) {
			throw new NullPointerException();
		}
	}

	@Override
	public boolean eval(Session session) throws NoAnswerException, UnknownAnswerException {

		Solution solution = (Solution) getTerminalObjects().get(0);

		
		Rating rating = (Rating) session.getBlackboard().getValue(solution,
				session.getPSMethodInstance(PSMethodUserSelected.class));

		// If no Rating is given by the user, a Rating with state.unclear is returned
		if (rating.hasState(State.UNCLEAR)) throw NoAnswerException.getInstance();

		return rating.hasState(State.ESTABLISHED);

	}

	public Solution getSolution() {
		return (Solution) getTerminalObjects().get(0);
	}

	@Override
	public Condition copy() {
		return new CondSolutionConfirmed((Solution) getTerminalObjects().get(0));
	}

	@Override
	public String toString() {
		return getClass().getName() + "(" + getSolution().getName() + ")";
	}


}
