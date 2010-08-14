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
package de.d3web.caserepository;

import java.util.Iterator;

/**
 * This interface specifies the methods required for all CaseRepository
 * implementations.
 * 
 * A CaseRepository stores multiple {@link CaseObject} instances. CaseObjects
 * can be added and removed directly.
 * 
 * The stored {@link CaseObject} instances are accessible via an
 * {@link Iterator}.
 * 
 * @author Sebastian Furth (denkbares GmbH)
 * 
 */
public interface CaseRepository {

	/**
	 * Tries to add the specified {@link CaseObject} to this CaseRepository.
	 * 
	 * If the CaseObject was successfully added, the true is returned. Otherwise
	 * the returned value will be false.
	 * 
	 * If you try to add null, an {@link IllegalArgumentException} will be
	 * thrown.
	 * 
	 * @param caseObject the CaseObject which will be added (null is not
	 *        allowed)
	 * @return true, if the CaseObject was added to the CaseRepository; false
	 *         otherwise
	 */
	public boolean add(CaseObject caseObject);

	/**
	 * Tries to remove the specified {@link CaseObject} from this
	 * CaseRepository.
	 * 
	 * If the CaseObject was successfully removed, then true will be returned.
	 * Otherwise the returned value will be false.
	 * 
	 * If you try to remove null, then an {@link IllegalArgumentException} will
	 * be thrown.
	 * 
	 * @param caseObject the CaseObject to be removed (null is not allowed).
	 * @return true, if the CaseObject was removed from the CaseRepository,
	 *         false otherwise.
	 */
	public boolean remove(CaseObject caseObject);

	/**
	 * Returns an {@link Iterator} instance, that offers access to the
	 * {@link CaseObject} instances stored in this CaseRepository.
	 * 
	 * @return the Iterator which offers access to the stored CaseObjects.
	 */
	public Iterator<CaseObject> iterator();

	/**
	 * Traverses the CaseRepository for a {@link CaseObject} with the specified
	 * unique identifier. If a CaseObject with this identifier was found, the it
	 * will be returned. Otherwise the returned value will be null.
	 * 
	 * @param id the specified identifier of the desired CaseObject
	 * @return the CaseObject with the specified ID if it exists, otherwise
	 *         null.
	 */
	public CaseObject getCaseObjectById(String id);

}
