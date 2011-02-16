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
import java.util.List;

import de.d3web.core.inference.KnowledgeSlice;

/**
 * An EdgeMap is a KowledgeSlice to be added at a TerminologyObject. It saves
 * all Egdes that contain the TerminologyObject in its guard.
 * 
 * @author Reinhard Hatko
 * @created 04.11.2010
 */
public class EdgeMap implements KnowledgeSlice {

	private final List<IEdge> edges;
	private final String id;

	public EdgeMap(String id) {
		this.id = id;
		this.edges = new ArrayList<IEdge>();
	}

	public void addEdge(IEdge edge) {
		edges.add(edge);
	}

	public List<IEdge> getEdges() {
		return edges;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((edges == null) ? 0 : edges.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		EdgeMap other = (EdgeMap) obj;
		if (edges == null) {
			if (other.edges != null) return false;
		}
		else if (!edges.equals(other.edges)) return false;
		if (id == null) {
			if (other.id != null) return false;
		}
		else if (!id.equals(other.id)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "EdgeMap [id=" + id + ", edges=" + edges + "]";
	}

}
