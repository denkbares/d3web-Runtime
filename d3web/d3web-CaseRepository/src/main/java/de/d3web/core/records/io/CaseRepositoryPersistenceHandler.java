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
package de.d3web.core.records.io;

import java.io.File;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.records.CaseRepository;

/**
 * This interface specifies the methods which are necessary for loading and
 * saving a CaseRepository. (@link CaseRepository)
 * 
 * @author Sebastian Furth (denkbares GmbH)
 * 
 */
public interface CaseRepositoryPersistenceHandler {

	/**
	 * Loads a CaseRepository (@link CaseRepository) from a specified File.
	 * 
	 * @param kb the underlying KnowledgeBase
	 * @param file the File containing the CaseRepository
	 * @return the loaded CaseRepository
	 */
	public CaseRepository load(KnowledgeBase kb, File file);

	/**
	 * Saves a CaseRepository (@link CaseRepository) to a specified File.
	 * 
	 * @param caseRepository the CaseRepository which will be saved
	 * @param file the File to which the CaseRepository will be saved.
	 */
	public void save(CaseRepository caseRepository, File file);

}
