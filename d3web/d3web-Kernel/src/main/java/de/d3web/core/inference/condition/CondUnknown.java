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

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.values.Unknown;

/**
 * Condition that checks if a Question has been answered with the value
 * {@link Unknown}.
 * 
 * Creation date: (23.11.2000 13:18:44)
 * 
 * @author Norman Bruemmer
 */
public class CondUnknown extends CondQuestion {

	/**
	 * Creates a new CondUnknown instance, where a {@link Question} has to be
	 * answered by {@link AnswerUnknown}.
	 * 
	 * @param question the specified question
	 */
	public CondUnknown(Question question) {
		super(question);
	}

	@Override
	public boolean eval(Session session) throws NoAnswerException {
		return (session.getBlackboard().getValue(getQuestion()) instanceof Unknown);
	}

	@Override
	public String toString() {
		return "\u2190 CondTextContains question: " + getQuestion().getName();
	}

}
