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

package de.d3web.kernel.psMethods.setCovering.simple;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.d3web.kernel.domainModel.Answer;

public class SimpleMCCoverage {
	
	private java.util.Set answers = new HashSet();
	private java.util.Set<Answer> definedAnswers = new HashSet();
	
	
	public void setAnswerSet(List answers){
		this.answers.addAll(answers);
	}
	
	public void addDefinedAnswer(Object []answers) {
		for (Object  object : answers) {
			if(object instanceof Answer) {
				definedAnswers.add((Answer)object);
			}
		}
	}
	
	public double calcIntersection() {
		if(answers.size() == 0 && definedAnswers.size() == 0) return 1.0;
		Set<Answer>  unity = new HashSet<Answer>();
		Set<Answer>  intersection = new HashSet<Answer>();
		
		for (Object answer : definedAnswers) {
			if(answer instanceof Answer) {
				unity.add((Answer)answer);
				if(answers.contains(answer)) {
					intersection.add((Answer)answer);
				}
			}
		}
		for (Object answer : answers) {
			if(answer instanceof Answer) {
				unity.add((Answer)(answer));
			}
		}
		return ((double)intersection.size()) / unity.size();
	}

}
