package de.d3web.core.records.io;

import de.d3web.caserepository.CaseObject;
import de.d3web.core.session.Session;

public interface CaseConversionHandler {

	void copyToSession(CaseObject source, Session target);

	void copyToCaseRecord(Session source, CaseObject target);
}
