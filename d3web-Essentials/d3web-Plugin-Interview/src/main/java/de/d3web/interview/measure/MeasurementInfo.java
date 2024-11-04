/*
 * Copyright (C) 2021 denkbares GmbH, Germany
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

package de.d3web.interview.measure;

import java.util.Date;

/**
 * Status of a {@link Measurement}
 */
public class MeasurementInfo {

	/**
	 * The current state of the measurement
	 */
	public final Measurement.State state;

	/**
	 * The date the measurement was started
	 */
	public final Date measurementStart;

	/**
	 * The time out of this measurement. -1 if there is no timeout.
	 */
	public final long measurementTimeOutMillis;

	public MeasurementInfo() {
		this(Measurement.State.NOT_MEASURING, new Date(0), -1);
	}

	public MeasurementInfo(Measurement.State state, Date measurementStart, long measurementTimeOutMillis) {
		this.state = state;
		this.measurementStart = measurementStart;
		this.measurementTimeOutMillis = measurementTimeOutMillis;
	}

	public boolean isMeasuring() {
		return this.state != Measurement.State.NOT_MEASURING;
	}
}
