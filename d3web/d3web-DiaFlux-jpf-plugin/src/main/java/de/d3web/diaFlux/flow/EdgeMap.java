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
package de.d3web.diaFlux.flow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.inference.FluxSolver;

/**
 * An EdgeMap is a KowledgeSlice to be added at a TerminologyObject. It saves
 * all Egdes that contain the TerminologyObject in its guard.
 * 
 * @author Reinhard Hatko
 * @created 04.11.2010
 */
public class EdgeMap implements KnowledgeSlice {

	private final Map<Flow, List<IEdge>> edges;
	private final String id;

	/**
	 * @param id
	 */
	public EdgeMap(String id) {
		this.id = id;
		this.edges = new HashMap<Flow, List<IEdge>>();
	}

	@Override
	public String getId() {
		return id;
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

	public void addEdge(IEdge edge) {
		Flow flow = edge.getStartNode().getFlow();

		if (!edges.containsKey(flow)) {
			edges.put(flow, new ArrayList<IEdge>(3));
		}

		List<IEdge> list = edges.get(flow);

		list.add(edge);

	}

	public List<IEdge> getEdges(Flow flow) {
		if (!edges.containsKey(flow)) {
			return Collections.EMPTY_LIST;
		}
		else {
			return edges.get(flow);
		}
	}

}
