/*
 * Copyright (C) 2010 denkbares GmbH, Würzburg, Germany
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

package de.d3web.utilities.tests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.d3web.utilities.ISetMap;
import de.d3web.utilities.SetMap;

/**
 * Unit test for {@link SetMap}
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 03.09.2010
 */
public class SetMapTest {

	SetMap<String, Double> setMapUnderTest;

	/**
	 * Initializes the setMapUnderTest and asserts that the add() methods work
	 * as expected
	 */
	@Before
	public void setUp() throws Exception {
		setMapUnderTest = new SetMap<String, Double>(true);
		// adding a null-value should work every time, because the corresponding
		// Set<Double> is only initialized
		assertThat(setMapUnderTest.add("key1", null), is(true));
		// now add a "real" Double Value (for the same key),
		// that should work also:
		assertThat(setMapUnderTest.add("key1", new Double(2.4)), is(true));
		// adding the same value again should fail:
		assertThat(setMapUnderTest.add("key1", new Double(2.4)), is(false));
		// add another value to the set (for the same key)
		assertThat(setMapUnderTest.add("key1", new Double(7.5)), is(true));

		// add some other values for the key "key2"
		setMapUnderTest.addAll("key2", Arrays.asList(1.8, 4.55, 0.1, 177.6, 44.91));
	}

	/**
	 * Test method for {@link de.d3web.utilities.AbstractSetMap#addAll(java.lang.Object, java.util.Collection)}.
	 */
	@Test
	public void testAddAllKeyCollectionOfType() {
		// Summary: Add some Double´s for a new key

		// 1.) create a list of Doubles and add them
		// to the SetMap under the key "newKey"
		List<Double> doublesList = Arrays.asList(1.7, 4.7, 3.0);
		setMapUnderTest.addAll("newKey", doublesList);

		// 2.) assert that they were added to the SetMap successfully:
		assertThat(setMapUnderTest.containsKey("newKey"), is(true));
		assertThat(setMapUnderTest.get("newKey").size(), is(3));
	}

	/**
	 * Test method for {@link de.d3web.utilities.AbstractSetMap#addAll(java.util.Collection, java.lang.Object)}.
	 */
	@Test
	public void testAddAllCollectionOfKeyType() {
		// Summary: Add the value 13.1 to the Set´s for the two keys "key1" and
		// "newKey". Because "newKey" isn't mapped in the SetMap before, a new
		// mapping should be created for this key.

		// 1.) assert that the key "newKey" isn't mapped:
		assertThat(setMapUnderTest.containsKey("newKey"), is(false));
		// 2.) now add the value 13.1 for both key's:
		setMapUnderTest.addAll(Arrays.asList("key1", "newKey"),
				new Double(13.1));
		// 3.) now assure that the new mapping was created, get the Set<Double>
		// and verify its size (should be 1)
		assertThat(setMapUnderTest.containsKey("newKey"), is(true));
		assertThat(setMapUnderTest.get("newKey").size(), is(1));
		// 4.) assure, that the Set<Double> for the key "key1"
		// now has three elements (two elements were in the list before the
		// operation, and one was added during the test)
		assertThat(setMapUnderTest.get("key1").size(), is(3));
	}

	/**
	 * Test method for {@link de.d3web.utilities.AbstractSetMap#addAll(java.util.Collection, java.util.Collection)}.
	 */
	@Test
	public void testAddAllCollectionOfKeyCollectionOfType() {
		// Summary: Add the values 13.1, 17.5 and 0.88 to the Set´s for the two
		// keys "key1" and "newKey". Because "newKey" isn't mapped in the SetMap
		// before, a new mapping should be created for this key.

		// 1.) assert that the key "key2" isn't mapped:
		assertThat(setMapUnderTest.containsKey("newKey"), is(false));
		// 2.) now add the value values 13.1, 17.5 and 0.88 for both key's:
		setMapUnderTest.addAll(
				Arrays.asList("key1", "newKey"),
				Arrays.asList(13.1, 17.5, 0.88));
		// 3.) now assure that the new mapping was created, get the Set<Double>
		// and verify its size (should be 3)
		assertThat(setMapUnderTest.containsKey("newKey"), is(true));
		assertThat(setMapUnderTest.get("newKey").size(), is(3));
		// 4.) assure, that the Set<Double> for the key "key1"
		// now has five elements (two elements were in the list before the
		// operation, and three were added during the test)
		assertThat(setMapUnderTest.get("key1").size(), is(5));
	}

	/**
	 * Test method for {@link de.d3web.utilities.AbstractSetMap#addAll(de.d3web.utilities.ISetMap)}.
	 */
	@Test
	public void testAddAllISetMapOfKeyType() {
		// Summary: Copy the SetMap and add all values again. Because copies
		// aren't added twice to the set, the SetMap is identical after the
		// addAll(copy)

		// 1.) Create the copy
		@SuppressWarnings("unchecked")
		ISetMap<String, Double> copy = (ISetMap<String, Double>) setMapUnderTest.clone();
		// 2.) Assert that the correct number of elements are in the sets
		assertThat(setMapUnderTest.get("key1").size(), is(2));
		assertThat(setMapUnderTest.get("key2").size(), is(5));
		// 3.) now try to add all the copied elements
		setMapUnderTest.addAll(copy);
		// 4.) the SetMap hasn't changed:
		assertThat(setMapUnderTest.get("key1").size(), is(2));
		assertThat(setMapUnderTest.get("key2").size(), is(5));
	}

