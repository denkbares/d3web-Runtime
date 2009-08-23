package de.d3web.kernel.psMethods.shared.comparators.num;
import java.util.List;

import de.d3web.kernel.domainModel.NumericalInterval;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.psMethods.shared.comparators.QuestionComparator;

/**
 * Creation date: (02.08.2001 16:07:34)
 * @author: Norman Brümmer
 */
public abstract class QuestionComparatorNum extends QuestionComparator {

	public double compare(List ans1, List ans2) {
		Object o1 = convertToIntervalOrDouble(ans1);
		Object o2 = convertToIntervalOrDouble(ans2);

		if ((o1 == null) || (o2 == null)) {
			return 0;
		}
		if (o1 instanceof NumericalInterval) {
			if (o2 instanceof NumericalInterval) {
				return o1.equals(o2) ? 1 : 0;
			} else if (o2 instanceof Double) {
				return ((NumericalInterval) o1).contains(((Double) o2).doubleValue()) ? 1 : 0;
			}
		} else if (o1 instanceof Double) {
			if (o2 instanceof NumericalInterval) {
				return ((NumericalInterval) o2).contains(((Double) o1).doubleValue()) ? 1 : 0;
			} else if (o2 instanceof Double) {
				return o1.equals(o2) ? 1 : 0;
			}
		}
		return 0;
	}

	private Object convertToIntervalOrDouble(List answers) {
		try {
			Object o = answers.get(0);
			if (o instanceof AnswerChoice) {
				return new Double(((AnswerChoice) o).getText());
			} else if (o instanceof AnswerNum) {
				return ((AnswerNum) o).getValue(null);
			} else {
				return o;
			}

		} catch (Exception e) {
			return null;
		}
	}
}