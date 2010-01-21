/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.kernel.psMethods.shared.comparators.num;
import java.util.Collections;
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
	private static final long serialVersionUID = 7865286934799745265L;
	protected List<Double> xValues = null;
	
	public List<Double> getxValues() {
		return Collections.unmodifiableList(xValues);
	}

	public List<Double> getyValues() {
		return Collections.unmodifiableList(yValues);
	}

	protected List<Double> yValues = null;

	/**
	 * Insert the method's description here.
	 * Creation date: (06.08.2001 18:53:08)
	 * @param ans de.d3web.kernel.domainModel.Answer
	 * @param val double
	 */
	public void addValuePair(Double x, Double y) {
		if ((xValues == null) || (yValues == null)) {
			xValues = new LinkedList<Double>();
			yValues = new LinkedList<Double>();
		}

		xValues.add(x);
		yValues.add(y);
	}

	public void putValuePair(Double x, Double y) {
		if ((xValues == null) || (yValues == null)) {
			xValues = new LinkedList<Double>();
			yValues = new LinkedList<Double>();
		}
		ListIterator<Double> xIter = xValues.listIterator();
		ListIterator<Double> yIter = yValues.listIterator();
		while (xIter.hasNext()) {
			Double nextX = xIter.next();
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
		Iterator<Double> xIter = xValues.iterator();
		Iterator<Double> yIter = yValues.iterator();
		while (xIter.hasNext()) {
			Double nextX = xIter.next();
			yIter.next();
			if (nextX.doubleValue() == x.doubleValue()) {
				xIter.remove();
				yIter.remove();
			}
		}
	}

	public double compare(List<?> ans1, List<?> ans2) {
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

		Iterator<Double> xiter = xValues.iterator();
		Iterator<Double> yiter = yValues.iterator();
		while (xiter.hasNext()) {
			Double x = xiter.next();
			Double y = yiter.next();
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
	public List<Double> getValues() {
		return xValues;
	}
}