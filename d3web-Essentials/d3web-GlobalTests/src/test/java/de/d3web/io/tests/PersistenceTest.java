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

package de.d3web.io.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLTestCase;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.io.tests.utils.Butil;
import de.d3web.io.tests.utils.JarExtractor;
import de.d3web.io.tests.utils.PersistenceHelper;
import com.denkbares.plugin.test.InitPluginManager;

/**
 * Saves a jar-File to a KnowledgeBase and reloads it. Original and Reloaded
 * should be similar.
 * 
 * @author Johannes Dienst
 * 
 */
public class PersistenceTest extends XMLTestCase {

	final String _originalFolder = "src/test/resources/kbs/original/";
	final String _reloadedFolder = "target/reloadedKBs/";
	final ArrayList<String> _excludedFolders;
	final ArrayList<String> _excludedFileTypes;

	public PersistenceTest(String name) throws Exception {
		super(name);
		_excludedFolders = new ArrayList<>();
		_excludedFolders.add("CVS");
		_excludedFolders.add(".svn");
		_excludedFolders.add("META-INF");
		_excludedFolders.add("CRS-INF");
		_excludedFolders.add("KB-INF");
		_excludedFileTypes = new ArrayList<>();
		_excludedFileTypes.add(".MF");
		_excludedFileTypes.add(".DS_Store");
	}

	@Override
	protected void setUp() throws Exception {
		try {
			InitPluginManager.init();
		}
		catch (IOException e1) {
			assertTrue("Error initialising plugin framework", false);
		}
		// Clean the old reloaded dir
		File fc = new File(_reloadedFolder);
		if (!fc.exists()) fc.mkdir();
		for (File v : fc.listFiles()) {
			assertEquals("Error while deleting File: " + v.getName(),
					true, PersistenceHelper.deleteRecursive(v));
		}

		// Read the Original KnowledgeBases from Directory
		File baseDir = new File(_originalFolder);
		File[] kbFolders = baseDir.listFiles();
		PersistenceManager mgr = PersistenceManager.getInstance();
		String folderName;
		File f;
		KnowledgeBase _originalBase;
		String subPath;

		for (int i = 0; i < kbFolders.length; i++) {
			if (kbFolders[i].isDirectory()) {
				folderName = kbFolders[i].getName();
				if (folderName.endsWith("CVS")) continue;
				if (folderName.contains("svn")) continue;
				subPath = folderName + "/" + folderName;
				f = new File(_originalFolder + subPath + "-Original.jar");
				_originalBase = mgr.load(f);

				// Save the original to new Jar
				new File(_reloadedFolder + folderName).mkdirs();
				f = new File(_reloadedFolder + subPath + "-Reloaded.jar");
				mgr.save(_originalBase, f);

				// reload
				JarExtractor.extract(f, new File(_reloadedFolder + folderName));
			}
		}
	}

	/**
	 * Diffs all files from originalFolder with the reloaded ones. 1.
	 * File-Existing-Test 2. File-Content-Test
	 * 
	 * @throws Exception
	 */
	public void testForEquality() throws Exception {

		// Test if Errors occurred
		PersistenceHelper h = new PersistenceHelper(_excludedFileTypes, _excludedFolders);
		h.testFileExisting(_originalFolder, _reloadedFolder);
		ArrayList<String[]> err = h.getErrors();
		StringBuffer message = new StringBuffer("Missing Files:\n\r");
		for (String[] m : err) {
			message.append(m[0]).append(" missing in ").append(m[1]).append(" or in reloaded-Folder").append("\n\r");
		}
		assertEquals(message.toString(), 0, err.size());

		// Test if files are similar
		ArrayList<File[]> pairs = h.getPairs();
		Diff diff;
		DetailedDiff df;
		BufferedReader org;
		BufferedReader rel;
		int actual = 0;
		for (File[] p : pairs) {
			message = new StringBuffer("Differences found in " +
					p[0].getName() + "::" + p[0].getAbsolutePath() +
					" (Without Properties):\n\r");
			org = new BufferedReader(new InputStreamReader(new FileInputStream(p[0]), "UTF-8"));
			rel = new BufferedReader(new InputStreamReader(new FileInputStream(p[1]), "UTF-8"));

			if (p[0].getName().endsWith(".xml")) {
				diff = new Diff(org, rel);
				df = new DetailedDiff(diff);
				actual = df.getAllDifferences().size();
				if (actual <= 1) actual = 0;

				for (Object f : df.getAllDifferences()) {
					message.append(f).append("\n\r");
				}

				assertEquals(message.toString(), 0, actual);
			}
			else {
				assertEquals("Difference in File: " + p[0].getPath(),
						Butil.readString(new FileInputStream(p[0])),
						Butil.readString(new FileInputStream(p[1])));
			}
		}
	}
}
