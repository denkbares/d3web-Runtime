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
package de.d3web.diaFlux.flow;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.denkbares.collections.DefaultMultiMap;
import de.d3web.core.inference.KnowledgeSlice;

/**
 * @author Reinhard Hatko Created on: 04.11.2009
 */
public class FlowSet implements KnowledgeSlice, Iterable<Flow> {

	private final Map<String, Flow> map = new HashMap<>();
	private final DefaultMultiMap<String, ComposedNode> calledFlowToComposedNode = new DefaultMultiMap<>();
	private final DefaultMultiMap<String, ComposedNode> callingFlowToComposedNode = new DefaultMultiMap<>();

	public boolean contains(String name) {
		return map.containsKey(name);
	}

	public boolean contains(Flow flow) {
		return map.get(flow.getName()) == flow;
	}

	public Flow get(String name) {
		return map.get(name);
	}

	public Set<String> getFlowNames() {
		return map.keySet();
	}

	public void addFlow(Flow flow) {
		Flow prev = map.put(flow.getName(), flow);
		// if we have removed an other existing flow, update caches also for removed one
		if (prev != null && prev != flow) refreshCaches(prev, false);
		refreshCaches(flow, true);
	}

	public void removeFlow(Flow flow) {
		map.remove(flow.getName(), flow);
		refreshCaches(flow, false);
	}

	/**
	 * Returns all nodes that are calling a flow of the specified flow name.
	 *
	 * @param flowName the name of the flow that is called
	 * @return all nodes that are calling the flow
	 */
	public Set<ComposedNode> getNodesCalling(String flowName) {
		return calledFlowToComposedNode.getValues(flowName);
	}

	/**
	 * Returns all nodes that are calling a flow of the specified flow.
	 *
	 * @param flow the flow that is called
	 * @return all nodes that are calling the flow
	 */
	public Set<ComposedNode> getNodesCalling(Flow flow) {
		return getNodesCalling(flow.getName());
	}

	/**
	 * After the nodes of any contained flowchart has changed, you may call this method to rebuild the caches of the
	 * contained flows.
	 * <p>
	 * This is NOT (!) necessary in most cases. For the default flow-set of the knowledge base, the caches are
	 * automatically updated incrementally. Only if other flow-set are used, they requires manual updates.
	 */
	public void refreshCaches() {
		calledFlowToComposedNode.clear();
		callingFlowToComposedNode.clear();
		for (Flow flow : map.values()) {
			refreshCaches(flow, true);
		}
	}

	/**
	 * After the nodes of any contained flowchart has changed, you must call this method to rebuild the caches of the
	 * contained flows.
	 */
	void refreshCaches(Flow flow) {
		boolean contained = contains(flow);
		if (contained) refreshCaches(flow, contained);
	}

	/**
	 * After the nodes of any contained flowchart has changed, you must call this method to rebuild the caches of the
	 * contained flows.
	 */
	private void refreshCaches(Flow flow, boolean added) {
		Set<ComposedNode> prevNodes = callingFlowToComposedNode.removeKey(flow.getName());
		Set<ComposedNode> currNodes = new HashSet<>(flow.getNodesOfClass(ComposedNode.class));

		// add the complete list, as before all nodes are removed
		if (added) callingFlowToComposedNode.putAll(flow.getName(), currNodes);

		// remove all disappearing nodes
		// (Note: if the flow is not added, all are going to be removed)
		for (ComposedNode node : prevNodes) {
			if (!added || !currNodes.contains(node)) {
				calledFlowToComposedNode.remove(node.getCalledFlowName(), node);
			}
		}

		// add all added nodes, if the flow is added to this set
		if (added) {
			for (ComposedNode node : currNodes) {
				// we ignore if already added, because adding multiple times has no effect
				// if (!prevNodes.contains(node)) {
				calledFlowToComposedNode.put(node.getCalledFlowName(), node);
				//}
			}
		}
	}

	@NotNull
	public Collection<Flow> getFlows() {
		return Collections.unmodifiableCollection(map.values());
	}

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	@NotNull
	@Override
	public Iterator<Flow> iterator() {
		return getFlows().iterator();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof FlowSet)) return false;
		FlowSet flows = (FlowSet) o;
		return Objects.equals(map, flows.map);
	}

	@Override
	public int hashCode() {
		return Objects.hash(map);
	}

	@Override
	public String toString() {
		return "FlowSet " + map.keySet();
	}
}
