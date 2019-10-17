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

import java.util.Set;

import com.denkbares.collections.MinimizedLinkedHashSet;
import de.d3web.core.inference.KnowledgeSlice;

/**
 * An EdgeMap is a KowledgeSlice to be added at a TerminologyObject. It saves all Edges that contain the
 * TerminologyObject in its guard.
 *
 * @author Reinhard Hatko
 * @created 04.11.2010
 */
public class EdgeList implements KnowledgeSlice {

	private final Set<Edge> edges = new MinimizedLinkedHashSet<>();

	public void addEdge(Edge edge) {
		edges.add(edge);
	}

	public void removeEdge(Edge edge) {
		edges.remove(edge);
	}

	public Set<Edge> getEdges() {
		return edges;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + edges.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		EdgeList other = (EdgeList) obj;
		return edges.equals(other.edges);
	}

	@Override
	public String toString() {
		return "EdgeList [" + edges + "]";
	}
}
