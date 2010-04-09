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

package de.d3web.empiricalTesting.caseVisualization.jung;

import org.apache.commons.collections15.Transformer;

import de.d3web.empiricalTesting.Finding;
import de.d3web.empiricalTesting.RatedTestCase;


/**
 * This class transforms (Edge)Findings which are the
 * edges of our graph into a nice formatted String
 * for rendering.
 * 
 * @author Sebastian Furth
 *
 */
public class EdgeTransformer implements Transformer<EdgeFinding, String> {

	/**
	 * Graph which's edges are transformed.
	 * This reference is necessary for getting
	 * the answers of all asked questions.
	 */
	private final CaseTree<RatedTestCase, EdgeFinding> graph;
	
	/**
	 * Creates an instance of EdgeTransformer backed on
	 * the committed CaseTree object.
	 * @param graph CaseTree which's elements are transformed.
	 */
	public EdgeTransformer(CaseTree<RatedTestCase, EdgeFinding> graph) {
		this.graph = graph;
	}
	
	/**
	 * Transforms a Finding to nice
	 * formatted String which is necessary
	 * for rendering.
	 */
    public String transform(EdgeFinding ef) {
    	
    	StringBuilder result = new StringBuilder();
    	result.append("<html><center>");
    	for (Finding f : graph.getDest(ef).getFindings()) {
			result.append(f.getValue().getValue().toString());
    		result.append("<br/>");
    	}
    	result.delete(result.length() - 4, result.length());
    	result.append("</center></html>");
    	
        return result.toString();
    }
	
}
