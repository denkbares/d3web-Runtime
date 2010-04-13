/*
 * Copyright (C) 2010 denkbares GmbH
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
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
