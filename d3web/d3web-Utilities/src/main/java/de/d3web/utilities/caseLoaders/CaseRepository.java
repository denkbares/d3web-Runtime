/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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

package de.d3web.utilities.caseLoaders;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.d3web.caserepository.CaseObject;

/**
 * Holds cases that has been loaded by a CaseLoader (e.g. filtered)
 * Creation date: (16.08.2001 20:18:27)
 * @author: Norman Brümmer
 */
public class CaseRepository {

	private static CaseRepository instance = null;

	private Map casesByKbId = null;

	private CaseRepository() {
		super();
		casesByKbId = new HashMap();
	}

	public static CaseRepository getInstance() {
		if (instance == null) {
			instance = new CaseRepository();
		}
		return instance;
	}

	public void purgeAllCases(String kbid) {
		casesByKbId.remove(kbid);
	}

	private Map getCasesFor(String kbid) {
		Map cases = (Map) casesByKbId.get(kbid);
		if (cases == null) {
			cases = new HashMap();
			casesByKbId.put(kbid, cases);
		}
		return cases;
	}

	public void addCase(String kbid, CaseObject co) {
		String caseId = co.getId();
		Map cases = getCasesFor(kbid);
		if (!cases.containsKey(caseId)) {
			cases.put(caseId, co);
		}
	}

	public Set getCaseIds(String kbid) {
		Map cases = getCasesFor(kbid);
		return cases.keySet();
	}

	public Collection getCasesForKnowledgeBase(String kbid) {
		Map caseMap = getCasesFor(kbid);
		return caseMap.values();
	}

	/**
	 * Creation date: (16.08.2001 20:21:48)
	 * @return boolean
	 */
	public boolean removeCase(String kbid, CaseObject co) {
		Map cases = getCasesFor(kbid);
		if (!cases.containsValue(co)) {
			return false;
		} else {
			String caseId = co.getId();
			cases.remove(caseId);
			return true;
		}
	}

	public void removeCaseById(String kbid, String caseId) {
		Map cases = getCasesFor(kbid);
		cases.remove(caseId);
	}

	public CaseObject getCaseById(String kbid, String id) {
		Map cases = getCasesFor(kbid);
		return (CaseObject) cases.get(id);
	}
}