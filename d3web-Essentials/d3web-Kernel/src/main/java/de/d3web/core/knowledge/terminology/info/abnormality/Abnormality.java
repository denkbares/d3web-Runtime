/*
 * Copyright (C) 2011 denkbares GmbH
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

import de.d3web.core.session.Value;

/**
 * Stores Abnormalities for values
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 03.02.2011
 */
public interface Abnormality {

	public static final double A0 = 0;
	public static final double A1 = 0.0625;
	public static final double A2 = 0.125;
	public static final double A3 = 0.25;
	public static final double A4 = 0.5;
	public static final double A5 = 1;

	/**
	 * 
	 * @param answerValue double
	 * @return double, value of abnormality of the AbnormalityInterval which
	 *         contains answerValue, A0 if answerValue is not contained in any
	 *         AbnormalityInterval
	 */
	public abstract double getValue(Value ans);
}
