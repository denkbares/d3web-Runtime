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
package de.d3web.costbenefit.blackboard;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.costbenefit.model.SearchModel;
import de.d3web.costbenefit.model.Target;
import de.d3web.interview.Interview;
import de.d3web.interview.InterviewAgenda;
import de.d3web.interview.InterviewAgenda.AgendaEntry;
import de.d3web.interview.inference.PSMethodInterview;

/**
 * CaseObject for CostBenefit
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class CostBenefitCaseObject implements SessionObject {

	private QContainer[] currentSequence;
	private SearchModel searchModel;
	private List<Fact> indicatedFacts = new LinkedList<Fact>();
	private int currentPathIndex = -1;
	private Set<Solution> undiscriminatedSolutions = new HashSet<Solution>();
	private Set<Target> discriminatingTargets = new HashSet<Target>();
	private final Session session;

	public CostBenefitCaseObject(Session session) {
		this.session = session;
	}

	public Session getSession() {
		return session;
	}

	public QContainer[] getCurrentSequence() {
		return currentSequence;
	}

	public boolean hasCurrentSequence() {
		return this.currentSequence != null;
	}

	public void setCurrentSequence(QContainer[] newSequence) {
		this.currentSequence = Arrays.copyOf(newSequence, newSequence.length);
		Logger.getLogger(getClass().getName()).fine(
				"new cost benefit sequence: " + Arrays.asList(newSequence));
	}

	public SearchModel getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(SearchModel searchModel) {
		this.searchModel = searchModel;
	}

	public int getCurrentPathIndex() {
		return currentPathIndex;
	}

	public void setCurrentPathIndex(int currentPathIndex) {
		this.currentPathIndex = currentPathIndex;
	}

	public void incCurrentPathIndex() {
		currentPathIndex++;
		Logger.getLogger(getClass().getName()).fine(
				"next qcontianer: "
						+ (currentPathIndex >= currentSequence.length
								? null
								: currentSequence[currentPathIndex]));
	}

	/**
	 * Resets the path
	 */
	public void resetPath() {
		currentSequence = null;
		for (Fact fact : indicatedFacts) {
			session.getBlackboard().removeInterviewFact(fact);
		}
		indicatedFacts = new LinkedList<Fact>();
		Interview interview = session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class));
		InterviewAgenda interviewAgenda = interview.getInterviewAgenda();
		List<AgendaEntry> toDelete = new LinkedList<InterviewAgenda.AgendaEntry>();
		for (AgendaEntry entry : interviewAgenda.getAgenda()) {
			if (entry.getIndication().hasState(State.MULTIPLE_INDICATED)) {
				toDelete.add(entry);
			}
		}
		interviewAgenda.getAgenda().removeAll(toDelete);
		this.currentPathIndex = -1;
	}

	public void setIndicatedFacts(List<Fact> indicatedFacts) {
		this.indicatedFacts = indicatedFacts;
	}

	public List<Fact> getIndicatedFacts() {
		return Collections.unmodifiableList(indicatedFacts);
	}

	public boolean removeIndicatedFact(Fact fact) {
		return indicatedFacts.remove(fact);
	}

	public Set<Solution> getUndiscriminatedSolutions() {
		return undiscriminatedSolutions;
	}

	public void setUndiscriminatedSolutions(Set<Solution> undiscriminatedSolutions) {
		this.undiscriminatedSolutions = undiscriminatedSolutions;
	}

	public void setDiscriminatingTargets(Set<Target> allDiscriminatingTargets) {
		this.discriminatingTargets = allDiscriminatingTargets;
	}

	public Set<Target> getDiscriminatingTargets() {
		return discriminatingTargets;
	}

	public QContainer getCurrentQContainer() {
		if (currentSequence == null) return null;
		return currentSequence[currentPathIndex];
	}

}
