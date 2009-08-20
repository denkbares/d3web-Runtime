package de.d3web.persistence.xml;
import java.net.URL;

import de.d3web.kernel.domainModel.KnowledgeBase;
/**
 * PersistenceHandler that is able to load KnowledgeBases
 * Creation date: (06.06.2001 15:18:25)
 * @author Michael Scharvogel
 */
public interface AuxiliaryPersistenceHandler extends PersistenceHandler {
	public KnowledgeBase load(KnowledgeBase kb, URL url);
}