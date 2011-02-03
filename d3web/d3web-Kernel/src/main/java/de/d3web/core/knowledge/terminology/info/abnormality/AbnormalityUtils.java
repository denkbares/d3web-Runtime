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

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Value;

public class AbnormalityUtils {

	public static double convertConstantStringToValue(String c) {
		if (c.equalsIgnoreCase("A0")) {
			return Abnormality.A0;
		}
		else if (c.equalsIgnoreCase("A1")) {
			return Abnormality.A1;
		}
		else if (c.equalsIgnoreCase("A2")) {
			return Abnormality.A2;
		}
		else if (c.equalsIgnoreCase("A3")) {
			return Abnormality.A3;
		}
		else if (c.equalsIgnoreCase("A4")) {
			return Abnormality.A4;
		}
		else if (c.equalsIgnoreCase("A5")) {
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
	 * @created 25.06.2010
	 * @param q Question
	 * @param v Value
	 * @return Abnormality
	 */
	public static double getAbnormality(Question q, Value v) {
		Abnormality abnormality;
		if (q instanceof QuestionNum) {
			abnormality = q.getInfoStore().getValue(BasicProperties.ABNORMALITIY_NUM);
		}
		else {
			abnormality = q.getInfoStore().getValue(BasicProperties.DEFAULT_ABNORMALITIY);
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
	 * @created 25.06.2010
	 * @return default abnormality
	 */
	public static double getDefault() {
		return Abnormality.A5;
	}

}
