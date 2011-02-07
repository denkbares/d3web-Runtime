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

package de.d3web.scoring.inference;

import java.util.Collection;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSMethodAdapter;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.AbstractTerminologyObject;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.scoring.HeuristicRating;
import de.d3web.scoring.Score;

/**
 * Heuristic problem-solver which adds scores to diagnoses on the basis of
 * question values. If score of a diagnosis exceeds a threshold value, then this
 * diagnosis will be suggested/established/excluded.
 * <p>
 * <B>Currently implemented strategies:</B>
 * <P>
 * <B>SFA</B>: single fault assumption:<BR>
 * If a diagnosis established, then quit the case instantly (feature
 * "continue case" is available) All other diagnoses that are also suggested,
 * are returned to be "suggested".
 * <P>
 * <B>Best Solution Only</B> Only return the best established solution as
 * "established"; all other established diagnoses are returned to be "suggested"
 * 
 * Creation date: (28.08.00 18:04:09)
 * 
 * @author joba
 */
public final class PSMethodHeuristic extends PSMethodAdapter {

	private static PSMethodHeuristic instance = null;

	private PSMethodHeuristic() {
		super();
	}

	/**
	 * Creation date: (04.12.2001 12:36:25)
	 * 
	 * @return the one and only instance of this ps-method (Singleton)
	 */
	public static PSMethodHeuristic getInstance() {
		if (instance == null) {
			instance = new PSMethodHeuristic();
		}
		return instance;
	}

	/**
	 * Check if AbstractTerminologyObject has nextQASet rules and check them, if available
	 */
	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {
		for (PropagationEntry change : changes) {
			// do not handle strategic changes
			if (!change.isStrategic()) checkRulesFor(session, change.getObject());
		}
	}

	/**
	 * @param session
	 * @param nob
	 */
	private void checkRulesFor(Session session, TerminologyObject nob) {
		KnowledgeSlice knowledgeSlices = ((AbstractTerminologyObject) nob).getKnowledge(this.getClass(),
				MethodKind.FORWARD);
		if (knowledgeSlices != null) {
			RuleSet rs = (RuleSet) knowledgeSlices;
			for (Rule rule : rs.getRules()) {
				rule.check(session);
			}
		}
	}

	@Override
	public String toString() {
		return "heuristic problem-solver";
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {

		long resultTime = Long.MIN_VALUE;

		HeuristicRating[] ratings = new HeuristicRating[facts.length];
		for (int i = 0; i < facts.length; i++) {
			ratings[i] = (HeuristicRating) facts[i].getValue();
			resultTime = Math.max(resultTime, facts[i].getTime());
		}
		Solution terminologyObject = (Solution) facts[0].getTerminologyObject();
		Score aprioriProbability = terminologyObject.getAprioriProbability();
		Value value = HeuristicRating.add(aprioriProbability, ratings);

		return new DefaultFact(terminologyObject, value, resultTime, this, this);
	}

	@Override
	public boolean hasType(Type type) {
		return type == Type.problem;
	}
}