package de.d3web.persistence.xml;

import java.util.Collection;

import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.persistence.utilities.PersistentObjectDescriptor;

/**
 * Interface for AuxiliaryPersistenceHandler, that need to save several documents
 * 
 * @author pkluegl
 *
 */
public interface MultipleAuxiliaryPersistenceHandler extends AuxiliaryPersistenceHandler {
	
	/**
	 * [TODO]: Peter: not the best solution:
	 * Problem : if a handler needs to save several documents AND there is no possiblity
	 * to create an extra handler for each extra document.
	 * Therefore PersistanceManager will differ and choose the correct method
	 * 
	 * saves the given knowledge base
	 * @return the DOM-Document OR InputStream representing the saved knowledge base
	 */
	public Collection<PersistentObjectDescriptor> saveAll(KnowledgeBase kb);

}
