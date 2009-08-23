package de.d3web.kernel.psMethods.shared.comparators.num;
import java.util.List;

import de.d3web.kernel.domainModel.answers.AnswerNum;

/**
 * Insert the type's description here.
 * Creation date: (07.08.2001 02:02:34)
 * @author: Norman Brümmer
 */
public class QuestionComparatorNumDivisionDenominator extends QuestionComparatorNum {
	private double denominator = 0;

	public double compare(List ans1, List ans2) {
		try {
			double x1 = ((Double) ((AnswerNum) ans1.get(0)).getValue(null)).doubleValue();
			double x2 = ((Double) ((AnswerNum) ans2.get(0)).getValue(null)).doubleValue();

			if (denominator == 0) {
				denominator = Math.abs(x1 - x2);
			}
			return 1 - (Math.abs(x1 - x2) / denominator);
		} catch (Exception e) {
			return super.compare(ans1, ans2);
		}

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (09.08.2001 18:07:17)
	 * @return java.lang.String
	 */
	public java.lang.String getXMLString() {
		StringBuffer sb = new StringBuffer();

		sb.append("<KnowledgeSlice ID='" + getId() + "' type='QuestionComparatorNumDivisionDenominator'>\n");
		sb.append("<question ID='" + getQuestion().getId() + "'/>\n");
		sb.append("<unknownSimilarity value='" + getUnknownSimilarity() + "'/>");
		sb.append("<denominator value='" + denominator + "'/>\n");
		sb.append("</KnowledgeSlice>\n");

		return sb.toString();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (07.08.2001 02:05:42)
	 * @param newDenominator double
	 */
	public void setDenominator(double newDenominator) {
		denominator = newDenominator;
	}
	/**
	 * @return
	 */
	public double getDenominator() {
		return denominator;
	}

}