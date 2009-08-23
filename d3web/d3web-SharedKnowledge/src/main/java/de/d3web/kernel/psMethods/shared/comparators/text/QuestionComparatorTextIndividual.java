package de.d3web.kernel.psMethods.shared.comparators.text;
import java.util.List;

import de.d3web.kernel.domainModel.answers.AnswerText;
import de.d3web.kernel.psMethods.shared.comparators.IndividualComparator;
/**
 * Insert the type's description here.
 * Creation date: (27.02.2002 13:46:53)
 * @author: Norman Brümmer
 */
public class QuestionComparatorTextIndividual
	extends de.d3web.kernel.psMethods.shared.comparators.QuestionComparator
	implements IndividualComparator {

	public double compare(List answers1, List answers2) {
		try {
			AnswerText ans1 = (AnswerText) answers1.get(0);
			AnswerText ans2 = (AnswerText) answers2.get(0);

			if (ans1.getValue(null).equals(ans2.getValue(null))) {
				return 1;
			}
		} catch (Exception x) {
			return 0;
		}
		return 0;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (27.02.2002 13:46:53)
	 * @return java.lang.String
	 */
	public String getXMLString() {
		StringBuffer sb = new StringBuffer();

		sb.append("<KnowledgeSlice ID='" + getId() + "' type='QuestionComparatorTextIndividual'>\n");
		sb.append("<question ID='" + getQuestion().getId() + "'/>\n");
		sb.append("<unknownSimilarity value='" + getUnknownSimilarity() + "'/>");
		sb.append("</KnowledgeSlice>\n");

		return sb.toString();
	}
}