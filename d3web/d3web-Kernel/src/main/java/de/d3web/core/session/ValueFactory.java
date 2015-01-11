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

package de.d3web.core.session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.utils.Log;

public final class ValueFactory {

	private ValueFactory() { // enforce noninstantiability
	}

	/**
	 * Creates a {@link Value} for a {@link Question}. If the given String is no
	 * valid representation for a Value for the given Question, <tt>null</tt>
	 * will be returned.
	 *
	 * @param question    the question for which the {@link Value} is created
	 * @param valueString a String representation of the {@link Value} to be
	 *                    created
	 * @created 11.08.2012
	 * @returns a {@link Value} or <tt>null</tt> if the given String is no valid
	 * representation for a Value for the given Question
	 */
	public static Value createValue(Question question, String valueString) {
		return createValue(question, valueString, null);
	}

	/**
	 * Creates a {@link Value} for a {@link Question}. If the given String is no
	 * valid representation for a Value for the given Question, <tt>null</tt>
	 * will be returned.<br/>
	 * In case of a {@link QuestionMC}, the new Value is merged with the
	 * existing Value (if possible). The existing value is allowed to be
	 * <tt>null</tt>!
	 *
	 * @param question      the question for which the {@link Value} is created
	 * @param valueString   a String representation of the {@link Value} to be
	 *                      created
	 * @param existingValue the existing value for the question to be merged in
	 *                      case of a QuestionMC
	 * @created 11.08.2012
	 * @returns a {@link Value} or <tt>null</tt> if the given String is no valid
	 * representation for a Value for the given Question
	 */
	public static Value createValue(Question question, String valueString, Value existingValue) {

		Value value = null;

		if (valueString.equals(Unknown.getInstance().getValue())) {
			value = Unknown.getInstance();
		}

		else if (question instanceof QuestionChoice) {
			value = createQuestionChoiceValue((QuestionChoice) question, valueString,
					existingValue);
		}

		else if (question instanceof QuestionNum) {
			try {
				value = new NumValue(Double.parseDouble(valueString.replace(',', '.')));
			}
			catch (IllegalArgumentException e) {
				// null will be returned
			}
		}

		else if (question instanceof QuestionText) {
			value = new TextValue(valueString);
		}

		else if (question instanceof QuestionDate) {
			try {
				value = DateValue.createDateValue(valueString);
			}
			catch (IllegalArgumentException ignore) {
				try {
					value = new DateValue(new Date(Long.parseLong(valueString)));
				}
				catch (NumberFormatException e) {
					// null will be returned
				}
			}
		}
		return value;
	}

	/**
	 * Creates a {@link Value} for a {@link QuestionChoice}. If the given String
	 * is no valid representation for a Value for the given Question,
	 * <tt>null</tt> will be returned.<br/>
	 * In case of a {@link QuestionMC}, the new Value is merged with the
	 * existing Value (if possible). The existing value is allowed to be
	 * <tt>null</tt>!
	 *
	 * @param question      the question for which the {@link Value} is created
	 * @param valueString   a String representation of the {@link Value} to be
	 *                      created
	 * @param existingValue the existing value for the question to be merged in
	 *                      case of a QuestionMC
	 * @created 11.08.2012
	 * @returns a {@link Value} or <tt>null</tt> if the given String is no valid
	 * representation for a Value for the given Question
	 */
	public static Value createQuestionChoiceValue(QuestionChoice question, String valueString, Value existingValue) {
		Choice choice = KnowledgeBaseUtils.findChoice(question, valueString);
		Value value = null;
		if (question instanceof QuestionMC) {
			value = createQuestionMCValue((QuestionMC) question, choice, existingValue);
		}
		else if (choice != null) {
			value = new ChoiceValue(choice);
		}
		return value;
	}

	/**
	 * Creates a {@link Value} for a {@link QuestionChoice}. If the given String
	 * is no valid representation for a Value for the given Question,
	 * <tt>null</tt> will be returned.<br/>
	 *
	 * @param question    the question for which the {@link Value} is created
	 * @param valueString a String representation of the {@link Value} to be
	 *                    created
	 * @created 11.08.2012
	 * @returns a {@link Value} or <tt>null</tt> if the given String is no valid
	 * representation for a Value for the given Question
	 */
	public static Value createQuestionChoiceValue(QuestionChoice question, String valueString) {
		Value value = null;
		Choice choice = KnowledgeBaseUtils.findChoice(question, valueString);
		if (question instanceof QuestionMC) {
			value = createQuestionMCValue((QuestionMC) question, choice, null);
		}
		else if (choice != null) {
			value = new ChoiceValue(choice);
		}
		return value;
	}

	private static Value createQuestionMCValue(QuestionMC question, Choice choice, Value existingValue) {
		Value value;
		List<Choice> choices = new ArrayList<Choice>();
		if (existingValue instanceof ChoiceValue) {
			Choice existingChoice = ((ChoiceValue) existingValue)
					.getChoice(question);
			choices.add(existingChoice);
		}
		else if (existingValue instanceof MultipleChoiceValue) {
			try {
				List<Choice> temp = ((MultipleChoiceValue) existingValue)
						.asChoiceList(question);
				choices.addAll(temp);
			}
			catch (IllegalArgumentException e) {
				Log.warning(e.getMessage());
			}
		}
		if (choice != null && !choices.remove(choice)) {
			choices.add(choice);
		}
		if (choices.isEmpty()) {
			value = Unknown.getInstance();
		}
		else {
			value = MultipleChoiceValue.fromChoices(choices);
		}
		return value;
	}

	public static String getID_or_Value(Value value) { // NOSONAR this method
		// name is ok
		if (value instanceof ChoiceValue) {
			return ((ChoiceValue) value).getChoiceID().getText();
		}
		else if (value instanceof Unknown) {
			return Unknown.UNKNOWN_ID;
		}
		else if (value instanceof UndefinedValue) {
			return UndefinedValue.UNDEFINED_ID;
		}
		else {
			return value.getValue().toString();
		}
	}
}
