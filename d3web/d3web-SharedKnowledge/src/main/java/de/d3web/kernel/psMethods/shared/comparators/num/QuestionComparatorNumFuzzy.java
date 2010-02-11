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

import java.util.List;

import de.d3web.core.session.values.AnswerNum;

/**
 * Fuzzy comparator for numerical values. A value "toCompare" can be compared
 * against a reference value (e.g. the covered numerical value of a diagnosis).
 * The similarity between "toCompare" and the reference value will be determined
 * by means of a curve specified by four parameters (A, B, C, D). The parameters
 * B and C can be set to <code>Double.NEGATIVE_INFINITY</code> resp.
 * <code>Double.POSITIVE_INFINITY</code>.
 * 
 * <pre>
 *       1         ----+---.
 *                /    |    \.
 *               /     |      \.
 *       0 ------      |        -------------
 *              A  B   |  C     D
 *                     R
 *       
 * </pre>
 * 
 * R: the reference value<br>
 * <br>
 * A: increasingLeft (start increasing to 1)<br>
 * B: constLeft (end increasing; staying at 1)<br>
 * C: constRight (start decreasing to 0)<br>
 * D: decreasingRight (start decreasing to 0; staying at 0)<br>
 * <br>
 * For example, if a diagnosis covers a specific value x of a question and all
 * greater values with 100% and the 20% smaller values with increasing
 * similarity, one can set the parameters as follows:<br>
 * <br>
 * increasingLeft = 0.2 (20% smaller values increasing)<br>
 * constLeft = 0<br>
 * constRight = +INFINITY (all greater values)<br>
 * decreasingRight = 0<br>
 * interpretationMethod = INTERPRETATION_METHOD_PRECENTAGE<br>
 * 
 * @author gbuscher
 */
public class QuestionComparatorNumFuzzy extends QuestionComparatorNum {
	
	private static final long serialVersionUID = 9050181706018462531L;
	public final static String POSITIVE_INFINITY_STR = "+INFINITY";
	public final static String NEGATIVE_INFINITY_STR = "-INFINITY";

	/**
	 * All four values specifying the similarity curve will be treated as
	 * absolute values.
	 */
	public final static String INTERPRETATION_METHOD_ABSOLUTE = "absolute";

	/**
	 * All four values specifying the similarity curve will be treated as
	 * percentages of the (variable) reference value.
	 */
	public final static String INTERPRETATION_METHOD_PRECENTAGE = "percentage";

	/**
	 * @see "INTERPRETATION_METHOD_" -constants of this class
	 */
	private String interpretationMethod = INTERPRETATION_METHOD_ABSOLUTE;

	/**
	 * point left of the reference value, where the curve starts increasing from
	 * 0 to 1
	 */
	private Double increasingLeft = new Double(0);

	/**
	 * point left of the reference value, where the curve is 1 for the first
	 * time; the curve will stay at 1 until constRight
	 */
	private Double constLeft = new Double(0);

	/**
	 * point right of the reference value, where the curve starts decreasing
	 * from 1 to 0
	 */
	private Double constRight = new Double(0);

	/**
	 * point right of the reference value, where the curve is (and stays) 0
	 */
	private Double decreasingRight = new Double(0);

