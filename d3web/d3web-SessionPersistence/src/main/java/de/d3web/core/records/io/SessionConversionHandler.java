package de.d3web.core.records.io;

import de.d3web.core.records.SessionRecord;
import de.d3web.core.session.Session;

public interface SessionConversionHandler {

	void copyToSession(SessionRecord source, Session target);

	void copyToSessionRecord(Session source, SessionRecord target);
}
