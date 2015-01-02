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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import de.d3web.collections.MappingIterator;
import de.d3web.collections.MappingIterator.MappingFunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author Volker Belli (denkbares GmbH)
 * @created 02.01.2015
 */
public class MappingIteratorTest {

	private static final MappingFunction<Integer, String> mapper =
			new MappingFunction<Integer, String>() {
				@Override
				public String apply(Integer i) {
					return "mapped:" + i;
				}
			};

	@Test
	public void basic() {
		List<Integer> list = Arrays.asList(1, 2, 3);
		List<String> expected = Arrays.asList("mapped:1", "mapped:2", "mapped:3");
		assertItems(expected, new MappingIterator<Integer, String>(list.iterator(), mapper));
	}

	@Test
	public void remove() {
		List<Integer> list = new ArrayList<Integer>(Arrays.asList(1, 2, 3));
		List<String> expected = Arrays.asList("mapped:2", "mapped:3");

		MappingIterator<Integer, String> iterator =
				new MappingIterator<Integer, String>(list.iterator(), mapper);
		iterator.next();
		iterator.remove();
		assertItems(expected, iterator);

		// check also if remove has been written to original list
		assertEquals(2, list.size());
		assertEquals(new Integer(2), list.get(0));
		assertEquals(new Integer(3), list.get(1));
	}

	private void assertItems(List<String> expected, Iterator<String> actual) {
		Iterator<String> exIter = expected.iterator();
		while (actual.hasNext() && exIter.hasNext()) {
			assertEquals("wrong item", exIter.next(), actual.next());
		}
		assertFalse("missing item", exIter.hasNext());
		assertFalse("additional item", actual.hasNext());
	}
}
