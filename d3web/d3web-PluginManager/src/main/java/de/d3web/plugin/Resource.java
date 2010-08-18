/*
 * Copyright (C) 2010 denkbares GmbH, Würzburg, Germany
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

package de.d3web.plugin;

import java.io.IOException;
import java.io.InputStream;

/**
 * This interface describes a resource file located within a plugin.
 * 
 * @author volker_belli
 * 
 */
public interface Resource {

	/**
	 * Returns the expected size of the resource. It may return -1 if the size
	 * cannot be determinated.
	 * 
	 * @return the size of the resource
	 */
	public long getSize();

	/**
	 * Returns a created InputStream to read the binary data of the underlying
	 * resource. The caller is responsible to close the stream after reading
	 * from it.
	 * 
	 * @return the stream to read the data from
	 * @throws IOException the stream could not been created
	 */
	public InputStream getInputStream() throws IOException;

	/**
	 * Returns the relative path of this resource within the public resource
	 * folder of the underlying plugin.
	 * 
	 * @return the relative path of the resource
	 */
	public String getPathName();
}
