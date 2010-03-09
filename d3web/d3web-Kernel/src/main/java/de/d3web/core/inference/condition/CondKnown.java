/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.core.inference.condition;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.values.AnswerUnknown;

/**
 * This condition checks, if an IDObject (e.g. Question) has a value and was 
 * not answered with {@link AnswerUnknown}.
 * The composite pattern is used for this. This class is a "leaf".
 * Creation date: (13.09.2000 14:07:14)
 * 
 * @author Norman Bruemmer
 */
public class CondKnown extends CondQuestion {

	private static final long serialVersionUID = -8494189870196724391L;

	/**
	 * Creates a new CondKnown object for the given {@link Question}.
	 * @param the given question
	 */
	public CondKnown(Question question) {
		super(question);
	}

	@Override
	public boolean eval(XPSCase theCase) throws NoAnswerException {
		try {
			checkAnswer(theCase);
			return true;
		} catch (UnknownAnswerException ex) {
			return false;
		}
	}

	@Override
	public String toString() {
		return "\u2190 CondKnown question: " + question.getId();
	}

	@Override
	public AbstractCondition copy() {
		return new CondKnown(getQuestion());
	}
}