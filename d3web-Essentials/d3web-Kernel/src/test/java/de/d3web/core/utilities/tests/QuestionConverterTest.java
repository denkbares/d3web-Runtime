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
package de.d3web.core.utilities.tests;

import java.io.IOException;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.utilities.QuestionConverter;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Tests {@link QuestionConverter}
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 15.05.2013
 */
public class QuestionConverterTest {

	@Before
	public void setUp() throws IOException {
		InitPluginManager.init();
	}

	@Test
	public void testOCtoMCConversion() {
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		String rootQuestionName = "root Question";
		QuestionOC rootQuestion = new QuestionOC(kb, rootQuestionName);
		QuestionOC followUpQuestion = new QuestionOC(rootQuestion, "follow up Question");
		QContainer qcon = new QContainer(kb, "Container");
		String qContainerQuestionName = "QContainer Question";
		QuestionOC qContainerQuestion = new QuestionOC(qcon, qContainerQuestionName);
		Choice choice = new Choice("choice a");
		qContainerQuestion.getInfoStore().addValue(BasicProperties.INIT, choice.getName());
		qContainerQuestion.addAlternative(choice);
		String germanChoicePrompt = "Alternative a";
		choice.getInfoStore().addValue(MMInfo.PROMPT, Locale.GERMAN, germanChoicePrompt);

		QuestionMC mcRootQuestion = QuestionConverter.convertOCtoMC(rootQuestion);
		QuestionMC mcQContainerQuestion = QuestionConverter.convertOCtoMC(qContainerQuestion);

		Question rootQuestionAfterConversion = kb.getManager().searchQuestion(rootQuestionName);
		Assert.assertTrue(rootQuestionAfterConversion instanceof QuestionMC);
		Assert.assertEquals(mcRootQuestion, rootQuestionAfterConversion);
		Assert.assertEquals(1, rootQuestionAfterConversion.getChildren().length);
		Assert.assertEquals(followUpQuestion, rootQuestionAfterConversion.getChildren()[0]);
		Assert.assertEquals(0, mcRootQuestion.getParents().length);

		Question qContainerQuestionAfterConversion = kb.getManager().searchQuestion(
				qContainerQuestionName);
		Assert.assertTrue(qContainerQuestionAfterConversion instanceof QuestionMC);
		Assert.assertEquals(mcQContainerQuestion, qContainerQuestionAfterConversion);
		Assert.assertEquals(0, mcQContainerQuestion.getChildren().length);
		Assert.assertEquals(1, mcQContainerQuestion.getParents().length);
		Assert.assertEquals(qcon, mcQContainerQuestion.getParents()[0]);
		Assert.assertEquals(1, mcQContainerQuestion.getAllAlternatives().size());
		Choice convertedChoice = mcQContainerQuestion.getAllAlternatives().get(0);
		Assert.assertEquals(choice.getName(), convertedChoice.getName());
		Assert.assertEquals(qContainerQuestion, choice.getQuestion());
		Assert.assertEquals(mcQContainerQuestion, convertedChoice.getQuestion());
		Assert.assertNotSame(choice, convertedChoice);
		Assert.assertEquals(convertedChoice.getName(),
				mcQContainerQuestion.getInfoStore().getValue(BasicProperties.INIT));
		Assert.assertEquals(germanChoicePrompt,
				convertedChoice.getInfoStore().getValue(MMInfo.PROMPT, Locale.GERMAN));
	}
}
