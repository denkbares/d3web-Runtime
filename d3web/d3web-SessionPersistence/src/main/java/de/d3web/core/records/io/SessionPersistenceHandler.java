/*
 * Copyright (C) 2011 denkbares GmbH
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

import java.io.IOException;

import org.w3c.dom.Element;

import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.records.SessionRecord;

/**
 * A SessionPersistenceHandler reads and writes s special part of a
 * SessionRecord.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public interface SessionPersistenceHandler {

	/**
	 * Reads the information from the sessionElement and adds them to the
	 * sessionRecord
	 * 
	 * @param sessionElement XML Element containing the session, the
	 *        SessionPersistanceHandler automatically extracts his nodes
	 * @param sessionRecord SessionRecord, in which the information will be
	 *        inserted
	 * @param listener ProgressListener, which will be informed about the
	 *        progress
	 * @throws IOException if an error occurs
	 */
	void read(Element sessionElement, SessionRecord sessionRecord, ProgressListener listener) throws IOException;

	/**
	 * Appends Elements containing knowledge this PersistanceHandler can handle
	 * 
	 * @param sessionElement XML Element, where the created elements should be
	 *        appended
	 * @param sessionRecord SessionRecord containing the information
	 * @param listener ProgressListener, which will be informed about the
	 *        progress
	 * @throws IOException if an error occurs
	 */
	void write(Element sessionElement, SessionRecord sessionRecord, ProgressListener listener) throws IOException;
}
