package de.d3web.kernel.psMethods.shared.comparators.mc;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.psMethods.shared.comparators.IndividualComparator;

/**
 * Creation date: (07.08.2001 02:42:47)
 * @author: Norman Brümmer
 */
public class QuestionComparatorMCIndividual extends QuestionComparatorMC implements IndividualComparator {

	public double compare(List ans1, List ans2) {
		List proved = new LinkedList();
		double compCount = 0;
		double succCount = 0;

		Iterator iter = ans1.iterator();
		while (iter.hasNext()) {
			Answer ans = (Answer) iter.next();
			compCount++;
			if (ans2.contains(ans)) {
				succCount++;
			}
			proved.add(ans);
		}

		iter = ans2.iterator();
		while (iter.hasNext()) {
			Answer ans = (Answer) iter.next();
			if (!proved.contains(ans)) {
				compCount++;
			}
		}
		return succCount / compCount;
	}

	/**
	 * Creation date: (09.08.2001 18:07:08)
	 * @return java.lang.String
	 */
	public java.lang.String getXMLString() {
		StringBuffer sb = new StringBuffer();

		sb.append("<KnowledgeSlice ID='" + getId() + "' type='QuestionComparatorMCIndividual'>\n");
		sb.append("<question ID='" + getQuestion().getId() + "'/>\n");
		sb.append("<unknownSimilarity value='" + getUnknownSimilarity() + "'/>");
		sb.append("</KnowledgeSlice>\n");

		return sb.toString();
	}
}