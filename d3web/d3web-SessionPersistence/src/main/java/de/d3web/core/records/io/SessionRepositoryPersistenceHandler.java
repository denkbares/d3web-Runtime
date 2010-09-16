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
import java.io.IOException;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.records.SessionRepository;

/**
 * This interface specifies the methods which are necessary for loading and
 * saving a SessionRepository. (@link SessionRepository)
 * 
 * @author Sebastian Furth (denkbares GmbH)
 * 
 */
public interface SessionRepositoryPersistenceHandler {

	/**
	 * Loads a SessionRepository (@link SessionRepository) from a specified
	 * File.
	 * 
	 * @param kb the underlying KnowledgeBase
	 * @param file the File containing the SessionRepository
	 * @return the loaded SessionRepository
	 * @throws IOException
	 */
	public SessionRepository load(KnowledgeBase kb, File file) throws IOException;

	/**
	 * Saves a SessionRepository (@link SessionRepository) to a specified File.
	 * 
	 * @param sessionRepository the SessionRepository which will be saved
	 * @param file the File to which the SessionRepository will be saved.
	 * @throws IOException
	 */
	public void save(SessionRepository sessionRepository, File file) throws IOException;

}
