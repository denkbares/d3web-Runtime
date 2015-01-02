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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import de.d3web.collections.CachedIterable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author Volker Belli (denkbares GmbH)
 * @created 02.01.2015
 */
public class CachedIterableTest {

	@Test
	public void basic() {
		List<String> list = Arrays.asList("a", "b", "c");
		Iterator<String> iterator = list.iterator();
		// create cache iterable on iterator
		CachedIterable<String> cache = new CachedIterable<String>(iterator);
		// iterate two times to make sure that we can iterate repeatedly,
		// even the original iterator is consumed
		assertItems(list, cache.iterator());
		assertItems(list, cache.iterator());
		// check that the original iterator is consumes
		assertFalse(iterator.hasNext());

		// repeat to check also with iterable
		cache = new CachedIterable<String>(list);
		assertItems(list, cache.iterator());
		assertItems(list, cache.iterator());
	}

	@Test
	public void spans() {
		List<String> list = Arrays.asList("a", "b", "c");
		Iterator<String> iterator = list.iterator();
		// create cache iterable on iterator
		CachedIterable<String> cache = new CachedIterable<String>(iterator);

		assertItems(list.subList(0, 0), cache.iterator(-1, -1));
		assertItems(list.subList(0, 1), cache.iterator(-1, 1));
		assertItems(list.subList(1, 1), cache.iterator(1, 1));
		assertItems(list.subList(1, 1), cache.iterator(1, -1));
		assertItems(list.subList(2, 2), cache.iterator(2, 1));
		assertItems(list.subList(2, 2), cache.iterator(2, 2));
		assertItems(list.subList(1, 2), cache.iterator(1, 2));
		assertItems(list.subList(1, 3), cache.iterator(1, 1000));
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
