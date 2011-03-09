/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.costbenefit.inference;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.d3web.core.inference.StrategicSupport;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.costbenefit.model.Target;

/**
 * The DefaultTargetFunction creates one target of each QContainer, which
 * contains a relevant question. Multitargets are not created.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class DefaultTargetFunction implements TargetFunction {

	@Override
	public Collection<Target> getTargets(Session session,
			Collection<Question> relevantQuestions,
			Collection<Solution> diagnosisToDiscriminate, StrategicSupport strategicSupport) {
		Set<Target> set = new HashSet<Target>();
		for (Question question : relevantQuestions) {
			// ignore contra indicated question to be used as targets
			if (isContraIndicated(session, question)) continue;
			// if the question is not already answered,
			// use its containers as possible targets
			if (isUndefined(session, question)) addParentContainers(set, session, question);
		}
		return set;
	}

	private static boolean isUndefined(Session session, Question question) {
		Value value = session.getBlackboard().getValue(question);
		return UndefinedValue.isUndefinedValue(value);
	}

	private static boolean isContraIndicated(Session session, InterviewObject object) {
		Indication indication = session.getBlackboard().getIndication(object);
		return indication.isContraIndicated();
	}

	private static void addParentContainers(Set<Target> targets, Session session, TerminologyObject object) {
		for (TerminologyObject parent : object.getParents()) {
			// if the parent is contra indicated
			// we are not allowed to use it
			if (isContraIndicated(session, (InterviewObject) parent)) continue;
			if (parent instanceof QContainer) {
				// if questionnaire, use it as target
				targets.add(new Target((QContainer) parent));
			}
			else {
				// if not, check its parents
				// (maybe the discriminating question is a follow-up question)
				addParentContainers(targets, session, parent);
			}
		}

	}
}
