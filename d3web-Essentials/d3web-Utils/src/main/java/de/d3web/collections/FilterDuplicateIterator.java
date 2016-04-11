/*
 * Copyright (C) 2013 denkbares GmbH
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

package de.d3web.collections;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Volker Belli (denkbares GmbH)
 * @created 13.03.2014
 */
public class FilterDuplicateIterator<E> extends FilterIterator<E> {
	private final Set<E> accepted = new HashSet<E>();

	public FilterDuplicateIterator(Iterator<E> source) {
		super(source);
	}

	@Override
	public boolean accept(E item) {
		return accepted.add(item);
	}
}
