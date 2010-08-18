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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.d3web.core.knowledge.TerminologyObject;

/**
 * This class handles a dynamic set of {@link FactAggregator}s.
 * 
 * @author volker_belli
 * 
 */
class FactStorage {

	private final Blackboard blackboard;
	private final Map<TerminologyObject, FactAggregator> mediators = new HashMap<TerminologyObject, FactAggregator>();
	private final Set<TerminologyObject> noFactObjects = new HashSet<TerminologyObject>();

	public FactStorage(Blackboard blackboard) {
		this.blackboard = blackboard;
	}

	public FactAggregator getAggregator(TerminologyObject termObject) {
		FactAggregator aggregator = this.mediators.get(termObject);
		if (aggregator == null) {
			aggregator = new FactAggregator(this.blackboard);
			this.mediators.put(termObject, aggregator);
		}
		return aggregator;
	}

	/**
	 * Adds a new fact to this storage. If an other fact for the same
	 * terminology object and with the same source has already been added, that
	 * fact will be replaced by the specified one.
	 * 
	 * @param fact the fact to be added
	 */
	public void add(Fact fact) {
		TerminologyObject terminologyObject = fact.getTerminologyObject();
		getAggregator(terminologyObject).addFact(fact);
		// this terminology object now has facts
		this.noFactObjects.remove(terminologyObject);
	}

	/**
	 * Removes a fact from this storage. If the fact does not exists in the
	 * storage, this method has no effect.
	 * 
	 * @param fact the fact to be removed
	 */
	public void remove(Fact fact) {
		TerminologyObject terminologyObject = fact.getTerminologyObject();
		FactAggregator mediator = getAggregator(terminologyObject);
		mediator.removeFact(fact);
		// check if the mediator has become empty, then remember the terminology
		// object
		if (mediator.isEmpty()) {
			this.noFactObjects.add(terminologyObject);
		}
	}

	/**
	 * Removes all facts with the specified source from this storage for the
	 * specified terminology object. If no such fact exists in the storage, this
	 * method has no effect.
	 * 
	 * @param termObject the terminology object to remove the facts from
	 * @param source the fact source to be removed
	 */
	public void remove(TerminologyObject termObject, Object source) {
		FactAggregator aggreagator = getAggregator(termObject);
		aggreagator.removeFactsBySource(source);
		// check if the aggreagator has become empty, then remember the
		// terminology object
		if (aggreagator.isEmpty()) {
			this.noFactObjects.add(termObject);
		}
	}

	/**
	 * Removes all facts from this storage for the specified terminology object.
	 * If no such fact exists in the storage, this method has no effect.
	 * 
	 * @param termObject the terminology object to remove the facts from
	 * @param source the fact source to be removed
	 */
	public void remove(TerminologyObject termObject) {
		FactAggregator aggreagator = getAggregator(termObject);
		aggreagator.clear();
		// then remember the terminology object to be empty
		this.noFactObjects.add(termObject);
	}

	/**
	 * cleans up the mediators hashtable from unused entries.
	 */
	private void cleanupMediators() {
		for (TerminologyObject object : this.noFactObjects) {
			this.mediators.remove(object);
		}
		this.noFactObjects.clear();
	}

	/**
	 * Returns a list of all terminology objects that have been rated with
	 * facts.
	 * 
	 * @return the list of rated terminology objects
	 */
	public Collection<TerminologyObject> getValuedObjects() {
		cleanupMediators();
		return this.mediators.keySet();
	}
}