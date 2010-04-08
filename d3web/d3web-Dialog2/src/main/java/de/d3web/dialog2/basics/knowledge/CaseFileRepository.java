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

package de.d3web.dialog2.basics.knowledge;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import sun.net.www.ParseUtil;
import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.CaseRepository;
import de.d3web.caserepository.CaseRepositoryImpl;
import de.d3web.caserepository.sax.CaseRepositoryReader;
import de.d3web.caserepository.utilities.CaseRepositoryWriter;
import de.d3web.core.knowledge.terminology.info.DCElement;

/**
 * Provides mostly the same functionality as CaseRepository but does not hold
 * all cases in central memory. Every case is stored in a separate file (in form
 * of a mini-caserepository).
 * 
 * @author gbuscher
 */
public class CaseFileRepository {

	/**
	 * FilenameFilter to filter only the case-files in the
	 * caserepository-directory.
	 */
	private class CaseFilenameFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			if (name.endsWith(FILE_EXTENSION)) {
				return true;
			}
			return false;
		}
	}

	/**
	 * the names of all case-files consist of [title of the case] +
	 * [ID_SEPARATOR] + [id of the case] + [FILE_EXTENSION]
	 */
	public final static String ID_SEPARATOR = "#";

	public final static String FILE_EXTENSION = ".xml";
	private File repositoryDir;
	private String kbid;

	private Long maxCaseId = null; // the maximum caseId of all cases within
	// this repository
	private CaseRepositoryWriter coWriter = null;

	private CaseRepositoryReader coCreator = null;

	public static Logger logger = Logger.getLogger(CaseFileRepository.class);

	/**
	 * Creates a new CaseFileRepository, where all cases are stored in the
	 * directory given by "repositoryDir".
	 * 
	 * @param repositoryDir
	 *            File-object of the directory, where all cases shall be stored
	 * @param kbid
	 *            ID of the knowledgebase that refers to the cases
	 * @param coWriter
	 *            CaseObjectListWriter
	 * @param coCreator
	 *            CaseObjectListCreator
	 */
	public CaseFileRepository(File repositoryDir, String kbid,
			CaseRepositoryWriter coWriter, CaseRepositoryReader coCreator) {
		this.repositoryDir = repositoryDir;
		this.kbid = kbid;
		this.coWriter = coWriter;
		this.coCreator = coCreator;
	}

	/**
	 * Creates a new CaseFileRepository, where all cases are stored in the
	 * directory given by "repositoryURL".
	 * 
	 * @param repositoryURL
	 *            URL of the directory, where all cases shall be stored
	 * @param kbid
	 *            ID of the knowledgebase that refers to the cases
	 * @param coWriter
	 *            CaseObjectListWriter
	 * @param coCreator
	 *            CaseObjectListCreator
	 */
	public CaseFileRepository(URL repositoryURL, String kbid,
			CaseRepositoryWriter coWriter, CaseRepositoryReader coCreator) {
		String s = ParseUtil.decode(repositoryURL.getPath());
		repositoryDir = new File(s.replace('/', File.separatorChar));
		this.kbid = kbid;
		this.coWriter = coWriter;
		this.coCreator = coCreator;
	}

	/**
	 * Adds a case to the repository by creating a new file where the case is
	 * stored. If the case-file already exists, it will be overwritten.
	 * 
	 * @param co
	 *            CaseObject to add
	 */
	public void addCase(CaseObject co) {
		if ((co == null) || (coWriter == null)) {
			return;
		}

		String caseId = co.getId();
		String name = co.getDCMarkup().getContent(DCElement.TITLE);

		// deleteCasesWithSameTitle(name);

		File caseFile = new File(repositoryDir, name + ID_SEPARATOR + caseId
				+ FILE_EXTENSION);
		if (!caseFile.getParentFile().exists()) {
			caseFile.getParentFile().mkdirs();
		}
		
		CaseRepository repository = new CaseRepositoryImpl();
		repository.add(co);
		coWriter.saveToFile(caseFile, repository);

		try {
			long numId = Long.parseLong(co.getId());
			if (numId > getMaxCaseId()) {
				maxCaseId = new Long(numId);
			}
		} catch (Exception ex) {
			logger.warn(ex);
		}
	}

	/*
	 * private void deleteCasesWithSameTitle(String title) { File[] files =
	 * getFileList(repositoryDir); for (int i = 0; i < files.length; i++) { if
	 * ((files[i].isFile()) && (files[i].getName().matches(title + ID_SEPARATOR
	 * + ".*" + FILE_EXTENSION))) { files[i].delete(); } } }
	 */

	private String extractCaseId(String fileName) {
		fileName = fileName.substring(0, fileName.length()
				- FILE_EXTENSION.length());
		return fileName.substring(fileName.lastIndexOf(ID_SEPARATOR) + 1);
	}

	private String extractCaseTitle(String fileName) {
		return fileName.substring(0, fileName.lastIndexOf(ID_SEPARATOR));
	}

	/**
	 * Searches for the case with the given id and returns it, if it could be
	 * found; null otherwise.
	 * 
	 * @param caseId
	 *            String
	 * @return CaseObject
	 */
	public CaseObject getCaseById(String caseId) {
		File caseFile = getCaseFileById(caseId);
		if (caseFile != null) {
			CaseRepository cases = coCreator.createCaseRepository(caseFile,
					KnowledgeBaseRepository.getInstance()
							.getKnowledgeBase(kbid));

			// Normally, there is only one case stored in a caseFile
			Iterator<CaseObject> iter = cases.iterator();
			while (iter.hasNext()) {
				CaseObject co = iter.next();
				if (co.getId() == null) {
					co.getDCMarkup().setContent(DCElement.IDENTIFIER,
							Integer.toString(co.hashCode()));
				}
				return co;
			}
		}
		return null;
	}

	/**
	 * Returns the File, in which the case with the specified id is stored.
	 * 
	 * @param id
	 *            String
	 * @return File
	 */
	private File getCaseFileById(String id) {
		File[] files = getFileList(repositoryDir);
		for (int i = 0; i < files.length; i++) {
			if ((files[i].isFile())
					&& (extractCaseId(files[i].getName()).equals(id))) {
				return files[i];
			}
		}
		return null;
	}

	/**
	 * Returs a Set of all caseIds.
	 * 
	 * @return Set
	 */
	public Set<String> getCaseIds() {
		Set<String> idSet = new HashSet<String>();
		File[] files = getFileList(repositoryDir);
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				idSet.add(extractCaseId(files[i].getName()));
			}
		}
		return idSet;
	}

	/**
	 * Returns a Collection which contains CaseObjectDescriptors for all cases
	 * within the repository.
	 * 
	 * @return Collection
	 */
	public Collection<CaseObjectDescriptor> getCaseObjectDescriptors() {
		Collection<CaseObjectDescriptor> coDescriptors = new LinkedList<CaseObjectDescriptor>();
		File[] files = getFileList(repositoryDir);
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				String fileName = files[i].getName();
				CaseObjectDescriptor cod = new CaseObjectDescriptor(
						extractCaseTitle(fileName), extractCaseId(fileName),
						new Date(files[i].lastModified()));
				coDescriptors.add(cod);
			}
		}
		return coDescriptors;
	}

	private File[] getFileList(File dir) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir.listFiles(new CaseFilenameFilter());
	}

	public long getMaxCaseId() {
		if (maxCaseId != null) {
			return maxCaseId.longValue();
		} else {
			Set<String> caseIds = getCaseIds();
			long max = 0;
			Iterator<String> iter = caseIds.iterator();
			while (iter.hasNext()) {
				String id = iter.next();
				try {
					long numId = Long.parseLong(id);
					if (numId > max) {
						max = numId;
					}
				} catch (Exception e) {
					logger.warn(e);
				}
			}
			maxCaseId = new Long(max);
			return max;
		}
	}

	/**
	 * Returns true, if the repository contains one ore more cases.
	 * 
	 * @return boolean
	 */
	public boolean hasCases() {
		File[] files = getFileList(repositoryDir);
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Deletes all cases within the caserepositoy-directory.
	 */
	public void purgeAllCases() {
		File[] files = getFileList(repositoryDir);
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				files[i].delete();
			}
		}
		maxCaseId = null;
	}

	/**
	 * Removes the specified case by removing the associated case-file.
	 * 
	 * @return boolean (true, if successful)
	 */
	public boolean removeCase(CaseObject co) {
		return removeCaseById(co.getId());
	}

	/**
	 * Removes the case with the specified Id by removing the associated
	 * case-file.
	 * 
	 * @param caseId
	 *            String
	 * @return boolean (true, if successful)
	 */
	public boolean removeCaseById(String caseId) {
		File caseFile = getCaseFileById(caseId);
		if (caseFile != null) {
			return caseFile.delete();
		}
		return false;
	}

}