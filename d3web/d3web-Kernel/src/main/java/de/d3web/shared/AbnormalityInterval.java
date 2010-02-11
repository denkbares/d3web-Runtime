package de.d3web.shared;

import de.d3web.core.terminology.info.NumericalInterval;

/**
 * AbnormalityInterval has
 * 
 * lowerBoundary, upperBoundary, value & type
 * type specifies if and which endpoints of the interval are included or not
 * CLOSED_... means lowerBoundary is included, OPEN_... means lowerBoundary is not included
 * ..._CLOSED and ..._OPEN analogous
 * 
 * Abnormality offers
 * 
 * interferesWith(AbnormalityInterval) to check if two intervals overlap or connects
 * contains(double) to check if a double lies between upperBoundary and upperBoundary
 * isRightOpen() to check if the upperBoundary is not included
 * isLeftOpen() ...
 */
public class AbnormalityInterval extends NumericalInterval {

	private static final long serialVersionUID = 641297301842171931L;
	private double value;

	public AbnormalityInterval(
		double lowerBoundary,
		double upperBoundary,
		double value,
		boolean leftOpen,
		boolean rightOpen)
		throws NumericalInterval.IntervalException {
		super(lowerBoundary, upperBoundary, leftOpen, rightOpen);
		setValue(value);
	}

	public String toString() {
		return "AbnormalityInterval"
			+ " ("
			+ AbstractAbnormality.convertValueToConstantString(value)
			+ "): "
			+ (isLeftOpen() ? "(" : "[")
			+ getLeft()
			+ ", "
			+ getRight()
			+ (isRightOpen() ? ")" : "]");
	}

	/**
	 * @return
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param d
	 */
	public void setValue(double d) {
		value = d;
	}

}
