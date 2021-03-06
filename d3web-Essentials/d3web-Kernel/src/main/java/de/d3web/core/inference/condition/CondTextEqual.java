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

package de.d3web.core.inference.condition;

import de.d3web.core.knowledge.terminology.QuestionText;

/**
 * Condition for text questions, where the value has to be equal to a given
 * value (String value). The composite pattern is used for this. This class is a
 * "leaf".
 * 
 * @author joba
 * @deprecated use {@link CondRegex} instead
 */
@Deprecated
public class CondTextEqual extends CondTextQuestion {

	/**
	 * Creates a new condition, where the specified text question needs to be
	 * equal to the specified value.
	 * 
	 * @param question the specified text question
	 * @param value the specified value (String)
	 */
	public CondTextEqual(QuestionText question, String value) {
		super(question, value);
	}

	@Override
	protected boolean compare(String caseValue) {
		return caseValue.equals(getValue());
	}

	@Override
	public String toString() {
		return getQuestion().getName()
				+ " = " + getValue();
	}
}
