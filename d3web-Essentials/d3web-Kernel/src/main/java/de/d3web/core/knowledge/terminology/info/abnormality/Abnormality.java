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

	double A0 = 0;
	double A1 = 0.0625;
	double A2 = 0.125;
	double A3 = 0.25;
	double A4 = 0.5;
	double A5 = 1;

	/**
	 * Returns the value of the abnormality for the specified answer value, or the default
	 * abnormality of {@link #A5} (1.0) if the abnormality is not explicitly defined in this
	 * Abnormality. To check is a abnormality is explicitly defined, use {@link #isSet(Value)}.
	 *
	 * @param answerValue the answer to get the abnormality for
	 * @return the value of abnormality for the specified answer
	 */
	double getValue(Value answerValue);

	/**
	 * Returns if the abnormality is defined for the specified value. If not, the {@link
	 * #getValue(Value)} method will still return a value, usually A5 if the value is completely
	 * undefined.
	 *
	 * @param answerValue the answer to test if the abnormality is set for
	 * @return true, if the abnormality is (explicitly) defined
	 */
	boolean isSet(Value answerValue);
}
