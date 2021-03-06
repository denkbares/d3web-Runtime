/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.scoring;

import java.util.ArrayList;
import java.util.List;

import de.d3web.core.inference.ActionAddValueFact;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import com.denkbares.utils.EqualsUtils;

/**
 * Action to add scores to solutions (heuristic problem-solver) <br>
 * Creation date: (19.06.2001 17:18:31)
 * 
 * @author Joachim Baumeister
 */
public class ActionHeuristicPS extends ActionAddValueFact {

	private Solution solution;
	private Score score;

	@Override
	public String toString() {
		return (getSolution() == null ? "-?-" : getSolution()) + " = "
				+ getScore();
	}

	/**
	 * @return all objects participating on the action.<BR>
	 */
	@Override
	public List<? extends TerminologyObject> getBackwardObjects() {
		List<Solution> terminals = new ArrayList<>(1);
		if (getSolution() != null) {
			terminals.add(getSolution());
		}
		return terminals;
	}

	/**
	 * Creates a new ActionHeuristicPS for the given corresponding Rule Creation
	 * date: (19.06.2001 17:41:53)
	 */
	public ActionHeuristicPS() {
		super();
	}

    public ActionHeuristicPS(Solution solution, Score score) {
        super();
        this.solution = solution;
        this.score = score;
    }

	/**
	 * Executes the included action.
	 */
	@Override
	public void doIt(Session session, Object source, PSMethod psmethod) {

		Fact fact = FactFactory.createFact(getSolution(), new HeuristicRating(getScore()),
				source, psmethod);

		session.getBlackboard().addValueFact(fact);

	}

	/**
	 * @return the Diagnosis this Action can add scores to
	 */
	public Solution getSolution() {
		return solution;
	}

	/**
	 * @return the Score that can be added to the corresponding Diagnosis
	 */
	public Score getScore() {
		return score;
	}

	/**
	 * sets the given Diagnosis and resets the corresponding rule as Knowledge
	 * slice
	 */
	public void setSolution(Solution solution) {
		this.solution = solution;
	}

	/**
	 * Sets the score to be added to the specified diagnosis, if the rule can
	 * fire.
	 */
	public void setScore(Score score) {
		this.score = score;
	}

	/**
	 * Tries to undo the included action.
	 */
	@Override
	public void undo(Session session, Object source, PSMethod psmethod) {
		session.getBlackboard().removeValueFact(solution, source);
		// nothing to do, the fact created in doIt will be automatically deleted
		// from blackboard
	}

	@Override
	public int hashCode() {
		int hash = 0;
		if (getSolution() != null) {
			hash += getSolution().hashCode();
		}
		if (getScore() != null) {
			hash += getScore().hashCode();
		}
		return hash;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof ActionHeuristicPS) {
			ActionHeuristicPS a = (ActionHeuristicPS) o;
			return EqualsUtils.isSame(a.getSolution(), getSolution())
					&& EqualsUtils.isSame(a.getScore(), getScore());
		}
		else {
			return false;
		}
	}
}