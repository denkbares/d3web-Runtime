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

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.w3c.dom.Element;

import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
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

	@Test
	public void toCheck() throws IOException, TransformationException {
		InitPluginManager.init();
		KnowledgeBase kb = PersistenceManager.getInstance().load(new File("/Users/Albrecht/Downloads/s428.d3web"));
		String conditionXml = "<Condition type=\"or\">\n" +
				"<Condition name=\"infection\" type=\"numEqual\" value=\"-1.0\"/>\n" +
				"<Condition name=\"infection_update\" type=\"equal\" value=\"No\"/>\n" +
				"<Condition type=\"and\">\n" +
				"<Condition name=\"infection_update\" type=\"equal\" value=\"Yes\"/>\n" +
				"<Condition type=\"or\">\n" +
				"<Condition name=\"infection\" type=\"numEqual\" value=\"1.0\"/>\n" +
				"<Condition type=\"TimeDBCondition\">(now - latestChange(filter(infection[], '=', 0))) / 1h  &lt;= negative_infection_time_range_max</Condition>\n" +
				"</Condition>\n" +
				"</Condition>\n" +
				"</Condition>";
		ConditionPersistenceCheckTemplate conditionCheckTemplate = new ConditionPersistenceCheckTemplate(conditionXml);

		ConditionCheck conditionCheck = (ConditionCheck) conditionCheckTemplate.toCheck(kb);
		Condition condition = conditionCheck.getConditionObject();
		assertTrue("Wrong type of Condition, expected " + CondOr.class.getSimpleName(), condition instanceof CondOr);

		ConditionPersistenceCheckHandler handler = new ConditionPersistenceCheckHandler();

		Element conditionElement = handler.write(conditionCheckTemplate, new TestCasePersistence());

		assertEquals("<Check type=\"Condition\">" + conditionXml + "</Check>", XMLUtil.getElementAsString(conditionElement));

	}


}
