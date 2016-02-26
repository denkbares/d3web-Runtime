/*
 * Copyright (C) 2014 denkbares GmbH
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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * An object that maps keys to values. In contrast to an ordinary {@link Map} this map will contain
 * any number of entries even with the same keys. Therefore adding a key/value pair will not
 * overwrite any other key/value pair with the same key. This results to potentially having a set of
 * values for a given key.
 * <p>
 * <p>
 * The <tt>MultiMap</tt> interface provides five <i>collection views</i>, which allow a map's
 * contents to be viewed as a set of keys, set of values, or set of key-value mappings, set of
 * values for a specific key and a set of keys for a specific value. The <i>order</i> of a map is
 * defined as the order in which the iterators on the map's collection views return their elements.
 * Some implementations, may guarantees as to their order, others, may not.
 * <p>
 * <p>
 * Note: great care must be exercised if mutable objects are used as map keys. The behavior of a map
 * is not specified if the value of an object is changed in a manner that affects <tt>equals</tt>
 * comparisons while the object is a key in the map. A special case of this prohibition is that it
 * is not permissible for a map to contain itself as a key. While it is permissible for a map to
 * contain itself as a value, extreme caution is advised: the <tt>equals</tt> and <tt>hashCode</tt>
 * methods are no longer well defined on such a map.
 * <p>
 * <p>
 * <p>
 * Note: bor bidirectional map implementations also great care must be exercised if mutable objects
 * are used as map values (!) for the same reasons as above.
 * <p>
 * <p>
 * The "destructive" methods contained in this interface, that is, the methods that modify the map
 * on which they operate, are specified to throw <tt>UnsupportedOperationException</tt> if this map
 * does not support the operation. If this is the case, these methods may, but are not required to,
 * throw an <tt>UnsupportedOperationException</tt> if the invocation would have no effect on the
 * map. For example, invoking the {@link #putAll(MultiMap)} method on an unmodifiable map may, but
 * is not required to, throw the exception if the map whose mappings are to be "superimposed" is
 * empty.
 * <p>
 * <p>
 * Some map implementations have restrictions on the keys and values they may contain. For example,
 * some implementations prohibit null keys and values, and some have restrictions on the types of
 * their keys. Attempting to insert an ineligible key or value throws an unchecked exception,
 * typically <tt>NullPointerException</tt> or <tt>ClassCastException</tt>. Attempting to query the
 * presence of an ineligible key or value may throw an exception, or it may simply return false;
 * some implementations will exhibit the former behavior and some will exhibit the latter. More
 * generally, attempting an operation on an ineligible key or value whose completion would not
 * result in the insertion of an ineligible element into the map may throw an exception or it may
 * succeed, at the option of the implementation. Such exceptions are marked as "optional" in the
 * specification for this interface.
 * <p>
 * <p>
 * Many methods in Collections Framework interfaces are defined in terms of the {@link
 * Object#equals(Object) equals} method. For example, the specification for the {@link
 * #containsKey(Object) containsKey(Object key)} method says: "returns <tt>true</tt> if and only if
 * this map contains a mapping for a key <tt>k</tt> such that <tt>(key==null ? k==null :
 * key.equals(k))</tt>." This specification should <i>not</i> be construed to imply that invoking
 * <tt>Map.containsKey</tt> with a non-null argument <tt>key</tt> will cause <tt>key.equals(k)</tt>
 * to be invoked for any key <tt>k</tt>. Implementations are free to implement optimizations whereby
 * the <tt>equals</tt> invocation is avoided, for example, by first comparing the hash codes of the
 * two keys. (The {@link Object#hashCode()} specification guarantees that two objects with unequal
 * hash codes cannot be equal.) More generally, implementations of the various Collections Framework
 * interfaces are free to take advantage of the specified behavior of underlying {@link Object}
 * methods wherever the implementor deems it appropriate.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author Volker Belli (denkbares GmbH)
 * @created 08.01.2014
 */
public interface MultiMap<K, V> {

	// Query Operations

