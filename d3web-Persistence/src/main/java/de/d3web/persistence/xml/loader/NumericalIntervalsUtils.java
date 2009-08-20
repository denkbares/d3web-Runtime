package de.d3web.persistence.xml.loader;

import org.w3c.dom.Node;

import de.d3web.kernel.domainModel.NumericalInterval;

/**
 * some utilities for XML-en- & decoding of NumericalIntervals
 * yes, they could offer some more funtionality like getXMLCodeFor(List of NumericalIntervals)
 * but there is the AbnormalityNum.AbnormalityInterval (extension of NumericalInterval)
 * which also has to be encoded ...
 * @author hoernlein
 */
public abstract class NumericalIntervalsUtils {

	/**
	 * the tag-name for single intervals
	 */
	public final static String TAG = "NumInterval";

	/**
	 * the tag-name for a group of intervals
	 */
	public final static String GROUPTAG = "NumIntervals";

	private final static String POSITIVE_INFINITY = "+INFINITY";
	private final static String NEGATIVE_INFINITY = "-INFINITY";

	private final static String LOWER_TAG = "lower";
	private final static String UPPER_TAG = "upper";
	private final static String TYPE_TAG = "type";

	public static class NumericalIntervalException extends Exception {
	}

	/**
	 * returns the double value for the lower boundary of the interval encoded in node
	 * @param node Node
	 * @return double
	 * @throws NumericalIntervalException
	 */
	public static double node2lower(Node node) throws NumericalIntervalException {
		try {
			return string2double(node.getAttributes().getNamedItem(LOWER_TAG).getNodeValue());
		} catch (Exception ex) {
			throw new NumericalIntervalException();
		}
	}

	/**
	 * returns the double value for the upper boundary of the interval encoded in node
	 * @param node Node
	 * @return double
	 * @throws NumericalIntervalException
	 */
	public static double node2upper(Node node) throws NumericalIntervalException {
		try {
			return string2double(node.getAttributes().getNamedItem(UPPER_TAG).getNodeValue());
		} catch (Exception ex) {
			throw new NumericalIntervalException();
		}
	}

	private static double string2double(String value) throws NumericalIntervalException {
		if (NEGATIVE_INFINITY.equals(value))
			return Double.NEGATIVE_INFINITY;
		if (POSITIVE_INFINITY.equals(value))
			return Double.POSITIVE_INFINITY;
		else {
			try {
				return Double.parseDouble(value);
			} catch (NumberFormatException ex) {
				throw new NumericalIntervalException();
			}
		}
	}

	/**
	 * returns the types of the boundaries of the interval encoded in node
	 * in the form boolean[] { is lower boundary closed, is upper boundary closed }
	 * @param node Node
	 * @return double
	 * @throws NumericalIntervalException
	 */
	public static boolean[] node2booleanTypes(Node node) throws NumericalIntervalException {
		try {
			return string2booleanTypes(node.getAttributes().getNamedItem(TYPE_TAG).getNodeValue());
		} catch (Exception ex) {
			throw new NumericalIntervalException();
		}
	}

	private static boolean[] string2booleanTypes(String value) throws NumericalIntervalException {
		if ("LeftOpenRightOpenInterval".equals(value))
			return new boolean[] { true, true };
		if ("LeftOpenRightClosedInterval".equals(value))
			return new boolean[] { true, false };
		if ("LeftClosedRightOpenInterval".equals(value))
			return new boolean[] { false, true };
		if ("LeftClosedRightClosedInterval".equals(value))
			return new boolean[] { false, false };
		else
			throw new NumericalIntervalException();
	}

	/**
	 * returns the lower boundary of the NumericalInterval encoded as an attribute-String
	 * @param i NumericalInterval
	 * @return String
	 */
	public static String interval2lowerAttribute(NumericalInterval i) {
		return double2lowerAttribute(i.getLeft());
	}

	/**
	 * returns the upper boundary of the NumericalInterval encoded as an attribute-String
	 * @param i NumericalInterval
	 * @return String
	 */
	public static String interval2upperAttribute(NumericalInterval i) {
		return double2upperAttribute(i.getRight());
	}

	private static String double2lowerAttribute(double d) {
		return LOWER_TAG + "=\"" + double2string(d) + "\"";
	}

	private static String double2upperAttribute(double d) {
		return UPPER_TAG + "=\"" + double2string(d) + "\"";
	}

	private static String double2string(double d) {
		if (d == Double.NEGATIVE_INFINITY)
			return NEGATIVE_INFINITY;
		if (d == Double.POSITIVE_INFINITY)
			return POSITIVE_INFINITY;
		else
			return Double.toString(d);
	}

	/**
	 * returns the type(s) of the NumericalInterval encoded as an attribute-String
	 * @param i NumericalInterval
	 * @return String
	 */
	public static String interval2typeAttribute(NumericalInterval i) {
		return booleanTypes2typeAttribute(i.isLeftOpen(), i.isRightOpen());
	}

	private static String booleanTypes2typeAttribute(boolean isLeftOpen, boolean isRightOpen) {
		return TYPE_TAG + "=\"" + booleanTypes2string(isLeftOpen, isRightOpen) + "\"";
	}

	private static String booleanTypes2string(boolean isLeftOpen, boolean isRightOpen) {
		return "Left" + (isLeftOpen ? "Open" : "Closed") + "Right" + (isRightOpen ? "Open" : "Closed") + "Interval";
	}

}
