/*
 * Copyright (C) 2010 denkbares GmbH, Würzburg, Germany
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

package de.d3web.shared;

import de.d3web.core.knowledge.terminology.info.NumericalInterval;

/**
 * AbnormalityInterval has
 * 
 * lowerBoundary, upperBoundary, value & type type specifies if and which
 * endpoints of the interval are included or not CLOSED_... means lowerBoundary
 * is included, OPEN_... means lowerBoundary is not included ..._CLOSED and
 * ..._OPEN analogous
 * 
 * Abnormality offers
 * 
 * interferesWith(AbnormalityInterval) to check if two intervals overlap or
 * connects contains(double) to check if a double lies between upperBoundary and
 * upperBoundary isRightOpen() to check if the upperBoundary is not included
 * isLeftOpen() ...
 */
public class AbnormalityInterval extends NumericalInterval {

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

	@Override
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