	/**
	 * Returns the number of key-value mappings in this map. If the map contains more than
	 * <tt>Integer.MAX_VALUE</tt> elements, returns <tt>Integer.MAX_VALUE</tt>.
	 *
	 * @return the number of key-value mappings in this map
	 */
	int size();

	/**
	 * Returns <tt>true</tt> if this map contains no key-value mappings.
	 *
	 * @return <tt>true</tt> if this map contains no key-value mappings
	 */
	boolean isEmpty();

	/**
	 * Returns <tt>true</tt> if this map contains at least one mapping for the specified key. More
	 * formally, returns <tt>true</tt> if and only if this map contains a mapping for a key
	 * <tt>k</tt> such that <tt>(key==null ? k==null : key.equals(k))</tt>. (There can be at most
	 * one such mapping.)
	 *
	 * @param key key whose presence in this map is to be tested
	 * @return <tt>true</tt> if this map contains at least one mapping for the specified key
	 * @throws ClassCastException if the key is of an inappropriate type for this map (optional)
	 * @throws NullPointerException if the specified key is null and this map does not permit null
	 * keys (optional)
	 */
	boolean containsKey(Object key);

	/**
	 * Returns <tt>true</tt> if this map maps one or more keys to the specified value. More
	 * formally, returns <tt>true</tt> if and only if this map contains at least one mapping to a
	 * value <tt>v</tt> such that <tt>(value==null ? v==null : value.equals(v))</tt>. This operation
	 * will probably require time linear in the map size for most implementations of the
	 * <tt>Map</tt> interface.
	 *
	 * @param value value whose presence in this map is to be tested
	 * @return <tt>true</tt> if this map maps one or more keys to the specified value
	 * @throws ClassCastException if the value is of an inappropriate type for this map (optional)
	 * @throws NullPointerException if the specified value is null and this map does not permit null
	 * values (optional)
	 */
	boolean containsValue(Object value);

	/**
	 * Returns <tt>true</tt> if this map contains a entry which maps the specified key to the
	 * specified value . More formally, returns <tt>true</tt> if and only if this map contains at
	 * least one mapping with a key <tt>k</tt> and a value <tt>v</tt> such that <tt>(key==null ?
	 * k==null : key.equals(k))</tt> and <tt>(value==null ? v==null : value.equals(v))</tt>. This
	 * operation will probably require time linear in the map size for most implementations of the
	 * <tt>Map</tt> interface.
	 *
	 * @param key key whose presence in this map is to be tested
	 * @param value value whose presence in this map is to be tested
	 * @return <tt>true</tt> if this map maps the specified key to the specified value
	 * @throws ClassCastException if the value is of an inappropriate type for this map (optional)
	 * @throws NullPointerException if the specified value is null and this map does not permit null
	 * values (optional)
	 */
	boolean contains(Object key, Object value);

	/**
	 * Returns all values to which the specified key is mapped. It returns an empty set if this map
	 * contains no mapping for the key.
	 * <p>
	 * <p>
	 * More formally, if this map contains a mapping from a key {@code k} to a value {@code v} such
	 * that {@code (key==null ? k==null : key.equals(k))}, then this method returns {@code v} in the
	 * set; otherwise v is not contained in the returned set.
	 *
	 * @param key the key whose associated values are to be returned
	 * @return the value to which the specified key is mapped, or {@code null} if this map contains
	 * no mapping for the key
	 * @throws ClassCastException if the key is of an inappropriate type for this map (optional)
	 * @throws NullPointerException if the specified key is null and this map does not permit null
	 * keys (optional)
	 */
	Set<V> getValues(Object key);

	/**
	 * Returns all keys to which the specified value is mapped. It returns an empty set if this map
	 * contains no mapping for the value.
	 * <p>
	 * <p>
	 * More formally, if this map contains a mapping from a key {@code k} to a value {@code v} such
	 * that {@code (value==null ? v==null : value.equals(v))}, then this method returns {@code k} in
	 * the set; otherwise k is not contained in the returned set.
	 *
	 * @param value the value whose associated keys are to be returned
	 * @return the value to which the specified key is mapped, or {@code null} if this map contains
	 * no mapping for the key
	 * @throws ClassCastException if the key is of an inappropriate type for this map (optional)
	 * @throws NullPointerException if the specified key is null and this map does not permit null
	 * keys (optional)
	 */
	Set<K> getKeys(Object value);

