/*
 * Copyright (C) 2016 denkbares GmbH. All rights reserved.
 */

package de.d3web.interview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.denkbares.utils.Log;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;

/**
 * This implements a form strategy that decorates an existing one, but may split long forms at defined places.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 25.08.2016
 */
public class SplittingFormStrategy implements FormStrategy {

	private final FormStrategy delegate;
	private final FormSplitter splitter;

	public SplittingFormStrategy(FormStrategy delegate, FormSplitter splitter) {
		this.delegate = Objects.requireNonNull(delegate);
		this.splitter = Objects.requireNonNull(splitter);
	}

	@Override
	public Form nextForm(List<InterviewObject> agendaEntries, Session session) {
		// check the delegate to get the complete form for us
		Form completeForm = delegate.nextForm(agendaEntries, session);
		if (completeForm == null) return null;
		if (completeForm.isEmpty()) return completeForm;

		// split the original questions by some rule defined through #requireSplit
		List<SplitForm> groups = split(completeForm);

		// if not split at all, use the original (complete) form
		if (groups.size() == 1) return groups.get(0);

		// and build the forms out of it, until a not completely answered form is available
		FormStrategyUtils utils = new FormStrategyUtils(session);
		for (SplitForm form : groups) {
			if (utils.hasAnyValueUndefined(form.getActiveQuestions())) {
				return form;
			}
		}

		// otherwise, if completely answered, there is something unexpected, so use the original form.
		Log.warning("The form is already answered, check behaviour of: " + delegate);
		return completeForm;
	}

	@Override
	public boolean isActive(Question question, Session session) {
		return delegate.isActive(question, session);
	}

	@Override
	public boolean isForcedActive(Question question, Session session) {
		return delegate.isForcedActive(question, session);
	}

	/**
	 * Splits the specified (original) form into groups of questions. The order of the questions is preserved. All
	 * active questions of the original form are in the returned list of groups.
	 *
	 * @param completeForm the form to be split
	 * @return the returned list of active question groups
	 */
	@NotNull
	private List<SplitForm> split(Form completeForm) {
		LinkedList<List<Question>> groups = new LinkedList<>();
		Question previous = null;
		for (Question question : completeForm.getPotentialQuestions()) {
			// check if (first/next) group is required
			if ((previous == null) || splitter.requireSplit(previous, question)) {
				groups.add(new ArrayList<>());
			}
			groups.getLast().add(question);
			previous = question;
		}

		// prepare the currently active questions
		HashSet<Question> active = new HashSet<>(completeForm.getActiveQuestions());
		// if there is no active question, skip form
		groups.removeIf(group -> Collections.disjoint(active, group));

		// and build the forms out of it
		List<SplitForm> result = new ArrayList<>(groups.size());
		int groupNumber = 1;
		for (List<Question> group : groups) {
			result.add(new SplitForm(completeForm, groupNumber++, groups.size(), group));
		}
		return result;
	}

	@Override
	public List<Form> getForms(InterviewObject object, Session session) {
		return delegate.getForms(object, session).stream()
				.map(this::split).flatMap(List::stream).collect(Collectors.toList());
	}

	@Override
	public List<Question> getActiveQuestions(InterviewObject object, Session session) {
		// improve performance by directly delegate, as we do not need any wrapping here
		return delegate.getActiveQuestions(object, session);
	}

	private static class SplitForm implements Form {

		private final Form delegate;
		private final int groupNumber;
		private final int totalGroupCount;
		private final List<Question> potentialQuestions;

		public SplitForm(Form delegate, int groupNumber, int totalGroupCount, List<Question> potentialQuestions) {
			this.delegate = delegate;
			this.groupNumber = groupNumber;
			this.totalGroupCount = totalGroupCount;
			this.potentialQuestions = potentialQuestions;
		}

		@NotNull
		@Override
		public String getName() {
			return (totalGroupCount == 1)
					? delegate.getName()
					: (delegate.getName() + "#" + groupNumber);
		}

		@NotNull
		@Override
		public String getPrompt(Locale lang) {
			String prompt = delegate.getPrompt(lang);
			if (totalGroupCount == 1) return prompt;
			String suffixFormat = " #%d";
			return prompt + String.format(suffixFormat, groupNumber);
		}

		@Override
		public boolean isEmpty() {
			return getActiveQuestions().isEmpty();
		}

		@Override
		public List<Question> getActiveQuestions() {
			HashSet<Question> allowed = new HashSet<>(delegate.getActiveQuestions());
			return potentialQuestions.stream()
					.filter(allowed::contains)
					.collect(Collectors.toList());
		}

		@Override
		public List<Question> getPotentialQuestions() {
			return potentialQuestions;
		}

		@Override
		public QContainer getRoot() {
			return delegate.getRoot();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof SplitForm)) return false;
			SplitForm splitForm = (SplitForm) o;
			return groupNumber == splitForm.groupNumber &&
					Objects.equals(delegate, splitForm.delegate);
		}

		@Override
		public int hashCode() {
			return Objects.hash(delegate, groupNumber);
		}

		@SuppressWarnings("deprecation")
		@Override
		public InterviewObject getInterviewObject() {
			return delegate.getInterviewObject();
		}
	}

	@FunctionalInterface
	public interface FormSplitter {
		/**
		 * Returns true if the from should be split between the two specified questions.
		 *
		 * @param previous the question before the potential split
		 * @param next     the question after the potential split
		 * @return true if the form should be split in between
		 */
		boolean requireSplit(Question previous, Question next);
	}
}
