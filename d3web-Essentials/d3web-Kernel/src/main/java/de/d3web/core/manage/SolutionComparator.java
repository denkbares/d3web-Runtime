/*
 * Copyright (C) 2017 denkbares GmbH. All rights reserved.
 */

package de.d3web.core.manage;

import java.util.Comparator;

import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.scoring.HeuristicRating;

public class SolutionComparator implements Comparator<Solution> {

	private final Session session;

	public SolutionComparator(Session session) {
		this.session = session;
	}

	@Override
	public int compare(Solution o1, Solution o2) {
		Rating rating1 = session.getBlackboard().getRating(o1);
		Rating rating2 = session.getBlackboard().getRating(o2);
		int comparison = rating2.compareTo(rating1);
		if (comparison == 0 && rating1 instanceof HeuristicRating && rating2 instanceof HeuristicRating) {
			// solutions with higher score should also be shown higher
			comparison = -Double.compare(((HeuristicRating) rating1).getScore(), ((HeuristicRating) rating2).getScore());
		}
		if (comparison == 0) {
			comparison = Integer.compare(session.getKnowledgeBase()
					.getManager()
					.getTreeIndex(o1), session.getKnowledgeBase().getManager().getTreeIndex(o2));
		}
		return comparison;
	}
}
