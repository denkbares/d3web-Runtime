/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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

package de.d3web.testcase.persistence.test;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Element;

import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.testcase.model.ConditionCheck;
import de.d3web.testcase.model.TransformationException;
import de.d3web.testcase.persistence.ConditionPersistenceCheckHandler;
import de.d3web.testcase.persistence.ConditionPersistenceCheckTemplate;
import de.d3web.testcase.persistence.TestCasePersistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Testing {@link ConditionPersistenceCheckTemplate}
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 30.10.15
 */
public class ConditionPersistenceCheckTemplateTest {

	private static KnowledgeBase knowledgeBase;

	@BeforeClass
	public static void init() throws IOException {
		InitPluginManager.init();
		knowledgeBase = new KnowledgeBase();
		knowledgeBase.getManager().putTerminologyObject(new Solution(knowledgeBase, "solution"));
		knowledgeBase.getManager().putTerminologyObject(new QuestionYN(knowledgeBase, "questionYN"));
	}

	@Test
	public void toCheck() throws IOException, TransformationException {

		String conditionXml = "<Condition type=\"and\">\n" +
				"<Condition name=\"solution\" type=\"DState\" value=\"EXCLUDED\"/>\n" +
				"<Condition name=\"questionYN\" type=\"equal\" value=\"Yes\"/>\n" +
				"</Condition>";
		ConditionPersistenceCheckTemplate conditionCheckTemplate = new ConditionPersistenceCheckTemplate(conditionXml);

		ConditionCheck conditionCheck = (ConditionCheck) conditionCheckTemplate.toCheck(knowledgeBase);
		Condition condition = conditionCheck.getConditionObject();
		assertTrue("Wrong type of Condition, expected " + CondAnd.class.getSimpleName(), condition instanceof CondAnd);

		ConditionPersistenceCheckHandler handler = new ConditionPersistenceCheckHandler();
		Element conditionElement = handler.write(conditionCheckTemplate, new TestCasePersistence());

		assertEquals("<Check type=\"Condition\">" + conditionXml + "</Check>", XMLUtil.getElementAsString(conditionElement));

	}


}
