/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.core.io;

import java.io.IOException;
import java.io.InputStream;

import com.denkbares.progress.ProgressListener;
import de.d3web.core.knowledge.KnowledgeBase;

/**
 * Interface for all reading persistence handlers.
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public interface KnowledgeReader {

	/**
	 * This method reads the input stream and inserts the knowledge in the {@link KnowledgeBase} instance.
	 *
	 * @param persistenceManager the persistence manager responsible for reading
	 * @param knowledgeBase      KnowledgeBase in which die knowledge should be inserted
	 * @param stream             InputStream containing the knowledge
	 * @param progress           progress listener which will be informed during this operation, null is not accepted
	 * @throws IOException when an IO errors occurs during reading the part of the knowledge base
	 */
	void read(PersistenceManager persistenceManager, KnowledgeBase knowledgeBase, InputStream stream, ProgressListener progress) throws IOException;
}
