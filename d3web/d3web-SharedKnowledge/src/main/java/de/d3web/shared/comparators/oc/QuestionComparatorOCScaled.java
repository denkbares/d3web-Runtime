/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.d3web.shared.comparators.oc;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.UndefinedValue;

/**
 * Insert the type's description here. Creation date: (03.08.2001 16:06:16)
 * 
 * @author: Norman Brümmer
 */
public class QuestionComparatorOCScaled extends QuestionComparatorOC {

	private List<Double> values = null;
	private double constant = 0;

	@Override
	public double compare(Value ans1, Value ans2) {
		if (UndefinedValue.isUndefinedValue(ans1)
				&& UndefinedValue.isUndefinedValue(ans2)) {
			return 1;
		}
		else if (UndefinedValue.isUndefinedValue(ans1)) {
			return 0;
		}
		else if (UndefinedValue.isUndefinedValue(ans2)) {
			return 0;
		}
		if (getQuestion() == null) {
			return 0;
		}

		Hashtable<Choice, Double> ansValHash = new Hashtable<Choice, Double>();
		checkValues();
		checkConstant();
		// build ansValHash
		List<Choice> alternatives = ((QuestionOC) getQuestion()).getAllAlternatives();
		Iterator<Choice> altIter = alternatives.iterator();
		Iterator<Double> valIter = values.iterator();
		while (altIter.hasNext()) {
			ansValHash.put(altIter.next(), valIter.next());
		}
		Choice answer1 = (Choice) ans1.getValue();
		Choice answer2 = (Choice) ans2.getValue();
		double val1 = (ansValHash.get(answer1)).doubleValue();
		double val2 = (ansValHash.get(answer2)).doubleValue();
		double delta = Math.abs((val2 - val1));
		return (delta == 0) ? 1 : (delta >= constant) ? 0 : 1 - delta / constant;
	}

	/**
	 * Insert the method's description here. Creation date: (06.08.2001
	 * 17:19:46)
	 * 
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
	 * Insert the method's description here. Creation date: (06.08.2001
	 * 17:01:47)
	 * 
	 * @param newValues java.util.List
	 */
	public void setValues(double[] newValues) {
		values = new LinkedList<Double>();
		for (int i = 0; i < newValues.length; ++i) {
			values.add(new Double(newValues[i]));
		}
	}

	/**
	 * Insert the method's description here. Creation date: (06.08.2001
	 * 17:01:47)
	 * 
	 * @param newValues java.util.List
	 */
	public void setValues(List<Double> newValues) {
		values = newValues;
	}

	public List<Double> getValues() {
		checkValues();
		return values;
	}

	protected void checkValues() {
		// check values (if null or inconsistent create default-values
		// (1,2,3,...))
		List<Choice> alternatives = ((QuestionOC) getQuestion()).getAllAlternatives();
		if ((values == null) || (values.size() != alternatives.size())) {
			values = new LinkedList<Double>();
			for (int i = 0; i < alternatives.size(); ++i) {
				values.add(new Double(i + 1));
			}
		}
	}

	protected void checkConstant() {
		// check constant (if 0, build default-const.)
		if (constant == 0 && values != null && !values.isEmpty()) {
			double max = (values.get(0)).doubleValue();
			double min = max;
			Iterator<Double> iter = values.iterator();
			while (iter.hasNext()) {
				double val = iter.next().doubleValue();
				if (max < val) max = val;
				if (min > val) min = val;
			}

			constant = max - min;
			// if const. still 0, set it to 1 (div by zero...)
		}
		if (constant == 0) {
			constant = 1;
		}

	}
}