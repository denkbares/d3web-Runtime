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

package de.d3web.shared;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Value;

/**
 * Represents the abnormality of a symptom Creation date: (06.08.2001 15:51:58)
 * 
 * @author: Norman Brümmer
 */
public class Abnormality extends AbstractAbnormality {

	private final Map<Value, Double> values = new HashMap<Value, Double>();

	/**
	 * with this method you can add an answer-abnorm.Value pair Creation date:
	 * (06.08.2001 16:25:46)
	 * 
	 * @param ans de.d3web.kernel.domainModel.Answer
	 * @param value double
	 */
	public void addValue(Value ans, double value) {
		values.put(ans, new Double(value));
	}

	/**
	 * Returns the abnormality to the given answer Creation date: (06.08.2001
	 * 16:28:14)
	 * 
	 * @return double
	 * @param ans de.d3web.kernel.domainModel.Answer
	 */
	@Override
	public double getValue(Value ans) {
		Double ret = values.get(ans);
		if (ret != null) {
			return ret.doubleValue();
		}

		return A5;
	}

	public boolean isSet(Value ans) {
		return (values.get(ans) != null);
	}

	public Set<Value> getAnswerSet() {
		return values.keySet();
	}

	/**
	 * Sets the Abnormality of the Question for the given the Value
	 * 
	 * @created 25.06.2010
	 * @param q Question
	 * @param value Value
	 * @param abnormality Abnormality
	 */
	public static void setAbnormality(Question q, Value value, double abnormality) {
		Abnormality abnormalitySlice = (Abnormality) q.getKnowledge(PROBLEMSOLVER, METHOD_KIND);
		if (abnormalitySlice == null) {
			abnormalitySlice = new Abnormality();
			q.addKnowledge(PROBLEMSOLVER, abnormalitySlice, METHOD_KIND);
		}
		abnormalitySlice.addValue(value, abnormality);
	}

}