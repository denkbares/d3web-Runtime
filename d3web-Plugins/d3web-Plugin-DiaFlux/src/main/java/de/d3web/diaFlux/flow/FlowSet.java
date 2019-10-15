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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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

	public Flow addFlow(Flow flow) {
		for (ComposedNode node : flow.getNodesOfClass(ComposedNode.class)) {
			calledFlowToComposedNode.put(node.getCalledFlowName(), node);
		}
		return map.put(flow.getName(), flow);
	}

	public void removeFlow(Flow flow) {
		for (ComposedNode node : flow.getNodesOfClass(ComposedNode.class)) {
			calledFlowToComposedNode.remove(node.getCalledFlowName(), node);
		}
		map.remove(flow.getName(), flow);
	}

	/**
	 * Returns all nodes that are calling a flow of the specified flow name.
	 *
	 * @param flowName the name of the flow that is called
	 * @return all nodes that are calling the flow
	 */
	public Collection<ComposedNode> getNodesCalling(String flowName) {
		return calledFlowToComposedNode.getValues(flowName);
	}

	/**
	 * Returns all nodes that are calling a flow of the specified flow.
	 *
	 * @param flow the flow that is called
	 * @return all nodes that are calling the flow
	 */
	public Collection<ComposedNode> getNodesCalling(Flow flow) {
		return getNodesCalling(flow.getName());
	}

	/**
	 * After the nodes of any contained flowchart has changed, you must call this method to rebuild the caches of the
	 * contained flows.
	 */
	public void refreshCaches() {
		ArrayList<Flow> flows = new ArrayList<>(map.values());
		map.clear();
		calledFlowToComposedNode.clear();
		flows.forEach(this::addFlow);
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
