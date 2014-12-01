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

import de.d3web.collections.SubSpanIterator;

import static org.junit.Assert.*;

public class SubSpanIteratorTest {

	@Test
	public void basic() {
		List<String> list = Arrays.asList("a", "b", "c");
		assertElements(new SubSpanIterator<String>(list.iterator(), 0, 3), "a", "b", "c");
		assertElements(new SubSpanIterator<String>(list.iterator(), 0, 0));
		assertElements(new SubSpanIterator<String>(list.iterator(), 0, -1), "a", "b", "c");
		assertElements(new SubSpanIterator<String>(list.iterator(), -1, -1), "a", "b", "c");
		assertElements(new SubSpanIterator<String>(list.iterator(), 1, 1));
		assertElements(new SubSpanIterator<String>(list.iterator(), 1, 2), "b");
		assertElements(new SubSpanIterator<String>(list.iterator(), 1, 3), "b", "c");
		assertElements(new SubSpanIterator<String>(list.iterator(), 2, 3), "c");
		assertElements(new SubSpanIterator<String>(list.iterator(), 2, 4), "c");
		assertElements(new SubSpanIterator<String>(list.iterator(), 2, -1), "c");
		assertElements(new SubSpanIterator<String>(list.iterator(), 10, -1));
	}

	private <E> void assertElements(Iterator<E> actual, E... expected) {
		for (E e : expected) {
			assertTrue("to few elements in iterator", actual.hasNext());
			assertEquals("unexpected element in iterator", e, actual.next());
		}
		assertFalse("to many elements in iterator", actual.hasNext());
	}
}
