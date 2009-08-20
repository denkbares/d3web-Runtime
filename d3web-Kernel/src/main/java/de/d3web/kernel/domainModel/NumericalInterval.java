package de.d3web.kernel.domainModel;

/**
 * A Numerical Interval <BR>
 * Represents a numerical interval with a left and 
 * a right border. <BR>
 * Borders can be open or closed.
 * @author mweniger
 */
public class NumericalInterval implements Comparable, java.io.Serializable {

	public static class IntervalException extends IllegalArgumentException {
		/**
		* 
		*/
		public IntervalException() {
		}

		/**
		 * @param s
		 */
		public IntervalException(String s) {
		}

	}

	private double left;
	private double right;

	private boolean leftOpen;
	private boolean rightOpen;

	/**
	 * Constructs a NumerialInterval
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
		checkValidity();
	}

	/**
	 * Constructs a closed NumericalInterval
	 * @param left
	 * @param right
	 */
	public NumericalInterval(double left, double right) {
		this.left = left;
		this.right = right;
		leftOpen = false;
		rightOpen = false;
		checkValidity();
	}

	public void checkValidity() throws IntervalException {
		if (getLeft() == getRight() && isLeftOpen() && isRightOpen()) {
			throw new IntervalException("an interval containing only one value must not be open on any side");
		} else if (getLeft() > getRight()) {
			throw new IntervalException(
				"leftBoundary (" + getLeft() + ") should be really lower than the rightBoundary (" + getRight() + ")");
		}
	}

	/**
	 * Checks whether value is contained in Interval
	 * @param value
	 * @return boolean
	 */
	public boolean contains(double value) {
		if (left < value && value < right)
			return true;
		if (value == left && !isLeftOpen())
			return true;
		if (value == right && !isRightOpen())
			return true;
		return false;
	}

	/**
	 * Returns left boundary
	 * @return double
	 */
	public double getLeft() {
		return left;
	}

	/**
	 * Returns whether left boundary is open or not
	 * @return boolean
	 */
	public boolean isLeftOpen() {
		return leftOpen;
	}

	/**
	 * Returns right boundary
	 * @return double
	 */
	public double getRight() {
		return right;
	}

	/**
	 * Returns whether right boundary is open or not
	 * @return boolean
	 */
	public boolean isRightOpen() {
		return rightOpen;
	}

	/**
	 * Sets the left boundary
	 * @param left The left to set
	 */
	public void setLeft(double left) {
		this.left = left;
	}

	/**
	 * Sets the whether left boundary is open or not.
	 * @param leftopen The leftopen to set
	 */
	public void setLeftOpen(boolean leftOpen) {
		this.leftOpen = leftOpen;
	}

	/**
	 * Sets the right boundary
	 * @param right The right to set
	 */
	public void setRight(double right) {
		this.right = right;
	}

	/**
	 * Sets  whether right boundary is open or not.
	 * @param rightopen The rightopen to set
	 */
	public void setRightOpen(boolean rightOpen) {
		this.rightOpen = rightOpen;
	}

	/**
	 * Indicates whether some other Interval is equal to this one.
	 * @param o other interval
	 * @return boolean
	 */
	public boolean equals(Object other) {

		if (this == other)
			return true;
		else if ((other == null) || (getClass() != other.getClass())) {
			return false;
		} else {

			NumericalInterval o = ((NumericalInterval) other);
			if (leftOpen == o.isLeftOpen()
				&& rightOpen == o.isRightOpen()
				&& left == o.getLeft()
				&& right == o.getRight())
				return true;
			// [TODO]:?:Gleichheit evtl. mit nem Epsilon-Wert prüfen 0.99999... == 1!
			else
				return false;

		}
	}

	/**
	 * Checks whether this Interval intersects some other Interval
	 * @param o other interval
	 * @return boolean
	 */
	public boolean intersects(NumericalInterval other) {

		if (other.getRight() < this.getLeft())
			return false;
		if (other.getRight() == this.getLeft()) {
			if (this.isLeftOpen() || other.isRightOpen())
				return false;
			else
				return true;
		}
		if (other.getLeft() > this.getRight())
			return false;
		if (other.getLeft() == this.getRight()) {
			if (this.isRightOpen() || other.isLeftOpen())
				return false;
			return true;
		}
		return true;

	}

	public String toString() {
		String str = "";
		if (leftOpen)
			str += "(";
		else
			str += "[";

		str += left + ", " + right;

		if (rightOpen)
			str += ")";
		else
			str += "]";

		return str;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {

		return toString().hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		NumericalInterval ni = (NumericalInterval) o;
		if (left < ni.left) {
			return -1;
		} else if (left > ni.left) {
			return 1;
		} else if (right < ni.right) {
			return -1;
		} else if (right > ni.right) {
			return 1;
		} else {
			return 0;
		}
	}

}
