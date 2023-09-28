/*
 * Copyright (C) 2012 denkbares GmbH, Germany
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
package de.d3web.xcl.inference;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.BasicProperties;

/**
 * Class that allows to add values based on a collection of keys. In contrast to
 * Map<List<K>, V>, it allows to use also different types of Collections (Set,
 * List, ...). Please note that the iteration order is also relevant. This Map
 * also allows to specify iteratively the keys and finally gets the value.
 *
 * @author volker_belli
 * @created 30.05.2012
 */
public class InformationPots<K> {

	private static class WeightSum {

		private float value = 0f;
	}

	private static final class MultiKey<K> {

		private K[] keys;
		private final int hashCode;

		public MultiKey(K[] keys) { // NOSONAR
			this.keys = keys;
			this.hashCode = Arrays.hashCode(keys);
		}

		public void makePersistent() {
			this.keys = Arrays.copyOf(this.keys, this.keys.length);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof MultiKey<?> other) {
				return (hashCode == other.hashCode)
						&& Arrays.equals(keys, other.keys);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return hashCode;
		}
	}

	private final Map<MultiKey<K>, WeightSum> map = new HashMap<>();
	private float totalWeight = 0;

	/**
	 * Adds a solution (or its weight to be more specific) to all pots possible.
	 * The possible pots are the pots for every combination of answers
	 * specified. The specified answers are a Collection of answers for a vector
	 * (ArrayList) of questions. The collection of answers may contain
	 * <code>null</code> that represents 'any normal value' of that question.
	 *
	 * @param solution the solution to add to the pots
	 * @param answers  all answers for all relevant questions
	 * @created 01.06.2012
	 */
	public void addWeights(Solution solution, List<? extends Collection<K>> answers) {
		addWeights(solution.getInfoStore().getValue(BasicProperties.APRIORI), answers);
	}

	/**
	 * Adds a solution weight to all pots possible. The possible pots are the
	 * pots for every combination of answers specified. The specified answers
	 * are a Collection of answers for a vector (ArrayList) of questions. The
	 * collection of answers may contain <code>null</code> that represents 'any
	 * normal value' of that question.
	 *
	 * @param weight  the weight to be added
	 * @param answers all answers for all relevant questions
	 * @created 01.06.2012
	 */
	public void addWeights(float weight, List<? extends Collection<K>> answers) {
		if (weight == 0f) return;
		totalWeight += weight;
		int size = answers.size();
		@SuppressWarnings("unchecked")
		K[] keys = (K[]) new Object[size];
		addWeights(weight, answers, size - 1, keys);
	}

	/**
	 * Recursively creates all combinations of answers into the specified array
	 * and adds the weight to the resulting pots.
	 *
	 * @param weight     the weight to be added
	 * @param allAnswers all possible answers for each question
	 * @param index      the current index to be processed
	 * @param result     the array to be used destructively
	 * @created 30.05.2012
	 */
	private void addWeights(float weight, List<? extends Collection<K>> allAnswers, int index, K[] result) {
		if (index == -1) {
			MultiKey<K> key = new MultiKey<>(result);
			WeightSum weightSum = map.get(key);
			if (weightSum == null) {
				weightSum = new WeightSum();
				key.makePersistent(); // require persistent copy
				map.put(key, weightSum);
			}
			weightSum.value += weight;
			return;
		}
		for (K answer : allAnswers.get(index)) {
			result[index] = answer;
			addWeights(weight, allAnswers, index - 1, result);
		}
	}

	public double getInformationGain() {
		// calculate information gain
		// Russel & Norvig p. 805
		double sum = 0;
		double all = 0;
		for (WeightSum weight : map.values()) {
			double p = (double) weight.value / totalWeight;
			sum += (-1) * p * Math.log10(p) / Math.log10(2);
			all += weight.value;
		}
		// because the pots are not disjoint (if excluding relations),
		// we have to normalize to the total value of all pots (because probability does not sum to 1.0)
		return sum * (totalWeight / all);
	}

	/**
	 * Returns the total weight of all solutions added yet to this
	 * InformationPots.
	 *
	 * @return the total weight added yet
	 * @created 01.06.2012
	 */
	public float getTotalWeight() {
		return totalWeight;
	}
}
