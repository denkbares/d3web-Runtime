package de.d3web.kernel.psMethods.shared.comparators.oc;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.kernel.domainModel.qasets.QuestionOC;

/**
 * Insert the type's description here.
 * Creation date: (03.08.2001 16:06:16)
 * @author: Norman Brümmer
 */
public class QuestionComparatorOCScaled extends QuestionComparatorOC {
	private java.util.List values = null;
	private double constant = 0;

	public double compare(List ans1, List ans2) {
		if (getQuestion() == null) {
			return 0;
		}

		Hashtable ansValHash = new Hashtable();
		checkValues();
		checkConstant();
		// build ansValHash
		List alternatives = ((QuestionOC) getQuestion()).getAllAlternatives();
		Iterator altIter = alternatives.iterator();
		Iterator valIter = values.iterator();
		while (altIter.hasNext()) {
			ansValHash.put(altIter.next(), valIter.next());
		}
		double val1 = ((Double) ansValHash.get(ans1.get(0))).doubleValue();
		double val2 = ((Double) ansValHash.get(ans2.get(0))).doubleValue();
		try {
			return 1 - java.lang.Math.abs((val2 - val1)) / constant;
		} catch (Exception x) {
			System.err.println("Something went wrong while calculating the similarity...");
			System.err.println(x);
			return 0;
		}

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10.08.2001 14:01:34)
	 * @return java.lang.String
	 */
	public java.lang.String getXMLString() {
		StringBuffer sb = new StringBuffer();

		sb.append("<KnowledgeSlice ID='" + getId() + "' type='QuestionComparatorOCScaled'>\n");
		sb.append("<question ID='" + getQuestion().getId() + "'/>\n");
		sb.append("<unknownSimilarity value='" + getUnknownSimilarity() + "'/>");
		sb.append("<scala>\n");

		Iterator iter = getValues().iterator();
		while (iter.hasNext()) {
			double val = ((Double) iter.next()).doubleValue();
			sb.append("<scalavalue value='" + val + "'/>\n");
		}

		sb.append("</scala>\n");

		sb.append("<constant value='" + constant + "'/>\n");
		sb.append("</KnowledgeSlice>\n");

		return sb.toString();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (06.08.2001 17:19:46)
	 * @param newConstant int
	 */
	public void setConstant(double newConstant) {
		constant = newConstant;
	}

	/**
	 * @return
	 */
	public double getConstant() {
		checkConstant();
		return constant;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (06.08.2001 17:01:47)
	 * @param newValues java.util.List
	 */
	public void setValues(double[] newValues) {
		values = new LinkedList();
		for (int i = 0; i < newValues.length; ++i) {
			values.add(new Double(newValues[i]));
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (06.08.2001 17:01:47)
	 * @param newValues java.util.List
	 */
	public void setValues(java.util.List newValues) {
		values = newValues;
	}

	public List getValues() {
		checkValues();
		return values;
	}

	protected void checkValues() {
		// check values (if null or inconsistent create default-values (1,2,3,...))
		List alternatives = ((QuestionOC) getQuestion()).getAllAlternatives();
		if ((values == null) || (values.size() != alternatives.size())) {
			values = new LinkedList();
			for (int i = 0; i < alternatives.size(); ++i) {
				values.add(new Double(i + 1));
			}
		}
	}

	protected void checkConstant() {
		// check constant (if 0, build default-const.)
		if (constant == 0 && values != null && !values.isEmpty()) {
			double max = ((Double) values.get(0)).doubleValue();
			double min = max;
			Iterator iter = values.iterator();
			while (iter.hasNext()) {
				double val = ((Double) iter.next()).doubleValue();
				if (max < val)
					max = val;
				if (min > val)
					min = val;
			}

			constant = max - min;
			// if const. still 0, set it to 1 (div by zero...)
		}
		if (constant == 0) {
			constant = 1;
		}

	}
}