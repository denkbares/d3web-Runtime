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

package de.d3web.core.knowledge.terminology;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.session.Value;
import de.d3web.scoring.Score;
import de.d3web.scoring.inference.PSMethodHeuristic;

/**
 * This class stores the static, non case-dependent parts of a solutions. The
 * value of a solution, i.e., its state and score, is dependent from the
 * problem-solver context. If no problem-solver context is given, then the
 * heuristic problem-solver is the default context.
 * 
 * @author joba, chris
 * @see NamedObject
 * @see Rating
 */
public class Solution extends AbstractTerminologyObject implements ValueObject {

	/**
	 * A solution can have a prior probability, that is taken into account by
	 * the particular problem-solvers differently. The {@link PSMethodHeuristic}
	 * , for example, adds the a-priori probability as soon as the solution
	 * receives scores from a rule.
	 */
	private Score aprioriProbability;

	/**
	 * Creates a new Solution and adds it to the knowledgebase, so no manual
	 * adding of the created object to the kb is needed
	 * 
	 * @param kb {@link KnowledgeBase} in which the Solution should be inserted
	 * @param name the name of the new QContainer
	 */
	public Solution(KnowledgeBase kb, String name) {
		super(kb, name);
	}

	/**
	 * Creates a new Solution, adds it to the knowledgebase and adds it to it's
	 * parent, so no manual adding of the created object to the kb is needed
	 * 
	 * @param parent the parent {@link Solution}
	 * @param name the name of the new QContainer
	 */
	public Solution(Solution parent, String name) {
		this(parent.getKnowledgeBase(), name);
		parent.addChild(this);
	}

	/**
	 * Returns the prior probability of this solution. The 'probability' is
	 * represented by a {@link Score}, and the use of this probability depends
	 * on the particular {@link PSMethod}.
	 * 
	 * @return the apripori probability
	 */
	public Score getAprioriProbability() {
		return aprioriProbability;
	}

	/**
	 * Sets the new apriori probability of this instance. The value is fixed to
	 * the predefined {@link Score} values: P5, P4, P3, P2, N2, N3, N4, N5.
	 * <p>
	 * Creation date: (25.09.00 15:13:34)
	 * 
	 * @param aprioriProbability the new apriori probability of this instance
	 * @throws IllegalArgumentException if the aprioriProbability is not
	 *         valid
	 */
	public void setAprioriProbability(Score aprioriProbability) throws IllegalArgumentException {
		// check if legal probability entry
		if (!Score.APRIORI.contains(aprioriProbability)
				&& (aprioriProbability != null)) {
			throw new IllegalArgumentException(aprioriProbability
					+ " not a valid apriori probability.");
		}
		else {
			this.aprioriProbability = aprioriProbability;
		}
	}

	@Override
	public Value getDefaultValue() {
		return new Rating(State.UNCLEAR);
	}

	public void addChild(Solution child) {
		super.addChild(child);
	}

	public boolean removeChild(Solution child) {
		return super.removeChild(child);
	}

}