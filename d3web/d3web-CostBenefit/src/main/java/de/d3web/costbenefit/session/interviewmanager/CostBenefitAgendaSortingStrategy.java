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
	public List<AgendaEntry> sort(List<AgendaEntry> entries) {
		// we keep the order of the entries, but we delete the "inactive" entries
		List<AgendaEntry> originalEntries = new ArrayList<AgendaEntry>(entries);
		for (AgendaEntry agendaEntry : originalEntries) {
			if (agendaEntry.hasState(InterviewState.INACTIVE)) {
				entries.remove(agendaEntry);
			}
		}
		return entries;
	}

}
