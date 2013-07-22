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

package de.d3web.empiricaltesting;

import java.util.Collection;
import java.util.Locale;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.scoring.HeuristicRating;

public final class CaseUtils {

	private static CaseUtils instance;

	private CaseUtils() {
	}

	public static CaseUtils getInstance() {
		if (instance == null) instance = new CaseUtils();
		return instance;
	}

	public String pretty(String text) {
		if (text.isEmpty()) return "";
		text = text.replaceAll("<", "kleiner");
		text = text.replaceAll(">", "groesser");
		return text;
	}

	public String removeBadChars(String text) {
		String badChars = ": =()[]{},.?/\\-#'";
		for (int i = 0; i < badChars.length(); i++) {
			text = text.replace(badChars.charAt(i), '_');
			text = text.replace("_", "");
		}
		return text;
	}

	public static String getPrompt(NamedObject object) {
		String prompt = object.getInfoStore().getValue(MMInfo.PROMPT, Locale.getDefault());
		if (prompt == null) {
			prompt = object.getName();
		}
		return prompt;
	}

	public static String getPrompt(TerminologyObject object, Value value) {
		if (value instanceof ChoiceValue && object instanceof QuestionChoice) {
			Choice choice = ((ChoiceValue) value).getChoice((QuestionChoice) object);
			return CaseUtils.getPrompt(choice);
		}
		if (value instanceof MultipleChoiceValue && object instanceof QuestionChoice) {
			StringBuilder result = new StringBuilder();
			Collection<ChoiceID> choiceIDs = ((MultipleChoiceValue) value).getChoiceIDs();
			if (choiceIDs.isEmpty()) return "--";
			for (ChoiceID choiceID : choiceIDs) {
				Choice choice = choiceID.getChoice((QuestionChoice) object);
				result.append(", ").append(CaseUtils.getPrompt(choice));
			}
			return result.toString().substring(2);
		}
		return value.toString();
	}

	/**
	 * Returns a state corresponding to the committed score.
	 * 
	 * @param score Rating representing the score of a RatedSolution.
	 * @return DiagnosisState corresponding to the committed scored.
	 */
	public static de.d3web.core.knowledge.terminology.Rating getState(Rating score) {

		if (score instanceof ScoreRating) {
			return new HeuristicRating(((ScoreRating) score).getRating());
		}
		else if (score instanceof StateRating) {
			return ((StateRating) score).getRating();
		}

		return null;
	}
}
