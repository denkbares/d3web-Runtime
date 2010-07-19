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

package de.d3web.empiricaltesting.casevisualization.jung;

import java.awt.Color;
import java.awt.Paint;

import org.apache.commons.collections15.Transformer;

import de.d3web.empiricaltesting.RatedTestCase;

/**
 * This class transforms RatedTestCases which are the
 * vertices of our graph into colors.
 * 
 * @author Sebastian Furth
 *
 */
public class VertexColorTransformer implements Transformer<RatedTestCase, Paint> {
		
	/**
	 * Returns a color depending on the
	 * information given in a RatedTestCase.
	 * 
	 * @param rtc RatedTestCase
	 * 
	 * @return GREEN, if RatedTestCase.isCorrect(),
	 *         RED, if !RatedTestCase.isCorrect(),
	 *         GRAY, if RatedTestCase.wasTestedBefore()
	 */
    public Paint transform(RatedTestCase rtc) {
    	
//    	String color = ConfigLoader.getInstance().getProperty("edgeColorNewCase");
//    	
//    	if (!rtc.isCorrect()) {
//    		color = ConfigLoader.getInstance().getProperty("edgeColorIncorrectCase");
//    	} else if (rtc.wasTestedBefore() 
//    			&& ConfigLoader.getInstance().getProperty("renderOldCasesLikeNewCases").equals("false")) {
//    		color = ConfigLoader.getInstance().getProperty("edgeColorOldCase");
//    	}
//    	
//    	return ColorConverter.getInstance().convert(color);
    	
    	return Color.BLACK;
    }

}
