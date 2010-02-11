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

package de.d3web.xcl;

import de.d3web.core.inference.condition.AbstractCondition;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.terminology.QuestionChoice;

public class XCLRelation {
	public static double DEFAULT_WEIGHT = 1;
	private AbstractCondition conditionedFinding;
	private double weight = DEFAULT_WEIGHT;
	private String id;
	private static int count = 0;
	
	private String kdmomID = null;
	
	public String getKdmomID() {
		return kdmomID;
	}

	public void setKdmomID(String kdmomID) {
		this.kdmomID = kdmomID;
	}

	private XCLRelation() {
		super();
		count++;
		id = "XCLRelation_" + count;
	}
	
	private XCLRelation(String id) {
		super();
		this.id = id;
		if (id.startsWith("XCLRelation_")) {
			String number = id.substring("XCLRelation_".length());
			int num = Integer.parseInt(number);
			if(count < num) {
				count = num + 1;
			}
		}
	}

	public static XCLRelation createXCLRelation(AbstractCondition conditionedFinding, 
			double weight) {
		XCLRelation r = new XCLRelation();
		r.setConditionedFinding(conditionedFinding);
		r.setWeight(weight);
		return r;
	}
	public static XCLRelation createXCLRelation(AbstractCondition conditionedFinding, 
			double weight, String id) {
		XCLRelation r = new XCLRelation(id);
		r.setConditionedFinding(conditionedFinding);
		r.setWeight(weight);
		return r;
	}

	public static XCLRelation createXCLRelation(AbstractCondition conditionedFinding) {
		return createXCLRelation(conditionedFinding, DEFAULT_WEIGHT);
	}
	
	public static XCLRelation createXCLRelation(QuestionChoice question, AnswerChoice answer) {
		return createXCLRelation(question, answer, DEFAULT_WEIGHT);
	}

	public static XCLRelation createXCLRelation(QuestionChoice question, AnswerChoice answer, double weight) {
		return createXCLRelation(new CondEqual(question, answer), weight);
	}

	public static XCLRelation createXCLRelation(String id, QuestionChoice question, AnswerChoice answer) {
		XCLRelation r = createXCLRelation(new CondEqual(question, answer));
		r.setId(id);
		return r;
	}
	public boolean eval(XPSCase theCase) throws NoAnswerException,UnknownAnswerException{
			return conditionedFinding.eval(theCase);
		
	}

	public AbstractCondition getConditionedFinding() {
		return conditionedFinding;
	}

	public void setConditionedFinding(AbstractCondition conditionedFinding) {
		this.conditionedFinding = conditionedFinding;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public boolean equals(Object o) {
		if (o instanceof XCLRelation) {
			XCLRelation r = (XCLRelation)o;
			return (id.equals(r.id) &&
					conditionedFinding.equals(r.conditionedFinding) && 
					weight == r.weight);
		}
		return false;
	}
	
	public String toString() {
		String w = " ";
		if (weight != DEFAULT_WEIGHT) 
			w += weight;
		return conditionedFinding.toString() + w;
	}
		
	public int hashCode() {
		return toString().hashCode();
	}

	public double getDegreeOfTruth(XPSCase theCase) throws NoAnswerException, UnknownAnswerException {
		if(conditionedFinding.eval(theCase)) {
			return 1;
		}
		return 0;
	}
	
	
}
