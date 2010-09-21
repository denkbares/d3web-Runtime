/*
 * Copyright (C) 2010 denkbares GmbH
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
package de.d3web.core.records.io;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.records.SessionConversionFactory;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.file.records.io.SingleXMLSessionRepository;
import de.d3web.plugin.test.InitPluginManager;

/**
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 21.09.2010
 */
public class SessionPersistenceTest {

	private SessionRecord sessionRecord;
	private KnowledgeBase kb;
	private QuestionOC questionOC;
	private QuestionMC questionMC;
	private Choice[] choices;
	private Choice[] choices2;

	@Before
	public void setUp() throws IOException {
		InitPluginManager.init();
		KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance();
		kb = kbm.getKnowledgeBase();
		kb.setId("TestKB");
		choices = new Choice[2];
		choices[0] = new Choice("Answer1");
		choices[1] = new Choice("Answer2");
		choices2 = new Choice[2];
		choices2[0] = new Choice("Answer1");
		choices2[1] = new Choice("Answer2");
		questionOC = kbm.createQuestionOC("Question",
				kb.getRootQASet(), choices);
		questionMC = kbm.createQuestionMC("Question2", kb.getRootQASet(), choices);
		Session session = SessionFactory.createSession(kb);
		Blackboard blackboard = session.getBlackboard();
		blackboard.addValueFact(FactFactory.createUserEnteredFact(questionOC, new ChoiceValue(
				choices[0])));
		blackboard.addValueFact(FactFactory.createUserEnteredFact(questionMC,
				MultipleChoiceValue.fromChoices(Arrays.asList(choices2))));
		sessionRecord = SessionConversionFactory.copyToSessionRecord(session);
		new File("target/temp").mkdirs();

	}

	@Test
	public void testSingleXMLPersistence() throws IOException {
		SingleXMLSessionRepository sessionRepository = new SingleXMLSessionRepository();
		sessionRepository.add(sessionRecord);
		File file = new File("target/temp/file.xml");
		sessionRepository.save(file);
		SingleXMLSessionRepository reloadedRepository = new SingleXMLSessionRepository();
		reloadedRepository.load(kb, file);
		Session session = SessionConversionFactory.copyToSession(reloadedRepository.iterator().next());
		Blackboard blackboard = session.getBlackboard();
		ChoiceValue value = (ChoiceValue) blackboard.getValue(questionOC);
		Assert.assertEquals(choices[0], value.getValue());
		MultipleChoiceValue value2 = (MultipleChoiceValue) blackboard.getValue(questionMC);
		Collection<?> values = (Collection<?>) value2.getValue();
		Assert.assertTrue(values.size() == 2 && values.contains(new ChoiceValue(choices2[0]))
				&& values.contains(new ChoiceValue(choices2[1])));
	}
}