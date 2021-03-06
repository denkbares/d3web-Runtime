/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.io.tests.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Offers the functionality used by PersistenceTest. Saves errors and File-Pairs
 * while diffing Folders
 * 
 * @author Johannes Dienst
 */
public class PersistenceHelper {

	private final ArrayList<File[]> pairs = new ArrayList<>();
	private final ArrayList<String[]> errors = new ArrayList<>();
	private final ArrayList<String> _excludedFolders;
	private final ArrayList<String> _excludedFileTypes;

	public PersistenceHelper(List<String> fileTypes, List<String> folders) {
		_excludedFileTypes = new ArrayList<>(fileTypes);
		_excludedFolders = new ArrayList<>(folders);
	}

	/**
	 * Scans a directory for a given Filename Attention: File with same Name are
	 * not considered yet.
	 */
	public File scanDirectory(File[] sub, String s) {

		for (int i = 0; i < sub.length; i++) {

			// This is not perfect, but works quite well for the tests.
			if (sub[i].getPath().endsWith(s)) return sub[i];

			if (sub[i].isDirectory() && (!sub[i].getName().endsWith("CVS"))) {
				File t = scanDirectory(sub[i].listFiles(), s);
				if (t != null) return t;
			}
		}
		return null;
	}

	/**
	 * Diff's two folders and writes out missing files and file-pairs.
	 */
	public void testFileExisting(String _originalFolder, String _reloadedFolder) {
		File _oF = new File(_originalFolder);
		File _rF = new File(_reloadedFolder);

		File[] rFs = sortDirectory(_rF);
		File[] oFs = sortDirectory(_oF);

		for (File f : oFs) {
			File foundRootFolder = scanDirectory(rFs, f.getName());
			if (foundRootFolder == null) {
				errors.add(new String[] {
						f.getName(), f.getPath() });
				continue;
			}
			// scan subfolders
			this.diffFolders(f, foundRootFolder);
		}
	}

	/**
	 * Sorts out the Directories not to be compared. Excluded fileTypes and
	 * directories are in the exclude lists.
	 */
	private File[] sortDirectory(File f) {
		ArrayList<File> proved = new ArrayList<>();

		if (f.isDirectory() && !_excludedFolders.contains(f.getName())) {
			for (File f1 : f.listFiles())
				if (f1.isDirectory() && !_excludedFolders.contains(f1.getName())) proved.add(f1);
				else if (f1.isFile() && !_excludedFileTypes.contains(f1.getName())) proved.add(f1);

		}

		return proved.toArray(new File[proved.size()]);
	}

	/**
	 * Diffs two subfolders.
	 */
	private void diffFolders(File sub, File foundRootFolder) {
		// File[] reloadedContent = foundRootFolder.listFiles();
		// File[] subcontent = sub.listFiles();
		File[] reloadedContent = sortDirectory(foundRootFolder);
		File[] subcontent = sortDirectory(sub);

		for (int i = 0; i < subcontent.length; i++) {

			if (subcontent[i].isFile() && !subcontent[i].getName().endsWith(".jar")) {
				boolean a = false;
				for (File f : reloadedContent) {
					if (f.getName().endsWith(subcontent[i].getName())) {
						pairs.add(new File[] {
								f, subcontent[i] });
						a = true;
						break;
					}
				}
				if (!a) {
					errors.add(new String[] {
							subcontent[i].getName(), subcontent[i].getPath() });
				}
			}

			if (subcontent[i].isDirectory()) {
				boolean b = false;
				for (File f : reloadedContent) {
					if (f.getName().endsWith(subcontent[i].getName())) {
						b = true;
						this.diffFolders(subcontent[i], f);
					}
				}
				if (!b) {
					errors.add(new String[] {
							subcontent[i].getName(), subcontent[i].getPath() });
				}
			}
		}
	}

	public ArrayList<File[]> getPairs() {
		return pairs;
	}

	public ArrayList<String[]> getErrors() {
		return errors;
	}

	/**
	 * Deletes the content of a directory completely.
	 */
	public static boolean deleteRecursive(File dir) {
		if (dir.isFile() || (dir.listFiles().length == 0)) {
			dir.delete();
			return true;
		}

		for (File f : dir.listFiles()) {
			deleteRecursive(f);
		}
		return dir.delete();
	}
}
