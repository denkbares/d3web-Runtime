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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Test;

import de.d3web.collections.PeekableIterator;

/**
 * Class for testing the PeekableIterator
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 15.10.14.
 */
public class PeekableIteratorTest {

	@Test
	public void basic() {
		// create list to iterate
		int COUNT = 100;
		List<Integer> list = new ArrayList<Integer>(COUNT);
		for (int i=0; i<COUNT; i++) list.add(i);

		// compare the iterators
		Iterator<Integer> expected = list.iterator();
		PeekableIterator<Integer> actual = new PeekableIterator<Integer>(list.iterator());

		while (expected.hasNext()) {
			// both iterators must have same length
			Assert.assertTrue("peek-iterator has less elements", actual.hasNext());

			// both iterators must have same items for peek and next
			int exp = expected.next();

			// try peek first (0-4 times)
			for (int i=0; i<(exp+2)%5; i++) {
				int act = actual.peek();
				Assert.assertEquals("peek returns wrong value", exp, act);
			}

			// try also next
			int act = actual.next();
			Assert.assertEquals("next returns wrong value", exp, act);
		}

		// both iterators must have same length
		Assert.assertFalse("peek-iterator has additional elements", actual.hasNext());
	}

	@Test
	public void nullElements() {
		// try with null as last element
		PeekableIterator<String> iter = new PeekableIterator<String>(Arrays.asList("foo", null));

		// check for "foo"
		Assert.assertTrue(iter.hasNext());
		Assert.assertEquals("foo", iter.peek());
		Assert.assertEquals("foo", iter.next());

		// check for null
		Assert.assertTrue(iter.hasNext());
		Assert.assertEquals(null, iter.peek());
		Assert.assertEquals(null, iter.next());

		// check end
		Assert.assertFalse(iter.hasNext());

		// try also with null as first element
		iter = new PeekableIterator<String>(Arrays.asList(null, "foo").iterator());

		// check for null
		Assert.assertTrue(iter.hasNext());
		Assert.assertEquals(null, iter.peek());
		Assert.assertEquals(null, iter.next());

		// check for "foo"
		Assert.assertTrue(iter.hasNext());
		Assert.assertEquals("foo", iter.peek());
		Assert.assertEquals("foo", iter.next());

		// check end
		Assert.assertFalse(iter.hasNext());
	}

	@Test (expected = NoSuchElementException.class)
	public void exceedRangeByNext() {
		new PeekableIterator<Integer>(Collections.<Integer>emptyIterator()).next();
	}

	@Test (expected = NoSuchElementException.class)
	public void exceedRangeByPeek() {
		new PeekableIterator<Integer>(Collections.<Integer>emptyIterator()).peek();
	}
}
