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
package de.d3web.core.records.io;

import java.io.File;

import de.d3web.caserepository.CaseRepository;
import de.d3web.core.knowledge.KnowledgeBase;

/**
 * This implementation of the CaseRepositoryPersistenceHandler interface
 * can handle exactly one XML file. This XML file has to contain the whole
 * CaseRepository.
 * 
 * @author Sebastian Furth (denkbares GmbH)
 *
 */
public class SingleXMLCaseRepositoryHandler extends AbstractCaseRepositoryHandler {
	
	/*
	 * Singleton instance
	 */
	private static SingleXMLCaseRepositoryHandler instance = new SingleXMLCaseRepositoryHandler();
	
	private SingleXMLCaseRepositoryHandler() {}
	
	/**
	 * Returns an instance of SingleXMLCaseRepositoryHandler.
	 * @return instance of SingleXMLCaseRepositoryHandler
	 */
	public static SingleXMLCaseRepositoryHandler getInstance() {
		return instance;
	}
	
	@Override
	public CaseRepository load(KnowledgeBase kb, File file) {
		if (kb == null)
			throw new IllegalArgumentException("KnowledgeBase is null. Unable to load CaseRepository.");
		if (file == null)
			throw new IllegalArgumentException("File is null. Unable to load CaseRepository.");
		return getCaseRepositoryReader().createCaseRepository(file, kb);
	}


	@Override
	public void save(CaseRepository caseRepository, File file) {
		if (caseRepository == null)
			throw new IllegalArgumentException("CaseRepository is null. Unable to save CaseRepository.");
		if (file == null)
			throw new IllegalArgumentException("File is null. Unable to save CaseRepository.");
		getCaseRepositoryWriter().saveToFile(file, caseRepository);
	}
	

}
