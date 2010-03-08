/*
 * Copyright (C) 2009 denkbares GmbH
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.costBenefit.inference;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.d3web.core.inference.StrategicSupport;
import de.d3web.core.session.XPSCase;
import de.d3web.core.terminology.Diagnosis;
import de.d3web.core.terminology.NamedObject;
import de.d3web.core.terminology.QContainer;
import de.d3web.core.terminology.Question;
import de.d3web.costBenefit.model.Target;

/**
 * The DefaultTargetFunction creates one target of each QContainer, which
 * contains a relevant question. Multitargets are not created.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class DefaultTargetFunction implements TargetFunction {

	@Override
	public Collection<Target> getTargets(XPSCase theCase,
			Collection<Question> relevantQuestions,
			Collection<Diagnosis> diagnosisToDiscriminate, StrategicSupport strategicSupport) {
		Set<Target> set = new HashSet<Target>();
		for (Question q : relevantQuestions) {
			if (!q.isDone(theCase))
				addParentContainers(set, q);
		}
		return set;
	}

	private static void addParentContainers(Set<Target> targets, NamedObject q) {
		for (NamedObject qaset : q.getParents()) {
			if (qaset instanceof QContainer) {
				targets.add(new Target((QContainer) qaset));
			} else {
				addParentContainers(targets, qaset);
			}
		}

	}
}
