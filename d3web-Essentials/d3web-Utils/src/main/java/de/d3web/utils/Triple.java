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

/**
 * This class implements a typed, null-save triple of three other objects.
 * 
 * @author volker_belli
 * 
 */
public class Triple<T1, T2, T3> extends Tuple {

	public Triple(T1 a, T2 b, T3 c) {
		super(a, b, c);
	}

	@SuppressWarnings("unchecked")
	public T1 getA() {
		return (T1) get(0);
	}

	@SuppressWarnings("unchecked")
	public T2 getB() {
		return (T2) get(1);
	}

	@SuppressWarnings("unchecked")
	public T3 getC() {
		return (T3) get(2);
	}

	@Override
	public String toString() {
		return "#Triple["
				+ String.valueOf(getA()) + "; "
				+ String.valueOf(getB()) + "; "
				+ String.valueOf(getC()) + "]";
	}
}
