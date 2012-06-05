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
import java.util.Map.Entry;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.utilities.NamedObjectComparator;

/**
 * This class handles a dynamic set of {@link FactAggregator}s.
 * <p>
 * <b>Note:<br>
 * This class is for internal purpose only. Until you do not provide an own
 * blackboard implementation, do not use this class directly! </b>
 * 
 * @author volker_belli
 */
public class DefaultFactStorage implements FactStorage {

	private final Map<TerminologyObject, FactAggregator> mediators = new HashMap<TerminologyObject, FactAggregator>();

	/**
	 * Returns the {@link FactAggregator} for a specified terminology object. If
	 * no such aggregator exists yet,a new one is created (lazy).
	 * 
	 * @created 01.05.2011
	 * @param termObject the object to access the aggregator for
	 * @return the aggregator for the object
	 */
	private FactAggregator getAggregator(TerminologyObject termObject) {
		FactAggregator aggregator = this.mediators.get(termObject);
		if (aggregator == null) {
			aggregator = new FactAggregator();
			this.mediators.put(termObject, aggregator);
		}
		return aggregator;
	}

	/**
	 * Returns a newly created deep copy of this object for building a fully
	 * working copy of the current session.
	 * 
	 * @created 04.06.2012
	 * @return a deep copy of this object
	 */
	@Override
	public DefaultFactStorage copy() {
		DefaultFactStorage result = new DefaultFactStorage();
		for (Entry<TerminologyObject, FactAggregator> entry : mediators.entrySet()) {
			result.mediators.put(entry.getKey(), entry.getValue().copy());
		}
		return result;
	}

	@Override
	public Fact getMergedFact(TerminologyObject termObject) {
		FactAggregator aggregator = this.mediators.get(termObject);
		if (aggregator != null) {
			return aggregator.getMergedFact();
		}
		return null;
	}

	@Override
	public Collection<Fact> getAllFacts(TerminologyObject termObject) {
		FactAggregator aggregator = this.mediators.get(termObject);
		if (aggregator != null) {
			return Collections.unmodifiableCollection(aggregator.getAllFacts());
		}
		return Collections.emptyList();
	}

	@Override
	public Fact getMergedFact(TerminologyObject termObject, PSMethod psMethod) {
		FactAggregator aggregator = this.mediators.get(termObject);
		if (aggregator != null) {
			return aggregator.getMergedFact(psMethod);
		}
		return null;
	}

	@Override
	public Fact getFact(TerminologyObject termObject, PSMethod psMethod, Object source) {
		FactAggregator aggregator = this.mediators.get(termObject);
		if (aggregator != null) {
			return aggregator.getFact(psMethod, source);
		}
		return null;
	}

	@Override
	public boolean hasFact(TerminologyObject termObject) {
		FactAggregator aggregator = this.mediators.get(termObject);
		if (aggregator != null) {
			return !aggregator.isEmpty();
		}
		return false;
	}

	@Override
	public boolean hasFact(TerminologyObject termObject, PSMethod method) {
		FactAggregator aggregator = this.mediators.get(termObject);
		if (aggregator != null) {
			return aggregator.hasFacts(method);
		}
		return false;
	}

	@Override
	public void add(Fact fact) {
		TerminologyObject terminologyObject = fact.getTerminologyObject();
		getAggregator(terminologyObject).addFact(fact);
	}

	@Override
	public void remove(Fact fact) {
		TerminologyObject terminologyObject = fact.getTerminologyObject();
		FactAggregator aggregator = this.mediators.get(terminologyObject);
		if (aggregator != null) {
			aggregator.removeFact(fact);
			// check if the aggregator has become empty remove it
			if (aggregator.isEmpty()) {
				this.mediators.remove(terminologyObject);
			}
		}
	}

	@Override
	public void remove(TerminologyObject termObject, Object source) {
		FactAggregator aggregator = this.mediators.get(termObject);
		if (aggregator != null) {
			aggregator.removeFactsBySource(source);
			// check if the aggregator has become empty remove it
			if (aggregator.isEmpty()) {
				this.mediators.remove(termObject);
			}
		}
	}

	@Override
	public void remove(TerminologyObject termObject) {
		this.mediators.remove(termObject);
	}

	@Override
	public Collection<TerminologyObject> getValuedObjects() {
		return this.mediators.keySet();
	}

	@Override
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