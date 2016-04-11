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

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.session.QuestionValue;
import de.d3web.core.session.Value;

/**
 * This class represents a choice entered by a user during a dialog session.
 * 
 * @author joba
 * 
 */
public class ChoiceValue implements QuestionValue {

	private final ChoiceID choiceID;

	/**
	 * Constructs a new ChoiceValue from a specified choice
	 * 
	 * @param choice the Choice for which a new ChoiceValue should be
	 *        instantiated
	 * @throws NullPointerException if a null object was passed in
	 */
	public ChoiceValue(Choice choice) {
		this(new ChoiceID(choice));
	}

	/**
	 * Constructs a new ChoiceValue from a specified choice text
	 * 
	 * @param text the choice text for which a new ChoiceValue should be
	 *        instantiated
	 * @throws NullPointerException if a null object was passed in
	 */
	public ChoiceValue(String text) {
		this(new ChoiceID(text));
	}

	/**
	 * Constructs a new ChoiceValue from a specified {@link ChoiceID}
	 * 
	 * @param choiceID the {@link ChoiceID} for which a new ChoiceValue should be
	 *        instantiated
	 * @throws NullPointerException if a null object was passed in
	 */
	public ChoiceValue(ChoiceID choiceID) {
		if (choiceID == null) {
			throw new NullPointerException();
		}
		this.choiceID = choiceID;
	}

	/**
	 * @return the current {@link ChoiceID} of this choice value
	 */
	@Override
	public Object getValue() {
		return choiceID;
	}

	public ChoiceID getChoiceID() {
		return choiceID;
	}

	public Choice getChoice(QuestionChoice question) {
		return choiceID.getChoice(question);
	}

	public String getAnswerChoiceID() {
		return choiceID.getText();
	}

	@Override
	public String toString() {
		return choiceID.toString();
	}

	@Override
	public int hashCode() {
		// must be identical to MultipleChoiceValue with one alternative
		// to not violate the hashCode/equals contract
		final int prime = 31;
		int result = 1;
		result = prime * result + choiceID.hashCode();
		return result;
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
		else if (obj instanceof MultipleChoiceValue) {
			MultipleChoiceValue other = (MultipleChoiceValue) obj;
			return other.getChoiceIDs().size() == 1 && other.getChoiceIDs().contains(choiceID);
		}
		else if (obj instanceof ChoiceValue) {
			ChoiceValue other = (ChoiceValue) obj;
			return choiceID.equals(other.getChoiceID());
		}
		else {
			return false;
		}
	}

	@Override
	public int compareTo(Value o) {
		if (o instanceof ChoiceValue) {
			return this.choiceID.compareTo(((ChoiceValue) o).choiceID);
		}
		// there is no possibility to compare ChoiceValue since
		// we do not know the other ChoiceValue instances
		return 0;
	}

}