	// Modification Operations

	/**
	 * Associates the specified value with the specified key in this map (optional operation). If
	 * the map previously contained a mapping for the equal key and value, it is not guaranteed if
	 * the existing mapping is overwritten or not.
	 *
	 * @param key key with which the specified value is to be associated
	 * @param value value to be associated with the specified key
	 * @return <tt>true</tt> if (and only if) the specified key to value mapping has not been
	 * present in this map and therefore has been added to the map
	 * @throws UnsupportedOperationException if the <tt>put</tt> operation is not supported by this
	 * map
	 * @throws ClassCastException if the class of the specified key or value prevents it from being
	 * stored in this map
	 * @throws NullPointerException if the specified key or value is null and this map does not
	 * permit null keys or values
	 * @throws IllegalArgumentException if some property of the specified key or value prevents it
	 * from being stored in this map
	 */
	boolean put(K key, V value);

	/**
	 * Removes the mappings for a key from this map if it is present (optional operation). More
	 * formally, if this map contains a mapping from key <tt>k</tt> to value <tt>v</tt> such that
	 * <code>(key==null ?  k==null : key.equals(k))</code>, that mapping is removed.
	 * <p>
	 * <p>
	 * Returns all the values to which this map previously associated the key, or an empty set if
	 * the map contained no mapping for the key.
	 * <p>
	 * <p>
	 * The map will not contain a mapping for the specified key once the call returns.
	 *
	 * @param key key whose mappings are to be removed from the map
	 * @return the previous values associated with <tt>key</tt>
	 * @throws UnsupportedOperationException if the <tt>remove</tt> operation is not supported by
	 * this map
	 * @throws ClassCastException if the key is of an inappropriate type for this map (optional)
	 * @throws NullPointerException if the specified key is null and this map does not permit null
	 * keys (optional)
	 */
	Set<V> removeKey(Object key);

	/**
	 * Removes the mappings for a value from this map if it is present (optional operation). More
	 * formally, if this map contains a mapping from key <tt>k</tt> to value <tt>v</tt> such that
	 * <code>(value==null ?  v==null : value.equals(v))</code>, that mapping is removed.
	 * <p>
	 * <p>
	 * Returns all the keys to which this map previously associated the value, or an empty set if
	 * the map contained no mapping to the value.
	 * <p>
	 * <p>
	 * The map will not contain a mapping for the specified value once the call returns.
	 *
	 * @param value value whose mappings are to be removed from the map
	 * @return the previous keys associated to the <tt>value</tt>
	 * @throws UnsupportedOperationException if the <tt>remove</tt> operation is not supported by
	 * this map
	 * @throws ClassCastException if the key is of an inappropriate type for this map (optional)
	 * @throws NullPointerException if the specified key is null and this map does not permit null
	 * keys (optional)
	 */
	Set<K> removeValue(Object value);

	/**
	 * Removes the mapping for a key to value mapping from this map if it is present (optional
	 * operation). More formally, if this map contains a mapping from key <tt>k</tt> to value
	 * <tt>v</tt> such that <code>(key==null ?  k==null : key.equals(k))</code> and
	 * <code>(value==null ?  v==null : value.equals(v))</code>, that mapping is removed.
	 * <p>
	 * <p>
	 * Returns <tt>true</tt> if (and only if) there was such a mapping contained in this map and
	 * therefore the map has been changed as a result of this call.
	 * <p>
	 * <p>
	 * The map will not contain the specified mapping once the call returns.
	 *
	 * @param key key whose mapping are to be removed from the map
	 * @param value value whose mapping are to be removed from the map
	 * @return <tt>true</tt> if (and only if) the specified key to value mapping has been present in
	 * this map and therefore has been removed from the map
	 * @throws UnsupportedOperationException if the <tt>remove</tt> operation is not supported by
	 * this map
	 * @throws ClassCastException if the key is of an inappropriate type for this map (optional)
	 * @throws NullPointerException if the specified key is null and this map does not permit null
	 * keys (optional)
	 */
	boolean remove(Object key, Object value);

