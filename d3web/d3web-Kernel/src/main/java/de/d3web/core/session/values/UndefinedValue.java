/*
 * Copyright (C) 2010 denkbares GmbH
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.core.session.values;

import de.d3web.core.session.Value;

/**
 * A class to represent the undefined value state of a question. If no answer
 * has been assigned to a question (or a initially given answer was retracted),
 * then the UndefinedValue is assigned to the question.
 * 
 * @author joba (denkbares GmbH)
 * @created 07.04.2010
 */
public class UndefinedValue implements Value {
	
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
		return null;
	}

	@Override
	public int compareTo(Value o) {
		if (o == null || o instanceof UndefinedValue) {
			return 0;
		} else {
			return -1;
		}
	}

}
