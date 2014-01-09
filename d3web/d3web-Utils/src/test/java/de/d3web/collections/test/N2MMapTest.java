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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.d3web.collections.N2MMap;
import de.d3web.collections.MultiMap;

public class N2MMapTest {

	private MultiMap<String, Integer> map;

	@Before
	public void setUp() {
		map = new N2MMap<String, Integer>();
	}

	@Test
	public void toStringTest() {
		assertEquals("{}", map.toString());
		map.put("a", 4);
		assertEquals("{a=4}", map.toString());
		map.put("b", 4);
		map.put("a", 5);
		assertEquals("{a=4, a=5, b=4}", map.toString());
	}

	@Test
	public void isEmpty() {
		assertTrue(map.isEmpty());
		map.put("a", 4);
		assertFalse(map.isEmpty());
		map.put("b", 4);
		map.put("a", 5);
		assertFalse(map.isEmpty());
		map.remove("a", 4);
		assertFalse(map.isEmpty());
		map.remove("b", 4);
		assertFalse(map.isEmpty());
		map.remove("a", 5);
		assertTrue(map.isEmpty());
	}

	@Test
	public void size() {
		assertEquals(0, map.size());
		map.put("a", 4);
		assertEquals(1, map.size());
		map.put("b", 4);
		map.put("a", 5);
		assertEquals(3, map.size());
		map.put("a", 5);
		assertEquals(3, map.size());
		map.remove("a", 4);
		assertEquals(2, map.size());
		map.remove("b", 4);
		assertEquals(1, map.size());
		map.remove("a", 5);
		assertEquals(0, map.size());
	}

	@Test
	public void contains() {
		assertFalse(map.contains("a", 1));
		assertFalse(map.containsKey("a"));
		assertFalse(map.containsValue(1));
		map.put("a", 1);
		assertTrue(map.contains("a", 1));
		assertTrue(map.containsKey("a"));
		assertTrue(map.containsValue(1));
	}

	@Test
	public void get() {
		assertEquals("[]", toString(map.getValues("a")));
		assertEquals("[]", toString(map.getKeys(1)));
		map.put("a", 1);
		map.put("a", 2);
		map.put("b", 2);
		map.put("b", 3);
		assertEquals("[1, 2]", toString(map.getValues("a")));
		assertEquals("[2, 3]", toString(map.getValues("b")));
		assertEquals("[a]", toString(map.getKeys(1)));
		assertEquals("[a, b]", toString(map.getKeys(2)));
		assertEquals("[b]", toString(map.getKeys(3)));
	}

	@Test
	public void put() {
		Map<String, Integer> hash = new HashMap<String, Integer>();
		hash.put("a", 1);
		hash.put("b", 1);

		MultiMap<String, Integer> n2m = new N2MMap<String, Integer>();
		n2m.put("a", 2);
		n2m.put("a", 3);

		map.put("a", 0);
		map.putAll(hash);
		map.putAll(n2m);
		map.put("a", 4);

		assertEquals("[0, 1, 2, 3, 4]", toString(map.getValues("a")));
		assertEquals("[1]", toString(map.getValues("b")));
		assertEquals("[a]", toString(map.getKeys(0)));
		assertEquals("[a, b]", toString(map.getKeys(1)));
		assertEquals("[a]", toString(map.getKeys(2)));
		assertEquals("[a]", toString(map.getKeys(3)));
		assertEquals("[a]", toString(map.getKeys(4)));
		assertEquals("[]", toString(map.getKeys(5)));
	}

	private static <T extends Comparable<? super T>> String toString(Collection<T> c) {
		List<T> list = new ArrayList<T>(c);
		Collections.sort(list);
		return list.toString();
	}

	@Test
	public void remove() {
		map.put("a", 1);
		map.put("a", 2);
		map.put("a", 3);
		map.put("b", 1);
		map.put("b", 2);
		map.put("b", 3);
		map.put("c", 1);
		map.put("c", 2);
		map.put("c", 3);

		map.remove("a", 1);
		assertEquals(8, map.size());

		map.removeKey("b");
		assertEquals(5, map.size());

		map.removeValue(3);
		assertEquals(3, map.size());
		assertTrue(map.containsKey("a"));
		assertFalse(map.containsKey("b"));
		assertTrue(map.containsKey("c"));
		assertTrue(map.containsValue(1));
		assertTrue(map.containsValue(2));
		assertFalse(map.containsValue(3));
		assertEquals("{a=2, c=1, c=2}", map.toString());

		map.clear();
		assertEquals(0, map.size());
		assertTrue(map.isEmpty());
		assertFalse(map.containsKey("a"));
		assertFalse(map.containsKey("b"));
		assertFalse(map.containsKey("c"));
		assertFalse(map.containsValue(1));
		assertFalse(map.containsValue(2));
		assertFalse(map.containsValue(3));
	}

	@Test
	public void sets() {
		assertEquals("[]", toString(map.keySet()));
		assertEquals("[]", toString(map.valueSet()));
		map.put("a", 1);
		map.put("a", 2);
		map.put("b", 2);
		assertEquals("[a, b]", toString(map.keySet()));
		assertEquals("[1, 2]", toString(map.valueSet()));
	}

	@Test
	public void misc() {
		Map<String, Integer> hash = new HashMap<String, Integer>();
		hash.put("a", 1);
		hash.put("b", 1);

		// add other before
		map.put("c", 3);
		map.putAll(hash);

		// add other after
		MultiMap<String, Integer> map2 = new N2MMap<String, Integer>();
		map2.putAll(hash);
		map2.put("c", 3);
		map2.put("c", 4);
		map2.put("d", 3);
		map2.put("d", 4);
		map2.put("e", 5);
		map2.removeKey("d");
		map2.removeValue(4);
		map2.remove("e", 5);

		// adapt hash-map
		hash.put("c", 3);

		assertEquals(toString(map.keySet()), toString(map.keySet()));
		assertEquals(toString(map.valueSet()), toString(map.valueSet()));
		assertEquals(map.hashCode(), hash.hashCode());
		assertEquals(map.hashCode(), map2.hashCode());

		assertTrue(map.equals(map2));
		assertTrue(map2.equals(map));

		assertFalse(map.equals(hash));
		assertFalse(hash.equals(map));
	}
}
