/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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

package de.d3web.empiricaltesting2.casevisualization.jung;

import de.d3web.empiricaltesting2.RatedTestCase;
import edu.uci.ics.jung.graph.DelegateForest;

/**
 * This class serves as datastructure for the
 * graph. It extends the DelegateTree class
 * from the JUNG library. 
 * 
 * This class contains some extra methods which 
 * are necessary to get an appropriate handling
 * for the RatedTestCase vertices.
 * 
 * @author Sebastian Furth
 *
 */
public class CaseTree<V, E> extends DelegateForest<V, E> {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance of CaseTree.
	 */
	public CaseTree() {
		super();
	}
		
	
	/**
	 * Checks if a vertex which is equal to the
	 * committed already exists in the graph. If
	 * so the existing vertex is returned.
	 * 
	 * <b>Don't use this method with other types
	 * than RatedTestCase!</b>
	 * 
	 * @param vertex Vertex which's existence is 
	 *               checked.
	 * @return Existing vertex, otherwise null.
	 */
	@SuppressWarnings("unchecked")
	public V getVertexEqualTo(V vertex) {
			
		for (Object o : getVertices()) {
			if (o instanceof RatedTestCase) {
				if (((RatedTestCase) o).equals(vertex)) {
					return (V) o;
				}
			}
		}
		
		return null;
	}	
	
}