	// Bulk Operations

	/**
	 * Copies all of the mappings from the specified map to this map (optional operation). The
	 * effect of this call is equivalent to that of calling {@link #put(Object, Object) put(k, v)}
	 * on this map once for each mapping from key <tt>k</tt> to value <tt>v</tt> in the specified
	 * map. The behavior of this operation is undefined if the specified map is modified while the
	 * operation is in progress.
	 *
	 * @param m mappings to be stored in this map
	 * @return true if this multi map has changed due to this operation, false if no (new) values
	 * have been added.
	 * @throws UnsupportedOperationException if the <tt>putAll</tt> operation is not supported by
	 * this map
	 * @throws ClassCastException if the class of a key or value in the specified map prevents it
	 * from being stored in this map
	 * @throws NullPointerException if the specified map is null, or if this map does not permit
	 * null keys or values, and the specified map contains null keys or values
	 * @throws IllegalArgumentException if some property of a key or value in the specified map
	 * prevents it from being stored in this map
	 */
	boolean putAll(Map<? extends K, ? extends V> m);

	/**
	 * Copies all of the mappings from the specified map to this map (optional operation). The
	 * effect of this call is equivalent to that of calling {@link #put(Object, Object) put(k, v)}
	 * on this map once for each mapping from key <tt>k</tt> to value <tt>v</tt> in the specified
	 * map. The behavior of this operation is undefined if the specified map is modified while the
	 * operation is in progress.
	 *
	 * @param m mappings to be stored in this map
	 * @return true if this multi map has changed due to this operation, false if no (new) values
	 * have been added.
	 * @throws UnsupportedOperationException if the <tt>putAll</tt> operation is not supported by
	 * this map
	 * @throws ClassCastException if the class of a key or value in the specified map prevents it
	 * from being stored in this map
	 * @throws NullPointerException if the specified map is null, or if this map does not permit
	 * null keys or values, and the specified map contains null keys or values
	 * @throws IllegalArgumentException if some property of a key or value in the specified map
	 * prevents it from being stored in this map
	 */
	boolean putAll(MultiMap<? extends K, ? extends V> m);

	/**
	 * Add each of the specified values for the specified key to this multi map. The effect of this
	 * call is equivalent to that of calling {@link #put(Object, Object) put(k, v)} on this map once
	 * for the specified <tt>key</tt> and each value of the specified <tt>values</tt>. The behavior
	 * of this operation is undefined if the specified map is modified while the operation is in
	 * progress.
	 *
	 * @param key the key to store the values for
	 * @param values the values to be stored
	 * @return true if this multi map has changed due to this operation, false if no (new) values
	 * have been added.
	 * @throws UnsupportedOperationException if the <tt>putAll</tt> operation is not supported by
	 * this map
	 * @throws ClassCastException if the class of a key or value in the specified map prevents it
	 * from being stored in this map
	 * @throws NullPointerException if the specified values are null, or if this multi map does not
	 * permit null keys or values, and the specified key is null or the specified values contains
	 * null
	 * @throws IllegalArgumentException if some property of a key or value in the specified map
	 * prevents it from being stored in this map
	 */
	default boolean putAll(K key, Collection<? extends V> values) {
		boolean hasChanged = false;
		for (V value : values) {
			hasChanged |= put(key, value);
		}
		return hasChanged;
	}

	/**
	 * Add each of the specified values for the specified key to this multi map. The effect of this
	 * call is equivalent to that of calling {@link #put(Object, Object) put(k, v)} on this map once
	 * for the specified <tt>key</tt> and each value of the specified <tt>values</tt>. The behavior
	 * of this operation is undefined if the specified map is modified while the operation is in
	 * progress.
	 *
	 * @param key the key to store the values for
	 * @param values the values to be stored
	 * @return true if this multi map has changed due to this operation, false if no (new) values
	 * have been added.
	 * @throws UnsupportedOperationException if the <tt>putAll</tt> operation is not supported by
	 * this map
	 * @throws ClassCastException if the class of a key or value in the specified map prevents it
	 * from being stored in this map
	 * @throws NullPointerException if the specified values are null, or if this multi map does not
	 * permit null keys or values, and the specified key is null or the specified values contains
	 * null
	 * @throws IllegalArgumentException if some property of a key or value in the specified map
	 * prevents it from being stored in this map
	 */
	@SuppressWarnings("unchecked")
	default boolean putAll(K key, V... values) {
		boolean hasChanged = false;
		for (V value : values) {
			hasChanged |= put(key, value);
		}
		return hasChanged;
	}

