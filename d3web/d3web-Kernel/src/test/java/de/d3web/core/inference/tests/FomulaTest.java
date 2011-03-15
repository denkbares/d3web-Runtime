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
package de.d3web.core.inference.tests;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import de.d3web.abstraction.formula.FormulaNumber;
import de.d3web.abstraction.formula.Operator;
import de.d3web.abstraction.formula.QNumWrapper;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.plugin.test.InitPluginManager;

/**
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 15.03.2011
 */
public class FomulaTest {

	@Test
	public void test() throws IOException {
		InitPluginManager.init();
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		QuestionNum question = new QuestionNum(kb, "Question");
		QNumWrapper wrappedQuestion = new QNumWrapper(question);
		FormulaNumber three = new FormulaNumber(3.0);
		Operator add = new Operator(wrappedQuestion, three, Operator.Operation.Add);
		Operator sub = new Operator(wrappedQuestion, three, Operator.Operation.Sub);
		Operator div = new Operator(wrappedQuestion, three, Operator.Operation.Div);
		Operator max = new Operator(wrappedQuestion, three, Operator.Operation.Max);
		Operator min = new Operator(wrappedQuestion, three, Operator.Operation.Min);
		Operator mult = new Operator(wrappedQuestion, three, Operator.Operation.Mult);
		Session session = SessionFactory.createSession(kb);
		Assert.assertTrue(UndefinedValue.isUndefinedValue(add.eval(session)));
		session.getBlackboard().addValueFact(
				FactFactory.createUserEnteredFact(question, new NumValue(9)));
		Assert.assertEquals(12.0, ((NumValue) add.eval(session)).getDouble());
		Assert.assertEquals(6.0, ((NumValue) sub.eval(session)).getDouble());
		Assert.assertEquals(3.0, ((NumValue) div.eval(session)).getDouble());
		Assert.assertEquals(9.0, ((NumValue) max.eval(session)).getDouble());
		Assert.assertEquals(3.0, ((NumValue) min.eval(session)).getDouble());
		Assert.assertEquals(27.0, ((NumValue) mult.eval(session)).getDouble());
	}
}
