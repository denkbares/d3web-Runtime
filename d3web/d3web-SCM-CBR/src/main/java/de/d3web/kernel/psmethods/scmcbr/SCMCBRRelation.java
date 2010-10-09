/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.kernel.psmethods.scmcbr;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.session.Session;
import de.d3web.core.session.values.ChoiceValue;

/**
 * 
 * @author Reinhard Hatko Created: 17.09.2009
 * 
 */
public final class SCMCBRRelation {

	private static final String PREFIX = "SCMCBRRelation_";

	public static final double DEFAULT_WEIGHT = 1;
	private Condition conditionedFinding;
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

	private SCMCBRRelation() {
		count++;
		id = PREFIX + count;
	}

	private SCMCBRRelation(String id) {
		this.id = id;

		String number = id.substring(PREFIX.length());
		int num = Integer.parseInt(number);
		if (count < num) {
			count = num + 1;
		}
	}

	public static SCMCBRRelation createSCMCBRRelation(Condition conditionedFinding,
			double weight) {
		SCMCBRRelation r = new SCMCBRRelation();
		r.setConditionedFinding(conditionedFinding);
		r.setWeight(weight);
		return r;
	}

	public static SCMCBRRelation createSCMCBRRelation(Condition conditionedFinding,
			double weight, String id) {
		SCMCBRRelation r = new SCMCBRRelation(id);
		r.setConditionedFinding(conditionedFinding);
		r.setWeight(weight);
		return r;
	}

	public static SCMCBRRelation createSCMCBRRelation(Condition conditionedFinding) {
		return createSCMCBRRelation(conditionedFinding, DEFAULT_WEIGHT);
	}

	public static SCMCBRRelation createSCMCBRRelation(QuestionChoice question, Choice answer) {
		return createSCMCBRRelation(question, answer, DEFAULT_WEIGHT);
	}

	public static SCMCBRRelation createSCMCBRRelation(QuestionChoice question, Choice answer, double weight) {
		return createSCMCBRRelation(new CondEqual(question, new ChoiceValue(answer)),
				weight);
	}

	public static SCMCBRRelation createSCMCBRRelation(String id, QuestionChoice question, Choice answer) {
		SCMCBRRelation r = createSCMCBRRelation(new CondEqual(question, new ChoiceValue(
				answer)));
		r.setId(id);
		return r;
	}

	public boolean eval(Session session) throws NoAnswerException, UnknownAnswerException {
		return conditionedFinding.eval(session);

	}

	public double getSimilarity(Session session) {

		return 0;
	}

	public Condition getConditionedFinding() {
		return conditionedFinding;
	}

	public void setConditionedFinding(Condition conditionedFinding) {
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

	@Override
	public boolean equals(Object o) {
		if (o instanceof SCMCBRRelation) {
			SCMCBRRelation r = (SCMCBRRelation) o;
			return (id.equals(r.id) &&
					conditionedFinding.equals(r.conditionedFinding) && weight == r.weight);
		}
		return false;
	}

	@Override
	public String toString() {
		String w = " ";
		if (weight != DEFAULT_WEIGHT) w += weight;
		return conditionedFinding.toString() + w;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	public double getDegreeOfTruth(Session session) throws NoAnswerException, UnknownAnswerException {
		if (conditionedFinding.eval(session)) {
			return 1;
		}
		return 0;
	}

}
