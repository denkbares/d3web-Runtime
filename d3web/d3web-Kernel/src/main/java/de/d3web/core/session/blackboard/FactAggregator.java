package de.d3web.core.session.blackboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.indication.inference.PSMethodUserSelected;

/**
 * This class organizes the facts of one type for one terminology object. It is
 * capable to merge different opinions (facts from different solvers) to one
 * final value of the terminology object.
 * 
 * @author volker_belli
 * 
 */
class FactAggregator {

	private final Blackboard blackboard;
	private final List<Fact> facts = new ArrayList<Fact>();
	private Fact mergedFact = null;

	public FactAggregator(Blackboard blackboard) {
		this.blackboard = blackboard;
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
	 * 
	 * @param fact the fact to be removed
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

	private void invalidate() {
		this.mergedFact = null;
	}

	private void mergeFacts() {
		// do nothing if we do not have any facts
		if (facts.size() == 0) return;

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
			this.mergedFact = psm.mergeFacts(facts.toArray(new Fact[facts.size()]));
			return;
		}

		// otherwise we have to do the complex merge operation
		// so we build a set of fact collections
		Map<PSMethod, Collection<Fact>> factBoxes = new HashMap<PSMethod, Collection<Fact>>();
		for (Fact fact : this.facts) {
			PSMethod key = fact.getPSMethod();
			Collection<Fact> box = factBoxes.get(key);
			if (box == null) {
				box = new ArrayList<Fact>();
				factBoxes.put(key, box);
			}
			box.add(fact);
		}
		// and collect them to single facts
		List<Fact> mergedFacts = new ArrayList<Fact>();
		for (Collection<Fact> box : factBoxes.values()) {
			Fact[] facts = box.toArray(new Fact[box.size()]);
			Fact mergedFact = (facts.length == 1)
					? facts[0] // if we only have one, no merge is required
					: facts[0].getPSMethod().mergeFacts(facts);
			mergedFacts.add(mergedFact);
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
		int bestPSMIndex = Integer.MAX_VALUE;
		for (Fact fact : mergedFacts) {
			// user wins it all
			// TODO: check for "source solvers" instead of user
			// TODO: shall we consider to have multiple source solvers?
			PSMethod psMethod = fact.getPSMethod();
			if (psMethod instanceof PSMethodUserSelected) {
				return fact;
			}
			int index = this.blackboard.getSession().getPSMethods().indexOf(psMethod);
			if (index < bestPSMIndex) {
				bestFact = fact;
				bestPSMIndex = index;
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
	 * Merges and returns all facts of one psmethod
	 * 
	 * @param psMethod
	 * @return merged Fact
	 */
	public Fact getMergedFact(PSMethod psMethod) {
		List<Fact> psmfacts = new LinkedList<Fact>();
		for (Fact f : facts) {
			if (f.getPSMethod().equals(psMethod)) {
				psmfacts.add(f);
			}
		}
		if (psmfacts.size() > 0) {
			return psMethod.mergeFacts(psmfacts.toArray(new Fact[psmfacts.size()]));
		}
		else {
			return null;
		}
	}

}
