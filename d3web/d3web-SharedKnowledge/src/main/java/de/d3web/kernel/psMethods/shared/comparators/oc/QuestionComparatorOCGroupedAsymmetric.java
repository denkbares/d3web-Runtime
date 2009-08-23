package de.d3web.kernel.psMethods.shared.comparators.oc;

import java.util.Iterator;
import java.util.List;

import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.psMethods.shared.comparators.GroupedComparatorAsymmetric;
import de.d3web.kernel.psMethods.shared.comparators.PairRelation;

public class QuestionComparatorOCGroupedAsymmetric extends
		QuestionComparatorOCGrouped implements GroupedComparatorAsymmetric{
	
	public java.lang.String getXMLString() {
		StringBuffer sb = new StringBuffer();

		sb.append("<KnowledgeSlice ID='" + getId() + "' type='QuestionComparatorOCGroupedAsymmetric'>\n");
		sb.append("<question ID='" + getQuestion().getId() + "'/>\n");
		sb.append("<unknownSimilarity value='" + getUnknownSimilarity() + "'/>");
		sb.append("<pairRelations>\n");

		Iterator iter = pairRelations.iterator();
		while (iter.hasNext()) {
			PairRelation rel = (PairRelation) iter.next();
			sb.append(rel.getXMLString());
		}

		sb.append("</pairRelations>\n");
		sb.append("</KnowledgeSlice>\n");

		return sb.toString();
	}
	
	public double compare(List answers1, List answers2) {
		try {
			AnswerChoice ans1 = (AnswerChoice) answers1.get(0);
			AnswerChoice ans2 = (AnswerChoice) answers2.get(0);

			if (ans1.equals(ans2)) {
				return 1;
			}

			Iterator iter = pairRelations.iterator();
			while (iter.hasNext()) {
				PairRelation r = (PairRelation) iter.next();
				if (r.getAnswer1().equals(ans1) && r.getAnswer2().equals(ans2)) {
					return r.getValue();
				}
			}
		} catch (Exception x) {
			System.err.println("OCGroupedAsymmetric: Exception while comparing: " + x);
			return 0;
		}
		return 0;
	}
	
	
}
