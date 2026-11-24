/*
 * Copyright (C) 2026 denkbares GmbH. All rights reserved.
 */

package de.d3web.core.inference.condition.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.denkbares.plugin.test.InitPluginManager;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondKnown;
import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.ConditionCache.ConditionResult;
import de.d3web.core.inference.condition.DefaultConditionCache;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.Unknown;

import static org.junit.Assert.assertEquals;

/**
 * Checks that {@link Condition#evalToResult(Session)} (exception-free evaluation) returns the same result as the
 * reference evaluation via {@link Condition#eval(Session)} for all combinations of answered, unanswered and unknown
 * questions, including nested conditions, multiple choice questions, conditions without an optimized
 * implementation and subclasses that override eval.
 *
 * @author Albrecht Striffler (denkbares GmbH) + Claude
 * @created 24.11.2026
 */
public class ConditionEvalToResultTest {

	private KnowledgeBase kb;
	private final List<Question> questions = new ArrayList<>();
	private final List<List<Value>> states = new ArrayList<>();
	private final List<Condition> conditions = new ArrayList<>();

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		QContainer init = new QContainer(kb.getRootQASet(), "init");
		List<Condition> leaves = new ArrayList<>();
		for (int i = 1; i <= 3; i++) {
			QuestionOC q = new QuestionOC(init, "q" + i, "yes", "no");
			questions.add(q);
			ChoiceValue yes = new ChoiceValue(q.getAllAlternatives().get(0));
			ChoiceValue no = new ChoiceValue(q.getAllAlternatives().get(1));
			states.add(List.of(yes, no, Unknown.getInstance()));
			leaves.add(new CondEqual(q, yes));
		}
		QuestionMC mc = new QuestionMC(init, "mc", "a", "b", "c");
		questions.add(mc);
		ChoiceID a = new ChoiceID(mc.getAllAlternatives().get(0));
		ChoiceID b = new ChoiceID(mc.getAllAlternatives().get(1));
		ChoiceID c = new ChoiceID(mc.getAllAlternatives().get(2));
		states.add(List.of(new MultipleChoiceValue(a), new MultipleChoiceValue(a, b), new MultipleChoiceValue(c),
				Unknown.getInstance()));
		leaves.add(new CondEqual(mc, new ChoiceValue(mc.getAllAlternatives().get(0))));
		leaves.add(new CondEqual(mc, new MultipleChoiceValue(a, b)));

		Condition q1 = leaves.get(0), q2 = leaves.get(1), q3 = leaves.get(2), mcA = leaves.get(3), mcAB = leaves.get(4);
		conditions.addAll(leaves);
		conditions.add(new CondNot(q1));
		conditions.add(new CondNot(new CondNot(mcA)));
		conditions.add(new CondAnd(q1, q2));
		conditions.add(new CondOr(q1, q2));
		conditions.add(new CondAnd(q1, q2, q3, mcA));
		conditions.add(new CondOr(q1, q2, q3, mcAB));
		conditions.add(new CondAnd(new CondOr(q1, q2), new CondNot(q3)));
		conditions.add(new CondOr(new CondAnd(q1, mcA), q2, new CondNot(new CondOr(q3, mcAB))));
		conditions.add(new CondAnd());
		conditions.add(new CondOr());
		// a condition type without an optimized evalToResult, nested in optimized ones
		conditions.add(new CondOr(new CondKnown(questions.get(0)), new CondAnd(q2, new CondKnown(mc))));
		// subclasses overriding eval must keep their own semantics
		conditions.add(new CondOr(q1, q2) {
			@Override
			public boolean eval(Session session) {
				return true;
			}
		});
		conditions.add(new CondAnd(new CondNot(q1) {
			@Override
			public boolean eval(Session session) {
				return false;
			}
		}, q2));
	}

	@Test
	public void sameResultAsEval() {
		int sessions = 0;
		int[] counts = new int[ConditionResult.values().length];
		// every question: undefined (-1) or one of its states
		int[] index = new int[questions.size()];
		java.util.Arrays.fill(index, -1);
		while (true) {
			Session session = SessionFactory.createSession(kb);
			for (int i = 0; i < questions.size(); i++) {
				if (index[i] >= 0) {
					session.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(
							questions.get(i), states.get(i).get(index[i])));
				}
			}
			DefaultConditionCache cache = new DefaultConditionCache(session);
			for (Condition condition : conditions) {
				ConditionResult expected = Condition.evalToResult(condition, session);
				String message = condition + " with " + java.util.Arrays.toString(index);
				assertEquals(message, expected, condition.evalToResult(session));
				assertEquals(message, expected, cache.getResult(condition));
				counts[expected.ordinal()]++;
			}
			sessions++;
			// next combination
			int i = 0;
			while (i < index.length && ++index[i] >= states.get(i).size()) {
				index[i] = -1;
				i++;
			}
			if (i == index.length) break;
		}
		assertEquals(4 * 4 * 4 * 5, sessions);
		// make sure that all kinds of results have been compared
		for (ConditionResult result : ConditionResult.values()) {
			assertEquals("no result " + result, true, counts[result.ordinal()] > 0);
		}
	}
}
