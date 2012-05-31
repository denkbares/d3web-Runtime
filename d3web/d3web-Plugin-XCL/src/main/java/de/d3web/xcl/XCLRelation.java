/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.xcl;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.session.Session;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.utilities.HashCodeUtils;

public class XCLRelation {

	public final static double DEFAULT_WEIGHT = 1;
	private Condition conditionedFinding;
	private double weight = DEFAULT_WEIGHT;
	private String id;
	private XCLRelationType type = null;
	private static int count = 0;

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
			if (count < num) {
				count = num + 1;
			}
		}
	}

	public static XCLRelation createXCLRelation(Condition conditionedFinding,
			double weight) {
		XCLRelation r = new XCLRelation();
		r.setConditionedFinding(conditionedFinding);
		r.setWeight(weight);
		return r;
	}

	public static XCLRelation createXCLRelation(Condition conditionedFinding,
			double weight, String id) {
		XCLRelation r = new XCLRelation(id);
		r.setConditionedFinding(conditionedFinding);
		r.setWeight(weight);
		return r;
	}

	public static XCLRelation createXCLRelation(Condition conditionedFinding) {
		return createXCLRelation(conditionedFinding, DEFAULT_WEIGHT);
	}

	public static XCLRelation createXCLRelation(QuestionChoice question, Choice answer) {
		return createXCLRelation(question, answer, DEFAULT_WEIGHT);
	}

	public static XCLRelation createXCLRelation(QuestionChoice question, Choice answer, double weight) {
		return createXCLRelation(new CondEqual(question, new ChoiceValue(answer)), weight);
	}

	public static XCLRelation createXCLRelation(String id, QuestionChoice question, Choice answer) {
		XCLRelation r = createXCLRelation(new CondEqual(question, new ChoiceValue(answer)));
		r.setId(id);
		return r;
	}

	public boolean eval(Session session) throws NoAnswerException, UnknownAnswerException {
		return conditionedFinding.eval(session);

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
		if (o instanceof XCLRelation) {
			XCLRelation r = (XCLRelation) o;
			return id.equals(r.id) &&
					conditionedFinding.equals(r.conditionedFinding) && weight == r.weight;
		}
		return false;
	}

	@Override
	public String toString() {
		String string = conditionedFinding.toString();
		if (weight != DEFAULT_WEIGHT) string += " " + weight;
		return string;
	}

	@Override
	public int hashCode() {
		int hash = HashCodeUtils.SEED;
		hash = HashCodeUtils.hash(hash, id);
		hash = HashCodeUtils.hash(hash, conditionedFinding);
		hash = HashCodeUtils.hash(hash, weight);
		return hash;
	}

	// TODO: init type in constructor and make it final
	void initType(XCLRelationType type) {
		this.type = type;
	}

	/**
	 * Returns the type of this relation or null of the relation has not been
	 * added to an {@link XCLModel} yet.
	 * 
	 * @created 31.05.2012
	 */
	public XCLRelationType getType() {
		return this.type;
	}

	/**
	 * Returns if this relation if of the specified relation type.
	 * 
	 * @created 31.05.2012
	 * @param type the type to check aginst
	 * @return if the relation if of expected type, false otherwise
	 */
	public boolean hasType(XCLRelationType type) {
		return type.equals(this.type);
	}
}
