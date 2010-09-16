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
public class SessionRecord {

	private String id;

	private final Date creationDate;
	private Date lastEditDate;

	private List<FactRecord> facts = new LinkedList<FactRecord>();

	private Protocol protocol;

	private KnowledgeBase kb;

	private DCMarkup dcMarkup;

	public SessionRecord(KnowledgeBase kb) {
		this.kb = kb;
		id = "Case " + System.currentTimeMillis();
		creationDate = new Date();
		lastEditDate = new Date();
	}

	public SessionRecord(String id, KnowledgeBase kb, Date creationDate, Date lastEditDate) {
		this.id = id;
		this.kb = kb;
		this.creationDate = creationDate;
		this.lastEditDate = lastEditDate;
	}

	public void addFact(FactRecord fact) {
		facts.add(fact);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getLastEditDate() {
		return lastEditDate;
	}

	public void setLastEditDate(Date lastEditDate) {
		this.lastEditDate = lastEditDate;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public List<FactRecord> getFacts() {
		return facts;
	}

	public KnowledgeBase getKb() {
		return kb;
	}

	public void setDCMarkup(DCMarkup dcMarkup) {
		this.dcMarkup = dcMarkup;
	}

	public DCMarkup getDCMarkup() {
		return dcMarkup;
	}
}
