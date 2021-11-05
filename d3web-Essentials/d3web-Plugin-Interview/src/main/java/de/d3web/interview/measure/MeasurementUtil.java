/*
 * Copyright (C) 2021 denkbares GmbH, Germany
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

package de.d3web.interview.measure;

import org.jetbrains.annotations.Nullable;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;

/**
 * Some common util method regarding measuremtns
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 05.11.21
 */
public class MeasurementUtil {

	public static final String NUMERIC_SUFFIX = "_numeric";

	@Nullable
	public static Question getNumericMeasurementCompanionQuestion(@Nullable Question question) {
		if (question == null) return null;
		return question.getKnowledgeBase()
				.getManager()
				.searchQuestion(question.getName() + NUMERIC_SUFFIX);
	}

	@Nullable
	public static Question getChoiceMeasurementCompanionQuestion(@Nullable Question question) {
		if (question == null) return null;
		String name = question.getName();
		if (!name.endsWith(NUMERIC_SUFFIX)) return null;
		return question.getKnowledgeBase()
				.getManager()
				.searchQuestion(name.substring(0, name.length() - NUMERIC_SUFFIX.length()));
	}

	@Nullable
	public static Question getMeasurementCompanionQuestion(@Nullable Question question) {
		if (question instanceof QuestionChoice) {
			return getNumericMeasurementCompanionQuestion(question);
		}
		else {
			return getChoiceMeasurementCompanionQuestion(question);
		}
	}

	/**
	 * Get the measurement from the given question. This method also returns the measurement for questions that don't
	 * themselves have the question but are companion questions of the question with the actual measurement.
	 *
	 * @param question the question (or its companion) to get the measurement from
	 * @return the measurement of the given (or its companion's) question
	 */
	@Nullable
	public static Measurement getMeasurement(Question question) {
		Measurement measurement = Measurement.getMeasurement(question);
		if (measurement == null) {
			measurement = Measurement.getMeasurement(getNumericMeasurementCompanionQuestion(question));
		}
		return measurement;
	}

	/**
	 * Check if the given question has/belongs to a question. This also return true, if the measurement is stored for
	 * the measurement companion question of the given question.
	 *
	 * @param question the question to check if it (or its companion) has a measurement
	 * @return true if the question (or its companion) has a measurement, false otherwise
	 */
	public static boolean hasMeasurement(@Nullable Question question) {
		boolean measurement = Measurement.hasMeasurement(question);
		if (!measurement) {
			measurement = Measurement.hasMeasurement(getNumericMeasurementCompanionQuestion(question));
		}
		return measurement;
	}
}
