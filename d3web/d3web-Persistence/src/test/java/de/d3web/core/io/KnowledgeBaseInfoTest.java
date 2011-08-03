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
package de.d3web.core.io;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import de.d3web.core.io.PersistenceManager.KnowledgeBaseInfo;
import de.d3web.plugin.test.InitPluginManager;

/**
 * A simple test for KnowledgeBaseInfo
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 02.08.2011
 */
public class KnowledgeBaseInfoTest {

	@Test
	public void test() throws Exception {
		InitPluginManager.init();
		KnowledgeBaseInfo kbi = PersistenceManager.getInstance().loadKnowledgeBaseInfo(
				new File("src/test/resources/KBInfoTest.zip"));
		Assert.assertEquals("Markus", kbi.getAuthor());
		Assert.assertEquals("KBInfoTest", kbi.getName());
		Assert.assertNotNull(kbi.getFavIcon());
		Date created = kbi.getDate();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(created);
		Assert.assertEquals(2011, calendar.get(Calendar.YEAR));
		Assert.assertEquals(Calendar.AUGUST, calendar.get(Calendar.MONTH));
		Assert.assertEquals(2, calendar.get(Calendar.DAY_OF_MONTH));
	}
}
