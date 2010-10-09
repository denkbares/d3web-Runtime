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

package de.d3web.plugin.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import de.d3web.plugin.JPFPluginManager;

/**
 * Provides a static method to initialize the JPF-Pluginmanager by using a
 * classpath file generated from the Maven dependency plugin
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public abstract class InitPluginManager {

	/**
	 * Avoids the creation of an instance for this class.
	 */
	private InitPluginManager() {
	}

	/**
	 * Initializes the JPF-Pluginmanager with the information stored in
	 * "target/dependencies/output.txt" This file can be generated with the
	 * maven dependency plugin <BR>
	 * Important: Tests using this function must run maven install after each
	 * dependency update
	 * 
	 * @throws IOException
	 */
	public static void init() throws IOException {
		File classpath = new File("target/dependencies/output.txt");
		StringBuffer fileData = new StringBuffer();
		BufferedReader reader = new BufferedReader(
				new FileReader(classpath));
		try {
			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}
		}
		finally {
			reader.close();
		}
		String[] jars = fileData.toString().split(";");
		List<File> filteredJars = new ArrayList<File>();
		// adding the plugin itself
		File ownSources = new File("target/classes");
		if (checkIfPlugin(ownSources)) {
			filteredJars.add(ownSources);
		}
		for (String s : jars) {
			File jarFile = new File(s);
			if (checkIfPlugin(jarFile)) {
				filteredJars.add(jarFile);
			}
		}
		JPFPluginManager.init(filteredJars.toArray(new File[filteredJars.size()]));
	}

	private static boolean checkIfPlugin(File f) {
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (File file : files) {
				if (file.getName().equalsIgnoreCase("plugin.xml")) {
					return true;
				}
			}
			return false;
		}
		else {
			ZipFile zipfile;
			try {
				zipfile = new ZipFile(f);
				Enumeration<? extends ZipEntry> entries = zipfile.entries();
				while (entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();
					if (entry.getName().equalsIgnoreCase("plugin.xml")) {
						return true;
					}
				}
				return false;
			}
			catch (ZipException e) {
				return false;
			}
			catch (IOException e) {
				return false;
			}

		}
	}
}
