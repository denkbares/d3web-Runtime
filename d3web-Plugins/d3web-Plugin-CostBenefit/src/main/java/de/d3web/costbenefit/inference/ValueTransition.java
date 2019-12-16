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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Value;

/**
 * A ValueTransition contains a question and a List of ConditionalValueSetters, which are sorted by priority.
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class ValueTransition {

	private final Question question;
	private final List<ConditionalValueSetter> setters;

	public ValueTransition(Question question, ConditionalValueSetter... setters) {
		this(question, Arrays.asList(setters));
	}

	public ValueTransition(Question question, List<ConditionalValueSetter> setters) {
		super();
		this.question = question;
		this.setters = setters;
	}

	public Question getQuestion() {
		return question;
	}

	public List<ConditionalValueSetter> getSetters() {
		return setters;
	}

	/**
	 * Calculates a {@link Set} of all possible values, which can be set by this ValueTransition
	 *
	 * @return Set of possible values
	 * @created 04.07.2012
	 */
	public Set<Value> calculatePossibleValues() {
		Set<Value> result = new HashSet<>();
		for (ConditionalValueSetter setter : setters) {
			result.add(setter.getAnswer());
		}
		return result;
	}
}
