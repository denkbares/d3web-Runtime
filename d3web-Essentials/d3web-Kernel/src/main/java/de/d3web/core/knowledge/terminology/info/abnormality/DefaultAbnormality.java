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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.denkbares.strings.StringFragment;
import com.denkbares.strings.Strings;
import de.d3web.core.knowledge.InfoStore;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;

/**
 * Represents the abnormality of a symptom Creation date: (06.08.2001 15:51:58)
 *
 * @author Norman Brümmer
 */
public class DefaultAbnormality implements Abnormality {

	private final Map<ChoiceID, Double> abnormalities = new HashMap<>();

	/**
	 * This method sets the abnormality for the specified choice.
	 *
	 * @param choice      the choice to set the abnormality for
	 * @param abnormality the abnormality to be set
	 */
	public void addValue(Choice choice, double abnormality) {
		addValue(new ChoiceID(choice), abnormality);
	}

	/**
	 * This method sets the abnormality for the specified choice.
	 *
	 * @param choice      the choice to set the abnormality for
	 * @param abnormality the abnormality to be set
	 */
	public void addValue(ChoiceID choice, double abnormality) {
		abnormalities.put(choice, abnormality);
	}

	/**
	 * This method sets the abnormality for the choice(s) of the specified value.
	 *
	 * @param value       the choice value or multiple choice value to set the abnormality for
	 * @param abnormality the abnormality to be set
	 */
	public void addValue(Value value, double abnormality) {
		if (value instanceof MultipleChoiceValue) {
			MultipleChoiceValue mcv = (MultipleChoiceValue) value;
			for (ChoiceID cid : mcv.getChoiceIDs()) {
				addValue(cid, abnormality);
			}
		}
		else if (value instanceof ChoiceValue) {
			addValue(((ChoiceValue) value).getChoiceID(), abnormality);
		}
		else {
			throw new IllegalArgumentException("unsupported question value: " + value);
		}
	}

	/**
	 * Returns the abnormality to the given value. For multiple choice values, the abnormality will be constructed from
	 * the abnormalities of the particular choices. If no abnormality is specified for the value, {@link Abnormality#A5}
	 * will be returned.
	 *
	 * @param value the value to get the abnormality for
	 * @return the abnormality value
	 */
	@Override
	public double getValue(Value value) {
		if (value instanceof ChoiceValue) {
			return abnormalities.getOrDefault(((ChoiceValue) value).getChoiceID(), A5);
		}
		else if (value instanceof MultipleChoiceValue) {
			double max = A0;
			Collection<ChoiceID> choiceIDs = ((MultipleChoiceValue) value).getChoiceIDs();
			for (ChoiceID choiceID : choiceIDs) {
				double abnorm = abnormalities.getOrDefault(choiceID, A5);
				if (abnorm >= A5) return A5;
				max = Math.max(max, abnorm);
			}
			return max;
		}
		else {
			return A5;
		}
	}

	/**
	 * Returns the abnormality to the given choice. If no abnormality is specified for the choice, {@link
	 * Abnormality#A5} will be returned.
	 *
	 * @param value the choice to get the abnormality for
	 * @return the abnormality value
	 */
	public double getValue(ChoiceID value) {
		return abnormalities.getOrDefault(value, A5);
	}

	/**
	 * Returns the abnormality to the given choice. If no abnormality is specified for the choice, {@link
	 * Abnormality#A5} will be returned.
	 *
	 * @param value the choice to get the abnormality for
	 * @return the abnormality value
	 */
	public double getValue(Choice value) {
		return getValue(new ChoiceID(value));
	}

	@Override
	public boolean isSet(Value value) {
		if (value instanceof ChoiceValue) {
			return abnormalities.containsKey(((ChoiceValue) value).getChoiceID());
		}
		else if (value instanceof MultipleChoiceValue) {
			return abnormalities.keySet().containsAll(((MultipleChoiceValue) value).getChoiceIDs());
		}
		else {
			return false;
		}
	}

	public Set<ChoiceID> getChoicesSet() {
		return abnormalities.keySet();
	}

	/**
	 * Sets the Abnormality of the Question for the given the Value
	 *
	 * @param question    the question to set the abnormality for
	 * @param value       the value to set the abnormality for
	 * @param abnormality the abnormality value to be set
	 * @created 25.06.2010
	 */
	public static void setAbnormality(Question question, Value value, double abnormality) {
		InfoStore infoStore = question.getInfoStore();
		DefaultAbnormality abnormalitySlice = infoStore.getValue(BasicProperties.DEFAULT_ABNORMALITY);
		if (abnormalitySlice == null) {
			abnormalitySlice = new DefaultAbnormality();
			infoStore.addValue(BasicProperties.DEFAULT_ABNORMALITY, abnormalitySlice);
		}
		abnormalitySlice.addValue(value, abnormality);
	}

	/**
	 * Sets the Abnormality of the choice question for the given the choice.
	 *
	 * @param question    the question to set the abnormality for
	 * @param choice      the choice to set the abnormality for
	 * @param abnormality the abnormality value to be set
	 * @created 25.06.2010
	 */
	public static void setAbnormality(QuestionChoice question, Choice choice, double abnormality) {
		setAbnormality(question, new ChoiceValue(choice), abnormality);
	}

	/**
	 * Gets the abnormality for the given value for the given question. If an abnormality was set for the question, this
	 * method will always return an abnormality. If non was set specifically for the given value, {@link Abnormality#A5}
	 * will be returned. However, if not abnormality was set of any value of the given question, <tt>null</tt> will be
	 * returned.
	 *
	 * @param question the question with the abnormality
	 * @param value    the value to the abnormality for
	 * @return the abnormality for the given question and value, can be null was set
	 */
	public static @Nullable Double getAbnormality(Question question, Value value) {
		DefaultAbnormality abnormality = question.getInfoStore().getValue(BasicProperties.DEFAULT_ABNORMALITY);
		return (abnormality == null) ? null : abnormality.getValue(value);
	}

	/**
	 * Parses the abnormality from the given string representation. The String representation is a ";"-separated list
	 * of
	 * <code>&lt;choice-id&gt;:&lt;value&gt</code>. Spaces in between the individual separators are allowed. The
	 * abnormality value after the ':' can be as specified in {@link AbnormalityUtils#parseAbnormalityValue(String)}.
	 *
	 * @param s the string representation
	 * @return the parsed abnormality
	 */
	public static DefaultAbnormality valueOf(String s) {
		DefaultAbnormality defaultAbnormality = new DefaultAbnormality();
		for (StringFragment part : Strings.splitUnquoted(s, ";")) {
			String abnormalityString = part.toString();
			if (abnormalityString.trim().isEmpty()) continue;
			int lastColon = abnormalityString.lastIndexOf(":");
			String valueString = abnormalityString.substring(lastColon + 1);
			double abnormality = AbnormalityUtils.parseAbnormalityValue(valueString);
			ChoiceID choiceID = new ChoiceID(Strings.unquote(
					abnormalityString.substring(0, lastColon).trim()));
			defaultAbnormality.addValue(new ChoiceValue(choiceID), abnormality);
		}
		return defaultAbnormality;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Entry<ChoiceID, Double> entry : abnormalities.entrySet()) {
			sb.append(entry.getKey().getText());
			sb.append(":");
			sb.append(entry.getValue());
			sb.append(";");
		}
		if (sb.length() > 0) {
			return sb.substring(0, sb.length() - 1);
		}
		else {
			return "";
		}
	}
}
