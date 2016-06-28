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
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.Resource;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.utilities.NamedObjectComparator;
import de.d3web.core.utilities.ResourceComparator;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Tests comparators delivered with the kernel
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 03.08.2011
 */
public class ComparatorTest {

	private KnowledgeBase kb;

	@Before
	public void setUp() throws IOException {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();
	}

	@Test
	public void testNamedObjectComparator() {
		List<Solution> solutions = new LinkedList<Solution>();
		Solution a = new Solution(kb, "A");
		solutions.add(a);
		Solution z = new Solution(kb, "Z");
		solutions.add(z);
		Solution c = new Solution(kb, "C");
		solutions.add(c);
		Collections.sort(solutions, new NamedObjectComparator());
		Assert.assertEquals(a, solutions.get(0));
		Assert.assertEquals(c, solutions.get(1));
		Assert.assertEquals(z, solutions.get(2));
	}

	@Test
	public void testResourceComparator() throws IOException {
		List<Resource> resouces = new LinkedList<Resource>();
		TestResource b_b = new TestResource("b/b");
		resouces.add(b_b);
		TestResource c_a = new TestResource("c/a");
		resouces.add(c_a);
		TestResource b_a = new TestResource("b/a");
		resouces.add(b_a);
		Collections.sort(resouces, new ResourceComparator());
		Assert.assertEquals(b_a, resouces.get(0));
		Assert.assertEquals(b_b, resouces.get(1));
		Assert.assertEquals(c_a, resouces.get(2));
		// cover unused methods
		Assert.assertNull(b_a.getInputStream());
		Assert.assertEquals(0, b_a.getSize());
	}

	private static class TestResource implements Resource {

		private final String path;

		public TestResource(String path) {
			this.path = path;
		}

		@Override
		public long getSize() {
			return 0;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return null;
		}

		@Override
		public String getPathName() {
			return path;
		}

	}

}
