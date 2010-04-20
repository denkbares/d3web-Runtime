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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import de.d3web.core.knowledge.Resource;
/**
 * Class to store Jar Ressources in the KnowledgeBase
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class JarBinaryRessource implements Resource {

	private int size;
	private String entryPath;
	private File file;
	
	
	public JarBinaryRessource(ZipEntry entry, File zipfile) throws IOException {
		entryPath=entry.getName();
		size = (int) entry.getSize();
		this.file=zipfile;
		if (entry.getSize()>Integer.MAX_VALUE) {
			throw new IOException("File with more than 2 GB are not supported");
		}
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		ZipFile zipfile = new ZipFile(file);
		try {
			ZipEntry entry = zipfile.getEntry(entryPath);
			InputStream inputStream = zipfile.getInputStream(entry);
			byte[] buffer = new byte[size];
			int read = 0;
			int len = 0;
			while (read != -1 && len!=size) {
				len+=read;
				read = inputStream.read(buffer, len, size-len);
			} 
			if (size!=len) {
				throw new IOException("Cannot read complete entry");
			}
			return new ByteArrayInputStream(buffer);
		}
		finally {
			zipfile.close();
		}
	}

	@Override
	public String getPathName() {
		String s = entryPath.substring(PersistenceManager.MULTIMEDIA_PATH_PREFIX.length());
		return s;
	}

	@Override
	public long getSize() {
		return size;
	}

}
