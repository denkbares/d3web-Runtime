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

import de.d3web.core.inference.KnowledgeSlice;

/**
 * 
 * 
 * @author Reinhard Hatko
 * @created 25.11.2010
 */
public class NodeList implements KnowledgeSlice, Iterable<Node> {

	private final List<Node> nodes;

	public NodeList() {
		this.nodes = new ArrayList<Node>();
	}

	public void addNode(Node edge) {
		nodes.add(edge);
	}

	public List<Node> getNodes() {
		return nodes;
	}

	@Override
	public Iterator<Node> iterator() {
		return nodes.iterator();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		NodeList other = (NodeList) obj;
		if (nodes == null) {
			if (other.nodes != null) return false;
		}
		else if (!nodes.equals(other.nodes)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "NodeLists: " + nodes;
	}

}
