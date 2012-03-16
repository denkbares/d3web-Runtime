/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.testcase.record;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.session.Value;
import de.d3web.core.session.protocol.FactProtocolEntry;
import de.d3web.core.session.protocol.Protocol;
import de.d3web.core.session.protocol.ProtocolEntry;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.testcase.TestCaseUtils;
import de.d3web.testcase.model.Check;
import de.d3web.testcase.model.DefaultFinding;
import de.d3web.testcase.model.Finding;
import de.d3web.testcase.model.TestCase;

/**
 * Wraps a {@link Protocol} to provide a {@link TestCase}
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 26.01.2012
 */
public class SessionRecordWrapper implements TestCase {

	private final SessionRecord record;

	public SessionRecordWrapper(SessionRecord record) {
		this.record = record;
	}

	@Override
	public Collection<Date> chronology() {
		Set<Date> dates = new TreeSet<Date>();
		for (ProtocolEntry entry : record.getProtocol().getProtocolHistory()) {
			dates.add(entry.getDate());
		}
		return Collections.unmodifiableCollection(dates);
	}

	@Override
	public Collection<Finding> getFindings(Date date, KnowledgeBase kb) {
		List<Finding> findings = new LinkedList<Finding>();
		for (ProtocolEntry entry : record.getProtocol().getProtocolHistory()) {
			if (entry instanceof FactProtocolEntry && entry.getDate().equals(date)) {
				FactProtocolEntry fpe = (FactProtocolEntry) entry;
				if (fpe.getSolvingMethodClassName().equals(PSMethodUserSelected.class.getName())) {
					TerminologyObject object = kb.getManager().search(
							fpe.getTerminologyObjectName());
					Value value = fpe.getValue();
					findings.add(new DefaultFinding(object, value, entry.getDate()));
				}
			}
		}
		return findings;
	}

	@Override
	public Finding getFinding(Date date, TerminologyObject object) {
		for (ProtocolEntry entry : record.getProtocol().getProtocolHistory()) {
			if (entry instanceof FactProtocolEntry && entry.getDate().equals(date)) {
				FactProtocolEntry fpe = (FactProtocolEntry) entry;
				if (fpe.getSolvingMethodClassName().equals(PSMethodUserSelected.class.getName())) {
					if (object.getName().equals(fpe.getTerminologyObjectName())) {
						return new DefaultFinding(object, fpe.getValue(), entry.getDate());
					}
				}
			}
		}
		return null;
	}

	@Override
	public Collection<Check> getChecks(Date date, KnowledgeBase kb) {
		return Collections.emptyList();
	}

	@Override
	public Date getStartDate() {
		return record.getCreationDate();
	}

	@Override
	public Collection<String> check(KnowledgeBase kb) {
		Collection<String> errors = new HashSet<String>();
		for (ProtocolEntry entry : record.getProtocol().getProtocolHistory()) {
			if (entry instanceof FactProtocolEntry) {
				FactProtocolEntry fpe = (FactProtocolEntry) entry;
				if (fpe.getSolvingMethodClassName().equals(PSMethodUserSelected.class.getName())) {
					TerminologyObject object = kb.getManager().search(
							fpe.getTerminologyObjectName());
					if (object == null) {
						errors.add("TerminologyObject \"" + fpe.getTerminologyObjectName()
								+ "\" is not contained in the KB.");
					}
					else {
						Value value = fpe.getValue();
						TestCaseUtils.checkValues(errors, object, value);
					}
				}
			}
		}
		return errors;
	}
}
