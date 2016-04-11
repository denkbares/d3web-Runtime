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
package de.d3web.core.inference.tests;

import java.io.IOException;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

import de.d3web.abstraction.formula.Operator;
import de.d3web.abstraction.formula.QNumWrapper;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.plugin.test.InitPluginManager;

/**
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 01.10.2010
 */
public class RuleUpdateTest {

	@Test
	public void testWithoutCondition() throws IOException {
		InitPluginManager.init();
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		QuestionNum bmi = new QuestionNum(kb, "bmi");
		QuestionNum weight = new QuestionNum(kb, "weight");
		QuestionNum height = new QuestionNum(kb, "height");

		QNumWrapper weightWrapper = new QNumWrapper(weight);
		QNumWrapper heightWrapper = new QNumWrapper(height);
		Operator quad = new Operator(heightWrapper, heightWrapper, Operator.Operation.Mult);
		Operator div = new Operator(weightWrapper, quad, Operator.Operation.Div);
		RuleFactory.createSetValueRule(bmi, div, new CondAnd(new LinkedList<Condition>()));

		Session session = SessionFactory.createSession(kb);
		Blackboard blackboard = session.getBlackboard();
		blackboard.addValueFact(FactFactory.createUserEnteredFact(weight, new NumValue(75)));
		blackboard.addValueFact(FactFactory.createUserEnteredFact(height, new NumValue(1.87)));

		Value firstValue = blackboard.getValue(bmi);
		Assert.assertTrue(UndefinedValue.isNotUndefinedValue(firstValue));

		blackboard.addValueFact(FactFactory.createUserEnteredFact(weight, new NumValue(80)));
		Value secondValue = blackboard.getValue(bmi);
		Assert.assertFalse(firstValue.getValue().equals(secondValue.getValue()));
	}

}
