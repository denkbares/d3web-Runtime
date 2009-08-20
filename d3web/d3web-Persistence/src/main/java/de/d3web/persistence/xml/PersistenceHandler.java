package de.d3web.persistence.xml;
import org.w3c.dom.Document;

import de.d3web.kernel.domainModel.KnowledgeBase;
/**
 * This interface describes a identifiable handler that is able to save knowledge bases
 * Creation date: (06.06.2001 15:18:25)
 * @author Michael Scharvogel
 */
public interface PersistenceHandler {
	/**
	 * @return the ID of this handler
	 */
	public String getId();
	/**l
	 * @return the default location for saving the knowledge base (usually a URL)
	 */
	public String getDefaultStorageLocation();

	/**
	 * saves the given knowledge base
	 * @return the DOM-Document representing the saved knowledge base
	 */
	public Document save(KnowledgeBase kb);
	
}
