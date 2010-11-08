/*
 * Copyright (C) 2010 denkbares GmbH
 * 
 * This is free software for non commercial use
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 */
package de.d3web.folder.records.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.io.progress.DummyProgressListener;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.knowledge.InfoStore;
import de.d3web.core.knowledge.terminology.info.DCMarkup;
import de.d3web.core.records.DefaultSessionRepository;
import de.d3web.core.records.FactRecord;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.records.io.SessionPersistenceManager;
import de.d3web.core.session.protocol.Protocol;

/**
 * This implementation of the SessionRepositoryPersistenceHandler interface can
 * handle multiple XML files. The SessionRecord in the SessionRepository
 * committed for saving will be saved to separate XML files.
 * 
 * @author Sebastian Furth & Markus Friedrich (both denkbares GmbH)
 * 
 */
public class MultipleXMLSessionRepository extends DefaultSessionRepository {

	public static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH.mm.ss.SS");

	/**
	 * Loads the Session Records from a folder. The files are not parsed
	 * immediately, they will be parsed when someone accesses them. Files not
	 * ending on .xml will be ignored.
	 * 
	 * @created 20.09.2010
	 * @param folder Folder which represents the SessionRepository
	 * @throws IOException
	 * @throws ParseException
	 */
	public void load(File folder) throws IOException, ParseException {
		this.load(folder, new DummyProgressListener());
	}

	/**
	 * Loads the Session Records from a folder. The files are not parsed
	 * immediately, they will be parsed when someone accesses them. Files not
	 * ending on .xml will be ignored.
	 * 
	 * @created 20.09.2010
	 * @param folder Folder which represents the SessionRepository
	 * @param listener the progress listener to observe the progress of reading
	 * @throws IOException
	 * @throws ParseException
	 */
	public void load(File folder, ProgressListener listener) throws IOException, ParseException {
		if (folder == null) throw new NullPointerException(
				"File is null. Unable to load SessionRepository.");
		if (!folder.isDirectory()) throw new IllegalArgumentException(
				"This implementation of the SessionRepositoryPersistenceHandler requires a directory.");
		File[] listFiles = folder.listFiles();
		int counter = 0;
		for (File f : listFiles) {
			String name = f.getName();
			float percent = listFiles.length / (float) counter++;
			listener.updateProgress(percent, name);
			if (!f.isFile() || !name.endsWith(".xml")) continue;
			int underscore = name.indexOf('_');
			Date date = FILE_DATE_FORMAT.parse(name.substring(0, underscore));
			String id = name.substring(underscore + 1, name.length() - 4);
			add(new FileRecord(id, date, f));
		}
		listener.updateProgress(1f, "loading done");
	}

	/**
	 * Saves the SessionRepository to a Folder. For each session, an xml File
	 * will be created (Filename: id.xml). If there is a file with the same
	 * name, it will be overwritten.
	 * 
	 * @created 20.09.2010
	 * @param folder Folder where this Repository should be saved to
	 * @throws IOException
	 */
	public void save(File folder) throws IOException {
		this.save(folder, new DummyProgressListener());
	}

	/**
	 * Saves the SessionRepository to a Folder. For each session, an xml File
	 * will be created (Filename: id.xml). If there is a file with the same
	 * name, it will be overwritten.
	 * 
	 * @created 20.09.2010
	 * @param folder Folder where this Repository should be saved to
	 * @param listener the progress listener to observe the progress of writing
	 * @throws IOException
	 */
	public void save(File folder, ProgressListener listener) throws IOException {
		if (folder == null) throw new NullPointerException(
				"File is null. Unable to save SessionRepository.");
		if (folder.exists() && !folder.isDirectory()) throw new IllegalArgumentException(
				"This implementation of the SessionRepositoryPersistenceHandler requires a directory.");
		folder.mkdirs();
		SessionPersistenceManager spm = SessionPersistenceManager.getInstance();
		Collection<SessionRecord> records = sessionRecords.values();
		int counter = 0;
		for (SessionRecord sr : records) {
			float percent = records.size() / (float) counter++;
			listener.updateProgress(percent, sr.getName());
			String date = FILE_DATE_FORMAT.format(sr.getCreationDate());
			File file = new File(folder.getAbsolutePath() + "/" + date + "_" + sr.getId()
					+ ".xml");
			if (sr instanceof FileRecord && !((FileRecord) sr).modified) {
				FileRecord fr = (FileRecord) sr;
				if (fr.file.getCanonicalPath().equals(file.getCanonicalPath())) {
					// SessionRecord was not changed and should be saved to the
					// same directory -> nothing to do
					continue;
				}
				else {
					copyFile(fr.file, file);
				}
			}
			else {
				List<SessionRecord> templist = new LinkedList<SessionRecord>();
				templist.add(sr);
				spm.saveSessions(file, templist, new DummyProgressListener());
			}
		}
		listener.updateProgress(1f, "writing session records to disc done");
	}

