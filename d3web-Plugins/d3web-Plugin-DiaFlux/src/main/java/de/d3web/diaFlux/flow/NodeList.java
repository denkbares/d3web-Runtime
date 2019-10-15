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
package de.d3web.diaFlux.flow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import de.d3web.core.inference.KnowledgeSlice;

/**
 * @author Reinhard Hatko
 * @created 25.11.2010
 */
public class NodeList implements KnowledgeSlice, Iterable<Node> {

	private final List<Node> nodes;

	public NodeList() {
		this.nodes = new ArrayList<>(2);
	}

	public void addNode(Node node) {
		if (!nodes.contains(node)) {
			nodes.add(node);
		}
	}

	public void removeNode(Node node) {
		nodes.remove(node);
	}

	@NotNull
	public List<Node> getNodes() {
		return nodes;
	}

	@Override
	@NotNull
	public Iterator<Node> iterator() {
		return nodes.iterator();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof NodeList)) return false;
		NodeList nodes1 = (NodeList) o;
		return Objects.equals(nodes, nodes1.nodes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nodes);
	}

	@Override
	public String toString() {
		return "NodeLists: " + nodes;
	}
}
