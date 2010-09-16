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
package de.d3web.core.records;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.caserepository.CaseObject;

/**
 * Default Implementation of the CaseRepository Interface (@link
 * CaseRepository).
 * 
 * @author Sebastian Furth (denkbares GmbH)‚
 * 
 */
public class CaseRepositoryImpl implements CaseRepository {

	private List<CaseObject> caseObjects = new ArrayList<CaseObject>();

	@Override
	public boolean add(CaseObject caseObject) {
		if (caseObject == null) throw new IllegalArgumentException(
				"null can't be added to the CaseRepository.");
		if (caseObjects.contains(caseObject)) {
			Logger.getLogger(this.getClass().getSimpleName())
					.warning(
							"CaseObject " + caseObject.getId()
									+ " is already in the CaseRepository.");
			return false;
		}
		return caseObjects.add(caseObject);
	}

	@Override
	public Iterator<CaseObject> iterator() {
		return caseObjects.iterator();
	}

	@Override
	public boolean remove(CaseObject caseObject) {
		if (caseObject == null) throw new IllegalArgumentException(
				"null can't be removed from the CaseRepository.");
		if (!caseObjects.contains(caseObject)) throw new IllegalArgumentException("CaseObject "
				+ caseObject.getId() + " is not in the CaseRepository");

		return caseObjects.remove(caseObject);
	}

	@Override
	public CaseObject getCaseObjectById(String id) {
		if (id == null || id.matches("\\s+")) throw new IllegalArgumentException(id
				+ " is not a valid ID.");
		for (CaseObject co : caseObjects) {
			if (co.getId().equals(id)) return co;
		}
		return null;
	}

}
