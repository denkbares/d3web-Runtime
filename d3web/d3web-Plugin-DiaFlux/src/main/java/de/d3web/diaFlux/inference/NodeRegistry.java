/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
 * A Lookup-Table for nodes. Nodes can register for a pair of strings( flowname
 * and nodename) of a node they are interested in receiving events
 *
 *
 * @author Reinhard Hatko
 * @created 13.11.2010
 */
public class NodeRegistry implements KnowledgeSlice {

	private final Map<Pair<String, String>, List<INode>> map;

	public NodeRegistry() {
		this.map = new HashMap<Pair<String, String>, List<INode>>();
	}

	public void registerNode(String flowName, String exitNodeName, INode node) {
		Pair<String, String> pair = createPair(flowName, exitNodeName);

		List<INode> registrations = map.get(pair);

		if (registrations == null) {
			registrations = new ArrayList<INode>(3);
			map.put(pair, registrations);

		}

		registrations.add(node);
	}

	public void removeRegistration(String flowName, String nodeName, INode node) {

		Pair<String, String> pair = createPair(flowName, nodeName);

		List<INode> list = map.get(pair);

		if (list == null) return;

		list.remove(node);

	}

	public List<INode> getRegistrations(INode node) {
		return getRegistrations(node.getFlow().getName(), node.getName());

	}

	public List<INode> getRegistrations(String flowName, String nodeName) {

		Pair<String, String> pair = createPair(flowName, nodeName);

		List<INode> registrations = map.get(pair);

		if (registrations == null) return Collections.EMPTY_LIST;
		else return registrations;

	}

	private Pair<String, String> createPair(INode node) {
		return createPair(node.getFlow().getName(), node.getName());
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
