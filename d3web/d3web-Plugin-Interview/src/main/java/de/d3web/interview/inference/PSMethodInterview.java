/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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
package de.d3web.interview.inference;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

import de.d3web.core.inference.PSMethodAdapter;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.interview.DefaultInterview;
import de.d3web.interview.Interview;
import de.d3web.interview.NextUnansweredQuestionFormStrategy;

/**
 * This PSMethod is used to notify the {@link Interview} of new facts added to
 * the {@link Session}, i.e., new (contra-)indications and values added to the
 * {@link Blackboard}.
 * 
 * @author joba
 * 
 */
public class PSMethodInterview extends PSMethodAdapter implements SessionObjectSource<Interview> {

	private static PSMethodInterview instance;

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {
		Interview interview = session.getSessionObject(this);
		for (PropagationEntry change : changes) {
			interview.notifyFactChange(change);
		}
		// force sorting
		if (!changes.isEmpty()) {
			/*
			 * as a workaround we access the fully sorted interview agenda to
			 * force the sorting strategy being activated this is required,
			 * because some sorting strategies (like CostBenefitSortingStrategy
			 * are also changing the objects contained (not only sorting) and
			 * therefore have other side effects.
			 */
			Collection<InterviewObject> objects = interview.getInterviewAgenda().getCurrentlyActiveObjects();
			Logger.getLogger(getClass().getName()).fine(
					"Agenda (" + changes.size() + " changes): " + objects);
		}
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {

		return facts[0];
	}

	public static PSMethodInterview getInstance() {
		if (instance == null) {
			instance = new PSMethodInterview();
		}
		return instance;
	}

	@Override
	public boolean hasType(Type type) {
		return type == Type.consumer;
	}

	@Override
	public Set<TerminologyObject> getPotentialDerivationSources(TerminologyObject derivedObject) {
		return Collections.emptySet();
	}

	@Override
	public Set<TerminologyObject> getActiveDerivationSources(TerminologyObject derivedObject, Session session) {
		return Collections.emptySet();
	}

	/**
	 * returns the default priority
	 */
	@Override
	public double getPriority() {
		return 3;
	}

	@Override
	public Interview createSessionObject(Session session) {
		DefaultInterview interview = new DefaultInterview(session);
		// TODO: read PSConfig to get strategy
		interview.setFormStrategy(new NextUnansweredQuestionFormStrategy());
		return interview;
	}
}
