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

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.d3web.collections.DefaultMultiMap;
import de.d3web.collections.MultiMap;
import de.d3web.collections.MultiMaps;
import de.d3web.collections.MultiMaps.CollectionFactory;
import de.d3web.collections.N2MMap;

import static org.junit.Assert.*;

public class MultiMapsTest {

	private N2MMap<String, String> baseMap;

	@Before
	public void initBase() {
		baseMap = new N2MMap<>();
		baseMap.put("a", "1");
		baseMap.put("a", "2");
		baseMap.put("b", "2");
		baseMap.put("b", "3");
	}

	@Test
	public void factories() {
		checkFactory(MultiMaps.hashFactory());
		checkFactory(MultiMaps.hashMinimizedFactory());
		checkFactory(MultiMaps.treeFactory());
		checkFactory(MultiMaps.treeFactory(Comparator.<String>reverseOrder()));
		checkFactory(MultiMaps.linkedFactory());
	}

	@Test
	public void minimizedHashSet() {
		Set<String> set = MultiMaps.<String>hashMinimizedFactory().createSet();
		// test empty set
		assertTrue(set.isEmpty());
		assertEquals(0, set.size());
		assertFalse(set.iterator().hasNext());

		// one element
		set.add("test");
		assertTrue(set.contains("test"));
		assertFalse(set.contains(null));
		assertFalse(set.contains("?"));

		assertFalse(set.isEmpty());
		assertEquals(1, set.size());
		Iterator<String> iterator = set.iterator();
		assertTrue(iterator.hasNext());
		assertEquals("test", iterator.next());
		assertFalse(iterator.hasNext());
		iterator.remove();
		assertTrue(set.isEmpty());

		// three elements
		set.add("1");
		set.add(null);
		set.add("3");
		assertTrue(set.contains("1"));
		assertTrue(set.contains(null));
		assertTrue(set.contains("3"));
		assertFalse(set.contains("2"));

		assertFalse(set.isEmpty());
		assertEquals(3, set.size());

		iterator = set.iterator();
		assertTrue(iterator.hasNext());
		iterator.next();
		assertTrue(iterator.hasNext());
		iterator.next();
		assertTrue(iterator.hasNext());
		iterator.next();
		assertFalse(iterator.hasNext());

		iterator = set.iterator();
		iterator.next();
		iterator.remove();
		assertEquals(2, set.size());
		assertTrue(iterator.hasNext());
		iterator.next();
		iterator.remove();
		assertEquals(1, set.size());
		assertTrue(iterator.hasNext());
		iterator.next();
		iterator.remove();
		assertEquals(0, set.size());
	}

	@Test(expected=NoSuchElementException.class)
	public void minimizedHashSetException1() {
		Set<String> set = MultiMaps.<String>hashMinimizedFactory().createSet();
		set.iterator().next();
	}

	@Test(expected=IllegalStateException.class)
	public void minimizedHashSetException2() {
		Set<String> set = MultiMaps.<String>hashMinimizedFactory().createSet();
		set.iterator().remove();
	}

	@Test(expected=IllegalStateException.class)
	public void minimizedHashSetException3() {
		Set<String> set = MultiMaps.<String>hashMinimizedFactory().createSet();
		set.add("test");
		set.iterator().remove();
	}

	@Test(expected=IllegalStateException.class)
	public void minimizedHashSetException4() {
		Set<String> set = MultiMaps.<String>hashMinimizedFactory().createSet();
		set.add("test");
		Iterator<String> iterator = set.iterator();
		iterator.next();
		iterator.remove();
		iterator.remove();
	}

	private void checkFactory(CollectionFactory<String> factory) {
		N2MMap<String, String> map = new N2MMap<>(factory, factory);
		map.putAll(baseMap);
		assertEquals(baseMap, map);
	}

	@Test
	public void treeFactoryComparator() {
		N2MMap<String, String> map = new N2MMap<>(MultiMaps.treeFactory(Comparator.<String>reverseOrder()), MultiMaps.treeFactory(Comparator.<String>reverseOrder()));
		map.putAll(baseMap);
		assertEquals("[b, a]", map.keySet().toString());
		assertEquals("[3, 2, 1]", map.valueSet().toString());
	}

	@Test
	public void singletonMultiMap() {
		MultiMap<String, String> map = MultiMaps.singletonMultiMap("a", "b");

		assertTrue(map.containsKey("a"));
		assertFalse(map.containsValue("a"));
		assertFalse(map.containsKey("b"));
		assertTrue(map.containsValue("b"));

		assertTrue(map.contains("a", "b"));
		assertFalse(map.contains("a", "a"));
		assertFalse(map.contains("b", "b"));
		assertFalse(map.contains("b", "a"));

		assertFalse(map.isEmpty());
		assertEquals(1, map.size());

		assertEquals(Collections.singleton("a"), map.keySet());
		assertEquals(Collections.singleton("b"), map.valueSet());

		assertEquals(Collections.singleton("a"), map.getKeys("b"));
		assertEquals(Collections.singleton("b"), map.getValues("a"));
		assertTrue(map.getKeys("a").isEmpty());
		assertTrue(map.getValues("b").isEmpty());

		assertEquals("b", map.toMap().get("a").iterator().next());
		assertNull(map.toMap().get("b"));

		MultiMap<Object, Object> other = new DefaultMultiMap<>();
		assertNotEquals(other, map);
		assertNotEquals(other.hashCode(), map.hashCode());
		assertNotEquals(other.toString(), map.toString());
		other.put("a", "b");
		assertEquals(other, map);
		assertEquals(other.hashCode(), map.hashCode());
		assertEquals(other.toString(), map.toString());
		other.put("b", "a");
		assertNotEquals(other, map);
		assertNotEquals(other.hashCode(), map.hashCode());
		assertNotEquals(other.toString(), map.toString());
	}
}
