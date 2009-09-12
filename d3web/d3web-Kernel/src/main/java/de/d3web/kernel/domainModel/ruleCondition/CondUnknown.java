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

package de.d3web.kernel.domainModel.ruleCondition;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.answers.AnswerUnknown;
import de.d3web.kernel.domainModel.qasets.Question;

/**
 * Condition that checks if a Question has been answered by
 * {@link AnswerUnknown}.
 * 
 * Creation date: (23.11.2000 13:18:44)
 * @author Norman Bruemmer
 */
public class CondUnknown extends CondQuestion {

	/**
	 * Creates a new CondUnknown instance, where a {@link Question}
	 * has to be answered by {@link AnswerUnknown}.
	 * @param question the specified question
	 */
	public CondUnknown(Question question) {
		super(question);
	}

	@Override
	public boolean eval(XPSCase theCase) throws NoAnswerException {
		try {
			checkAnswer(theCase);
			return false;
		} catch (UnknownAnswerException ex) {
			return ((Answer) question.getValue(theCase).get(0)).isUnknown();
		}
	}

	@Override
	public String toString() {
		String questionID = "";
		if(question != null) {
			questionID = question.getId();
		}
		return "<Condition type='unknown' ID='"
			+ questionID
			+ "' value='unknown'>"
			+ "</Condition>\n";
	}

	@Override
	public AbstractCondition copy() {
		return new CondUnknown(getQuestion());
	}	
	

}
