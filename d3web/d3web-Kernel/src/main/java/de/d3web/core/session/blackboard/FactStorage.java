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
import java.util.List;
import java.util.Map;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.utilities.NamedObjectComparator;

/**
 * This class handles a dynamic set of {@link FactAggregator}s.
 * 
 * @author volker_belli
 * 
 */
final class FactStorage {

	private final Map<TerminologyObject, FactAggregator> mediators = new HashMap<TerminologyObject, FactAggregator>();

	/**
	 * Returns the {@link FactAggregator} for a specified terminology object. If
	 * no such aggregator exists yet,a new one is created (lazy).
	 * 
	 * @created 01.05.2011
	 * @param termObject the object to access the aggregator for
	 * @return the aggregator for the object
	 */
	public FactAggregator getAggregator(TerminologyObject termObject) {
		FactAggregator aggregator = this.mediators.get(termObject);
		if (aggregator == null) {
			aggregator = new FactAggregator();
			this.mediators.put(termObject, aggregator);
		}
		return aggregator;
	}

	/**
	 * Returns the merged fact if there are any facts available. This method
	 * return null is no fact is available.
	 * 
	 * @created 30.04.2011
	 * @param object the object to look for the merged fact
	 */
	public Fact getMergedFact(TerminologyObject termObject) {
		FactAggregator aggregator = this.mediators.get(termObject);
		if (aggregator != null) {
			return aggregator.getMergedFact();
		}
		return null;
	}

	/**
	 * Returns the merged fact for a specific problem or strategic solver if
	 * there are any facts available. This method return null is no fact is
	 * available for that solver.
	 * 
	 * @created 30.04.2011
	 * @param object the object to look for the merged fact
	 */
	public Fact getMergedFact(TerminologyObject termObject, PSMethod psMethod) {
		FactAggregator aggregator = this.mediators.get(termObject);
		if (aggregator != null) {
			return aggregator.getMergedFact(psMethod);
		}
		return null;
	}

	/**
	 * Returns the fact for a specific problem or strategic solver and a
	 * specific source. This method return null is no fact on the specified
	 * terminology object is available for that solver and that source.
	 * <p>
	 * Note that the fact must be unique for a solver and source, therefore
	 * merging facts is not needed at all.
	 * 
	 * @created 30.04.2011
	 * @param object the object to look for the merged fact
	 */
	public Fact getFact(TerminologyObject termObject, PSMethod psMethod, Object source) {
		FactAggregator aggregator = this.mediators.get(termObject);
		if (aggregator != null) {
			return aggregator.getFact(psMethod, source);
		}
		return null;
	}

	/**
	 * Returns if there are any facts available for the specified
	 * {@link TerminologyObject}.
	 * 
	 * @created 30.04.2011
	 * @param termObject the object to look for facts
	 * @return if there is at least one fact
	 */
	public boolean hasFact(TerminologyObject termObject) {
		FactAggregator aggregator = this.mediators.get(termObject);
		if (aggregator != null) {
			return !aggregator.isEmpty();
		}
		return false;
	}

	/**
	 * Returns if there are any facts available for the specified
	 * {@link TerminologyObject} derived by the specified problem or strategic
	 * solver.
	 * 
	 * @created 30.04.2011
	 * @param termObject the object to look for facts
	 * @param method the solver to search for facts
	 * @return if there is at least one fact
	 */
	public boolean hasFact(TerminologyObject termObject, PSMethod method) {
		FactAggregator aggregator = this.mediators.get(termObject);
		if (aggregator != null) {
			return aggregator.hasFacts(method);
		}
		return false;
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
	}

	/**
	 * Removes a fact from this storage. If the fact does not exists in the
	 * storage, this method has no effect.
	 * 
	 * @param fact the fact to be removed
	 */
	public void remove(Fact fact) {
		TerminologyObject terminologyObject = fact.getTerminologyObject();
		FactAggregator aggregator = this.mediators.get(terminologyObject);
		if (aggregator != null) {
			aggregator.removeFact(fact);
			// check if the aggregtor has become empty remove it
			if (aggregator.isEmpty()) {
				this.mediators.remove(terminologyObject);
			}
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
		FactAggregator aggregator = this.mediators.get(termObject);
		if (aggregator != null) {
			aggregator.removeFactsBySource(source);
			// check if the aggregtor has become empty remove it
			if (aggregator.isEmpty()) {
				this.mediators.remove(termObject);
			}
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
		this.mediators.remove(termObject);
	}

	/**
	 * Returns a list of all terminology objects that have been rated with
	 * facts.
	 * 
	 * @return the list of rated terminology objects
	 */
	public Collection<TerminologyObject> getValuedObjects() {
		return this.mediators.keySet();
	}

	/**
	 * Returns a collection of all problem and strategic solvers that are
	 * contributing at least one fact to the specified term objects.
	 * 
	 * @created 02.09.2011
	 * @param termObject the object to get the contributing solvers for
	 * @return the contributing solver instances
	 */
	public Collection<PSMethod> getContributingPSMethods(TerminologyObject termObject) {
		FactAggregator aggregator = this.mediators.get(termObject);
		if (aggregator != null) {
			return aggregator.getContributingPSMethods();
		}
		return Collections.emptySet();
	}

	/**
	 * Creates a "core dump" of the information contained in this FactStorage.
	 * The dump ensures that equal FactStorages creates the same dumps (e.g. the
	 * identical order of the information). Because of NOT containing every
	 * possible information, it is not guaranteed that two FactStorage with same
	 * dump may differ in some information (e.g. facts, hidden by other ones).
	 * <p>
	 * The following information are contained in the dump:
	 * <ul>
	 * <li>merged value facts for all {@link TerminologyObject}s that have facts
	 * <li>the single facts derived by each problem solver before they are
	 * merged globally
	 * </ul>
	 * <p>
	 * This method may be helpful for debugging and testing purposes.
	 * 
	 * @created 03.09.2011
	 * @return the dumped information
	 */
	public String dump() {
		List<TerminologyObject> objects = new ArrayList<TerminologyObject>(mediators.keySet());
		Collections.sort(objects, new NamedObjectComparator());
		StringBuilder result = new StringBuilder();
		for (TerminologyObject object : objects) {
			FactAggregator aggregator = getAggregator(object);

			// print object and merged fact
			result.append(object).append("\t= ");
			Fact fact = aggregator.getMergedFact();
			String psmName = fact.getPSMethod().getClass().getSimpleName();
			result.append(fact.getValue()).append(" [").append(psmName).append("]");

			// print also facts for each contributing psm
			Collection<PSMethod> psMethods = aggregator.getContributingPSMethods();
			if (psMethods.size() > 1) {
				result.append(" {");
				for (PSMethod psMethod : psMethods) {
					Fact subFact = aggregator.getMergedFact(psMethod);
					String subName = psMethod.getClass().getSimpleName();
					result.append(subName).append("=").append(subFact.getValue());
					result.append(", ");
				}
				result.deleteCharAt(result.length() - 1);
				result.deleteCharAt(result.length() - 1);
				result.append("}");
			}
			result.append("\n");
		}
		return result.toString();
	}
}