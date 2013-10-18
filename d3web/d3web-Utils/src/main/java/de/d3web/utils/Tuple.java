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

package de.d3web.utils;

import java.util.Arrays;

/**
 * This class implements a typed, null-save tuple of a number of other objects.
 * 
 * @author volker_belli
 * 
 */
public class Tuple {

	private final Object[] items;

	public Tuple(Object... items) {
		this.items = items;
	}

	public Object get(int index) {
		return items[index];
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Tuple)) {
			return false;
		}
		Tuple o = (Tuple) other;
		return Arrays.equals(this.items, o.items);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this.items);
	}
}
