/*
 * Copyright (C) 2014 denkbares GmbH, Germany
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

import de.d3web.collections.ValueSortedTreeMap;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertTrue;

/**
 * Created by jochenreutelshofer on 22.07.14.
 */
public class SortedValueTreeMapTest {

    public static final String A = "a";
    public static final String B = "b";
    public static final String C = "c";
    public static final String KEY_1 = "key1";
    public static final String KEY_2 = "key2";
    public static final String KEY_3 = "key3";
    public static final String KEY_4 = "key4";

    @Test
    public void testEntrySet() {

        SortedMap<String, String> map = getSortedMap();

        assertTrue(map.firstKey().equals(KEY_3));
        assertTrue(map.lastKey().equals(KEY_2));


        LinkedHashMap<String, String> expectedMap = getExpectedMap();

        final Set<Map.Entry<String, String>> entries = map.entrySet();
        final Iterator<Map.Entry<String, String>> entryIterator = entries.iterator();
        final Iterator<Map.Entry<String, String>> expectedIterator = expectedMap.entrySet().iterator();
        int size = entries.size();
        for (int i = 0; i < size; i++) {
            final Map.Entry<String, String> entry = entryIterator.next();
            final Map.Entry<String, String> expectedEntry = expectedIterator.next();
            assertTrue(entry.getValue().equals(expectedEntry.getValue()));
        }

    }

    @Test
    public void testValues() {

        SortedMap<String, String> map = getSortedMap();
        // b is first value
        final Collection<String> values = map.values();
        assertTrue(values.size() == 4);
        final Iterator<String> valueIterator = values.iterator();
        assertTrue(valueIterator.next().equals(A));

        // c is 4th value
        valueIterator.next();
        valueIterator.next();
        final String forthElement = valueIterator.next();
        assertTrue(forthElement.equals(C));

    }

    @Test
    public void testKeys() {
        SortedMap<String, String> map = getSortedMap();

        // key3 is first key
        final Set<String> keys = map.keySet();
        final Iterator<String> keyIterator = keys.iterator();
        assertTrue(keys.size() == 4);
        assertTrue(keyIterator.next().equals(KEY_3));

        // key2 is 4th key
        keyIterator.next();
        keyIterator.next();
        final String forthKeyElement = keyIterator.next();
        assertTrue(forthKeyElement.equals(KEY_2));

    }

    @Test
    public void testSubMap() {
        SortedMap<String, String> map = getSortedMap();

        // subMap that should contain the two 'b' values
        final SortedMap<String, String> subMap = map.subMap(KEY_1, KEY_2);
        assertTrue(subMap.size() == 2);
        assertTrue(subMap.get(subMap.firstKey()).equals(B));
        assertTrue(subMap.get(subMap.lastKey()).equals(B));

    }

    /**
     * Creates a linked map in the expected order.
     *
     * @return
     */
    private LinkedHashMap<String, String> getExpectedMap() {
        LinkedHashMap<String,String> expectedMap = new LinkedHashMap<String, String>();
        expectedMap.put(KEY_3, A);
        expectedMap.put(KEY_1, B);
        expectedMap.put(KEY_4, B);
        expectedMap.put(KEY_2, C);
        return expectedMap;
    }

    private SortedMap<String, String> getSortedMap() {
        SortedMap<String,String> map = new ValueSortedTreeMap<String,String>();
        map.put(KEY_1, B);
        map.put(KEY_2, C);
        map.put(KEY_3, A);
        map.put(KEY_4, B);
        return map;
    }
}
