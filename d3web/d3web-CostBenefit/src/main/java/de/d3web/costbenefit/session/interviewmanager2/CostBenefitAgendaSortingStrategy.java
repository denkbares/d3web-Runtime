package de.d3web.costbenefit.session.interviewmanager2;

import java.util.ArrayList;
import java.util.List;

import de.d3web.core.session.interviewmanager.AgendaSortingStrategy;
import de.d3web.core.session.interviewmanager.InterviewAgenda.AgendaEntry;
import de.d3web.core.session.interviewmanager.InterviewAgenda.InterviewState;

/**
 * The {@link CostBenefitAgendaSortingStrategy} does not do a specific sorting
 * of the {@link AgendaEntry} instances, but it removes entries from the agenda, 
 * that became INACTIVE.
 * 
 * @author joba
 *
 */
public class CostBenefitAgendaSortingStrategy implements AgendaSortingStrategy {

	@Override
	public void sort(List<AgendaEntry> entries) {
		// we keep the order of the entries, but we delete the "inactive" entries
		List<AgendaEntry> originalEntries = new ArrayList<AgendaEntry>(entries);
		for (AgendaEntry agendaEntry : originalEntries) {
			if (agendaEntry.hasState(InterviewState.INACTIVE)) {
				entries.remove(agendaEntry);
			}
		}
	}

}
