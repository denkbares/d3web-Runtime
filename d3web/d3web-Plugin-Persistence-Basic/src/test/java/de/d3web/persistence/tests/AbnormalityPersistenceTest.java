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

import junit.framework.Assert;

import org.junit.Test;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.abnormality.Abnormality;
import de.d3web.core.knowledge.terminology.info.abnormality.AbnormalityNum;
import de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Tests reading and writing of AbnormalityHandlers
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 02.02.2011
 */
public class AbnormalityPersistenceTest {

	@Test
	public void testWritingAndReading() throws IOException {
		InitPluginManager.init();
		KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance();
		KnowledgeBase kb = kbm.getKnowledgeBase();
		String[] answers = new String[2];
		answers[0] = "Antwort1";
		answers[1] = "Antwort2";
		QuestionOC oc = kbm.createQuestionOC("OC", null, answers);
		Choice choice = oc.getAlternatives().get(0);
		QuestionNum num = kbm.createQuestionNum("Num", null);
		DefaultAbnormality a = new DefaultAbnormality();
		a.addValue(new ChoiceValue(choice), Abnormality.A0);
		oc.getInfoStore().addValue(BasicProperties.DEFAULT_ABNORMALITIY, a);
		AbnormalityNum aNum = new AbnormalityNum();
		aNum.addValue(5, 10, Abnormality.A0, true, false);
		num.getInfoStore().addValue(BasicProperties.ABNORMALITIY_NUM, aNum);
		File file = new File("target/test/AbnormalityTest.d3web");
		file.mkdirs();
		PersistenceManager pm = PersistenceManager.getInstance();
		pm.save(kb, file);
		KnowledgeBase reloadedKB = pm.load(file);
		oc = (QuestionOC) reloadedKB.getManager().searchQuestion("OC");
		num = (QuestionNum) reloadedKB.getManager().searchQuestion("Num");
		choice = oc.getAlternatives().get(0);
		a = oc.getInfoStore().getValue(BasicProperties.DEFAULT_ABNORMALITIY);
		Assert.assertEquals(Abnormality.A0, a.getValue(new ChoiceValue(choice)));
		Choice choice2 = oc.getAlternatives().get(1);
		// A5 is default
		Assert.assertEquals(Abnormality.A5, a.getValue(new ChoiceValue(choice2)));

		aNum = num.getInfoStore().getValue(BasicProperties.ABNORMALITIY_NUM);
		Assert.assertEquals(Abnormality.A0, aNum.getValue(new NumValue(6)));
		Assert.assertEquals(Abnormality.A5, aNum.getValue(new NumValue(1)));
		// left open
		Assert.assertEquals(Abnormality.A5, aNum.getValue(new NumValue(5)));
		// right closed
		Assert.assertEquals(Abnormality.A0, aNum.getValue(new NumValue(10)));
	}
}
