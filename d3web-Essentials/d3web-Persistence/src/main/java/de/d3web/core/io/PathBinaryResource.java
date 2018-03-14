/*
 * Copyright (C) 2017 denkbares GmbH. All rights reserved.
 */
package de.d3web.core.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;

import de.d3web.core.knowledge.Resource;

/**
 * Class to store Path Resources with optional encryption in the KnowledgeBase
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 28.01.2017
 */
public class PathBinaryResource implements Resource {

	private final long size;
	private final String entryPath;
	private final Path file;
	private final Cipher cipher;

	public PathBinaryResource(Path file, String relativePath, Cipher cipher) throws IOException {
		this.file = file;
		this.entryPath = relativePath.replace("\\", "/");
		this.size = Files.size(file);
		this.cipher = cipher;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		InputStream stream = new BufferedInputStream(Files.newInputStream(file));
		// decrypt stream if required
		if (cipher != null) stream = new CipherInputStream(stream, cipher);
		return stream;
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
