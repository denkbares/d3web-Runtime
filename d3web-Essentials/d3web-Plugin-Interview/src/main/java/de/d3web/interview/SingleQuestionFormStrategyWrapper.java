/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.d3web.interview;

import java.util.List;
import java.util.stream.Collectors;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.values.UndefinedValue;

/**
 * Wraps a {@link FormStrategy} and returns always a{@link DefaultForm}
 * containing one question or the {@link EmptyForm}
 *
 * @author Markus Friedrich (denkbares GmbH)
 * @created 19.04.2013
 */
public class SingleQuestionFormStrategyWrapper implements FormStrategy {

	private final FormStrategy wrappedStrategy;

	public SingleQuestionFormStrategyWrapper(FormStrategy wrappedStrategy) {
		super();
		this.wrappedStrategy = wrappedStrategy;
	}

	@Override
	public Form nextForm(List<InterviewObject> agendaEntries, Session session) {
		Form nextForm = wrappedStrategy.nextForm(agendaEntries, session);
		return getNextUnansweredQuestionAsForm(nextForm, session);
	}

	@Override
	public List<Form> getForms(InterviewObject object, Session session) {
		return wrappedStrategy.getActiveQuestions(object, session).stream()
				.map(question -> new DefaultForm(question.getName(), question, session))
				.collect(Collectors.toList());
	}

	@Override
	public List<Question> getActiveQuestions(InterviewObject object, Session session) {
		// overwritten to improve performance, because no wrapping is required here
		return wrappedStrategy.getActiveQuestions(object, session);
	}

	private Form getNextUnansweredQuestionAsForm(Form form, Session session) {
		for (Question q : form.getActiveQuestions()) {
			if (UndefinedValue.isUndefinedValue(session.getBlackboard().getValue(q))) {
				return new DefaultForm(q.getName(), q, session);
			}
		}
		return EmptyForm.getInstance();
	}
}
