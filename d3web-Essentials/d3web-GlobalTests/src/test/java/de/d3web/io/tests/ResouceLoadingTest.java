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
package de.d3web.io.tests;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;

import org.junit.Test;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.Resource;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Tests loading a resource from a kb archieve
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 04.08.2011
 */
public class ResouceLoadingTest {

	@Test
	public void test() throws IOException {
		InitPluginManager.init();
		KnowledgeBase kb = PersistenceManager.getInstance().load(
				new File("src/test/resources/kbs/original/MMInfo/MMInfo-Original.jar"));
		Resource logo = kb.getResource("d3web logo.png");
		Assert.assertNotNull(logo);
		// file has 33741 bytes
		Assert.assertEquals(33741, logo.getSize());
		Assert.assertNotNull(logo.getInputStream());
	}

}
