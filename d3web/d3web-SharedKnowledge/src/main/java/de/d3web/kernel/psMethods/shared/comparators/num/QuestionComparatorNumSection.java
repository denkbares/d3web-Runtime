package de.d3web.kernel.psMethods.shared.comparators.num;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import de.d3web.kernel.domainModel.answers.AnswerNum;

/**
 * Insert the type's description here.
 * Creation date: (06.08.2001 18:45:02)
 * @author: Norman Brümmer
 */
public class QuestionComparatorNumSection extends QuestionComparatorNum {
	protected List xValues = null;
	protected List yValues = null;

	/**
	 * Insert the method's description here.
	 * Creation date: (06.08.2001 18:53:08)
	 * @param ans de.d3web.kernel.domainModel.Answer
	 * @param val double
	 */
	public void addValuePair(Double x, Double y) {
		if ((xValues == null) || (yValues == null)) {
			xValues = new LinkedList();
			yValues = new LinkedList();
		}

		xValues.add(x);
		yValues.add(y);
	}

	public void putValuePair(Double x, Double y) {
		if ((xValues == null) || (yValues == null)) {
			xValues = new LinkedList();
			yValues = new LinkedList();
		}
		ListIterator xIter = xValues.listIterator();
		ListIterator yIter = yValues.listIterator();
		while (xIter.hasNext()) {
			Double nextX = (Double) xIter.next();
			yIter.next();
			if (nextX.doubleValue() == x.doubleValue()) {
				yIter.set(y);
				return;
			}
		}

		xValues.add(x);
		yValues.add(y);
	}

	public void removeValuePair(Double x) {
		if (xValues == null) {
			return;
		}
		Iterator xIter = xValues.iterator();
		Iterator yIter = yValues.iterator();
		while (xIter.hasNext()) {
			Double nextX = (Double) xIter.next();
			yIter.next();
			if (nextX.doubleValue() == x.doubleValue()) {
				xIter.remove();
				yIter.remove();
			}
		}
	}

	public double compare(List ans1, List ans2) {
		try {
			Double x1 = (Double) ((AnswerNum) ans1.get(0)).getValue(null);
			Double x2 = (Double) ((AnswerNum) ans2.get(0)).getValue(null);

			double gx = getFunctionValue(x1);
			double gy = getFunctionValue(x2);

			return Math.min(gx, gy) / Math.max(gx, gy);
		} catch (Exception e) {
			return super.compare(ans1, ans2);
		}

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (06.08.2001 21:10:28)
	 * @return double
	 * @param val java.lang.Double
	 */
	public double getFunctionValue(Double val) {

		double ret = 0;

		Iterator xiter = xValues.iterator();
		Iterator yiter = yValues.iterator();
		while (xiter.hasNext()) {
			Double x = (Double) xiter.next();
			Double y = (Double) yiter.next();
			if (x.doubleValue() <= val.doubleValue()) {
				ret = y.doubleValue();
			}
		}

		return ret;

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (06.08.2001 21:30:15)
	 * @return java.util.Iterator
	 */
	public List getValues() {
		return xValues;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (09.08.2001 18:07:24)
	 * @return java.lang.String
	 */
	public java.lang.String getXMLString() {
		StringBuffer sb = new StringBuffer();

		sb.append("<KnowledgeSlice ID='" + getId() + "' type='QuestionComparatorNumSection'>\n");
		sb.append("<question ID='" + getQuestion().getId() + "'/>\n");
		sb.append("<unknownSimilarity value='" + getUnknownSimilarity() + "'/>");
		sb.append("<sections>\n");

		Iterator xiter = xValues.iterator();
		Iterator yiter = yValues.iterator();
		while (xiter.hasNext()) {
			double x = ((Double) xiter.next()).doubleValue();
			double y = ((Double) yiter.next()).doubleValue();
			sb.append("<section xvalue='" + x + "' yvalue='" + y + "'/>\n");
		}

		sb.append("</sections>\n");
		sb.append("</KnowledgeSlice>\n");

		return sb.toString();
	}
}