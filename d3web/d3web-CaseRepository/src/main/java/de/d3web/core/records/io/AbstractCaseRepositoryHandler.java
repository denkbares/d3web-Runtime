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

import de.d3web.caserepository.sax.CaseRepositoryReader;
import de.d3web.caserepository.utilities.CaseRepositoryWriter;

/**
 * Abstract implementation of the CaseRepositoryPersistenceHandler
 * Interface which offers some basic methods needed in all Implementations
 * 
 * @author Sebastian Furth
 *
 */
public abstract class AbstractCaseRepositoryHandler implements CaseRepositoryPersistenceHandler {
	
	private CaseRepositoryReader cr;
	private CaseRepositoryWriter cw;
	
	/**
	 * Returns an instance of CaseRepositoryReader which is
	 * necessary for loading CaseRepositories.
	 * @return instance of CaseRepositoryReader
	 */
	public CaseRepositoryReader getCaseRepositoryReader() {
		if (cr == null)
			cr = new CaseRepositoryReader();
		return cr;
	}
	
	/**
	 * Returns an instance of CaseRepositoryWriter which is
	 * necessary for saving CaseRepositories.
	 * @return instance of CaseRepositroyWriter
	 */
	public CaseRepositoryWriter getCaseRepositoryWriter() {
		if (cw == null)
			cw = new CaseRepositoryWriter();
		return cw;
	}

}
