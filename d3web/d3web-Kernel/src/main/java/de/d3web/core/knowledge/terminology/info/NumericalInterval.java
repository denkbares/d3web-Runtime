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

package de.d3web.core.knowledge.terminology.info;

/**
 * A Numerical Interval <BR>
 * Represents a numerical interval with a left and a right border. <BR>
 * Borders can be open or closed.
 * 
 * @author mweniger
 */
public class NumericalInterval implements Comparable<NumericalInterval> {

	public static class IntervalException extends IllegalArgumentException {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6464032113971663071L;

		/**
		 * @param s
		 */
		public IntervalException(String s) {
			super(s);
		}

	}

	private final double left;
	private final double right;

	private final boolean leftOpen;
	private final boolean rightOpen;

	/**
	 * Constructs a NumerialInterval
	 * 
	 * @param left
	 * @param right
	 * @param leftopen
	 * @param rightopen
	 */
	public NumericalInterval(double left, double right, boolean leftOpen, boolean rightOpen) {
		this.left = left;
		this.right = right;
		this.leftOpen = leftOpen;
		this.rightOpen = rightOpen;
	}

	/**
	 * Constructs a closed NumericalInterval
	 * 
	 * @param left
	 * @param right
	 */
	public NumericalInterval(double left, double right) {
		this(left, right, false, false);
	}

	public void checkValidity() throws IntervalException {
		if (getLeft() == getRight() && (isLeftOpen() || isRightOpen())) {
			throw new IntervalException(
					"an interval containing only one value must not be open on any side");
		}
		else if (getLeft() > getRight()) {
			throw new IntervalException(
					"leftBoundary (" + getLeft()
							+ ") should be really lower than the rightBoundary (" + getRight()
							+ ")");
		}
	}

	/**
	 * Checks whether value is contained in Interval
	 * 
	 * @param value
	 * @return boolean
	 */
	public boolean contains(double value) {
		if (left < value && value < right) {
			return true;
		}
		if (value == left && !isLeftOpen()) {
			return true;
		}
		if (value == right && !isRightOpen()) {
			return true;
		}
		return false;
	}

	/**
	 * Returns left boundary
	 * 
	 * @return double
	 */
	public final double getLeft() {
		return left;
	}

	/**
	 * Returns whether left boundary is open or not
	 * 
	 * @return boolean
	 */
	public final boolean isLeftOpen() {
		return leftOpen;
	}

	/**
	 * Returns right boundary
	 * 
	 * @return double
	 */
	public final double getRight() {
		return right;
	}

	/**
	 * Returns whether right boundary is open or not
	 * 
	 * @return boolean
	 */
	public final boolean isRightOpen() {
		return rightOpen;
	}

	/**
	 * Indicates whether some other Interval is equal to this one.
	 * 
	 * @param o other interval
	 * @return boolean
	 */
	@Override
	public boolean equals(Object other) {

		if (this == other) {
			return true;
		}
		else if ((other == null) || (getClass() != other.getClass())) {
			return false;
		}
		else {
			NumericalInterval o = ((NumericalInterval) other);
			return (leftOpen == o.isLeftOpen() && rightOpen == o.isRightOpen()
					&& left == o.getLeft() && right == o.getRight());
		}
	}

	/**
	 * Checks whether this Interval intersects some other Interval
	 * 
	 * @param o other interval
	 * @return boolean
	 */
	public boolean intersects(NumericalInterval other) {

		if (other.getRight() < this.getLeft()) {
			return false;
		}
		if (other.getRight() == this.getLeft()) {
			return (!this.isLeftOpen() && !other.isRightOpen());
		}
		if (other.getLeft() > this.getRight()) {
			return false;
		}
		if (other.getLeft() == this.getRight()) {
			if (this.isRightOpen() || other.isLeftOpen()) {
				return false;
			}
			return true;
		}
		return true;

	}

	public NumericalInterval intersect(NumericalInterval other) {

		NumericalInterval min; // smaller one wrt compareTo
		NumericalInterval max; // bigger one wrt compareTo

		if (this.compareTo(other) < 1) {
			min = this;
			max = other;
		}
		else {
			min = other;
			max = this;
		}

		double rightVal;
		boolean rightOpen;

		int compare = Double.compare(min.right, max.right);
		if (compare == 0) {
			rightVal = min.right;
			rightOpen = min.isRightOpen() || max.isRightOpen();
		}
		else if (compare < 0) {
			rightVal = min.right;
			rightOpen = min.isRightOpen();
		}
		else {
			rightVal = max.right;
			rightOpen = max.isRightOpen();

		}

		NumericalInterval result = new NumericalInterval(max.left, rightVal, max.isLeftOpen(),
				rightOpen);
		return result;

	}

	/**
	 * Checks whether this Interval contains some other Interval
	 * 
	 * @param o other interval
	 * @return boolean
	 */
	public boolean contains(NumericalInterval other) {

		if (this.getLeft() > other.getLeft()) {
			return false;
		}
		if (this.getRight() < other.getRight()) {
			return false;
		}

		if (this.getLeft() == other.getLeft()) {
			if (this.isLeftOpen() && !other.isLeftOpen()) {
				return false;
			}
		}

		if (this.getRight() == other.getRight()) {
			if (this.isRightOpen() && !other.isRightOpen()) {
				return false;
			}
		}

		return true;

	}

	public boolean isEmpty() {
		return (right < left) || (left == right && (leftOpen || rightOpen));
	}

	public static NumericalInterval valueOf(String s) {
		String[] split = s.split(" ");
		String leftSide = split[0].trim();
		String rightSide = split[1].trim();
		double left = Double.parseDouble(leftSide.substring(1));
		double right = Double.parseDouble(rightSide.substring(0, rightSide.length() - 1));

		boolean leftOpen;
		if (leftSide.startsWith("]")) leftOpen = true;
		else if (leftSide.startsWith("[")) leftOpen = false;
		else throw new IllegalArgumentException(s);

		boolean rightOpen;
		if (rightSide.endsWith("[")) rightOpen = true;
		else if (rightSide.endsWith("]")) rightOpen = false;
		else throw new IllegalArgumentException(s);

		return new NumericalInterval(left, right, leftOpen, rightOpen);

	}

	@Override
	public String toString() {
		String str;
		if (leftOpen) {
			str = "]";
		}
		else {
			str = "[";
		}

		str += left + " " + right;

		if (rightOpen) {
			str += "[";
		}
		else {
			str += "]";
		}

		return str;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(NumericalInterval ni) {
		if (left < ni.left) {
			return -1;
		}
		else if (left > ni.left) {
			return 1;
		} // left is equal, closed interval comes first, if any
		else if (leftOpen != ni.leftOpen) {
			return !leftOpen ? -1 : 1;
		}
		// left is equal wrt to value and openness
		else if (right < ni.right) {
			return -1;
		}
		else if (right > ni.right) {
			return 1;
		} // left is equal and right is equal-valued, check openness
		else if (rightOpen != ni.rightOpen) {
			return rightOpen ? -1 : 1;
		}// everything is equal
		else {
			return 0;
		}
	}

}
