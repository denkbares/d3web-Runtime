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

import org.junit.Before;
import org.junit.Test;

import de.d3web.collections.MultiMaps;
import de.d3web.collections.MultiMaps.CollectionFactory;
import de.d3web.collections.N2MMap;

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

	private void checkFactory(CollectionFactory<String> factory) {
		N2MMap<String, String> map = new N2MMap<String, String>(factory, factory);
		map.putAll(baseMap);
		assertEquals(baseMap, map);
	}
}
