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
import java.util.HashSet;
import java.util.Set;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.blackboard.DefaultFactStorage;
import de.d3web.core.session.blackboard.Fact;
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

	private final FactStorage covering = new DefaultFactStorage();
	private final FactStorage decorated;
	private final Set<TerminologyObject> coveredObjects = new HashSet<TerminologyObject>();

	/**
	 * Creates a new DecoratedFactStorage that covers the specified fact
	 * storage.
	 * 
	 * @param storage the FactStorage to be covered.
	 */
	public DecoratedFactStorage(FactStorage decoratedStorage) {
		this.decorated = decoratedStorage;
	}

	/**
	 * Ensures that the specified {@link TerminologyObject} uses our own fact
	 * storage instead of the covered one, after this method is called.
	 * 
	 * @created 16.09.2011
	 * @param terminologyObject the object to be covered
	 */
	private void makeCovered(TerminologyObject terminologyObject) {
		boolean isNew = coveredObjects.add(terminologyObject);
		if (isNew) {
			// thrill-seekers only: we only add the merged fact instead of all
			Fact existingFact = decorated.getMergedFact(terminologyObject);
			if (existingFact != null) {
				this.covering.add(existingFact);
			}
			// Collection<Fact> allFacts =
			// this.decorated.getAllFacts(terminologyObject);
			// for (Fact fact : allFacts) {
			// this.covering.add(fact);
			// }
		}
	}

	private boolean isCovered(TerminologyObject terminologyObject) {
		return coveredObjects.contains(terminologyObject);
	}

	// -------
	// for write methods make sure to use the delegated storage from now
	// -------

	@Override
	public void add(Fact fact) {
		makeCovered(fact.getTerminologyObject());
		covering.add(fact);
	}

	@Override
	public void remove(Fact fact) {
		makeCovered(fact.getTerminologyObject());
		covering.remove(fact);
	}

	@Override
	public void remove(TerminologyObject termObject) {
		makeCovered(termObject);
		covering.remove(termObject);
	}

	@Override
	public void remove(TerminologyObject termObject, Object source) {
		makeCovered(termObject);
		covering.remove(termObject, source);
	}

	// -------
	// for read methods check if to use our own storage or the delegated one
	// -------

	@Override
	public Fact getMergedFact(TerminologyObject termObject) {
		return isCovered(termObject)
				? covering.getMergedFact(termObject)
				: decorated.getMergedFact(termObject);
	}

	@Override
	public Collection<Fact> getAllFacts(TerminologyObject termObject) {
		return isCovered(termObject)
				? covering.getAllFacts(termObject)
				: decorated.getAllFacts(termObject);
	}

	@Override
	public Fact getMergedFact(TerminologyObject termObject, PSMethod psMethod) {
		return isCovered(termObject)
				? covering.getMergedFact(termObject, psMethod)
				: decorated.getMergedFact(termObject, psMethod);
	}

	@Override
	public Fact getFact(TerminologyObject termObject, PSMethod psMethod, Object source) {
		return isCovered(termObject)
				? covering.getFact(termObject, psMethod, source)
				: decorated.getFact(termObject, psMethod, source);
	}

	@Override
	public boolean hasFact(TerminologyObject termObject) {
		return isCovered(termObject)
				? covering.hasFact(termObject)
				: decorated.hasFact(termObject);
	}

	@Override
	public boolean hasFact(TerminologyObject termObject, PSMethod method) {
		return isCovered(termObject)
				? covering.hasFact(termObject, method)
				: decorated.hasFact(termObject, method);
	}

	@Override
	public Collection<TerminologyObject> getValuedObjects() {
		Set<TerminologyObject> allObjects = new HashSet<TerminologyObject>(
				decorated.getValuedObjects());
		allObjects.addAll(covering.getValuedObjects());
		return allObjects;
	}

	@Override
	public Collection<PSMethod> getContributingPSMethods(TerminologyObject termObject) {
		return isCovered(termObject)
				? covering.getContributingPSMethods(termObject)
				: decorated.getContributingPSMethods(termObject);
	}

}
