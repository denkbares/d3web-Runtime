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

import java.awt.Paint;

import org.apache.commons.collections15.Transformer;

import de.d3web.empiricaltesting2.ConfigLoader;
import de.d3web.empiricaltesting2.RatedTestCase;

/**
 * This class transforms Findings which are the
 * edges of our graph into colors.
 * 
 * @author Sebastian Furth
 *
 */
public class EdgeColorTransformer implements Transformer<EdgeFinding, Paint> {
	
	/**
	 * The underlying graph
	 */
	private CaseTree<RatedTestCase, EdgeFinding> graph;
	
	/**
	 * Default Constructor
	 * @param g CaseTree the underlying graph
	 */
	public EdgeColorTransformer(CaseTree<RatedTestCase, EdgeFinding> g) {
		this.graph = g;
	}
	
	/**
	 * Transforms a Finding to color
	 * depending on the state of the
	 * next RatedTestCase
	 */
    public Paint transform(EdgeFinding f) {
    	
    	RatedTestCase rtc = graph.getDest(f);
    	String color = ConfigLoader.getInstance().getProperty("edgeColorNewCase");
    	
    	if (!rtc.isCorrect()) {
    		color = ConfigLoader.getInstance().getProperty("edgeColorIncorrectCase");
    	} else if (rtc.wasTestedBefore() 
    			&& ConfigLoader.getInstance().getProperty("renderOldCasesLikeNewCases").equals("false")) {
    		color = ConfigLoader.getInstance().getProperty("edgeColorOldCase");
    	}
    	
    	return ColorConverter.getInstance().convert(color);
    	
    }

}