	/**
	 * Removes all of the mappings from this map (optional operation). The map will be empty after
	 * this call returns.
	 *
	 * @throws UnsupportedOperationException if the <tt>clear</tt> operation is not supported by
	 * this map
	 */
	void clear();

	// Views

	/**
	 * Returns an unmodifiable {@link Set} view of the keys contained in this map.
	 *
	 * @return a set view of the keys contained in this map
	 */
	Set<K> keySet();

	/**
	 * Returns an unmodifiable {@link Set} view of the values contained in this map.
	 *
	 * @return a set view of the values contained in this map
	 */
	Set<V> valueSet();

	/**
	 * Returns a {@link Set} view of the mappings contained in this map. The set is backed by the
	 * map, so changes to the map are reflected in the set, and vice-versa. If the map is modified
	 * while an iteration over the set is in progress (except through the iterator's own
	 * <tt>remove</tt> operation, or through the <tt>setValue</tt> operation on a map entry returned
	 * by the iterator) the results of the iteration are undefined. The set supports element
	 * removal, which removes the corresponding mapping from the map, via the
	 * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt> , <tt>retainAll</tt> and
	 * <tt>clear</tt> operations. It does not support the <tt>add</tt> or <tt>addAll</tt>
	 * operations.
	 *
	 * @return a set view of the mappings contained in this map
	 */
	Set<Map.Entry<K, V>> entrySet();

	// Comparison and hashing

	/**
	 * Compares the specified object with this map for equality. Returns <tt>true</tt> if the given
	 * object is also a MultiMap and the two maps represent the same mappings. More formally, two
	 * maps <tt>m1</tt> and <tt>m2</tt> represent the same mappings if
	 * <tt>m1.entrySet().equals(m2.entrySet())</tt>. This ensures that the <tt>equals</tt> method
	 * works properly across different implementations of the <tt>MultiMap</tt> interface.
	 *
	 * @param o object to be compared for equality with this map
	 * @return <tt>true</tt> if the specified object is equal to this map
	 */
	@Override
	boolean equals(Object o);

	/**
	 * Returns the hash code value for this map. The hash code of a map is defined to be the sum of
	 * the hash codes of each entry in the map's <tt>entrySet()</tt> view. This ensures that
	 * <tt>m1.equals(m2)</tt> implies that <tt>m1.hashCode()==m2.hashCode()</tt> for any two maps
	 * <tt>m1</tt> and <tt>m2</tt>, as required by the general contract of {@link Object#hashCode}.
	 *
	 * @return the hash code value for this map
	 * @see Map.Entry#hashCode()
	 * @see Object#equals(Object)
	 * @see #equals(Object)
	 */
	@Override
	int hashCode();

	/**
	 * Returns a string representation of this map. The string representation consists of a list of
	 * key-value mappings in the order returned by the map's <tt>entrySet</tt> view's iterator,
	 * enclosed in braces ( <tt>"{}"</tt>). Adjacent mappings are separated by the characters <tt>",
	 * "</tt> (comma and space). Each key-value mapping is rendered as the key followed by an equals
	 * sign (<tt>"="</tt>) followed by the associated value. Keys and values are converted to
	 * strings as by {@link String#valueOf(Object)}.
	 *
	 * @return a string representation of this map
	 */
	@Override
	public String toString();

	/**
	 * Returns an immutable {@link Map} that represents the contents of this MultiMap, usually
	 * without copying the contents of this map. If this map will be changed, the returned map will
	 * also change.
	 *
	 * @return a map representation of the contents of this multi map
	 * @created 09.01.2014
	 */
	public Map<K, Set<V>> toMap();
}
