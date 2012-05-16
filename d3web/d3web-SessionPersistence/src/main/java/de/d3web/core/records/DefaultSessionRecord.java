/*
 * Copyright (C) 2011 denkbares GmbH
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
package de.d3web.core.records;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import de.d3web.core.knowledge.InfoStore;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.SessionInfoStore;
import de.d3web.core.session.protocol.DefaultProtocol;
import de.d3web.core.session.protocol.Protocol;

/**
 * Persistent version of a session.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 15.09.2010
 */
public class DefaultSessionRecord implements SessionRecord {

	private final String id;
	private final Date creationDate;
	private Date lastEditDate;

	private final List<FactRecord> valueFacts = new LinkedList<FactRecord>();
	private final List<FactRecord> interviewFacts = new LinkedList<FactRecord>();

	private final Protocol protocol = new DefaultProtocol();

	private String name;

	private final InfoStore infoStore = new SessionInfoStore(this);

	public DefaultSessionRecord() {
		this(UUID.randomUUID().toString());
	}

	public DefaultSessionRecord(String id) {
		this(id, new Date());
	}

	public DefaultSessionRecord(String id, Date date) {
		this(id, date, date);
	}

	public DefaultSessionRecord(String id, Date creationDate, Date lastEditDate) {
		this.id = id;
		this.creationDate = creationDate;
		this.lastEditDate = lastEditDate;
	}

	@Override
	public void addValueFact(FactRecord fact) {
		valueFacts.add(fact);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Date getLastChangeDate() {
		return lastEditDate;
	}

	@Override
	public void touch(Date lastEditDate) {
		this.lastEditDate = lastEditDate;
	}

	@Override
	public Protocol getProtocol() {
		return protocol;
	}

	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public List<FactRecord> getValueFacts() {
		return Collections.unmodifiableList(valueFacts);
	}

	@Override
	public void touch() {
		touch(new Date());
	}

	@Override
	public void addInterviewFact(FactRecord fact) {
		interviewFacts.add(fact);
	}

	@Override
	public List<FactRecord> getInterviewFacts() {
		return Collections.unmodifiableList(interviewFacts);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public InfoStore getInfoStore() {
		return infoStore;
	}

	@Override
	public List<Solution> getSolutions(KnowledgeBase kb, State... states) {
		HashMap<Solution, FactRecord> map = new HashMap<Solution, FactRecord>();
		for (FactRecord fact : valueFacts) {
			Solution solution = kb.getManager().searchSolution(fact.getObjectName());
			if (solution != null) {
				FactRecord savedRecord = map.get(solution);
				if (savedRecord == null) {
					map.put(solution, fact);
				}
				// if the psm is null, it is a merged fact, so this fact should
				// be used
				else if (fact.getPsm() == null) {
					map.put(solution, fact);
				}
			}
		}
		List<Solution> result = new LinkedList<Solution>();
		for (Entry<Solution, FactRecord> entry : map.entrySet()) {
			for (State state : states) {
				Rating value = (Rating) entry.getValue().getValue();
				if (value.hasState(state)) {
					result.add(entry.getKey());
					continue;
				}
			}
		}
		return result;
	}
}
