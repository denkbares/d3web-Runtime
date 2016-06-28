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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.d3web.collections.DefaultMultiMap;
import de.d3web.core.inference.KnowledgeSlice;

/**
 * 
 * @author Reinhard Hatko Created on: 04.11.2009
 */
public class FlowSet implements KnowledgeSlice, Iterable<Flow> {

	private final Map<String, Flow> map = new HashMap<>();

	private final DefaultMultiMap<String, ComposedNode> calledFlowToComposedNode = new DefaultMultiMap<>();

	public boolean contains(String name) {
		return map.containsKey(name);
	}

	public boolean containsValue(Flow flow) {
		return map.containsValue(flow);
	}

	public Flow get(String name) {
		return map.get(name);
	}

	public Set<String> getFlowNames() {
		return map.keySet();
	}

	public Flow put(Flow flow) {
		Collection<ComposedNode> composedNodes = flow.getNodesOfClass(ComposedNode.class);
		for (ComposedNode composedNode : composedNodes) {
			String calledFlowName = composedNode.getCalledFlowName();
			calledFlowToComposedNode.put(calledFlowName, composedNode);
		}
		return map.put(flow.getName(), flow);
	}

	public Collection<ComposedNode> getNodesCalling(String flowName) {
		return calledFlowToComposedNode.getValues(flowName);
	}

	public int size() {
		return map.size();
	}

	public Collection<Flow> getFlows() {
		return map.values();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Iterator<Flow> iterator() {
		return getFlows().iterator();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + map.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		FlowSet other = (FlowSet) obj;
		return map.equals(other.map);
	}

	@Override
	public String toString() {
		return "FlowSet [map=" + map + "]";
	}

}
