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

package de.d3web.core.knowledge.terminology.info.abnormality;

import de.d3web.core.knowledge.terminology.info.NumericalInterval;

/**
 * AbnormalityInterval has
 * <p>
 * lowerBoundary, upperBoundary, value & type type specifies if and which
 * endpoints of the interval are included or not CLOSED_... means lowerBoundary
 * is included, OPEN_... means lowerBoundary is not included ..._CLOSED and
 * ..._OPEN analogous
 * <p>
 * Abnormality offers
 * <p>
 * interferesWith(AbnormalityInterval) to check if two intervals overlap or
 * connects contains(double) to check if a double lies between upperBoundary and
 * upperBoundary isRightOpen() to check if the upperBoundary is not included
 * isLeftOpen() ...
 */
public class AbnormalityInterval extends NumericalInterval {

	private final double value;

	public AbnormalityInterval(
			double lowerBoundary,
			double upperBoundary,
			double value,
			boolean leftOpen,
			boolean rightOpen)
			throws NumericalInterval.IntervalException {
		super(lowerBoundary, upperBoundary, leftOpen, rightOpen);
		checkValidity();
		this.value = value;
	}

	public AbnormalityInterval(NumericalInterval interval, double value) {
		super(interval);
		this.value = value;
	}

	@Override
	public String toString() {
		return super.toString() + " : "
				+ AbnormalityUtils.toAbnormalityValueString(value);
	}

	/**
	 * Returns the abnormality value of this interval. The abnormality value is a value
	 * between 0.0 and 1.0, where 0.0 is normal and 1.0 is abnormal.
	 *
	 * @return the abnormality value
	 */
	public double getValue() {
		return value;
	}
}
