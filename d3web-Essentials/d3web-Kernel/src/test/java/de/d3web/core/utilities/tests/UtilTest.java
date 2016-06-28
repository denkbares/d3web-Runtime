/*
 * Copyright (C) 2011 denkbares GmbH
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
package de.d3web.core.utilities.tests;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;

import org.junit.Test;

import de.d3web.core.knowledge.InfoStoreUtil;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.utils.EqualsUtils;
import de.d3web.utils.Triple;

/**
 * Tests static methods from all core packages
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 04.08.2011
 */
public class UtilTest {

	/**
	 * Tests EqualsUtils.isSame
	 * 
	 * @created 04.08.2011
	 */
	@Test
	public void isSameTest() {
		Object a = new Object();
		Object b = new Object();
		Assert.assertFalse(EqualsUtils.isSame(a, b));
		Assert.assertFalse(EqualsUtils.isSame(null, b));
		Assert.assertFalse(EqualsUtils.isSame(a, null));
		Assert.assertTrue(EqualsUtils.isSame(a, a));
		Assert.assertTrue(EqualsUtils.isSame(null, null));
	}

	/**
	 * Tests EqualsUtils.equals
	 * 
	 * @created 04.08.2011
	 */
	@Test
	public void arrayEquals() {
		int[] a = new int[2];
		int[] b = new int[2];
		int[] c = new int[3];
		for (int i = 1; i < a.length; i++) {
			a[i] = i;
			b[i] = i;
			c[i] = i;
		}
		Assert.assertTrue(EqualsUtils.equals(a, a));
		Assert.assertTrue(EqualsUtils.equals(a, b));
		Assert.assertFalse(EqualsUtils.equals(a, c));
		Assert.assertFalse(EqualsUtils.equals(c, a));
		Assert.assertFalse(EqualsUtils.equals(a, null));
		Assert.assertFalse(EqualsUtils.equals(null, c));
		b[0] = 1;
		Assert.assertFalse(EqualsUtils.equals(a, b));
		Assert.assertFalse(EqualsUtils.equals(a, new Object()));
		Assert.assertFalse(EqualsUtils.equals(new Object(), c));
	}

	/**
	 * Tests InfoStoreUtil.getTrimpleComparator()
	 * 
	 * @created 04.08.2011
	 * @throws IOException
	 */
	@Test
	public void testTripleComparator() throws IOException {
		InitPluginManager.init();
		Triple<Property<?>, Locale, Object> a = new Triple<>(
				MMInfo.PROMPT, null, "foo");
		Triple<Property<?>, Locale, Object> b = new Triple<>(
				MMInfo.PROMPT, null, "bar");
		Triple<Property<?>, Locale, Object> c = new Triple<>(
				MMInfo.PROMPT, Locale.US, "bar");
		Triple<Property<?>, Locale, Object> d = new Triple<>(
				MMInfo.DESCRIPTION, null, "foo bar");
		Triple<Property<?>, Locale, Object> e = new Triple<>(
				MMInfo.PROMPT, null, null);
		List<Triple<Property<?>, Locale, Object>> list = new LinkedList<>();
		list.add(a);
		list.add(b);
		list.add(c);
		list.add(d);
		list.add(e);
		Collections.sort(list, InfoStoreUtil.getTrimpleComparator());
		Assert.assertEquals(d, list.get(0));
		Assert.assertEquals(b, list.get(1));
		Assert.assertEquals(a, list.get(2));
		Assert.assertEquals(e, list.get(3));
		Assert.assertEquals(c, list.get(4));
	}
}
