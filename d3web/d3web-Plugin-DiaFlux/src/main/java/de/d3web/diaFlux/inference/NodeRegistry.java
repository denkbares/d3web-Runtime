/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.d3web.diaFlux.inference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.session.Session;
import de.d3web.core.utilities.Pair;
import de.d3web.diaFlux.flow.INode;

/**
 * A Lookup-Table for nodes and flows. Nodes can register for either a pair of
 * strings( flowname and nodename) of a node they are interested in receiving
 * events, or only a flow name.
 * 
 * 
 * @author Reinhard Hatko
 * @created 13.11.2010
 */
public class NodeRegistry implements KnowledgeSlice {

	private final Map<Pair<String, String>, List<INode>> nodeMap;
	private final Map<String, List<INode>> flowMap;

	public NodeRegistry() {
		this.nodeMap = new HashMap<Pair<String, String>, List<INode>>();
		this.flowMap = new HashMap<String, List<INode>>();
	}

	public void registerNode(String flowName, String exitNodeName, INode node) {
		Pair<String, String> pair = createPair(flowName, exitNodeName);

		List<INode> registrations = nodeMap.get(pair);

		if (registrations == null) {
			registrations = new ArrayList<INode>(3);
			nodeMap.put(pair, registrations);

		}

		registrations.add(node);
	}

	public void registerFlow(String flowName, INode node) {

		List<INode> registrations = flowMap.get(flowName);

		if (registrations == null) {
			registrations = new ArrayList<INode>(3);
			flowMap.put(flowName, registrations);

		}

		registrations.add(node);
	}

	public void removeNodeRegistration(String flowName, String nodeName, INode node) {

		Pair<String, String> pair = createPair(flowName, nodeName);

		List<INode> list = nodeMap.get(pair);

		if (list == null) {
			return;
		}

		list.remove(node);

	}

	public void removeFlowRegistration(String flowName, INode node) {

		List<INode> list = flowMap.get(flowName);

		if (list == null) {
			return;
		}

		list.remove(node);

	}

	public List<INode> getRegisteredNodes(INode node) {
		List<INode> nodes = new ArrayList<INode>(3);
		nodes.addAll(getNodeRegistrations(node.getFlow().getName(), node.getName()));
		nodes.addAll(getFlowRegistrations(node.getFlow().getName()));
		return nodes;
	}

	public List<INode> getNodeRegistrations(String flowName, String nodeName) {

		Pair<String, String> pair = createPair(flowName, nodeName);

		List<INode> registrations = nodeMap.get(pair);

		if (registrations == null) {
			return Collections.emptyList();
		}
		else {
			return registrations;
		}
	}

	protected List<INode> getFlowRegistrations(String flowName) {
		List<INode> registrations = flowMap.get(flowName);

		if (registrations == null) {
			return Collections.emptyList();
		}
		else {
			return registrations;
		}

	}

	private Pair<String, String> createPair(String flowName, String nodeName) {
		return new Pair<String, String>(flowName, nodeName);
	}

	@Override
	public String getId() {
		return "NodeRegistry"; // There will only be one
	}

	@Override
	public Class<? extends PSMethod> getProblemsolverContext() {
		return FluxSolver.class;
	}

	@Override
	public boolean isUsed(Session session) {
		return true;
	}

	@Override
	public void remove() {

	}

}
