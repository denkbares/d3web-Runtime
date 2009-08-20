package de.d3web.persistence.xml;

import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.w3c.dom.Document;

import de.d3web.kernel.domainModel.KnowledgeBase;

/**
 * This interface describes a handler for case repositories in 
 * knowledge base jar-sources
 */
public interface CaseRepositoryHandler {
	/**
	 * @return the ID of this handler
	 */
	public String getId();
	/**
	 * Loads the case repository from the given url
	 * @return a List of CaseObjects representing the cae repository
	 */
	public List load(KnowledgeBase kb, URL url);
	/**
	 * saves the given cases to the specified URL
	 */
	public Document save(Collection caseRepository);
	/**
	 * @return the default storage location (usually a URL) as String 
	 */
	public String getStorageLocation();
	/**
	 * Returns every MultimediaItem in this CaseRepository	 * @return List List of all MultimediaItems	 */
	public List getMultimediaItems(Collection caseRepository);
}
