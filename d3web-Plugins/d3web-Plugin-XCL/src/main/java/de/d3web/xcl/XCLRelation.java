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
import de.d3web.utils.HashCodeUtils;

public class XCLRelation {

	public final static double DEFAULT_WEIGHT = 1;

	private final Condition conditionedFinding;
	private final double weight;
	private final XCLRelationType type;

	public XCLRelation(Condition condition) {
		this(condition, DEFAULT_WEIGHT, XCLRelationType.explains);
	}

	public XCLRelation(Condition condition, XCLRelationType type) {
		this(condition, DEFAULT_WEIGHT, type);
	}

	public XCLRelation(Condition condition, double weight) {
		this(condition, weight, XCLRelationType.explains);
	}

	public XCLRelation(Condition condition, double weight, XCLRelationType type) {
		this.conditionedFinding = condition;
		this.weight = weight;
		this.type = type;
		if (type == null) throw new NullPointerException("type of relation must not be null");
	}

	public XCLRelation(QuestionChoice question, Choice answer) {
		this(new CondEqual(question, new ChoiceValue(answer)));
	}

	public XCLRelation(QuestionChoice question, Choice answer, XCLRelationType type) {
		this(new CondEqual(question, new ChoiceValue(answer)), type);
	}

	public XCLRelation(QuestionChoice question, Choice answer, double weight) {
		this(new CondEqual(question, new ChoiceValue(answer)), weight);
	}

	public XCLRelation(QuestionChoice question, Choice answer, double weight, XCLRelationType type) {
		this(new CondEqual(question, new ChoiceValue(answer)), weight, type);
	}

	public boolean eval(Session session) throws NoAnswerException, UnknownAnswerException {
		return conditionedFinding.eval(session);

	}

	public Condition getConditionedFinding() {
		return conditionedFinding;
	}

	public double getWeight() {
		return weight;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof XCLRelation) {
			XCLRelation r = (XCLRelation) o;
			return conditionedFinding.equals(r.conditionedFinding)
					&& weight == r.weight && type == r.type;
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
		hash = HashCodeUtils.hash(hash, conditionedFinding);
		hash = HashCodeUtils.hash(hash, weight);
		hash = HashCodeUtils.hash(hash, type.name());
		return hash;
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
	 * @param type the type to check against
	 * @return if the relation if of expected type, false otherwise
	 */
	public boolean hasType(XCLRelationType type) {
		return type == this.type;
	}
}
