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

import java.awt.BasicStroke;
import java.awt.Stroke;

import org.apache.commons.collections15.Transformer;

import de.d3web.empiricaltesting2.ConfigLoader;
import de.d3web.empiricaltesting2.RatedTestCase;

/**
 * This class specifies the strength of the
 * edges / strokes depending on the state
 * of the next RatedTestCase.
 * 
 * @author Sebastian Furth
 *
 */
public class EdgeStrokeTransformer implements Transformer<EdgeFinding, Stroke> {
	
	/**
	 * The underlying graph
	 */
	private CaseTree<RatedTestCase, EdgeFinding> graph;
	
	/**
	 * Default Constructor
	 * @param g CaseTree the underlying graph
	 */
	public EdgeStrokeTransformer(CaseTree<RatedTestCase, EdgeFinding> g) {
		this.graph = g;
	}
	
	/**
	 * Transforms a Finding to color
	 * depending on the state of the
	 * next RatedTestCase
	 */
    public Stroke transform(EdgeFinding f) {
    	
    	RatedTestCase rtc = graph.getDest(f);
    	String strength = ConfigLoader.getInstance().getProperty("edgeWidthNewCase");
    	
    	if (!rtc.isCorrect()) {
    		strength = ConfigLoader.getInstance().getProperty("edgeWidthIncorrectCase");
    	} else if (rtc.wasTestedBefore()) {
    		strength = ConfigLoader.getInstance().getProperty("edgeWidthOldCase");
    	}
    	
    	return new BasicStroke(Float.parseFloat(strength) / 2, 
    						   BasicStroke.CAP_ROUND, 
    						   BasicStroke.JOIN_MITER);
    	
    }

}
