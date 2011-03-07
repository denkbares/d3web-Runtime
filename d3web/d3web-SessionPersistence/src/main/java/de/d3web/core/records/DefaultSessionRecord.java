/*
 * Copyright (C) 2010 denkbares GmbH
 * 
 * This is free software for non commercial use
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 */
package de.d3web.core.records;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import de.d3web.core.knowledge.InfoStore;
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
}
