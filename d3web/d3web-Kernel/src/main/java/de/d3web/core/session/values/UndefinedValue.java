/*
 * Copyright (C) 2010 denkbares GmbH
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
package de.d3web.core.session.values;

import de.d3web.core.session.QuestionValue;
import de.d3web.core.session.Value;

/**
 * A class to represent the undefined value state of a question. If no answer
 * has been assigned to a question (or a initially given answer was retracted),
 * then the UndefinedValue is assigned to the question.
 * 
 * @author joba (denkbares GmbH)
 * @created 07.04.2010
 */
public final class UndefinedValue implements QuestionValue {

	public static final String UNDEFINED_ID = "Ma_Undefined";

	private static final UndefinedValue instance = new UndefinedValue();

	private UndefinedValue() {
		super();
	}

	public static UndefinedValue getInstance() {
		return instance;
	}

	@Override
	public Object getValue() {
		return "";
	}

	@Override
	public String toString() {
		return "Undefined";
	}

	@Override
	public int compareTo(Value o) {
		if (o == null || o instanceof UndefinedValue) {
			return 0;
		}
		else {
			return -1;
		}
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof UndefinedValue);
	}

	/**
	 * Returns just a static hashCode based on the ID.
	 */
	@Override
	public int hashCode() {
		return UNDEFINED_ID.hashCode();
	}

	/**
	 * Returns true, if the specified value is an {@link UndefinedValue}.
	 * 
	 * @param value the specified {@link Value} instance
	 * @return true if the specified value is an {@link UndefinedValue}
	 * @author joba
	 * @date 15.04.2010
	 */
	public static boolean isUndefinedValue(Value value) {
		return value instanceof UndefinedValue;
	}

	/**
	 * Returns true, if the specified value is not an {@link UndefinedValue}.
	 * 
	 * @param value the specified {@link Value} instance
	 * @return true if the specified value is not an {@link UndefinedValue};
	 *         false otherwise
	 * @author joba
	 * @date 15.04.2010
	 */
	public static boolean isNotUndefinedValue(Value value) {
		return !(value instanceof UndefinedValue);
	}

}
