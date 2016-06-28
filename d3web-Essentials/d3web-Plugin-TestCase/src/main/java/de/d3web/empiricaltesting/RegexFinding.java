/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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

package de.d3web.empiricaltesting;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.testcase.TestCaseUtils;
import de.d3web.testcase.model.Check;

/**
 * This is an expected Finding having a regex String instead of a regex. The value of the question will be checked
 * against a regular expression using a {@link de.d3web.core.inference.condition.CondRegex}.
 *
 * @see de.d3web.empiricaltesting.Finding
 * @deprecated use {@link de.d3web.testcase.model.ConditionCheck} instead
 */
@SuppressWarnings("deprecation")
@Deprecated
public class RegexFinding implements Comparable<RegexFinding>, Check {

	private final Question question;
	private String regex;

	public RegexFinding(Question question, String regex) {
		this.question = question;
		this.regex = regex;
	}

	@Override
	public String toString() {
		return getCondition();
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regexPattern) {
		this.regex = regexPattern;
	}

	public Question getQuestion() {
		return question;
	}

	public String getQuestionPrompt() {
		return TestCaseUtils.getPrompt(question);
	}

	@Override
	public int compareTo(RegexFinding o) {
		int comp = question.getName().compareTo(o.getQuestion().getName());
		if (comp != 0) return comp;
		comp = regex.compareTo(o.regex);
		return comp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((regex == null) ? 0 : regex.hashCode());
		result = prime * result
				+ ((question == null) ? 0 : question.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof RegexFinding)) return false;
		RegexFinding other = (RegexFinding) obj;
		if (regex == null) {
			if (other.regex != null) return false;
		}
		else if (!regex.equals(other.regex)) return false;
		if (question == null) {
			if (other.question != null) return false;
		}
		else if (!question.equals(other.question)) return false;
		return true;
	}

	@Override
	public boolean check(Session session) {
		return false;
	}

	@Override
	public String getCondition() {
		return question + " = " + regex;
	}
}
