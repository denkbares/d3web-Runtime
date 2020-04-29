/*
 * Copyright (C) 2011 denkbares GmbH, Germany
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
package de.d3web.costbenefit.blackboard;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactAggregator;
import de.d3web.core.session.blackboard.FactStorage;

/**
 * This class is a fact storage that also provides the facts from an existing
 * underlying fact storage. All changes to this fact storage will not effect the
 * underlying one.
 * <p>
 * To do that, for every write operation (possibly changes the facts) we
 * recognize our covered objects. For every read operation, we either use our
 * aggregator object or the one from the delegated storage.
 * 
 * @author volker_belli
 * @created 16.09.2011
 */
public class DecoratedFactStorage implements FactStorage {

	private final Map<TerminologyObject, FactAggregator> mediators = new HashMap<>();
	private final FactStorage decorated;

	/**
	 * Creates a new DecoratedFactStorage that covers the specified fact storage.
	 *
	 * @param decoratedStorage the FactStorage to be covered.
	 */
	public DecoratedFactStorage(FactStorage decoratedStorage) {
		this.decorated = decoratedStorage;
	}

	@Override
	public DecoratedFactStorage copy() {
		// create a copy with the same decorated item and a copy of the existing aggregators
		DecoratedFactStorage copy = new DecoratedFactStorage(decorated);
		mediators.forEach((object, aggegator) -> copy.mediators.put(object, aggegator.copy()));
		return copy;
	}

	/**
	 * Ensures that the specified {@link TerminologyObject} uses our own fact storage instead of the covered one, after
	 * this method is called.
	 *
	 * @param terminologyObject the object to be covered
	 * @created 16.09.2011
	 */
	private FactAggregator makeCovered(TerminologyObject terminologyObject) {
		return mediators.computeIfAbsent(terminologyObject, object -> {
			FactAggregator aggregator = new FactAggregator();
			// thrill-seekers only: we only add the merged fact instead of all
			Fact existingFact = decorated.getMergedFact(object);
			if (existingFact != null) {
				aggregator.addFact(existingFact);
			}
			return aggregator;
		});
	}

	// -------
	// for write methods make sure to use the delegated storage from now
	// -------

	@Override
	public void add(Fact fact) {
		makeCovered(fact.getTerminologyObject()).addFact(fact);
	}

	@Override
	public void remove(Fact fact) {
		makeCovered(fact.getTerminologyObject()).removeFact(fact);
	}

	@Override
	public void remove(TerminologyObject termObject) {
		// remove termObject by overwrite with empty aggregator
		mediators.put(termObject, new FactAggregator());
	}

	@Override
	public void remove(TerminologyObject termObject, Object source) {
		makeCovered(termObject).removeFactsBySource(source);
	}

	// -------
	// for read methods check if to use our own storage or the delegated one
	// -------

	@Override
	public Fact getMergedFact(TerminologyObject termObject) {
		FactAggregator aggregator = mediators.get(termObject);
		return (aggregator != null)
				? aggregator.getMergedFact()
				: decorated.getMergedFact(termObject);
	}

	@Override
	public Collection<Fact> getAllFacts(TerminologyObject termObject) {
		FactAggregator aggregator = mediators.get(termObject);
		return (aggregator != null)
				? Collections.unmodifiableCollection(aggregator.getAllFacts())
				: decorated.getAllFacts(termObject);
	}

	@Override
	public Fact getMergedFact(TerminologyObject termObject, PSMethod psMethod) {
		FactAggregator aggregator = mediators.get(termObject);
		return (aggregator != null)
				? aggregator.getMergedFact(psMethod)
				: decorated.getMergedFact(termObject, psMethod);
	}

	@Override
	public Fact getFact(TerminologyObject termObject, PSMethod psMethod, Object source) {
		FactAggregator aggregator = mediators.get(termObject);
		return (aggregator != null)
				? aggregator.getFact(psMethod, source)
				: decorated.getFact(termObject, psMethod, source);
	}

	@Override
	public boolean hasFact(TerminologyObject termObject) {
		FactAggregator aggregator = mediators.get(termObject);
		return (aggregator != null)
				? !aggregator.isEmpty()
				: decorated.hasFact(termObject);
	}

	@Override
	public boolean hasFact(TerminologyObject termObject, PSMethod method) {
		FactAggregator aggregator = mediators.get(termObject);
		return (aggregator != null)
				? aggregator.hasFacts(method)
				: decorated.hasFact(termObject, method);
	}

	@Override
	public Collection<TerminologyObject> getValuedObjects() {
		Collection<TerminologyObject> self = mediators.keySet();
		Collection<TerminologyObject> other = decorated.getValuedObjects();
		Set<TerminologyObject> allObjects = new HashSet<>((self.size() + other.size()) * 3 / 2);
		allObjects.addAll(self);
		allObjects.addAll(other);
		return allObjects;
	}

	@Override
	public Collection<PSMethod> getContributingPSMethods(TerminologyObject termObject) {
		FactAggregator aggregator = mediators.get(termObject);
		return (aggregator != null)
				? aggregator.getContributingPSMethods()
				: decorated.getContributingPSMethods(termObject);
	}

	/**
	 * Returns the merged fact of this {@link DecoratedFactStorage} or (transitively) the first decorated instance in
	 * the sequence. In contrast to {@link #getMergedFact(TerminologyObject)}, it does not return the merged fact of the
	 * underlying original session.
	 *
	 * @param object the object to get the merged fact for
	 * @return the merged fact or null if it has not been decorated
	 * @created 05.06.2012
	 */
	public Fact getDecoratedMergedFact(ValueObject object) {
		FactAggregator aggregator = mediators.get(object);
		if (aggregator != null) {
			return aggregator.getMergedFact();
		}
		if (decorated instanceof DecoratedFactStorage) {
			return ((DecoratedFactStorage) decorated).getDecoratedMergedFact(object);
		}
		return null;
	}
}
