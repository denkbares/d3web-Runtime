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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

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

	private final Collection<ChoiceID> choiceIDs;

	/**
	 * Constructs a new MultipleChoiceValue
	 * 
	 * @param values the ChoiceID[] for which a new MultipleChoiceValue should
	 *        be instantiated
	 * @throws NullPointerException if a null object was passed in
	 */
	public MultipleChoiceValue(ChoiceID... choiceIDs) {
		if (choiceIDs == null) {
			throw new NullPointerException();
		}
		this.choiceIDs = new LinkedHashSet<ChoiceID>();
		Collections.addAll(this.choiceIDs, choiceIDs);
	}

	/**
	 * Constructs a new MultipleChoiceValue
	 * 
	 * @param values the Collection of ChoiceID for which a new
	 *        MultipleChoiceValue should be instantiated
	 * @throws NullPointerException if a null object was passed in
	 */
	public MultipleChoiceValue(Collection<ChoiceID> choiceIDs) {
		if (choiceIDs == null) {
			throw new NullPointerException();
		}
		this.choiceIDs = new LinkedHashSet<ChoiceID>(choiceIDs);
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
	 * @return true, if the specified choice value is contained as choice in
	 *         this instance
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
		else return false;
	}

	/**
	 * Checks, if the specified {@link Choice} is contained as choice in this
	 * {@link MultipleChoiceValue}.
	 * 
	 * @param choice the Choice to be searched
	 * @return true, if the specified choice value is contained as choice in
	 *         this instance
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
	 * @return true, if the specified choice value is contained as choice in
	 *         this instance
	 * @author volker.belli
	 * @date 08.04.2010
	 */
	public boolean contains(ChoiceID choiceID) {
		return this.choiceIDs.contains(choiceID);
	}

	@Override
	public Object getValue() {
		return choiceIDs;
	}

	@Override
	public int compareTo(Value o) {
		if (o == null) {
			throw new NullPointerException();
		}
		if (o instanceof MultipleChoiceValue) {
			MultipleChoiceValue other = (MultipleChoiceValue) o;
			return choiceIDs.size() - other.choiceIDs.size();
		}
		else {
			return -1;
		}
	}

	public String getName() {
		StringBuffer b = new StringBuffer();
		for (Iterator<ChoiceID> iterator = choiceIDs.iterator(); iterator.hasNext();) {
			ChoiceID choiceID = iterator.next();
			b.append(choiceID.getText());
			if (iterator.hasNext()) {
				b.append(", ");
			}
		}
		return b.toString();
	}

	@Override
	public String toString() {
		return choiceIDs.toString();
	}

	public List<Choice> asChoiceList(QuestionChoice question) {
		List<Choice> choices = new ArrayList<Choice>(choiceIDs.size());
		for (ChoiceID choiceID : choiceIDs) {
			choices.add(choiceID.getChoice(question));
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
		else if (obj instanceof MultipleChoiceValue) {
			MultipleChoiceValue other = (MultipleChoiceValue) obj;
			return (choiceIDs.containsAll(other.choiceIDs) && other.choiceIDs.containsAll(choiceIDs));
		}
		else if (obj instanceof ChoiceValue) {
			if (choiceIDs.size() != 1) {
				return false;
			}
			else {
				ChoiceValue other = (ChoiceValue) obj;
				return choiceIDs.contains(other.getChoiceID());
			}
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + choiceIDs.hashCode();
		return result;
	}
}
