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
package de.d3web.core;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface to keep references to binaries in the kb.
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public interface Resource {

	/**
	 * @return the size of the data of this resource
	 */
	public long getSize();

	/**
	 * @return an InputStream to read the data of this resource
	 * @throws IOException
	 *             if the stream cannot be provided
	 */
	public InputStream getInputStream() throws IOException;

	/**
	 * Returns the relative path to the resource within the knowledge base. The
	 * path is similar to file system paths, but they do use the same file
	 * separator character ("/") with no respect to the underlying file systems.
	 * The path should be used case insensitive.
	 * 
	 * @return the path of the resource
	 */
	public String getPathName();

}
