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
package de.d3web.costbenefit.inference;

import java.util.Collection;

import de.d3web.core.inference.StrategicSupport;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.costbenefit.model.Target;

/**
 * A TargetFunction calculates the targets for a SearchAlgorithm based on
 * relevant questions and diagnosis.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public interface TargetFunction {

	/**
	 * Returns a collection of targets. These targets contain relevantQuestions
	 * to discriminate the diagnosis.
	 * 
	 * @param session
	 * @param relevantQuestions
	 * @param diagnosisToDiscriminate
	 * @return
	 */
	Collection<Target> getTargets(Session session,
			Collection<Question> relevantQuestions,
			Collection<Solution> diagnosisToDiscriminate, StrategicSupport strategicSupport);
}
