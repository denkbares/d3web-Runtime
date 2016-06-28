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

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;

import de.d3web.core.knowledge.Resource;

/**
 * Class to store Jar Resources in the KnowledgeBase
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class JarBinaryResource implements Resource {

	private final int size;
	private final String entryPath;
	private final File file;
	private final Cipher cipher;

	public JarBinaryResource(ZipEntry entry, File zipfile, Cipher cipher) throws IOException {
		this.entryPath = entry.getName();
		this.size = (int) entry.getSize();
		this.cipher = cipher;
		this.file = zipfile;
		if (entry.getSize() > Integer.MAX_VALUE) {
			throw new IOException("File with more than 2 GB are not supported");
		}
	}

	@Override
	public InputStream getInputStream() throws IOException {

		/*
		 * originally we copied the stream content into a buffer and returned a
		 * stream to that buffer
		 * 
		 * this shall no longer be needed as long as all callers handle the
		 * stream well (closing it every time) therefore this code is removed.
		 * 
		 * Instead, we deliver the zip entry stream directly, but add some
		 * decoration also closing the zip file when closing the stream itself.
		 * It is not possible to directly close the zip file, because after
		 * doing so, you cannot read from the stream any longer.
		 * 
		 * The decorated stream makes sure that the zip file is closed as soon
		 * as possible instead of relying on the garbage collector.
		 */

		final ZipFile zipfile = new ZipFile(file);
		ZipEntry entry = zipfile.getEntry(entryPath);
		InputStream stream = zipfile.getInputStream(entry);

		// decrypt stream if required
		if (cipher != null) stream = new CipherInputStream(stream, cipher);

		// we will return a decoration stream
		// that closes the zip file on closing the stream
		return new FilterInputStream(stream) {

			@Override
			public void close() throws IOException {
				super.close();
				zipfile.close();
			}
		};
	}

	@Override
	public String getPathName() {
		return entryPath.substring(PersistenceManager.MULTIMEDIA_PATH_PREFIX.length());
	}

	@Override
	public long getSize() {
		return size;
	}

}
