/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.values.Unknown;

/**
 * This condition checks, if an NamedObject (e.g. Question) has a value or was
 * answered with {@link Unknown}.
 *
 * @author Reinhard Hatko
 * @created 02.03.2011
 */
public class CondAnswered extends CondQuestion {

	/**
	 * Creates a new CondAnswered object for the given {@link Question}.
	 *
	 * @param question the given question
	 */
	public CondAnswered(Question question) {
		super(question);
	}

	@Override
	public boolean eval(Session session) throws NoAnswerException {
		try {
			checkAnswer(session);
			return true;
		}
		catch (UnknownAnswerException ex) {
			return true;
		}
	}

	@Override
	public String toString() {
		return getQuestion().getName() + " = answered";
	}
}
