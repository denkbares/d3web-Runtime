/*
 * Copyright (C) 2010 denkbares GmbH, Würzburg, Germany
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

package de.d3web.core.session.blackboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.session.Copyable;

/**
 * This class organizes the facts of one type for one terminology object. It is
 * capable to merge different opinions (facts from different solvers) to one
 * final value of the terminology object.
 * <p>
 * <b>Note:<br>
 * This class is for internal purpose only. Until you do not provide an own
 * blackboard implementation, do not use this class directly! </b>
 * 
 * @author Volker Belli (denkbares GmbH)
 */
final class FactAggregator implements Copyable<FactAggregator> {

	private final List<Fact> facts;
	private Fact mergedFact;

	public FactAggregator() {
		this.facts = new ArrayList<>();
		this.mergedFact = null;
	}

	/**
	 * Copy constructor to create a deep copy of this instance.
	 * 
	 * @param source the source instance to be copied
	 */
	private FactAggregator(FactAggregator source) {
		this.facts = new ArrayList<>(source.facts);
		this.mergedFact = source.mergedFact;
	}

	@Override
	public FactAggregator copy() {
		// use copy constructor
		return new FactAggregator(this);
	}

	/**
	 * Adds a new fact to the aggregator. If a fact with the same source as the
	 * new fact already exists, it will be overwritten.
	 * 
	 * @param fact the fact to be added
	 */
	public void addFact(Fact fact) {
		// first make sure that the source remains unique
		removeFactsBySource(fact.getSource());
		this.facts.add(fact);
		invalidate();
	}

	/**
	 * Removes the specified fact from this aggregator.
	 * 
	 * @param fact the fact to be removed
	 */
	public void removeFact(Fact fact) {
		this.facts.remove(fact);
		invalidate();
	}

	/**
	 * Removes all facts from this aggregator.
	 */
	public void clear() {
		this.facts.clear();
		invalidate();
	}

	/**
	 * Removes the fact for the specified source from this aggregator.
	 * 
	 * @param source the source to remove facts for
	 */
	public void removeFactsBySource(Object source) {
		Iterator<Fact> iterator = facts.iterator();
		while (iterator.hasNext()) {
			Fact fact = iterator.next();
			if (source.equals(fact.getSource())) {
				iterator.remove();
				invalidate();
				// source can only be found once (!),
				// because source is unique
				break;
			}
		}
	}

	/**
	 * Returns the merged fact of all currently added facts of this aggregator.
	 * 
	 * @return the merged fact
	 */
	public Fact getMergedFact() {
		if (this.mergedFact == null) {
			mergeFacts();
		}
		return this.mergedFact;
	}

	/**
	 * Returns the merged fact of all currently added facts of this aggregator.
	 * 
	 * @return the merged fact
	 */
	public Collection<Fact> getAllFacts() {
		return Collections.unmodifiableList(facts);
	}

	private void invalidate() {
		this.mergedFact = null;
	}

	private void mergeFacts() {
		// do nothing if we do not have any facts
		if (facts.isEmpty()) {
			return;
		}

		// use the one fact if there is only one
		if (facts.size() == 1) {
			this.mergedFact = this.facts.get(0);
			return;
		}

		// merge the facts if they are all of the same problem solver
		// (normal case for solutions)
		boolean hasUniquePSMethod = true;
		PSMethod psm = facts.get(0).getPSMethod();
		for (int i = 1; i < facts.size(); i++) {
			if (facts.get(i).getPSMethod() != psm) {
				hasUniquePSMethod = false;
				break;
			}
		}

		if (hasUniquePSMethod) {
			// if we have only one PSMethod, merge all facts by this one
			this.mergedFact = psm.mergeFacts(facts.toArray(new Fact[0]));
			return;
		}

		// otherwise we have to do the complex merge operation
		// so we build a set of fact collections
		Map<PSMethod, Collection<Fact>> factBoxes = new HashMap<>();
		for (Fact fact : this.facts) {
			PSMethod key = fact.getPSMethod();
			factBoxes.computeIfAbsent(key, k -> new ArrayList<>()).add(fact);
		}
		// and collect them to single facts
		List<Fact> mergedFacts = new ArrayList<>();
		for (Collection<Fact> box : factBoxes.values()) {
			Fact[] factArray = box.toArray(new Fact[0]);
			Fact newMergedFact = (factArray.length == 1)
					? factArray[0] // if we only have one, no merge is required
					: factArray[0].getPSMethod().mergeFacts(factArray);
			mergedFacts.add(newMergedFact);
		}
		// and find best of them, depending on the value type
		Object exampleValue = this.facts.get(0).getValue();
		if (exampleValue instanceof Rating) {
			this.mergedFact = mergeSolutionFacts(mergedFacts);
		}
		else if (exampleValue instanceof Indication) {
			this.mergedFact = mergeIndicationFacts(mergedFacts);
		}
		else {
			this.mergedFact = mergeFactsByPriority(mergedFacts);
		}

	}

