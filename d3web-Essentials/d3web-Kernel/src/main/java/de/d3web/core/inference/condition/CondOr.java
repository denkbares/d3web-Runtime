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

package de.d3web.core.inference.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.denkbares.strings.Strings;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.session.Session;
import de.d3web.core.session.ValueUtils;
import de.d3web.core.session.values.ChoiceID;

/**
 * Implements an "OR"-condition, where at least one sub-condition has to be true. The composite pattern is used for
 * this. This class is a "composite".
 *
 * @author Michael Wolber, joba
 */
public class CondOr extends NonTerminalCondition {

//	private final transient Condition optimized;

	/**
	 * Creates a new OR-condition with a list of disjunctive sub-conditions.
	 */
	public CondOr(Collection<Condition> terms) {
		super(terms);
//		this.optimized = optimize(terms);
	}

	/**
	 * Creates a new OR-condition with a list of disjunctive sub-conditions.
	 */
	public CondOr(Condition... terms) {
		this(Arrays.asList(terms));
	}

	/**
	 * Returns an optimized condition that simulates an "or" of the specified terms. The method returns null, if no
	 * optimization could be computed.
	 *
	 * @param originalTerms the disjunction terms of the or condition
	 * @return the optimized condition, or null
	 */
	@Nullable
	public static Condition optimize(Collection<Condition> originalTerms) {
		ArrayList<Condition> terms = new ArrayList<>(originalTerms);

		// get all choice conditions for the same question
		Map<QuestionChoice, List<CondEqual>> unifiables = new LinkedHashMap<>();
		for (Condition condition : terms) {
			if (condition instanceof CondEqual) {
				CondEqual condEqual = (CondEqual) condition;
				Question question = condEqual.getQuestion();
				if (question instanceof QuestionChoice && !ValueUtils.getChoiceIDs(condEqual.getValue()).isEmpty()) {
					unifiables.computeIfAbsent((QuestionChoice) question, k -> new ArrayList<>()).add(condEqual);
				}
			}
		}

		// create new condition, by replacing tests for multiple choices into one condition
		unifiables.forEach((question, subset) -> {
			if (subset.size() >= 2) {
				terms.removeAll(subset);
				List<ChoiceID> choices = subset.stream().map(CondEqual::getValue).map(ValueUtils::getChoiceIDs)
						.flatMap(Collection::stream).collect(Collectors.toList());
				terms.add(new CondAnyChoice(question, choices));
			}
		});

		if (terms.size() == 1) return terms.get(0);
		return (terms.size() < originalTerms.size()) ? new CondOr(terms) : null;
	}

	@Override
	public boolean eval(Session session) throws NoAnswerException, UnknownAnswerException {
//		if (optimized != null) {
//			return optimized.eval(session);
//		}

		boolean wasNoAnswer = false;
		boolean wasUnknownAnswer = false;

		for (Condition condition : getTerms()) {
			try {
				if (condition.eval(session)) {
					return true;
				}
			}
			catch (NoAnswerException nae) {
				wasNoAnswer = true;
			}
			catch (UnknownAnswerException uae) {
				wasUnknownAnswer = true;
			}
		}
		if (wasNoAnswer) {
			throw NoAnswerException.getInstance();
		}

		if (wasUnknownAnswer) {
			throw UnknownAnswerException.getInstance();
		}

		return false;
	}

	@Override
	public String toString() {
		return "(" + Strings.concat(" OR ", getTerms()) + ")";
	}
}
