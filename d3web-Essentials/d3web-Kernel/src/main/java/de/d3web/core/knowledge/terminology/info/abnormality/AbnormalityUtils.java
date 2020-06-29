/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.core.knowledge.terminology.info.abnormality;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.denkbares.strings.Strings;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;

import static de.d3web.core.knowledge.terminology.info.abnormality.Abnormality.*;

public class AbnormalityUtils {

	public static State getNormality(QuestionNum question, NumValue value) {
		Abnormality abnormality = BasicProperties.getAbnormality(question);
		if (!abnormality.isSet(value)) return State.NEUTRAL;
		double doubleAbnormality = abnormality.getValue(value);
		return doubleAbnormality <= MAX_NORMAL_LIMIT ? State.NORMAL
				: doubleAbnormality >= MIN_ABNORMAL_LIMIT ? State.ABNORMAL
				: State.NEUTRAL;
	}

	public static State getNormality(QuestionMC question, MultipleChoiceValue value) {
		Set<State> normalities = value.getChoiceIDs()
				.stream()
				.map(choiceID -> choiceID.getChoice(question))
				.map(AbnormalityUtils::getNormality)
				.collect(Collectors.toSet());
		return normalities.contains(State.ABNORMAL) ?
				State.ABNORMAL : normalities.contains(State.NORMAL) ?
				State.NORMAL : State.NEUTRAL;
	}

	public static State getNormality(Choice choice) {
		// check if choice is declared as normal
		Abnormality abnormality = BasicProperties.getAbnormality(choice.getQuestion());
		ChoiceValue choiceValue = new ChoiceValue(choice);
		State normality;

		if (abnormality.isSet(choiceValue)) {
			double norm = abnormality.getValue(choiceValue);
			normality = norm <= MAX_NORMAL_LIMIT ? State.NORMAL : State.ABNORMAL;
		}
		else if (choice.isAnswerYes()) {
			// handle yes of "yes-no-questions" as normal by default if not specified
			normality = State.NORMAL;
		}
		else if (choice.isAnswerNo()) {
			// handle yes of "yes-no-questions" as abnormal by default if not specified
			normality = State.ABNORMAL;
		}
		else {
			normality = State.NEUTRAL;
		}

		return normality;
	}

	/**
	 * Gets all answers with abnormality A0 of a question.
	 * @param question the question to get the normal answers for
	 * @return the normal answers of a question
	 */
	public static List<Choice> getNormalAnswers(QuestionChoice question) {
		Abnormality abnormality = BasicProperties.getAbnormality(question);
		List<Choice> normalAnswers = new ArrayList<>();
		for (Choice choice : question.getAllAlternatives()) {
			if (abnormality.getValue(new ChoiceValue(choice)) == A0) {
				normalAnswers.add(choice);
			}
		}
		return normalAnswers;
	}

	/**
	 * Parses an abnormality value of any of the specified forms:
	 * <ul>
	 * <li>Numeric value: 0.0 ... 1.0</li>
	 * <li>Numeric value with ',': 0,0 ... 1,0</li>
	 * <li>Abnormality constant: A0 ... A5</li>
	 * <li>Percentage: 0% ... 100%</li>
	 * </ul>
	 *
	 * @param text the text to be parsed
	 * @return the abnormality value as a double number
	 */
	public static double parseAbnormalityValue(String text) {
		if (Strings.isBlank(text)) return 0.0;
		text = Strings.trim(text);

		if (Strings.startsWithIgnoreCase(text, "A")) {
			if (text.length() != 2) {
				throw new IllegalArgumentException("Not a valid abnormality constant: " + text);
			}
			char d = text.charAt(1);
			if (d == '0') {
				return A0;
			}
			else if (d == '1') {
				return A1;
			}
			else if (d == '2') {
				return A2;
			}
			else if (d == '3') {
				return A3;
			}
			else if (d == '4') {
				return A4;
			}
			else if (d == '5') {
				return A5;
			}
			throw new IllegalArgumentException("Not a valid abnormality constant: " + text);
		}

		if (text.endsWith("%")) {
			return Double.parseDouble(Strings.trim(text.substring(0, text.length() - 1))) / 100;
		}

		// parse number
		return Double.parseDouble(text.replace(',', '.'));
	}

	public static String toAbnormalityValueString(double value) {
		//noinspection FloatingPointEquality
		return (value == A0) ? "A0"
				: (value == A1) ? "A1"
				: (value == A2) ? "A2"
				: (value == A3) ? "A3"
				: (value == A4) ? "A4"
				: (value == A5) ? "A5"
				: String.valueOf(value);
	}

	public static double convertConstantStringToValue(String c) {
		if ("A0".equalsIgnoreCase(c)) {
			return A0;
		}
		else if ("A1".equalsIgnoreCase(c)) {
			return A1;
		}
		else if ("A2".equalsIgnoreCase(c)) {
			return A2;
		}
		else if ("A3".equalsIgnoreCase(c)) {
			return A3;
		}
		else if ("A4".equalsIgnoreCase(c)) {
			return A4;
		}
		else if ("A5".equalsIgnoreCase(c)) {
			return A5;
		}
		else {
			return A0;
		}
	}

	public static String convertValueToConstantString(double value) {
		if (value < A1) {
			return "A0";
		}
		else if (value < A2) {
			return "A1";
		}
		else if (value < A3) {
			return "A2";
		}
		else if (value < A4) {
			return "A3";
		}
		else if (value < A5) {
			return "A4";
		}
		else {
			return "A5";
		}
	}

	/**
	 * Returns the Abnormality of the Question for the given Value
	 *
	 * @param q Question
	 * @param v Value
	 * @return Abnormality
	 * @created 25.06.2010
	 */
	public static double getAbnormality(Question q, Value v) {
		Abnormality abnormality;
		if (q instanceof QuestionNum) {
			abnormality = q.getInfoStore().getValue(BasicProperties.ABNORMALITY_NUM);
		}
		else {
			abnormality = q.getInfoStore().getValue(BasicProperties.DEFAULT_ABNORMALITY);
		}
		if (abnormality != null) {
			return abnormality.getValue(v);
		}
		else {
			return getDefault();
		}
	}

	/**
	 * Returns the Default Abnormality
	 *
	 * @return default abnormality
	 * @created 25.06.2010
	 */
	public static double getDefault() {
		return A5;
	}
}
