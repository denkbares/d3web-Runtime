/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.d3web.interview;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.PSConfig;
import de.d3web.core.inference.PSConfig.PSState;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.interview.inference.PSMethodInterview;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Tests the Persistence of PSConfigs configuring PSMethodInterview
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 25.03.2013
 */
public class TestInterviewPSConfig {

	private File parentFile;

	@Before
	public void setUp() throws IOException {
		InitPluginManager.init();
		parentFile = new File("target/test-kbs");
		parentFile.mkdirs();
	}

	@Test
	public void test() throws IOException {
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		PSMethodInterview psMethodInterview = new PSMethodInterview();
		PSConfig psConfig = new PSConfig(PSState.autodetect, psMethodInterview,
				"PSMethodInterview",
				"d3web-Interview", 3);
		File kbfile = new File(parentFile, "noStrategy.d3web");
		PSMethodInterview reloadedPSMerodInterview = getReloadedSessionPSMethodInterview(kb, kbfile);
		Assert.assertNull(reloadedPSMerodInterview.getDefaultFormStrategy());
		// add psconfig without a default strategy
		kb.addPSConfig(psConfig);
		reloadedPSMerodInterview = getReloadedSessionPSMethodInterview(kb, kbfile);
		Assert.assertNull(reloadedPSMerodInterview.getDefaultFormStrategy());
		// adding NextUnansweredQuestionFormStrategy as a default strategy to
		// the configured PSMethod
		psMethodInterview.setDefaultFormStrategy(new NextUnansweredQuestionFormStrategy());
		reloadedPSMerodInterview = getReloadedSessionPSMethodInterview(kb, kbfile);
		Assert.assertNotNull(reloadedPSMerodInterview.getDefaultFormStrategy());
		Assert.assertTrue(reloadedPSMerodInterview.getDefaultFormStrategy() instanceof NextUnansweredQuestionFormStrategy);
		// adding CurrentQContainerFormStrategy as a default strategy to the
		// configured PSMethod
		psMethodInterview.setDefaultFormStrategy(new CurrentQContainerFormStrategy());
		reloadedPSMerodInterview = getReloadedSessionPSMethodInterview(kb, kbfile);
		Assert.assertNotNull(reloadedPSMerodInterview.getDefaultFormStrategy());
		Assert.assertTrue(reloadedPSMerodInterview.getDefaultFormStrategy() instanceof CurrentQContainerFormStrategy);
	}

	private PSMethodInterview getReloadedSessionPSMethodInterview(KnowledgeBase kb, File kbfile) throws IOException {
		PersistenceManager.getInstance().save(kb, kbfile);
		KnowledgeBase reloadedKB = PersistenceManager.getInstance().load(kbfile);
		Session session = SessionFactory.createSession(reloadedKB);
		PSMethodInterview psm = session.getPSMethodInstance(PSMethodInterview.class);
		return psm;
	}

}
