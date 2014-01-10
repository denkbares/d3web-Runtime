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

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.d3web.collections.MultiMaps;
import de.d3web.collections.MultiMaps.CollectionFactory;
import de.d3web.collections.N2MMap;

public class MultiMapsTest {

	@Test
	public void factories() {
		N2MMap<String, Integer> base = new N2MMap<String, Integer>();
		base.put("a", 1);
		base.put("a", 2);
		base.put("b", 2);
		base.put("b", 3);

		@SuppressWarnings("unchecked")
		List<CollectionFactory<? extends Object>> factories = Arrays.asList(
				MultiMaps.hashFactory(),
				MultiMaps.hashMinimizedFactory(),
				MultiMaps.treeFactory(),
				MultiMaps.linkedFactory());
		for (@SuppressWarnings("rawtypes")
		CollectionFactory factory : factories) {
			@SuppressWarnings("unchecked")
			N2MMap<String, Integer> map = new N2MMap<String, Integer>(factory, factory);
			map.putAll(base);
			assertEquals(base, map);
		}
	}
}
