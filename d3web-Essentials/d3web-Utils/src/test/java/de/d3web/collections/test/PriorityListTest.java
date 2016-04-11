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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import de.d3web.collections.PriorityList;
import de.d3web.collections.PriorityList.Group;

public class PriorityListTest {

	private static final double DEFAULT_PRIO = 5d;
	private PriorityList<Double, String> list = new PriorityList<Double, String>(DEFAULT_PRIO);

	@Before
	public void setUp() {
		list.clear();
		for (int i = 1; i <= 5; i++) {
			list.add(new Double(i), "v" + i);
		}
	}

	@Test
	public void addAndRemove() {
		assertEquals("[v1, v2, v3, v4, v5]", list.toString());
		list.add(0, "v0");
		list.add(6, "v6");
		assertEquals("[v0, v1, v2, v3, v4, v5, v6]", list.toString());
		list.remove(3);
		assertEquals("[v0, v1, v2, v4, v5, v6]", list.toString());
		list.add(3d, "v3a");
		list.add(3.5d, "v3c");
		list.add(3d, "v3b");
		list.add(2d, "v2b");
		assertEquals("[v0, v1, v2, v2b, v3a, v3b, v3c, v4, v5, v6]", list.toString());
		list.remove("v5");
		list.remove("v6");
		list.remove(1);
		list.remove(0);
		assertEquals("[v2, v2b, v3a, v3b, v3c, v4]", list.toString());
	}

	@Test
	public void clearAndIsEmpty() {
		list.clear();
		assertTrue(list.isEmpty());
		assertEquals("[]", list.toString());
		assertEquals("[]", list.getElements(1d).toString());
		assertEquals("[]", list.getElements(DEFAULT_PRIO).toString());
		assertEquals(null, list.getHighestPriority());
		assertEquals(null, list.getLowestPriority());
	}

	@Test
	public void inits() {
		list.clear();
		list.add("value");
		assertEquals("[value]", list.getElements(DEFAULT_PRIO).toString());
		assertEquals("[value]", new PriorityList<Double, String>(DEFAULT_PRIO, list).toString());
	}

	@Test
	public void set() {
		list.set(list.indexOf("v2"), "foo");
		assertEquals("[v1, foo, v3, v4, v5]", list.toString());
	}

	@Test
	public void get() {
		list.add(3d, "v3a");
		list.add(3.5d, "v3c");
		list.add(3d, "v3b");
		assertEquals("[v3, v3a, v3b]", list.getElements(3d).toString());
		assertEquals("[v3c]", list.getElements(3.5).toString());
		assertEquals("[]", list.getElements(10d).toString());
		assertEquals("[1.0, 2.0, 3.0, 3.5, 4.0, 5.0]", list.getPriorities().toString());
		assertEquals(5d, list.getHighestPriority().doubleValue(), 0.001);
		assertEquals(1d, list.getLowestPriority().doubleValue(), 0.001);
		assertEquals(DEFAULT_PRIO, list.getDefaultPriority().doubleValue(), 0.001);

		assertEquals(2d, list.getPriority("v2").doubleValue(), 0.001);
		assertEquals(null, list.getPriority("no"));
	}

	@Test
	public void groups() {
		list.remove("v4");
		list.remove("v5");
		list.add(3d, "v3a");
		assertEquals("[1.0=[v1], 2.0=[v2], 3.0=[v3, v3a]]", list.getPriorityGroups().toString());
		assertEquals("1.0", list.groupIterator().next().getPriority().toString());
		assertEquals("[v1]", list.groupIterator().next().getElements().toString());

		Iterator<Group<Double, String>> iter = list.groupIterator();
		assertTrue(iter.hasNext());
		assertEquals("1.0=[v1]", iter.next().toString());
		assertTrue(iter.hasNext());
		assertEquals("2.0=[v2]", iter.next().toString());
		assertTrue(iter.hasNext());
		assertEquals("3.0=[v3, v3a]", iter.next().toString());
		assertFalse(iter.hasNext());
	}

	@Test(expected = NoSuchElementException.class)
	public void overIterate() {
		list.clear();
		Iterator<Group<Double, String>> iter = list.groupIterator();
		assertFalse(iter.hasNext());
		iter.next();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void removeIterate() {
		Iterator<Group<Double, String>> iter = list.groupIterator();
		iter.next();
		iter.remove();
	}

	@Test
	public void modifiedIterate() {
		list.clear();
		Iterator<Group<Double, String>> iter = list.groupIterator();
		assertFalse(iter.hasNext());

		// insert first item and see if it occurs
		list.add(2d, "v2");
		assertTrue(iter.hasNext());
		assertEquals("2.0=[v2]", iter.next().toString());
		assertFalse(iter.hasNext());

		// insert item before current priority and check that it not occurs
		list.add(1d, "v1");
		assertFalse(iter.hasNext());

		// insert item at current priority and check that it not occurs
		list.add(2d, "v2b");
		assertFalse(iter.hasNext());

		// insert item after current priority and see if it occurs
		list.add(3d, "v3");
		assertTrue(iter.hasNext());
		assertEquals("3.0=[v3]", iter.next().toString());
		assertFalse(iter.hasNext());

		// clear list and check that it does not fail to continue
		list.clear();
		assertFalse(iter.hasNext());
	}
}
