/*
 * Copyright (C) 2010 denkbares GmbH, Germany
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

import java.util.Collection;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.utils.HashCodeUtils;

/**
 * This class implements a reference to a choice within a knowledge base
 * 
 * @author volker_belli
 * @created 22.10.2010
 */
public class ChoiceID implements Comparable<ChoiceID> {

	private final String text;
	public static final String ID_SEPARATOR = "#####";

	/**
	 * Constructs a new ChoiceID from a specified choice text
	 * 
	 * @param value the choice text for which a new ChoiceID should be
	 *        instantiated
	 * @throws NullPointerException if a null object was passed in
	 */
	public ChoiceID(String text) {
		if (text == null) {
			throw new NullPointerException();
		}
		this.text = text;
	}

	/**
	 * Constructs a new ChoiceID from a specified {@link Choice}
	 * 
	 * @param value the choice for which a new ChoiceID should be instantiated
	 * @throws NullPointerException if a null object was passed in
	 */
	public ChoiceID(Choice choice) {
		this(choice.getName());
	}

	public String getText() {
		return text;
	}

	/**
	 * Searches and returns the {@link Choice} within a question that was
	 * specified by this choiceID. Returns null if no matching choice has been
	 * found.
	 * 
	 * @created 22.10.2010
	 * @param question the question to be searched for the ChoiceID
	 * @return the Choice found
	 */
	public Choice getChoice(QuestionChoice question) {
		for (Choice choice : question.getAllAlternatives()) {
			if (text.equals(choice.getName())) {
				return choice;
			}
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null) return false;
		if (this.getClass() != obj.getClass()) return false;
		return this.text.equals(((ChoiceID) obj).text);
	}

	@Override
	public int hashCode() {
		int result = HashCodeUtils.SEED;
		result = HashCodeUtils.hash(result, text);
		return result;
	}

	@Override
	public int compareTo(ChoiceID o) {
		if (o == null) return -1;
		return this.text.compareTo(o.text);
	}

	public static String encodeChoiceIDs(Collection<ChoiceID> chocieIDs) {
		return encodeChoiceIDs(chocieIDs.toArray(new ChoiceID[chocieIDs.size()]));
	}

	public static String encodeChoiceIDs(ChoiceID[] chocieIDs) {
		String id = "";
		for (ChoiceID choiceID : chocieIDs) {
			id += choiceID.getText() + ID_SEPARATOR;
		}
		if (id.length() > ID_SEPARATOR.length()) {
			id = id.substring(0, id.length() - ID_SEPARATOR.length());
		}
		return id;
	}

	public static ChoiceID[] decodeChoiceIDs(String encodedString) {
		String[] strings = encodedString.split(ID_SEPARATOR);
		ChoiceID[] choiceIDs = new ChoiceID[strings.length];
		for (int i = 0; i < choiceIDs.length; i++) {
			choiceIDs[i] = new ChoiceID(strings[i]);
		}
		return choiceIDs;
	}

	/**
	 * Returns if the string is a list mire than one of encoded ChoiceIDs.
	 * 
	 * @created 22.10.2010
	 * @param encodedString the string to be checked
	 * @return if the string consists of multiple encoded ChoiceIDs
	 */
	public static boolean isEncodedChoiceIDs(String encodedString) {
		return encodedString.contains(ID_SEPARATOR);
	}

	@Override
	public String toString() {
		return text;
	}
}
