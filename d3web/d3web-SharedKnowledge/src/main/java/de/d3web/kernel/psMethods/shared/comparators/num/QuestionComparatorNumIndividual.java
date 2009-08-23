package de.d3web.kernel.psMethods.shared.comparators.num;
import java.util.List;

import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.psMethods.shared.comparators.IndividualComparator;

/**
 * Creation date: (10.08.2001 22:55:40)
 * @author: Norman Brümmer
 */
public class QuestionComparatorNumIndividual extends QuestionComparatorNum implements IndividualComparator {

	public double compare(List ans1, List ans2) {
		double x1 = 0;
		double x2 = 0;
		try {
			x1 = ((Double) ((AnswerNum) ans1.get(0)).getValue(null)).doubleValue();
			x2 = ((Double) ((AnswerNum) ans2.get(0)).getValue(null)).doubleValue();

			return (x1 == x2) ? 1 : 0;

		} catch (Exception e) {
			return super.compare(ans1, ans2);
		}

	}

	/**
	 * @return java.lang.String
	 */
	public String getXMLString() {
		StringBuffer sb = new StringBuffer();

		sb.append("<KnowledgeSlice ID='" + getId() + "' type='QuestionComparatorNumIndividual'>\n");
		sb.append("<question ID='" + getQuestion().getId() + "'/>\n");
		sb.append("<unknownSimilarity value='" + getUnknownSimilarity() + "'/>");
		sb.append("</KnowledgeSlice>\n");

		return sb.toString();
	}
}