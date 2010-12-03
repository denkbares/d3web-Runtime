/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.d3web.diaFlux.test;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.plugin.test.InitPluginManager;

/**
 * 
 * @author Reinhard Hatko
 * @created 03.12.2010
 */
public abstract class AbstractDiaFluxTest {

	private static final String PATH = "src/test/resources/";
	protected final String fileName;
	protected KnowledgeBaseManagement kbm;
	protected Session session;

	AbstractDiaFluxTest(String fileName) {
		this.fileName = fileName;
	}

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();

		File file = new File(PATH + fileName);

		KnowledgeBase kb = PersistenceManager.getInstance().load(file);

		kbm = KnowledgeBaseManagement.createInstance(kb);

		session = SessionFactory.createSession(kbm.getKnowledgeBase());

	}

	protected void assertActiveNodes(String flowName, String[] activeIDs, boolean testInactive) {

		FlowSet flowSet = DiaFluxUtils.getFlowSet(session);

		Flow flow = flowSet.getByName(flowName);

		nextNode: for (INode node : flow.getNodes()) {
			boolean supported = DiaFluxUtils.getNodeData(node, session).isSupported();

			for (String id : activeIDs) {
				if (node.getID().equalsIgnoreCase(id)) {

					Assert.assertTrue("Node '" + id + "' must be active.", supported);
					continue nextNode;
				}
			}

			// current node is no supposed to be active
			if (testInactive) {
				if (supported) {
					Assert.assertFalse("Node '" + node.getID() + "' must be inactive.", supported);

				}
			}

		}

	}

	protected void answerOCQuestion(String questionName, String answerName) {
		System.out.println("Setting value for '" + questionName + "' to '" + answerName + "'.");

		QuestionChoice question = (QuestionChoice) kbm.findQuestion(questionName);

		Choice choice = kbm.findChoice(question, answerName);

		Fact fact = FactFactory.createUserEnteredFact(question, new ChoiceValue(choice));

		session.getBlackboard().addValueFact(fact);

	}

}