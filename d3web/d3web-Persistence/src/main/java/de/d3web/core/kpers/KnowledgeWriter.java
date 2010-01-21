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
package de.d3web.core.kpers;

import java.io.IOException;
import java.io.OutputStream;

import de.d3web.core.kpers.progress.ProgressListener;
import de.d3web.kernel.domainModel.KnowledgeBase;

/**
 * Interface for all writing persistance handlers.
 * @author Markus Friedrich (denkbares GmbH)
 */
public interface KnowledgeWriter {

	/**
	 * Writes the Knowledge this KnowledgeWriter can handle into the OutputStream
	 * @param kb Knowledgebase in which the Knowledge is contained
	 * @param stream Outputstream
	 * @param listener listner which will be informed during this operation, null is not accepted
	 * @throws IOException if an error occurs, an IO Exception is thrown
	 */
	public void write(KnowledgeBase kb, OutputStream stream, ProgressListener listener) throws IOException;
	
	/**
	 * The size of the knowledge which can be written with this KnowledgeWriter can be
	 * valued with this method
	 * @param kb KnowledgeBase containing the Knowledge
	 * @return the valued size of the write method
	 */
	public int getEstimatedSize(KnowledgeBase kb);
}
