/*
 * Copyright (C) 2010 denkbares GmbH, Würzburg, Germany
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
package de.d3web.costbenefit.session.interviewmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.interviewmanager.AgendaSortingStrategy;
import de.d3web.core.session.interviewmanager.InterviewAgenda.AgendaEntry;
import de.d3web.core.session.interviewmanager.InterviewAgenda.InterviewState;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.costbenefit.blackboard.CostBenefitCaseObject;

/**
 * The {@link CostBenefitAgendaSortingStrategy} does not do a specific sorting
 * of the {@link AgendaEntry} instances, but it removes entries from the agenda,
 * that became INACTIVE.
 * 
 * @author joba
 * 
 */
public class CostBenefitAgendaSortingStrategy implements AgendaSortingStrategy {

	private CostBenefitCaseObject co;

	public CostBenefitAgendaSortingStrategy(CostBenefitCaseObject co) {
		this.co = co;
	}

	@Override
	public List<AgendaEntry> sort(List<AgendaEntry> entries) {
		// we keep the order of the entries, but we delete the "inactive"
		// entries and put indicated questions to the beginning of the list
		Map<Question, AgendaEntry> questions = new HashMap<Question, AgendaEntry>();
		List<AgendaEntry> other = new LinkedList<AgendaEntry>();
		for (AgendaEntry agendaEntry : entries) {
			if (agendaEntry.hasState(InterviewState.ACTIVE)) {
				if (agendaEntry.getInterviewObject() instanceof Question) {
					questions.put((Question) agendaEntry.getInterviewObject(), agendaEntry);
				}
				else {
					other.add(agendaEntry);
				}
			}
			else {
				for (Fact fact : co.getIndicatedFacts()) {
					if (fact.getTerminologyObject() == agendaEntry.getInterviewObject()) {
						co.getSession().getBlackboard().removeInterviewFact(fact);
						co.removeIndicatedFact(fact);
						break;
					}
				}
			}
		}
		// Sorting the questions in the order they appear in their parents
		List<Question> questionsSorted = new LinkedList<Question>();
		for (Question q : questions.keySet()) {
			if (!questionsSorted.contains(q)) {
				// do not add questions having a value other than Undefined
				if (UndefinedValue.isUndefinedValue(co.getSession().getBlackboard().getValue(q))) {
					for (TerminologyObject object : q.getParents()) {
						for (TerminologyObject child : object.getChildren()) {
							if (!questionsSorted.contains(child)
									&& questions.keySet().contains(child)) {
								questionsSorted.add((Question) child);
							}
						}
					}
				}
			}
		}
		List<AgendaEntry> returnList = new ArrayList<AgendaEntry>(entries.size());
		for (Question q : questionsSorted) {
			returnList.add(questions.get(q));
		}
		// add other QASets in original order
		returnList.addAll(other);
		return returnList;
	}
}