	/**
	 * Merges the facts by the priority of the PSMethods derived that facts.
	 * This method if used, if the facts does not have a priority of their own,
	 * such as Answers.
	 * 
	 * @param mergedFacts the facts to be merged
	 * @return the fact with the highest PSMethod's priority
	 */
	private Fact mergeFactsByPriority(List<Fact> mergedFacts) {
		Fact bestFact = null;
		double bestPSMPriority = Double.MAX_VALUE;
		for (Fact fact : mergedFacts) {
			PSMethod psMethod = fact.getPSMethod();
			double priority = psMethod.getPriority();
			if (priority < bestPSMPriority) {
				bestFact = fact;
				bestPSMPriority = priority;
			}
		}
		return bestFact;
	}

	/**
	 * Merges the facts of solution ratings by the priority of the ratings. In
	 * addition it is considered, that the source solvers (e.g. user) have a
	 * higher priority as other solvers.
	 * 
	 * @param facts the facts to be merged
	 * @return the fact with the highest priority
	 */
	private Fact mergeSolutionFacts(List<Fact> facts) {
		return Facts.mergeSolutionFacts(facts);
	}

	/**
	 * Merges the facts of indications by the priority of the ratings. In
	 * addition it is considered, that the source solvers (e.g. user) have a
	 * higher priority as other solvers.
	 * 
	 * @param facts the facts to be merged
	 * @return the fact with the highest priority
	 */
	private Fact mergeIndicationFacts(List<Fact> facts) {
		return Facts.mergeIndicationFacts(facts);
	}

	/**
	 * Returns whether the aggregator is empty due to having no facts.
	 * 
	 * @return whether the aggregator is empty
	 */
	public boolean isEmpty() {
		return this.facts.isEmpty();
	}

	/**
	 * Returns whether the aggregator is empty for a specific problem or
	 * stratgic solver due to having no facts from this solver.
	 * 
	 * @param psMethod the solver to check for facts
	 * @return whether the aggregator is empty
	 */
	public boolean isEmpty(PSMethod psMethod) {
		for (Fact f : facts) {
			if (f.getPSMethod().equals(psMethod)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns whether the aggregator has facts for a specific problem or
	 * stratgic solver.
	 * 
	 * @param psMethod the solver to check for facts
	 * @return if there are facts for the solver
	 */
	public final boolean hasFacts(PSMethod psMethod) {
		return !isEmpty(psMethod);
	}

	/**
	 * Merges and returns all facts of one psmethod
	 * 
	 * @return merged Fact
	 */
	public Fact getMergedFact(PSMethod psMethod) {
		List<Fact> psmfacts = new LinkedList<>();
		for (Fact f : facts) {
			if (f.getPSMethod().equals(psMethod)) {
				psmfacts.add(f);
			}
		}
		if (psmfacts.size() == 1) {
			return psmfacts.get(0);
		}
		else if (psmfacts.size() > 1) {
			return psMethod.mergeFacts(psmfacts.toArray(new Fact[0]));
		}
		else {
			return null;
		}
	}

	@Override
	public String toString() {
		Fact fact = getMergedFact();
		return "[FactAggregator " + (fact != null ? fact.getValue() : "null") + "]";
	}

	public Fact getFact(PSMethod psmethod, Object source) {
		for (Fact fact : facts) {
			if (source.equals(fact.getSource()) && psmethod.equals(fact.getPSMethod())) {
				return fact;
			}
		}
		return null;
	}

	/**
	 * Returns a collection of all problem and strategic solvers that are
	 * contributing at least one fact to this FactAggregator.
	 * 
	 * @created 02.09.2011
	 * @return the contributing solver instances
	 */
	public Collection<PSMethod> getContributingPSMethods() {
		Set<PSMethod> result = new HashSet<>();
		for (Fact fact : facts) {
			result.add(fact.getPSMethod());
		}
		return result;
	}
}
