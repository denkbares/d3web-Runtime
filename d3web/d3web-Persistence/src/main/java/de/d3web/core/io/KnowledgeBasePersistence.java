package de.d3web.core.io;

import java.io.IOException;
import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;

public class KnowledgeBasePersistence implements Persistence<KnowledgeBase> {

	private final PersistenceManager manager;
	private final KnowledgeBase knowledgeBase;
	private final Document document;

	/**
	 * Creates a new {@link Persistence} for a knowledge base, that shall be
	 * read (partially) from the specified stream.
	 * 
	 * @param manager the persistence manager for this read procedure
	 * @param knowledgeBase the knowledge base
	 * @param stream the stream to read the xml document from
	 * @throws IOException if the xml document stream could been read from the
	 *         stream
	 */
	public KnowledgeBasePersistence(PersistenceManager manager, KnowledgeBase knowledgeBase, InputStream stream) throws IOException {
		this.manager = manager;
		this.knowledgeBase = knowledgeBase;
		this.document = XMLUtil.streamToDocument(stream);
	}

	/**
	 * Creates a new {@link Persistence} for a knowledge base, that shall be
	 * written (partially) to a newly created xml document.
	 * 
	 * @param manager the persistence manager for this read procedure
	 * @param knowledgeBase the knowledge base
	 */
	public KnowledgeBasePersistence(PersistenceManager manager, KnowledgeBase knowledgeBase) throws IOException {
		this.manager = manager;
		this.knowledgeBase = knowledgeBase;
		this.document = XMLUtil.createEmptyDocument();
	}

	@Override
	public KnowledgeBase getArtifact() {
		return knowledgeBase;
	}

	@Override
	public Document getDocument() {
		return document;
	}

	@Override
	public Object readFragment(Element element) throws NoSuchFragmentHandlerException, IOException {
		return this.manager.getFragmentManager().readFragment(element, this);
	}

	@Override
	public Element writeFragment(Object object) throws NoSuchFragmentHandlerException, IOException {
		return this.manager.getFragmentManager().writeFragment(object, this);
	}
}