	/**
	 * Special implementation of a Session Record. This record has a reference
	 * to a file, if some information other than die id oder the kb is asked,
	 * the record will be parsed. This Record also remembers, if something could
	 * have been modified since it was loaded.
	 * 
	 * @author Markus Friedrich (denkbares GmbH)
	 * @created 20.09.2010
	 */
	private class FileRecord implements SessionRecord {

		private File file;
		private boolean modified = false;
		private String id;
		private Date created;
		private SessionRecord realRecord = null;

		public FileRecord(String id, Date date, File f) {
			this.id = id;
			this.created = date;
			this.file = f;
		}

		@Override
		public void addValueFact(FactRecord fact) {
			parseIfNecessary();
			if (!realRecord.getValueFacts().contains(fact)) {
				realRecord.addValueFact(fact);
				touch();
			}
		}

		/**
		 * Parses the file to gain access to all informations
		 * 
		 * @throws IOException
		 * @created 20.09.2010
		 */
		private void parseSessionRecord() {
			Collection<SessionRecord> loadedSessions;
			try {
				loadedSessions = SessionPersistenceManager.getInstance().loadSessions(
						file, new DummyProgressListener());
				if (loadedSessions.size() > 1) {
					throw new IOException("The file " + file.getCanonicalPath()
							+ " contains more than one sessionrecord.");
				}
				else if (loadedSessions.size() == 0) {
					throw new IOException("The file " + file.getCanonicalPath()
							+ " contains more no sessionrecord.");
				}
				else {
					realRecord = loadedSessions.iterator().next();
				}
			}
			catch (IOException e) {
				throw new IllegalStateException("cannot parse record xml file", e);
			}
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public Date getLastChangeDate() {
			parseIfNecessary();
			return realRecord.getLastChangeDate();
		}

		@Override
		public void touch(Date lastEditDate) {
			modified = true;
			parseIfNecessary();
			realRecord.touch(lastEditDate);
		}

		@Override
		public Protocol getProtocol() {
			// if something gets a reference to the Protocol, something could be
			// changed
			parseIfNecessary();
			return realRecord.getProtocol();
		}

		@Override
		public Date getCreationDate() {
			return created;
		}

		@Override
		public List<FactRecord> getValueFacts() {
			parseIfNecessary();
			return Collections.unmodifiableList(realRecord.getValueFacts());
		}

		private void parseIfNecessary() {
			if (realRecord == null) {
				parseSessionRecord();
			}
		}

		@Override
		public void setDCMarkup(DCMarkup dcMarkup) {
			touch();
			parseIfNecessary();
			realRecord.setDCMarkup(dcMarkup);
		}

		@Override
		public DCMarkup getDCMarkup() {
			// if something gets a reference to the DCMarkup, something could be
			// changed
			parseIfNecessary();
			return realRecord.getDCMarkup();
		}

		@Override
		public void touch() {
			touch(new Date());
		}

		@Override
		public void addInterviewFact(FactRecord fact) {
			parseIfNecessary();
			if (!realRecord.getInterviewFacts().contains(fact)) {
				realRecord.addInterviewFact(fact);
				touch();
			}
		}

		@Override
		public List<FactRecord> getInterviewFacts() {
			parseIfNecessary();
			return Collections.unmodifiableList(realRecord.getInterviewFacts());
		}

		@Override
		public String getName() {
			parseIfNecessary();
			return realRecord.getName();
		}

		@Override
		public void setName(String name) {
			parseIfNecessary();
			realRecord.setName(name);
		}

		@Override
		public InfoStore getInfoStore() {
			parseIfNecessary();
			return realRecord.getInfoStore();
		}

	}

	private static void copyFile(File in, File out) throws IOException {
		out.delete();
		out.createNewFile();
		FileChannel inChannel = new FileInputStream(in).getChannel();
		FileChannel outChannel = new FileOutputStream(out).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		}
		finally {
			if (inChannel != null) inChannel.close();
			if (outChannel != null) outChannel.close();
		}
	}
}
