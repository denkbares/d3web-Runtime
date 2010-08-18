/*
 * Copyright (C) 2010 denkbares GmbH, Würzburg, Germany
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
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.session.Session;
import de.d3web.core.session.values.TextValue;

public abstract class CondTextQuestion extends CondQuestion {

	protected CondTextQuestion(Question idobject) {
		super(idobject);
	}

	protected String value;

	@Override
	public boolean eval(Session session)
			throws NoAnswerException, UnknownAnswerException {
		checkAnswer(session);
		TextValue value = (TextValue) session.getBlackboard().getValue(question);
		String textvalue = value.getValue().toString();
		if (textvalue != null) {
			return compare(textvalue);
		}
		else {
			return false;
		}
	}

	protected abstract boolean compare(String caseValue);

	/**
	 * Returns the {@link String} value, that has to be contained in the answer
	 * of the contained {@link QuestionText}.
	 * 
	 * @return the conditioned String value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the {@link String} value, that has to be contained in the answer of
	 * the contained {@link QuestionText}.
	 * 
	 * @param value the conditioned String value
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
