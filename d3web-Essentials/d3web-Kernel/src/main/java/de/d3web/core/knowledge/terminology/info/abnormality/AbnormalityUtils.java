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

import com.denkbares.strings.Strings;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Value;

public class AbnormalityUtils {

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
				return Abnormality.A0;
			}
			else if (d == '1') {
				return Abnormality.A1;
			}
			else if (d == '2') {
				return Abnormality.A2;
			}
			else if (d == '3') {
				return Abnormality.A3;
			}
			else if (d == '4') {
				return Abnormality.A4;
			}
			else if (d == '5') {
				return Abnormality.A5;
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
		return (value == Abnormality.A0) ? "A0"
				: (value == Abnormality.A1) ? "A1"
				: (value == Abnormality.A2) ? "A2"
				: (value == Abnormality.A3) ? "A3"
				: (value == Abnormality.A4) ? "A4"
				: (value == Abnormality.A5) ? "A5"
				: String.valueOf(value);
	}

	public static double convertConstantStringToValue(String c) {
		if ("A0".equalsIgnoreCase(c)) {
			return Abnormality.A0;
		}
		else if ("A1".equalsIgnoreCase(c)) {
			return Abnormality.A1;
		}
		else if ("A2".equalsIgnoreCase(c)) {
			return Abnormality.A2;
		}
		else if ("A3".equalsIgnoreCase(c)) {
			return Abnormality.A3;
		}
		else if ("A4".equalsIgnoreCase(c)) {
			return Abnormality.A4;
		}
		else if ("A5".equalsIgnoreCase(c)) {
			return Abnormality.A5;
		}
		else {
			return Abnormality.A0;
		}
	}

	public static String convertValueToConstantString(double value) {
		if (value < Abnormality.A1) {
			return "A0";
		}
		else if (value < Abnormality.A2) {
			return "A1";
		}
		else if (value < Abnormality.A3) {
			return "A2";
		}
		else if (value < Abnormality.A4) {
			return "A3";
		}
		else if (value < Abnormality.A5) {
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
		return Abnormality.A5;
	}
}
