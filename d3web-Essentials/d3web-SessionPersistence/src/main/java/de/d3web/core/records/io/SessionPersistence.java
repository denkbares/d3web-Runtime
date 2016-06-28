package de.d3web.core.records.io;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.NoSuchFragmentHandlerException;
import de.d3web.core.io.Persistence;
import de.d3web.core.records.SessionRecord;

/**
 * Implementation for a persistence model to read/write session records from/to
 * a xml dom structure by using fragment handlers.
 * 
 * @author Volker Belli (denkbares GmbH)
 * @created 27.11.2013
 */
public class SessionPersistence implements Persistence<SessionRecord> {

	private final SessionPersistenceManager manager;
	private final SessionRecord record;
	private final Element rootElement;

	/**
	 * Creates a new SessionPersistence to read/write a {@link SessionRecord}
	 * from/to a specified root element of a session record xml dom structure.
	 * 
	 * @param manager
	 * @param record
	 * @param rootElement
	 */
	public SessionPersistence(SessionPersistenceManager manager, SessionRecord record, Element rootElement) {
		this.manager = manager;
		this.record = record;
		this.rootElement = rootElement;
	}

	@Override
	public SessionRecord getArtifact() {
		return record;
	}

	@Override
	public Document getDocument() {
		return rootElement.getOwnerDocument();
	}

	@Override
	public Object readFragment(Element element) throws IOException {
		return manager.getFragmentManager().readFragment(element, this);
	}

	@Override
	public Element writeFragment(Object object) throws IOException {
		return manager.getFragmentManager().writeFragment(object, this);
	}
}
