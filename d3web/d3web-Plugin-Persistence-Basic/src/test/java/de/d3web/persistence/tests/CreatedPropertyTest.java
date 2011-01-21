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
package de.d3web.persistence.tests;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Tests reading and writing of the created property
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 21.01.2011
 */
public class CreatedPropertyTest {

	@Test
	public void testWritingAndReading() throws IOException {
		InitPluginManager.init();
		KnowledgeBase kb = new KnowledgeBase();
		Date beforeSaving = new Date();
		kb.getInfoStore().addValue(BasicProperties.CREATED, beforeSaving);
		File file = new File("/target/test/CreatedPropertyTest.jar");
		file.mkdirs();
		file.createNewFile();
		PersistenceManager pm = PersistenceManager.getInstance();
		pm.save(kb, file);
		KnowledgeBase reloadedKB = pm.load(file);
		Date afterSaving = reloadedKB.getInfoStore().getValue(BasicProperties.CREATED);
		Assert.assertEquals(beforeSaving, afterSaving);
		Date longtimeago = new Date(0);
		kb.getInfoStore().addValue(BasicProperties.CREATED, longtimeago);
		pm.save(kb, file);
		reloadedKB = pm.load(file);
		afterSaving = reloadedKB.getInfoStore().getValue(BasicProperties.CREATED);
		Assert.assertEquals(longtimeago, afterSaving);
		Assert.assertFalse(afterSaving.equals(beforeSaving));
	}
}
