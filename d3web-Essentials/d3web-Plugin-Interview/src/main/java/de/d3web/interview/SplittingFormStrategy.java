/*
 * Copyright (C) 2016 denkbares GmbH. All rights reserved.
 */

package de.d3web.interview;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import com.denkbares.utils.Log;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;

/**
 * This implements a form strategy that decorates an existing one, but may split long forms
 * at defined places.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 25.08.2016
 */
public class SplittingFormStrategy extends AbstractFormStrategy {

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
		LinkedList<List<Question>> groups = new LinkedList<>();
		Question previous = null;
		for (Question question : completeForm.getActiveQuestions()) {
			// check if (first/next) group is required
			if ((previous == null) || splitter.requireSplit(previous, question)) {
				groups.add(new ArrayList<>());
			}
			groups.getLast().add(question);
			previous = question;
		}

		// if not split at all, use the original (complete) form
		if (groups.size() == 1) return completeForm;

		// and build the forms out of it, until a not completely answered form is available
		int groupNumber = 1;
		for (List<Question> questions : groups) {
			if (hasAnyValueUndefined(questions, session)) {
				return new SplittedForm(completeForm, groupNumber, groups.size(), questions);
			}
			groupNumber++;
		}

		// otherwise, if completely answered, there is something unexpected, so use the original form.
		Log.warning("The form is already answered, check behaviour of: " + delegate);
		return completeForm;
	}

	@Override
	public Form getForm(InterviewObject object, Session session) {
		return delegate.getForm(object, session);
	}

	private static class SplittedForm implements Form {

		private final Form delegate;
		private final int groupNumber;
		private final int totalGroupCount;
		private final List<Question> activeQuestions;

		public SplittedForm(Form delegate, int groupNumber, int totalGroupCount, List<Question> activeQuestions) {
			this.delegate = delegate;
			this.groupNumber = groupNumber;
			this.totalGroupCount = totalGroupCount;
			this.activeQuestions = activeQuestions;
		}

		@NotNull
		@Override
		public String getName() {
			return (groupNumber == 1)
					? delegate.getName()
					: (delegate.getName() + "#" + groupNumber);
		}

		@NotNull
		@Override
		public String getPrompt(Locale lang) {
			String prompt = delegate.getPrompt(lang);
			String suffixFormat = " (%d / %d)";
			if (lang != null) {
				if ("DE".equals(lang.getCountry())) suffixFormat = " (%d von %d)";
				if ("EN".equals(lang.getCountry())) suffixFormat = " (%d of %d)";
			}
			return prompt + String.format(suffixFormat, groupNumber, totalGroupCount);
		}

		@Override
		public boolean isEmpty() {
			return activeQuestions.isEmpty();
		}

		@Override
		public List<Question> getActiveQuestions() {
			return activeQuestions;
		}

		@Override
		public QContainer getRoot() {
			return delegate.getRoot();
		}

		@SuppressWarnings("deprecation")
		@Override
		public InterviewObject getInterviewObject() {
			return delegate.getInterviewObject();
		}

		@SuppressWarnings("deprecation")
		@Override
		public String getTitle() {
			return getName();
		}

		@SuppressWarnings("deprecation")
		@Override
		public boolean isNotEmpty() {
			return !isEmpty();
		}
	}

	@FunctionalInterface
	public interface FormSplitter {
		/**
		 * Returns true if the from should be split between the two specified questions.
		 *
		 * @param previous the question before the potential split
		 * @param next the question after the potential split
		 * @return true if the form should be split in between
		 */
		boolean requireSplit(Question previous, Question next);
	}
}
