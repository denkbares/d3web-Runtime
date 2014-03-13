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

package de.d3web.collections.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import de.d3web.collections.GeneralizedSuffixTree;

import static org.junit.Assert.*;

/**
 * @author Volker Belli (denkbares GmbH)
 * @created 13.03.14.
 */
public class GeneralizedSuffixTreeTest {

	@Test
	public void basic() {
		GeneralizedSuffixTree<Integer> lookup = new GeneralizedSuffixTree<Integer>();
		lookup.put("Hello World", 17);
		lookup.put("Hello", 5);
		lookup.put("Bananas", 7);
		lookup.put("Hello Bananas", 27);
		lookup.put("Ananas", 1);
		lookup.put("Hippomania", 10);

		assertEquals(6, lookup.size());
		assertFalse(lookup.isEmpty());
		assertSearch(lookup, "world", 17);
		assertSearch(lookup, "hello", 5, 17, 27);
		assertSearch(lookup, "Ananas", 1, 7, 27);
		assertSearch(lookup, "o l", 5, 17, 27);
		assertSearch(lookup, "h an", 10, 27);

		lookup.put("Hippomania", 20);
		assertEquals(7, lookup.size());
		assertSearch(lookup, "h an", 10, 20, 27);

		lookup.removeKey("Hippomania");
		assertSearch(lookup, "h an", 27);

		lookup.removeValue(27);
		assertSearch(lookup, "h an");
		assertSearch(lookup, "world", 17);

		lookup.remove("Hello World", 17);
		assertSearch(lookup, "world");
		assertSearch(lookup, "hello", 5);
		assertSearch(lookup, "Ananas", 1, 7);
		assertSearch(lookup, "o l", 5);
		assertSearch(lookup, "h an");

		lookup.clear();
		assertSearch(lookup, "world");
		assertSearch(lookup, "hello");
		assertSearch(lookup, "Ananas");
		assertSearch(lookup, "o l");
		assertSearch(lookup, "h an");
		assertEquals(0, lookup.size());
		assertTrue(lookup.isEmpty());
	}

	private static <E> void assertSearch(GeneralizedSuffixTree<E> gst, String phrase, E... items) {
		Set<E> expected = new TreeSet<E>(Arrays.asList(items));
		Set<E> actual = search(gst, phrase);
		assertEquals(expected, actual);
	}

	private static <E> Set<E> search(GeneralizedSuffixTree<E> gst, String phrase) {
		Set<E> actual = new TreeSet<E>();
		for (E item : gst.search(phrase)) {
			actual.add(item);
		}
		return actual;
	}
}
