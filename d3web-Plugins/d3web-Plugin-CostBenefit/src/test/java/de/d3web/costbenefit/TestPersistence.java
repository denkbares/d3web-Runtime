/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.costbenefit;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;

import org.junit.Test;

import de.d3web.core.inference.PSConfig;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.costbenefit.inference.PSMethodCostBenefit;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Tests persistence of CostBenefit Configurations
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 08.05.2012
 */
public class TestPersistence {

	@Test
	public void testStrategicBenefit() throws IOException {
		InitPluginManager.init();
		PSMethodCostBenefit cb = new PSMethodCostBenefit();
		Assert.assertEquals(0.0, cb.getStrategicBenefitFactor(), 0);
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		kb.addPSConfig(new PSConfig(PSConfig.PSState.active, cb, "PSMethodCostBenefit",
				"d3web-CostBenefit", 6));
		File folder = new File("target/kb");
		folder.mkdirs();
		File file = new File(folder, "TestStrategicBenefit.d3web");
		PersistenceManager.getInstance().save(kb, file);
		KnowledgeBase kb2 = PersistenceManager.getInstance().load(file);
		PSMethodCostBenefit cb2 = getPSM(kb2);
		Assert.assertEquals(0.0, cb2.getStrategicBenefitFactor(), 0);
		cb.setStrategicBenefitFactor(5.4);
		PersistenceManager.getInstance().save(kb, file);
		KnowledgeBase kb3 = PersistenceManager.getInstance().load(file);
		PSMethodCostBenefit cb3 = getPSM(kb3);
		Assert.assertEquals(5.4, cb3.getStrategicBenefitFactor(), 0);
	}

	@Test
	public void testManualMode() throws IOException {
		InitPluginManager.init();
		PSMethodCostBenefit cb = new PSMethodCostBenefit();
		Assert.assertFalse(cb.isManualMode());
		Assert.assertEquals(0.0, cb.getStrategicBenefitFactor(), 0);
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		kb.addPSConfig(new PSConfig(PSConfig.PSState.active, cb, "PSMethodCostBenefit",
				"d3web-CostBenefit", 6));
		File folder = new File("target/kb");
		folder.mkdirs();
		File file = new File(folder, "TestManualMode.d3web");
		PersistenceManager.getInstance().save(kb, file);
		KnowledgeBase kb2 = PersistenceManager.getInstance().load(file);
		PSMethodCostBenefit cb2 = getPSM(kb2);
		Assert.assertFalse(cb2.isManualMode());
		cb.setManualMode(true);
		PersistenceManager.getInstance().save(kb, file);
		KnowledgeBase kb3 = PersistenceManager.getInstance().load(file);
		PSMethodCostBenefit cb3 = getPSM(kb3);
		Assert.assertTrue(cb3.isManualMode());
	}

	private static PSMethodCostBenefit getPSM(KnowledgeBase kb) {
		for (PSConfig config : kb.getPsConfigs()) {
			if (config.getPsMethod() instanceof PSMethodCostBenefit) {
				PSMethodCostBenefit psm = (PSMethodCostBenefit) config.getPsMethod();
				return psm;
			}
		}
		return null;
	}
}
