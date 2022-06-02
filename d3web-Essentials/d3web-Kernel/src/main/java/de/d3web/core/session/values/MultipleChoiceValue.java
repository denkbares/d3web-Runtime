/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.core.session.values;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.denkbares.strings.Strings;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.session.QuestionValue;
import de.d3web.core.session.Value;

/**
 * This class represents the container for multiple values that can be given to
 * a {@link QuestionMC}.
 *
 * @author joba (denkbares GmbH)
 * @created 07.04.2010
 */
public class MultipleChoiceValue implements QuestionValue {
	private static final Pattern QUOTE_CHECK = Pattern.compile("[,\"\\[\\]\\\\]");
	private final Collection<ChoiceID> choiceIDs;

	/**
	 * Constructs a new MultipleChoiceValue
	 *
	 * @param choiceIDs the ChoiceID[] for which a new MultipleChoiceValue should be instantiated
	 * @throws NullPointerException if a null object was passed in
	 */
	public MultipleChoiceValue(ChoiceID... choiceIDs) {
		if (choiceIDs == null) {
			throw new NullPointerException();
		}
		this.choiceIDs = new LinkedHashSet<>();
		Collections.addAll(this.choiceIDs, choiceIDs);
	}

	/**
	 * Constructs a new MultipleChoiceValue
	 *
	 * @param choiceIDs the Collection of ChoiceID for which a new MultipleChoiceValue should be
	 *                  instantiated
	 * @throws NullPointerException if a null object was passed in
	 */
	public MultipleChoiceValue(Collection<ChoiceID> choiceIDs) {
		if (choiceIDs == null) {
			throw new NullPointerException();
		}
		this.choiceIDs = new LinkedHashSet<>(choiceIDs);
	}

	public static MultipleChoiceValue fromChoicesValues(List<ChoiceValue> choices) {
		ChoiceID[] choiceIDs = new ChoiceID[choices.size()];
		int index = 0;
		for (ChoiceValue choice : choices) {
			choiceIDs[index] = choice.getChoiceID();
		}
		return new MultipleChoiceValue(choiceIDs);
	}

	public static MultipleChoiceValue fromChoices(List<Choice> choices) {
		ChoiceID[] choiceIDs = new ChoiceID[choices.size()];
		int index = 0;
		for (Choice choice : choices) {
			choiceIDs[index++] = new ChoiceID(choice);
		}
		return new MultipleChoiceValue(choiceIDs);
	}

	public static MultipleChoiceValue fromChoices(Choice... choices) {
		return fromChoices(Arrays.asList(choices));
	}

	public Collection<ChoiceID> getChoiceIDs() {
		return Collections.unmodifiableCollection(this.choiceIDs);
	}

	/**
	 * Checks, if all choices are included in the other
	 * {@link MultipleChoiceValue}.
	 *
	 * @param other another {@link MultipleChoiceValue}
	 * @return true, if all choice values are contained in the other value
	 * @author joba
	 * @date 08.04.2010
	 */
	public boolean containsAll(MultipleChoiceValue other) {
		return this.choiceIDs.containsAll(other.choiceIDs);
	}

	/**
	 * Checks, if the choice(s) of the specified {@link Value} is contained in
	 * this {@link MultipleChoiceValue}. The value may either be a
	 * {@link ChoiceValue} or a {@link MultipleChoiceValue}
	 *
	 * @param value the ChoiceValue to be searched
	 * @return true, if the specified choice value is contained as choice in this instance
	 * @author joba, volker.belli
	 * @date 08.04.2010
	 */
	public boolean contains(Value value) {
		if (value instanceof ChoiceValue) {
			return contains(((ChoiceValue) value).getChoiceID());
		}
		else if (value instanceof MultipleChoiceValue) {
			return containsAll((MultipleChoiceValue) value);
		}
		else {
			return false;
		}
	}

	/**
	 * Checks, if the specified {@link Choice} is contained as choice in this
	 * {@link MultipleChoiceValue}.
	 *
	 * @param choice the Choice to be searched
	 * @return true, if the specified choice value is contained as choice in this instance
	 * @author volker.belli
	 * @date 08.04.2010
	 */
	public boolean contains(Choice choice) {
		return contains(new ChoiceID(choice));
	}

	/**
	 * Checks, if the specified {@link ChoiceID} is contained as choice in this
	 * {@link MultipleChoiceValue}.
	 *
	 * @param choiceID the ChoiceID to be searched
	 * @return true, if the specified choice value is contained as choice in this instance
	 * @author volker.belli
	 * @date 08.04.2010
	 */
	public boolean contains(ChoiceID choiceID) {
		return this.choiceIDs.contains(choiceID);
	}

	/**
	 * @return the {@link Collection} of {@link ChoiceID}s of this multiple choice value
	 */
	@Override
	public Object getValue() {
		return choiceIDs;
	}

	@Override
	public int compareTo(Value o) {
		if (o == null) {
			throw new NullPointerException();
		}
		if (o instanceof MultipleChoiceValue other) {
			return choiceIDs.size() - other.choiceIDs.size();
		}
		else {
			return -1;
		}
	}

	public String getName() {
		return getChoiceIDs()
				.stream()
				.map(ChoiceID::getText)
				.map(this::quoteIfNecessary)
				.collect(Collectors.joining(", "));
	}

	@Override
	public String toString() {
		return "[" + getName() + "]";
	}

	private String quoteIfNecessary(String choiceName) {
		if (QUOTE_CHECK.matcher(choiceName).find()) {
			return Strings.quote(choiceName);
		}
		return choiceName;
	}

	/**
	 * Returns the choices of the specified question that are reference by this multiple choice
	 * value. If any of the choices are not available in the specified question, an {@link
	 * IllegalArgumentException} is thrown.
	 *
	 * @param question the question to get the choice from
	 * @return the choice of this value
	 */
	@NotNull
	public List<Choice> asChoiceList(QuestionChoice question) {
		List<Choice> choices = new ArrayList<>(choiceIDs.size());
		for (ChoiceID choiceID : choiceIDs) {
			Choice choice = choiceID.getChoice(question);
			if (choice == null) {
				throw new IllegalArgumentException("The question '"
						+ question.getName()
						+ "' does not contain the choiceID '"
						+ choiceID.getText()
						+ "'.");
			}
			choices.add(choice);
		}
		return choices;
	}

	/**
	 * Checks, if the specified object is a {@link ChoiceValue} or a
	 * {@link MultipleChoiceValue} and then checks the included values. It is
	 * important to notice, that this method also returns true, when a
	 * {@link ChoiceValue} is compared with a {@link MultipleChoiceValue} and
	 * both contain the same single choice.
	 *
	 * @author joba
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		else if (obj == null) {
			return false;
		}
		else if (obj instanceof MultipleChoiceValue other) {
			return (choiceIDs.containsAll(other.choiceIDs) && other.choiceIDs.containsAll(choiceIDs));
		}
		else if (obj instanceof ChoiceValue) {
			if (choiceIDs.size() == 1) {
				ChoiceValue other = (ChoiceValue) obj;
				return choiceIDs.contains(other.getChoiceID());
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		// must be identical to ChoiceValue if having only one alternative
		// to not violate the hashCode/equals contract
		final int prime = 31;
		int result = 1;
		for (ChoiceID choice : choiceIDs) {
			result = prime * result + choice.hashCode();
		}
		return result;
	}
}
