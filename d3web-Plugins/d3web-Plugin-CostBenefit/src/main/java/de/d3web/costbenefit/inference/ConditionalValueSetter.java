/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.costbenefit.inference;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.ConditionTrue;
import de.d3web.core.session.Value;

/**
 * This class contains a condition and an answer. If the condition is true and
 * no condition of a previous ConditionalValueSetter is the same ValueTransition
 * was true, this answer is set.
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class ConditionalValueSetter {

	private final Value answer;
	private final Condition condition;

	public ConditionalValueSetter(Value answer, Condition condition) {
		super();
		this.answer = answer;
		this.condition = condition;
	}

	public Value getAnswer() {
		return answer;
	}

	public Condition getCondition() {
		return condition;
	}

	@Override
	public String toString() {
		return " = " + answer + (condition == ConditionTrue.INSTANCE ? "" : " IF " + condition);
	}
}
