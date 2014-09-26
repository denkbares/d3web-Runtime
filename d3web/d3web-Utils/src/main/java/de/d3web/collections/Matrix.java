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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.d3web.utils.Pair;

/**
 * Utility collection class that provides a two-dimensional array that dynamically expands in both
 * dimensions as values are added. Both dimensions have indices starting from 0, as usual for
 * arrays.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 28.03.2014
 */
public class Matrix<E> {

	private final Map<Pair<Integer, Integer>, E> elements = new HashMap<Pair<Integer, Integer>, E>();
	private int rows = 0;
	private int cols = 0;

	/**
	 * Replaces the element at the specified position in this matrix with the specified element. If
	 * the current matrix is not big enough for the specified indices, it will be expanded in any
	 * direction to fit the indices.
	 *
	 * @param row row of the element to replace
	 * @param col col of the element to replace
	 * @param element element to be stored at the specified position
	 * @return the element previously at the specified position
	 * @throws IndexOutOfBoundsException if any of the indices is negative
	 */
	public E set(int row, int col, E element) {
		rows = Math.max(rows, row + 1);
		cols = Math.max(cols, col + 1);
		return elements.put(getKey(row, col), element);
	}

	/**
	 * Returns the element that is stored at the specified position in this matrix. If the current
	 * matrix does not hold an element at the specified indices, null is returned.
	 *
	 * @param row row of the element to replace
	 * @param col col of the element to replace
	 * @return the element previously at the specified position
	 * @throws IndexOutOfBoundsException if any of the indices is negative
	 */
	public E get(int row, int col) {
		return elements.get(getKey(row, col));
	}

	/**
	 * Returns the number of rows this matrix has. Therefore the valid row indices range from 0
	 * inclusively to the returned value exclusively.
	 *
	 * @return the number of rows of this matrix
	 */
	public int getRowSize() {
		return rows;
	}

	/**
	 * Returns the number of columns this matrix has. Therefore the valid column indices range from
	 * 0 inclusively to the returned value exclusively.
	 *
	 * @return the number of columns of this matrix
	 */
	public int getColSize() {
		return cols;
	}

	/**
	 * Returns the entire row of this matrix as a list. The list has always the size returned by
	 * #getColSize(). If any elements are not set in the specified row, the items are encountered as
	 * null.
	 *
	 * @param row the row to return from this matrix
	 * @return the entire row as an array
	 */
	public List<E> getRow(int row) {
		int size = getColSize();
		List<E> result = new ArrayList<E>(size);
		for (int col=0; col<size; col++) {
			result.add(get(row, col));
		}
		return result;
	}

	/**
	 * Returns the entire col of this matrix as a list. The list has always the size returned by
	 * #getRowSize(). If any elements are not set in the specified col, the items are encountered as
	 * null.
	 *
	 * @param col the column to return from this matrix
	 * @return the entire column as an array
	 */
	public List<E> getColumn(int col) {
		int size = getRowSize();
		List<E> result = new ArrayList<E>(size);
		for (int row=0; row<size; row++) {
			result.add(get(row, col));
		}
		return result;
	}

	private Pair<Integer, Integer> getKey(int row, int col) {
		if (row < 0) throw new IndexOutOfBoundsException("row must not be negative");
		if (col < 0) throw new IndexOutOfBoundsException("col must not be negative");
		return new Pair<Integer, Integer>(row, col);
	}
}
