/*
 * Copyright (C) 2017 denkbares GmbH. All rights reserved.
 */

package de.d3web.core.manage;

import java.util.Comparator;

import com.denkbares.collections.MultiMap;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.scoring.HeuristicRating;

/**
 * Comparator that sorts solutions according to their rating. It optionally allows a fine-sorting, that orders
 * same-rated solutions by a specified comparator. By default, the fine-sorting is according to the tree-position within
 * the solution hierarchy.
 */
public class SolutionComparator implements Comparator<Solution> {

	private final Session session;
	private final Comparator<Solution> fineSorting;

	public SolutionComparator(Session session) {
		this(session, SolutionComparator::compareTreeIndex);
	}

	public SolutionComparator(Session session, Comparator<Solution> fineSorting) {
		this.session = session;
		this.fineSorting = fineSorting;
	}

	@Override
	public int compare(Solution o1, Solution o2) {
		int comparison = compareRating(o1, o2);
		return (comparison != 0 || fineSorting == null) ? comparison : fineSorting.compare(o1, o2);
	}

	public int compareGroups(Solution group1, Solution group2, MultiMap<Solution, Solution> groups) {
		int comparison = compareRating(groups.getAnyValue(group1), groups.getAnyValue(group2));
		return (comparison != 0 || fineSorting == null) ? comparison : fineSorting.compare(group1, group2);
	}

	private int compareRating(Solution o1, Solution o2) {
		Rating rating1 = session.getBlackboard().getRating(o1);
		Rating rating2 = session.getBlackboard().getRating(o2);
		int comparison = rating2.compareTo(rating1);
		if (comparison == 0 && rating1 instanceof HeuristicRating && rating2 instanceof HeuristicRating) {
			// solutions with higher score should also be shown higher
			comparison = -Double.compare(((HeuristicRating) rating1).getScore(), ((HeuristicRating) rating2).getScore());
		}
		return comparison;
	}

	private static int compareTreeIndex(Solution o1, Solution o2) {
		return Integer.compare(
				o1.getKnowledgeBase().getManager().getTreeIndex(o1),
				o2.getKnowledgeBase().getManager().getTreeIndex(o2));
	}
}
