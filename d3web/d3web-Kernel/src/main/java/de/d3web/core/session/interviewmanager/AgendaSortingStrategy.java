package de.d3web.core.session.interviewmanager;

import java.util.List;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.session.interviewmanager.InterviewAgenda.AgendaEntry;

/** 
 * The {@link AgendaSortingStrategy} implements the behavior of the 
 * {@link InterviewAgenda} and the sorting of the indicated 
 * {@link InterviewObject} instances, respectively.
 * @author joba
 *
 */
public interface AgendaSortingStrategy {
	public void sort(List<AgendaEntry> entries);
}