	/**
	 * Test method for {@link de.d3web.utilities.AbstractSetMap#remove(java.lang.Object, java.lang.Object)}.
	 */
	@Test
	public void testRemoveKeyType() {
		// Summary: Tests that the removing of values from the mapped Value-Sets
		// works as expected and returns the correct results

		// 1.) assert that the SetMap contains a Set<Double> for the Key "key1"
		assertThat(setMapUnderTest.containsKey("key1"), is(true));
		// 2.) assert that the Set<Double> for the Key "key! contains 2.4
		assertThat(setMapUnderTest.get("key1").contains(new Double(2.4)), is(true));
		// 3.) remove the value 2.4 from the Set (for the key "key1")
		assertThat(setMapUnderTest.remove("key1", new Double(2.4)), is(true));
		// 4.) assert that the value 2.4 was deleted:
		assertThat(setMapUnderTest.get("key1").contains(new Double(2.4)), is(false));

		// 5.) Now delete another value for the key "key1". This is the last
		// remaining value in the Set<Double> for this key, and because the
		// removeEmpty flag was set, the key-value pair should be removed
		// afterwards!
		assertThat(setMapUnderTest.get("key1").contains(new Double(7.5)), is(true));
		assertThat(setMapUnderTest.remove("key1", new Double(7.5)), is(true));
		// 6.) now check that no key "key1" exists anymore:
		assertThat(setMapUnderTest.containsKey("key1"), is(false));

		// 7.) at last: removing of a not-existent value for a non-existent key
		// should fail in every case:
		assertThat(setMapUnderTest.remove("non-existent", new Double(111.1)), is(false));
	}

	/**
	 * Test method for {@link de.d3web.utilities.AbstractSetMap#removeAll(java.lang.Object, java.util.Collection)}.
	 */
	@Test
	public void testRemoveAllKeyCollectionOfType() {
		// Summary: Remove the values 1.8, 177.6 and 5.21 (which is not in the
		// Set) from the Set<Double> for the key "key2".

		// 1.) Before the operation, the Set should have five elements:
		assertThat(setMapUnderTest.get("key2").size(), is(5));
		// 2.) Now remove the three values (from which only two are in the set)
		setMapUnderTest.removeAll("key2", Arrays.asList(1.8, 177.6, 5.21));
		// 3.) Now, the Set should only have three elements (5 - 2 = 3)
		assertThat(setMapUnderTest.get("key2").size(), is(3));

		// 4.) call removeAll(key, null)
		setMapUnderTest.removeAll("key2", null);
		assertThat(setMapUnderTest.get("key2").size(), is(3));
	}

	/**
	 * Test method for {@link de.d3web.utilities.AbstractSetMap#removeAll(java.util.Collection, java.lang.Object)}.
	 */
	@Test
	public void testRemoveAllCollectionOfKeyType() {
		// Summary: Remove the value 4.55 of both sets for the keys "key1" and
		// "key2". Only the Set for the "key2" should change

		// 1.) Before the operation, the Set for the first key has two elements,
		// and the Set for the key "key2" has five elements:
		assertThat(setMapUnderTest.get("key1").size(), is(2));
		assertThat(setMapUnderTest.get("key2").size(), is(5));
		// 2.) now remove the elements:
		setMapUnderTest.removeAll(Arrays.asList("key1", "key2"), new Double(4.55));
		// 3.) The first set should now have still two elements,
		// and the second set four elements (one was removed):
		assertThat(setMapUnderTest.get("key1").size(), is(2));
		assertThat(setMapUnderTest.get("key2").size(), is(4));

		// call removeAll() operation für null sets
		setMapUnderTest.removeAll(null, new Double(3.1));
		assertThat(setMapUnderTest.get("key1").size(), is(2));
		assertThat(setMapUnderTest.get("key2").size(), is(4));
	}

	/**
	 * Test method for {@link de.d3web.utilities.AbstractSetMap#removeAll(java.util.Collection, java.util.Collection)}.
	 */
	@Test
	public void testRemoveAllCollectionOfKeyCollectionOfType() {
		// Summary: Remove the values 2.4, 4.55 and 0.1 from the sets for the
		// keys "key1" and "key2". 2.4 is only in the set for the first key,
		// whereas 4.55 and 0.1 are only in the set for the second key.

		// 1.) Before the operation, the Set for the first key has two elements,
		// and the Set for the key "key2" has five elements:
		assertThat(setMapUnderTest.get("key1").size(), is(2));
		assertThat(setMapUnderTest.get("key2").size(), is(5));
		// 2.) now remove the elements:
		setMapUnderTest.removeAll(
				Arrays.asList("key1", "key2"),
				Arrays.asList(2.4, 4.55, 0.1));
		// 3.) The first set should now have 1 element,
		// and the second set three elements:
		assertThat(setMapUnderTest.get("key1").size(), is(1));
		assertThat(setMapUnderTest.get("key2").size(), is(3));
	}

	/**
	 * Test method for {@link de.d3web.utilities.AbstractSetMap#getAllValues()}.
	 */
	@Test
	public void testGetAllValues() {
		// Summary: Get all the values for all keys in one set

		// 1.) execute the getAllValues() method
		Set<Double> allValues = setMapUnderTest.getAllValues();
		// 2.) construct the expected Set manually
		Set<Double> expectedSet = new HashSet<Double>(
				Arrays.asList(2.4, 7.5, 1.8, 4.55, 0.1, 177.6, 44.91));
		// 3.) compare the two Sets
		assertThat(allValues.equals(expectedSet), is(true));
		// -----
		// 4.) assert that getAllValues(null) returns empty Set
		assertThat(setMapUnderTest.getAllValues(null),
				is(equalTo((Set<Double>) new HashSet<Double>())));
		// 5.) assert that getAllValues() returns empty Set for not existend
		// key:
		assertThat(setMapUnderTest.getAllValues(
						new HashSet<String>(Arrays.asList("not-existent"))),
				is(equalTo((Set<Double>) new HashSet<Double>())));
	}


}
