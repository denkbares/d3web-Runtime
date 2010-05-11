package de.d3web.core.session.interviewmanager;

import java.util.List;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.session.Session;

public interface FormStrategy {
	Form nextForm(List<InterviewObject> agendaEnties, Session session);
}
