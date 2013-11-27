/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg denkbares GmbH
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
package de.d3web.core.io.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.info.NumericalInterval;
import de.d3web.core.knowledge.terminology.info.abnormality.AbnormalityInterval;
import de.d3web.core.knowledge.terminology.info.abnormality.AbnormalityUtils;

/**
 * FragmentHandler for NumericalIntervals and AbnormalityIntervals Other
 * handlers for successors of NumericalInterval must have a higher priority
 * 
 * @author hoernlein, Markus Friedrich (denkbares GmbH)
 */
public class NumericalIntervalHandler implements FragmentHandler<KnowledgeBase> {

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

		private static final long serialVersionUID = 6671719101060789116L;
	}

	private static double string2double(String value) throws NumericalIntervalException {
		if (NEGATIVE_INFINITY.equals(value)) {
			return Double.NEGATIVE_INFINITY;
		}
		else if (POSITIVE_INFINITY.equals(value)) {
			return Double.POSITIVE_INFINITY;
		}
		else {
			try {
				return Double.parseDouble(value);
			}
			catch (NumberFormatException ex) {
				throw new NumericalIntervalException();
			}
		}
	}

	private static boolean[] string2booleanTypes(String value) throws NumericalIntervalException {
		if ("LeftOpenRightOpenInterval".equals(value)) {
			return new boolean[] {
					true, true };
		}
		else if ("LeftOpenRightClosedInterval".equals(value)) {
			return new boolean[] {
					true, false };
		}
		else if ("LeftClosedRightOpenInterval".equals(value)) {
			return new boolean[] {
					false, true };
		}
		else if ("LeftClosedRightClosedInterval".equals(value)) {
			return new boolean[] {
					false, false };
		}
		else throw new NumericalIntervalException();
	}

	private static String double2string(double d) {
		if (d == Double.NEGATIVE_INFINITY) return NEGATIVE_INFINITY;
		if (d == Double.POSITIVE_INFINITY) return POSITIVE_INFINITY;
		else return Double.toString(d);
	}

	private static String booleanTypes2string(boolean isLeftOpen, boolean isRightOpen) {
		return "Left" + (isLeftOpen ? "Open" : "Closed") + "Right"
				+ (isRightOpen ? "Open" : "Closed") + "Interval";
	}

	private static NumericalInterval getIntervall(Element element) throws IOException {
		String lower = element.getAttribute(LOWER_TAG);
		String upper = element.getAttribute(UPPER_TAG);
		String type = element.getAttribute(TYPE_TAG);
		String value = element.getAttribute("value");
		if (element.getNodeName().equals(TAG) && lower != null && upper != null && type != null) {
			try {
				boolean[] borders = string2booleanTypes(type);
				if (value == null || value.length() == 0) {
					NumericalInterval interval = new NumericalInterval(string2double(lower),
							string2double(upper),
							borders[0], borders[1]);
					interval.checkValidity();
					return interval;
				}
				else {
					return new AbnormalityInterval(string2double(lower), string2double(upper),
							AbnormalityUtils
									.convertConstantStringToValue(value), borders[0], borders[1]);
				}
			}
			catch (NumericalIntervalException e) {
				throw new IOException(e);
			}
		}
		return null;
	}

	private static Element getIntervalElemenent(Document doc, NumericalInterval interval) {
		Element element = doc.createElement(TAG);
		element.setAttribute(LOWER_TAG, double2string(interval.getLeft()));
		element.setAttribute(UPPER_TAG, double2string(interval.getRight()));
		element.setAttribute(TYPE_TAG, booleanTypes2string(interval.isLeftOpen(),
				interval.isRightOpen()));
		if (interval instanceof AbnormalityInterval) {
			AbnormalityInterval ai = (AbnormalityInterval) interval;
			element.setAttribute("value",
					AbnormalityUtils.convertValueToConstantString(ai.getValue()));
		}
		return element;
	}

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals(TAG) || element.getNodeName().equals(GROUPTAG);
	}

	@Override
	public boolean canWrite(Object object) {
		if (object instanceof NumericalInterval) {
			return true;
		}
		else if (object instanceof List<?>) {
			List<?> list = (List<?>) object;
			if (list.size() == 0) {
				return false;
			}
			boolean checker = true;
			for (Object o : list) {
				if (!(o instanceof NumericalInterval)) {
					checker = false;
					break;
				}
			}
			return checker;
		}
		else {
			return false;
		}
	}

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		if (element.getNodeName().equals(TAG)) {
			return getIntervall(element);
		}
		else if (element.getNodeName().equals(GROUPTAG)) {
			List<NumericalInterval> list = new ArrayList<NumericalInterval>();
			List<Element> childNodes = XMLUtil.getElementList(element.getChildNodes());
			for (Element child : childNodes) {
				list.add((NumericalInterval) read(child, persistence));
			}
			return list;
		}
		else {
			throw new IOException("Object cannot be read with NumericalIntervalHandler.");
		}
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		if (object instanceof NumericalInterval) {
			return getIntervalElemenent(persistence.getDocument(), (NumericalInterval) object);
		}
		else if (object instanceof List<?>) {
			List<?> list = (List<?>) object;
			Element root = persistence.getDocument().createElement(GROUPTAG);
			for (Object o : list) {
				root.appendChild(write(o, persistence));
			}
			return root;
		}
		else {
			throw new IOException("Object cannot be written with NumericalIntervalHandler.");
		}
	}

}
