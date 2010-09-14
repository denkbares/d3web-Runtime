package de.d3web.core.records;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.info.DCMarkup;
import de.d3web.core.session.protocol.Protocol;

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
