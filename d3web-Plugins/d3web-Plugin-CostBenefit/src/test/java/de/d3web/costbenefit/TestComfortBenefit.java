/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.costbenefit;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.inference.condition.CondNumLess;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.values.NumValue;
import de.d3web.costbenefit.inference.ComfortBenefit;
import de.d3web.costbenefit.inference.ConditionalValueSetter;
import de.d3web.costbenefit.inference.PathExtender;
import de.d3web.costbenefit.inference.SearchAlgorithm;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.costbenefit.inference.ValueTransition;
import de.d3web.costbenefit.model.Path;
import de.d3web.costbenefit.model.SearchModel;
import de.d3web.costbenefit.model.Target;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Tests {@link ComfortBenefit}
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 02.02.2012
 */
public class TestComfortBenefit {

	private KnowledgeBase kb;
	private QContainer qContainerWithComfortBenefit;
	private PathExtender pathExtender;
	private Session session;
	private SearchModel model;
	private QContainer start;
	private QContainer end;
	private QuestionNum questionNum;
	private TestPath startPath;
	private Target target;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		start = new QContainer(kb, "start");
		questionNum = new QuestionNum(start, "num");
		CondAnd condTrue = new CondAnd(
				Collections.<Condition> emptyList());
		List<ConditionalValueSetter> cvslist = Arrays.asList(new ConditionalValueSetter(
				new NumValue(15.0), condTrue));
		new StateTransition(null, Arrays.asList(new ValueTransition(questionNum, cvslist)), start);
		qContainerWithComfortBenefit = new QContainer(kb, "comfortBenefit");
		end = new QContainer(kb, "end");
		new StateTransition(condTrue, Collections.<ValueTransition> emptyList(), end);
		List<QContainer> pathlist = new LinkedList<>();
		pathlist.add(start);
		pathlist.add(end);
		startPath = new TestPath(pathlist);
		target = new Target(end);
		session = SessionFactory.createSession(kb);
		model = new SearchModel(session);
		model.addTarget(target);
		target.setMinPath(startPath);
		model.checkTarget(target);
		pathExtender = new PathExtender(new TestAlgorithm());
	}

	@Test
	public void fittingConditions() {
		new ComfortBenefit(qContainerWithComfortBenefit);
		new StateTransition(new CondNumGreater(questionNum, 10.0),
				Collections.<ValueTransition> emptyList(),
				qContainerWithComfortBenefit);
		searchAndCheckPath(start, qContainerWithComfortBenefit, end);
	}

	@Test
	public void fittingComfortCondition() {
		new ComfortBenefit(qContainerWithComfortBenefit);
		new StateTransition(null,
				Collections.<ValueTransition> emptyList(),
				qContainerWithComfortBenefit);
		searchAndCheckPath(qContainerWithComfortBenefit, start, end);
	}

	@Test
	public void notfittingComfortBenefitCondition() {
		new ComfortBenefit(qContainerWithComfortBenefit, new CondNumLess(questionNum, 10.0));
		new StateTransition(null,
				Collections.<ValueTransition> emptyList(),
				qContainerWithComfortBenefit);
		searchAndCheckPath(start, end);
	}

	@Test
	public void notFittingStateTransitionCondition() {
		new ComfortBenefit(qContainerWithComfortBenefit);
		new StateTransition(new CondNumLess(questionNum, 10.0),
				Collections.<ValueTransition> emptyList(),
				qContainerWithComfortBenefit);
		searchAndCheckPath(start, end);
	}

	@Test
	public void noComfortBenefit() {
		new StateTransition(null,
				Collections.<ValueTransition> emptyList(),
				qContainerWithComfortBenefit);
		searchAndCheckPath(start, end);
	}

	@Test
	public void destroyingPrecontion() {
		QuestionNum questionNum2 = new QuestionNum(qContainerWithComfortBenefit, "num2");
		CondAnd condTrue = new CondAnd(
				Collections.<Condition> emptyList());
		List<ConditionalValueSetter> cvslist = Arrays.asList(new ConditionalValueSetter(
				new NumValue(15.0), condTrue));
		new StateTransition(new CondNumGreater(questionNum, 10.0),
				Arrays.asList(new ValueTransition(
						questionNum2, cvslist)), qContainerWithComfortBenefit);
		new ComfortBenefit(qContainerWithComfortBenefit);
		searchAndCheckPath(start, qContainerWithComfortBenefit, end);
		// overwritting statetransition of end
		new StateTransition(new CondNumLess(questionNum2, 10.0),
				Collections.<ValueTransition> emptyList(), end);
		// reset startPath
		target.setMinPath(startPath);
		searchAndCheckPath(start, end);
	}

	@Test
	public void testPersistence() throws Exception {
		new ComfortBenefit(qContainerWithComfortBenefit);
		File folder = new File("target/kb");
		folder.mkdirs();
		File file = new File(folder, "TestComfortBenefit.d3web");
		PersistenceManager.getInstance().save(kb, file);
		KnowledgeBase loadedKB = PersistenceManager.getInstance().load(file);
		Collection<ComfortBenefit> loadedComfortBenefits = loadedKB.getAllKnowledgeSlicesFor(ComfortBenefit.KNOWLEDGE_KIND);
		Assert.assertEquals(1, loadedComfortBenefits.size());
		ComfortBenefit comfortBenfit = loadedComfortBenefits.iterator().next();
		Assert.assertEquals(qContainerWithComfortBenefit.getName(),
				comfortBenfit.getQContainer().getName());
		Assert.assertEquals(new CondAnd(Collections.<Condition> emptyList()),
				comfortBenfit.getCondition());
	}

	private void searchAndCheckPath(QContainer... containers) {
		pathExtender.search(session, model);
		Path path = model.getBestCostBenefitTarget().getMinPath();
		List<QContainer> pathList = path.getPath();
		Assert.assertEquals(containers.length, pathList.size());
		for (int i = 0; i < containers.length; i++) {
			Assert.assertEquals(containers[i], pathList.get(i));
		}
	}

	private static class TestPath implements Path {

		private final List<QContainer> qcons;

		public TestPath(List<QContainer> qcons) {
			super();
			this.qcons = qcons;
		}

		@Override
		public List<QContainer> getPath() {
			return qcons;
		}

		@Override
		public double getCosts() {
			return 0;
		}

		@Override
		public double getNegativeCosts() {
			return 0;
		}

		@Override
		public boolean contains(QContainer qContainer) {
			return qcons.contains(qContainer);
		}

		@Override
		public boolean containsAll(Collection<QContainer> qContainers) {
			return qcons.containsAll(qContainers);
		}

		@Override
		public boolean contains(Collection<QContainer> qContainers) {
			for (QContainer qContainer : qContainers) {
				if (contains(qContainer)) {
					return true;
				}
			}
			return false;
		}

	}

	private static class TestAlgorithm implements SearchAlgorithm {

		@Override
		public void search(Session session, SearchModel model) {
			// nothing to do, best cost benefit startPath is set in setUp
		}

	}

}
