/*
 * Copyright (C) 2010 denkbares GmbH
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
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.info.DCMarkup;
import de.d3web.core.session.protocol.Protocol;

/**
 * Persistent version of a session
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 15.09.2010
 */
public class DefaultSessionRecord implements SessionRecord {

	private String id;

	private final Date creationDate;
	private Date lastEditDate;

	private List<FactRecord> valueFacts = new LinkedList<FactRecord>();
	private List<FactRecord> interviewFacts = new LinkedList<FactRecord>();

	private Protocol protocol;

	private KnowledgeBase kb;

	private DCMarkup dcMarkup;

	private String name;

	public DefaultSessionRecord(KnowledgeBase kb) {
		this.kb = kb;
		id = "Session " + System.currentTimeMillis();
		creationDate = new Date();
		lastEditDate = new Date();
	}

	public DefaultSessionRecord(String id, KnowledgeBase kb, Date creationDate, Date lastEditDate) {
		this.id = id;
		this.kb = kb;
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

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
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
	public KnowledgeBase getKnowledgeBase() {
		return kb;
	}

	@Override
	public void setDCMarkup(DCMarkup dcMarkup) {
		this.dcMarkup = dcMarkup;
	}

	@Override
	public DCMarkup getDCMarkup() {
		return dcMarkup;
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

	public void setName(String name) {
		this.name = name;
	}
}
