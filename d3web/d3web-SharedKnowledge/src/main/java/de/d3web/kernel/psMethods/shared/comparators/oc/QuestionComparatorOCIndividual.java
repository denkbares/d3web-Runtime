package de.d3web.kernel.psMethods.shared.comparators.oc;
import java.util.List;

import de.d3web.kernel.psMethods.shared.comparators.IndividualComparator;
/**
 * Insert the type's description here.
 * Creation date: (03.08.2001 14:28:19)
 * @author: Norman Brümmer
 */
public class QuestionComparatorOCIndividual extends QuestionComparatorOC implements IndividualComparator {

	public double compare(List ans1, List ans2) {
		try {
			if (ans1.get(0).equals(ans2.get(0))) {
				return 1;
			}
		} catch (Exception x) {
			System.err.println("OCindivCompare: answer missing");
			return 0;
		}

		return 0;

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10.08.2001 13:57:16)
	 * @return java.lang.String
	 */
	public java.lang.String getXMLString() {
		StringBuffer sb = new StringBuffer();

		sb.append("<KnowledgeSlice ID='" + getId() + "' type='QuestionComparatorOCIndividual'>\n");
		sb.append("<question ID='" + getQuestion().getId() + "'/>\n");
		sb.append("<unknownSimilarity value='" + getUnknownSimilarity() + "'/>");
		sb.append("</KnowledgeSlice>\n");

		return sb.toString();
	}
}