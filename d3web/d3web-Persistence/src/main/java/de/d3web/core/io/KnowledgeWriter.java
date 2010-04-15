/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.core.io;

import java.io.IOException;
import java.io.OutputStream;

import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.knowledge.KnowledgeBase;

/**
 * Interface for all writing persistence handlers.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public interface KnowledgeWriter {

	/**
	 * Writes the parts of the specified {@link KnowledgeBase} instance, that
	 * this KnowledgeWriter can handle, into the {@link OutputStream}.
	 * 
	 * @param knowledgeBase
	 *            the specified knowledge base in which the handled knowledge is
	 *            included
	 * @param stream
	 *            the specified {@link OutputStream} to which the handled
	 *            knowledge is written
	 * @param listener
	 *            the specified listener which will be notified during this
	 *            operation, null is not accepted
	 * @throws IOException
	 *             when an IO expection ocurrs during the write action
	 */
	public void write(KnowledgeBase knowledgeBase, OutputStream stream, ProgressListener listener) throws IOException;

	/**
	 * The size of the knowledge--written by this {@link KnowledgeWriter}--is
	 * valued by this method.
	 * 
	 * @param knowledgeBase
	 *            knowledge base containing the knowledge under estimation
	 * @return the valued size of the write method
	 */
	public int getEstimatedSize(KnowledgeBase knowledgeBase);
}
