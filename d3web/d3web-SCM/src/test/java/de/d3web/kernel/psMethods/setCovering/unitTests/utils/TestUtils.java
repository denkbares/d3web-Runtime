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