	/**
	 * Returns the similarity value in [0;1] between the value to compare and
	 * the reference value. (e.g. the reference value can be the numerical
	 * value, which is covered by a diagnosis)
	 */
	public double compare(List<?> toCompareAnswer, List<?> referenceAnswer) {
		if (referenceAnswer.isEmpty()
				|| !(referenceAnswer.get(0) instanceof AnswerNum)
				|| toCompareAnswer.isEmpty()
				|| !(toCompareAnswer.get(0) instanceof AnswerNum)) {
			return 0;
		}

		Double reference = (Double) ((AnswerNum) referenceAnswer.get(0))
				.getValue(null);
		Double toCompare = (Double) ((AnswerNum) toCompareAnswer.get(0))
				.getValue(null);
		Double constLeftPoint = new Double(reference);
		Double constRightPoint = new Double(reference);

		if (interpretationMethod.equals(INTERPRETATION_METHOD_ABSOLUTE)) {
			constLeftPoint = new Double(reference + constLeft);
			constRightPoint = new Double(reference + constRight);
		} else if (interpretationMethod
				.equals(INTERPRETATION_METHOD_PRECENTAGE)) {
			constLeftPoint = new Double(reference + (reference * constLeft));
			constRightPoint = new Double(reference + (reference * constRight));
		}

		// toCompare lies within constant similarity range?
		if (constLeftPoint.compareTo(toCompare) <= 0
				&& toCompare.compareTo(constRightPoint) <= 0) {
			return 1;
		}

		// toCompare lies left of constant similarity range?
		if (toCompare.compareTo(constLeftPoint) < 0) {
			Double increasingLeftPoint = constLeftPoint;
			if (interpretationMethod.equals(INTERPRETATION_METHOD_ABSOLUTE)) {
				increasingLeftPoint = new Double(constLeftPoint
						+ increasingLeft);
			} else if (interpretationMethod
					.equals(INTERPRETATION_METHOD_PRECENTAGE)) {
				increasingLeftPoint = new Double(constLeftPoint
						+ (reference * increasingLeft));
			}

			// toCompare lies within increasing similarity range?
			if (increasingLeftPoint.compareTo(toCompare) < 0) {
				return getInterpolationValue(increasingLeftPoint,
						constLeftPoint, toCompare);
			}

		} else {
			// toCompare lies right of constant similarity range
			Double decreasingRightPoint = constRightPoint;
			if (interpretationMethod.equals(INTERPRETATION_METHOD_ABSOLUTE)) {
				decreasingRightPoint = new Double(constRightPoint
						+ decreasingRight);
			} else if (interpretationMethod
					.equals(INTERPRETATION_METHOD_PRECENTAGE)) {
				decreasingRightPoint = new Double(constRightPoint
						+ (reference * decreasingRight));
			}

			// toCompare lies within decreasing similarity range?
			if (toCompare.compareTo(decreasingRightPoint) < 0) {
				return 1 - getInterpolationValue(constRightPoint,
						decreasingRightPoint, toCompare);
			}
		}

		return 0;
	}

	/**
	 * Returns the value of the interpolationPoint on the following graph: The
	 * graph is a straight line going through the coordinates (leftLimit, 0) and
	 * (rightLimit, 1).
	 */
	private double getInterpolationValue(double leftLimit, double rightLimit,
			double interpolationPoint) {
		if (rightLimit - leftLimit == 0) {
			return 0;
		}
		return (interpolationPoint - leftLimit) / (rightLimit - leftLimit);
	}

	/**
	 * point left of the reference value, where the curve is 1 for the first
	 * time; the curve will stay at 1 until constRight
	 */
	public void setConstLeft(Double constLeft) {
		this.constLeft = constLeft;
	}

	/**
	 * point right of the reference value, where the curve starts decreasing
	 * from 1 to 0
	 */
	public void setConstRight(Double constRight) {
		this.constRight = constRight;
	}

	/**
	 * point right of the reference value, where the curve is (and stays) 0
	 */
	public void setDecreasingRight(Double decreasingRight) {
		this.decreasingRight = decreasingRight;
	}

	/**
	 * point left of the reference value, where the curve starts increasing from
	 * 0 to 1
	 */
	public void setIncreasingLeft(Double increasingLeft) {
		this.increasingLeft = increasingLeft;
	}

	/**
	 * @see "INTERPRETATION_METHOD_" -constants of this class
	 */
	public void setInterpretationMethod(String interpretationMethod) {
		this.interpretationMethod = interpretationMethod;
	}

	/**
	 * point left of the reference value, where the curve is 1 for the first
	 * time; the curve will stay at 1 until constRight
	 */
	public Double getConstLeft() {
		return constLeft;
	}

	/**
	 * point right of the reference value, where the curve starts decreasing
	 * from 1 to 0
	 */
	public Double getConstRight() {
		return constRight;
	}

	/**
	 * point right of the reference value, where the curve is (and stays) 0
	 */
	public Double getDecreasingRight() {
		return decreasingRight;
	}

	/**
	 * point left of the reference value, where the curve starts increasing from
	 * 0 to 1
	 */
	public Double getIncreasingLeft() {
		return increasingLeft;
	}

	/**
	 * @see "INTERPRETATION_METHOD_" -constants of this class
	 */
	public String getInterpretationMethod() {
		return interpretationMethod;
	}
	
	public static Double stringToDouble(String s) {
		if (s.equalsIgnoreCase(NEGATIVE_INFINITY_STR)) {
			return Double.NEGATIVE_INFINITY;
		} else if (s.equalsIgnoreCase(POSITIVE_INFINITY_STR)) {
			return Double.POSITIVE_INFINITY;
		}
		return Double.parseDouble(s);
	}
}
