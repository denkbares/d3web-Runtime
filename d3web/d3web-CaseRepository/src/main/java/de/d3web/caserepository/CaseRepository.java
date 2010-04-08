package de.d3web.caserepository;

import java.util.Iterator;

/**
 * This interface specifies the methods required for
 * all CaseRepository implementations.
 * 
 * A CaseRepository stores multiple CaseObjects. 
 * CaseObjects can be added and removed directly.
 * 
 * The stored CaseObjects are accessible via an
 * Iterator.
 * 
 * @author Sebastian Furth (denkbares GmbH)
 *
 */
public interface CaseRepository {
	
	/**
	 * Tries to add the specified CaseObject (@link CaseObject)
	 * to this CaseRepository. 
	 * 
	 * If the CaseObject was successfully added true will be returned. 
	 * Otherwise the returned value will be false.
	 * 
	 * If you try to add null, an IllegalArgumentException will be thrown.
	 * 
	 * @param caseObject the CaseObject which will be added (null is not allowed).
	 * @return true if the CaseObject was added to the CaseRepository, otherwise false.
	 */
	public boolean add(CaseObject caseObject);
	
	/**
	 * Tries to remove the specified CaseObject(@link CaseObject)
	 * from this CaseRepository.
	 * 
	 * If the CaseObject was successfully removed true will be returned.
	 * Otherwise the returned value will be false.
	 * 
	 * If you try to remove null, an IllegalArgumentException will be thrown.
	 * 
	 * @param caseObject the CaseObject which will be removed (null is not allowed).
	 * @return true if the CaseObject was removed from the CaseRepository, otherwise false.
	 */
	public boolean remove(CaseObject caseObject);
	
	/**
	 * Returns an Iterotor which offers access to the CaseObjects (@link CaseObject)
	 * stored in this CaseRepository.
	 * 
	 * @return the Iterator which offers access to the stored CaseObjects.
	 */
	public Iterator<CaseObject> iterator();
	
	/**
	 * Searches the CaseRepository for a CaseObject with the specified ID. If a
	 * CaseObject with this ID was found it will be returned. Otherwise the
	 * returned value will be null.
	 * 
	 * @param id the ID of the desired CaseObject
	 * @return the CaseObject with the specified ID if it exists, otherwise null.
	 */
	public CaseObject getCaseObjectById(String id);

}
