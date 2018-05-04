/*
 * Copyright (C) 2018 denkbares GmbH. All rights reserved.
 */

package de.d3web.core.inference.condition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.UndefinedValue;

/**
 * Condition that is true, if any of the specified choices is set in the choice question.
 * <p>
 * Currently this condition cannot be persisted. It is used to optimize existing CondOr on-the-fly.
 *
 * @@author Volker Belli (denkbares GmbH)
 * @created 04.05.2018
 */
public class CondAnyChoice extends CondQuestion {
	private final Collection<String> choiceNames = new HashSet<>();

	/**
	 * Creates a new condition that evaluates to true if any of the choices is selected.
	 *
	 * @param question the question to check
	 * @param choices  the value the question needs to be assigned to
	 * @throws IllegalArgumentException if the Value is instance of {@link UndefinedValue}
	 */
	public CondAnyChoice(QuestionChoice question, Collection<ChoiceID> choices) {
		super(question);
		for (ChoiceID choice : choices) {
			choiceNames.add(choice.getText());
		}
	}

	@Override
	public boolean eval(Session session)
			throws NoAnswerException, UnknownAnswerException {
		Value value = checkAnswer(session);
		if (value instanceof ChoiceValue) {
			return choiceNames.contains(((ChoiceValue) value).getAnswerChoiceID());
		}
		if (value instanceof MultipleChoiceValue) {
			return ((MultipleChoiceValue) value).getChoiceIDs().stream()
					.anyMatch(c -> choiceNames.contains(c.getText()));
		}
		return false;
	}

	@Override
	public String toString() {
		return getQuestion().getName() + " = " + choiceNames.stream().sorted().collect(Collectors.joining(" OR "));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CondAnyChoice)) return false;
		if (!super.equals(o)) return false;
		CondAnyChoice that = (CondAnyChoice) o;
		return Objects.equals(choiceNames, that.choiceNames);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), choiceNames);
	}
}