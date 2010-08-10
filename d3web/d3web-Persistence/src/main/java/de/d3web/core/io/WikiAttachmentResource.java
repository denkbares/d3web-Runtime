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
package de.d3web.core.io;

import java.io.IOException;
import java.io.InputStream;

import de.d3web.core.knowledge.Resource;

/**
 * Resource for storing an attachment of an Wikipage.
 * See also: {@link ImageToResourceType}
 * 
 * TODO The size must be retrieved
 * 
 * @author Johannes Dienst
 *
 */
public class WikiAttachmentResource implements Resource {

	private int size;
	private String path;
	private InputStream input;
	
	
	public WikiAttachmentResource(InputStream input, String path) {
		this.input = input;
		this.path = path;
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		return this.input;
	}

	@Override
	public String getPathName() {
		return path;
	}

	@Override
	public long getSize() {
		return this.size;
	}

}
