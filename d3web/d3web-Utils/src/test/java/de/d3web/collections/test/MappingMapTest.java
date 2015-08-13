/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import de.d3web.collections.MappingIterator;
import de.d3web.collections.MappingMap;

import static org.junit.Assert.*;

/**
 * @author Volker Belli (denkbares GmbH)
 * @created 13.08.2015
 */
public class MappingMapTest {

	private static final Map<String, Double> source = new LinkedHashMap<String, Double>();
	private static final Map<String, Long> mapped = new MappingMap<String, Double, Long>(
			source, new MappingIterator.MappingFunction<Double, Long>() {
		@Override
		public Long apply(Double sourceItem) {
			return Math.round(sourceItem);
		}
	});

	@BeforeClass
	public static void init() {
		source.put("a", 0.2);
		source.put("b", 1.4);
		source.put("c", 2.6);
		source.put("d", 3.8);
	}

	@Test
	public void size() {
		assertEquals(source.size(), mapped.size());
	}

	@Test
	public void contains() {
		assertTrue(mapped.containsKey("a"));
		assertTrue(mapped.containsKey("b"));
		assertTrue(mapped.containsKey("c"));
		assertTrue(mapped.containsKey("d"));
		assertFalse(mapped.containsKey("e"));

		assertTrue(mapped.containsValue(0L));
		assertTrue(mapped.containsValue(1L));
		assertTrue(mapped.containsValue(3L));
		assertTrue(mapped.containsValue(4L));
		assertFalse(mapped.containsValue(2L));
		assertFalse(mapped.containsValue(5L));
	}

	@Test
	public void get() {
		assertEquals(new Long(0), mapped.get("a"));
		assertEquals(new Long(1), mapped.get("b"));
		assertEquals(new Long(3), mapped.get("c"));
		assertEquals(new Long(4), mapped.get("d"));
		assertEquals(null, mapped.get("e"));
	}

	@Test
	public void iteration() {
		assertElements(
				Arrays.asList("a", "b", "c", "d").iterator(),
				mapped.keySet().iterator());
		assertElements(
				Arrays.asList(0L, 1L, 3L, 4L).iterator(),
				mapped.values().iterator());
	}

	private <E> void assertElements(Iterator<E> expected, Iterator<E> actual) {
		while (expected.hasNext() && actual.hasNext()) {
			assertEquals(expected.next(), actual.next());
		}
		assertFalse("missing elements in actual", expected.hasNext());
		assertFalse("to many elements in actual", actual.hasNext());
	}
}
