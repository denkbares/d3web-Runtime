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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.d3web.collections.MultiMaps;
import de.d3web.collections.MultiMaps.CollectionFactory;
import de.d3web.collections.N2MMap;

import static org.junit.Assert.*;

public class MultiMapsTest {

	private N2MMap<String, String> baseMap;

	@Before
	public void initBase() {
		baseMap = new N2MMap<String, String>();
		baseMap.put("a", "1");
		baseMap.put("a", "2");
		baseMap.put("b", "2");
		baseMap.put("b", "3");
	}

	@Test
	public void factories() {
		checkFactory(MultiMaps.<String> hashFactory());
		checkFactory(MultiMaps.<String> hashMinimizedFactory());
		checkFactory(MultiMaps.<String> treeFactory());
		checkFactory(MultiMaps.<String> linkedFactory());
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
		N2MMap<String, String> map = new N2MMap<String, String>(factory, factory);
		map.putAll(baseMap);
		assertEquals(baseMap, map);
	}
}
