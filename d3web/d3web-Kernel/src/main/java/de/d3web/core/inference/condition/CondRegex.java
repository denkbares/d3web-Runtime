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

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;

/**
 * This condition checks, whether a specified regular expression matches the
 * value assigned to a question The composite pattern is used for this. This
 * class is a "leaf".
 * <p>
 * Being intended to be used for QuestionText, this condition may be used for
 * the string representation of any type of questions. The value of the question
 * is converted into the base objects default string representation and then the
 * regular expression is matched against the entire value string. Especially for
 * one-choice values (despite text-values) this might be handy.
 * <p>
 * For a detailed documentation of regular expressions, see {@link Pattern}
 * class. Please also note that no flags can be specified, but they might be
 * specified inline as part of the regular expression as described in the
 * {@link Pattern}'s documentation.
 * 
 * @author Volker Belli (denkbares GmbH)
 */
public class CondRegex extends CondQuestion {

	private final String regex;
	private final Pattern pattern;

	/**
	 * Creates a new condition to check the value assigned to a question against
	 * a regular expression.
	 * 
	 * @param question the question to check
	 * @param regex the regular expression to match the question's value
	 * @throws NullPointerException if any of the arguments are null
	 * @throws PatternSyntaxException if the regex is not in correct syntax
	 */
	public CondRegex(Question question, String regex) throws NullPointerException, PatternSyntaxException {
		super(question);
		if (question == null) throw new NullPointerException();
		if (regex == null) throw new NullPointerException();
		this.regex = regex;
		this.pattern = Pattern.compile(regex);
	}

	public String getRegex() {
		return regex;
	}

	private Pattern getPattern() {
		return pattern;
	}

	@Override
	public boolean eval(Session session)
			throws NoAnswerException, UnknownAnswerException {
		Value value = checkAnswer(session);
		String valueString = value.toString();
		return getPattern().matcher(valueString).matches();
	}

	@Override
	public String toString() {
		return getQuestion().getName() +
				" MATCHES /" + this.regex + "/";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((regex == null) ? 0 : regex.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		CondRegex other = (CondRegex) obj;
		if (regex == null) {
			if (other.regex != null) return false;
		}
		else if (!regex.equals(other.regex)) return false;
		return true;
	}

}