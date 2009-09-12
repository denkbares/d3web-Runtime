/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.kernel.psMethods.setCovering.unitTests.utils;

import java.util.HashMap;
import java.util.Map;

import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.kernel.psMethods.setCovering.SCNodeFactory;
import de.d3web.kernel.psMethods.shared.QuestionWeightValue;
import de.d3web.kernel.psMethods.shared.Weight;

/**
 * This Class provides some utility methods for often used procedures in the
 * test cases.
 * 
 * @author bruemmer
 */
public class TestUtils {

	/**
	 * 
	 * @param keys
	 *            Keys with that the Map will be filled
	 * @return a Map with the given Object[] as keys and null as values
	 */
	public static Map createMap(Object[] keys) {
		Map ret = new HashMap();
		if (keys != null) {
			for (int i = 0; i < keys.length; ++i) {
				ret.put(keys[i], null);
			}
		}
		return ret;

	}

	public static PredictedFinding createFindingNum(String id, double value, KnowledgeBase kb,
			int weight) {
		QuestionNum q = new QuestionNum();
		q.setId(id);
		q.setKnowledgeBase(kb);

		Weight w = new Weight();
		QuestionWeightValue qww = new QuestionWeightValue();
		qww.setQuestion(q);
		qww.setValue(weight);
		w.setQuestionWeightValue(qww);

		return SCNodeFactory.createFindingNumEquals(q, new Double(value));
	}

	public static SCDiagnosis createSCDiagnosis(String id, KnowledgeBase kb) {
		Diagnosis diag = new Diagnosis();
		diag.setId(id);
		diag.setKnowledgeBase(kb);

		return SCNodeFactory.createSCDiagnosis(diag);
	}

}
