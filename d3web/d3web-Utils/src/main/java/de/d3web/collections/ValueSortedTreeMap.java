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

package de.d3web.collections;

import java.util.*;

/**
 * A SortedMap which is sorted by values.
 *
 * NOTE: You may not put 'null' as a value into this map. It will cause an IllegalArgumentException.
 *
 * Created by Jochen Reutelshofer on 22.07.14.
 */
public class ValueSortedTreeMap<K extends Comparable<K>,V extends Comparable<V>> implements SortedMap<K,V> {

    /*
    The map being sorted by values
     */
    private final SortedMap<K,V> sortedMap;


    /*
      we need any auxiliary map for the implementation of the comparator, sas
      the TreeMap implementation instance uses compare() we within get()/contains()
     */
    private final Map<K,V> auxMap = new HashMap<K, V>();
    private V justInserted = null;

    public ValueSortedTreeMap() {
        sortedMap = new TreeMap<K,V>(new ValueComparator<K,V>());
    }

    /**
     * Compares two keys of a map according to the corresponding values
     *
     * @param <K>
     * @param <V>
     */
    class ValueComparator<K, V extends Comparable<V>> implements Comparator<K> {

        @Override
        public int compare(K o1, K o2) {
            if(o1.equals(o2)) return 0;
            // here the auxiliary map is required

            final V value1 = (V) auxMap.get(o1);
            V value2 = (V) auxMap.get(o2);
            if(value2 != null && value2.equals(justInserted)) {
                value2 = null;
            }
            if(value1 == null) return -1;
            if(value2 == null) return 1;
            return value1.compareTo(value2);
        }
    }


    /*
    All these interface methods of SortedMap are just delegated to the encapsulated SortedMap instance,
    except for the modifying operation which are also performed on the auxiliary map.
     */

    @Override
    public Comparator<? super K> comparator() {
        return sortedMap.comparator();
    }

    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return sortedMap.subMap(fromKey, toKey);
    }

    @Override
    public SortedMap<K, V> headMap(K toKey) {
        return sortedMap.headMap(toKey);
    }

    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
        return sortedMap.tailMap(fromKey);
    }

    @Override
    public K firstKey() {
        return sortedMap.firstKey();
    }

    @Override
    public K lastKey() {
        return  sortedMap.lastKey();
    }

    @Override
    public int size() {
        return sortedMap.size();
    }

    @Override
    public boolean isEmpty() {
        return sortedMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return sortedMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return sortedMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return sortedMap.get(key);
    }

    @Override
    public V put(K key, V value) {
        if(key == null || value == null) {
            throw new IllegalArgumentException( key == null ? "key was null" : "value was null");
        }
        auxMap.put(key, value);
        justInserted = value;
        V result = sortedMap.put(key, value);
        justInserted = null;
        return result;
    }

    @Override
    public V remove(Object key) {
        auxMap.remove(key);
        return sortedMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        auxMap.putAll(m);
        sortedMap.putAll(m);
    }

    @Override
    public void clear() {
        auxMap.clear();
        sortedMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return sortedMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return sortedMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return sortedMap.entrySet();
    }


}
