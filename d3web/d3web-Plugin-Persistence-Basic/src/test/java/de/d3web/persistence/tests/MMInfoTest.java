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
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.plugin.test.InitPluginManager;

/**
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 14.02.2011
 */
public class MMInfoTest {

	@Test
	public void test() throws IOException {
		InitPluginManager.init();
		KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance();
		KnowledgeBase kb = kbm.getKnowledgeBase();
		String[] answers = new String[1];
		answers[0] = "Answer 1";
		QuestionOC oc = kbm.createQuestionOC("Question", null, answers);
		Choice choice = oc.getAlternative(0);
		oc.getInfoStore().addValue(MMInfo.DESCRIPTION, Locale.GERMAN, "Frage");
		choice.getInfoStore().addValue(MMInfo.DESCRIPTION, Locale.GERMAN, "Antwort 1");
		File file = new File("target/test/MMinfoTest.d3web");
		file.mkdirs();
		PersistenceManager pm = PersistenceManager.getInstance();
		pm.save(kb, file);
		KnowledgeBase reloadedKB = pm.load(file);
		kbm = KnowledgeBaseManagement.createInstance(reloadedKB);
		oc = (QuestionOC) kbm.findQuestion("Question");
		Assert.assertEquals("Frage", oc.getInfoStore().getValue(MMInfo.DESCRIPTION, Locale.GERMAN));
		Assert.assertEquals("Antwort 1",
				oc.getAlternative(0).getInfoStore().getValue(MMInfo.DESCRIPTION, Locale.GERMAN));
	}
}
