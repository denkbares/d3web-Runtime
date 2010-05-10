package de.d3web.core.session.interviewmanager;

import java.util.List;

import de.d3web.core.knowledge.InterviewObject;

public interface FormStrategy {
	Form nextForm(List<InterviewObject> agendaEnties);
}
