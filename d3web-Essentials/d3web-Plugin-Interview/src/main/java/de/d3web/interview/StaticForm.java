/*
 * Copyright (C) 2024 denkbares GmbH, Germany
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.interview.measure.Measurement;

/**
 * A form that doesn't change its active questions as the state of the session changes. There is an update() method
 * though, to allow updating manually. We also keep the original form as a delegate for possible later
 * reference (e.g. the Form is edited later).
 * <p>
 * This class is not thread safe!
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 21.08.20
 */
public class StaticForm implements Form {

	private final Form delegate;
	private List<Question> activeQuestions = null;

	public StaticForm(Form delegate) {
		this.delegate = delegate;
		update();
	}

	/**
	 * Update the active questions of this form with the current state of active questions of the delegate form
	 *
	 * @return the questions that are no longer active with the calling of this update
	 */
	public synchronized List<Question> update() {
		final List<Question> activeQuestions = delegate.getActiveQuestions();
		if (this.activeQuestions == null) {
			this.activeQuestions = new ArrayList<>(activeQuestions);
			return Collections.emptyList();
		}
		else {
			this.activeQuestions.removeAll(activeQuestions);
			final ArrayList<Question> removed = new ArrayList<>(this.activeQuestions);
			this.activeQuestions = new ArrayList<>(activeQuestions);
			return removed;
		}
	}

	@Override
	@NotNull
	public String getName() {
		return delegate.getName();
	}

	@Override
	@NotNull
	public String getPrompt(Locale lang) {
		return delegate.getPrompt(lang);
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public synchronized List<Question> getActiveQuestions() {
		return new ArrayList<>(this.activeQuestions);
	}

	@Override
	public List<Question> getPotentialQuestions() {
		return delegate.getPotentialQuestions();
	}

	@Override
	public QContainer getRoot() {
		return delegate.getRoot();
	}

	@Override
	public List<Measurement> getMeasurements() {
		return delegate.getMeasurements();
	}

	@Override
	public Question findQuestion(Measurement measurement) {
		return delegate.findQuestion(measurement);
	}

	@Override
	public InterviewObject getInterviewObject() {
		return delegate.getInterviewObject();
	}

	@Override
	public String getTitle() {
		return delegate.getTitle();
	}

	@Override
	public boolean isNotEmpty() {
		return delegate.isNotEmpty();
	}

	public Form getDelegate() {
		return delegate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof StaticForm that)) return false;
		return Objects.equals(delegate, that.delegate);
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}
}
