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

package de.d3web.persistence.multimedia;

import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.w3c.dom.Document;

import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.multimedia.MMExtensionsDataManager;
import de.d3web.persistence.utilities.PersistentObjectDescriptor;
import de.d3web.persistence.xml.MultipleAuxiliaryPersistenceHandler;

public class MMExtensionsPersistenceHandler implements
	MultipleAuxiliaryPersistenceHandler {

	private String dir = "Multimedia/";
	public final static String MMEXTENSIONS = "MMExtensions";
	
	
	public KnowledgeBase load(KnowledgeBase kb, URL url) {
		try {
			loadFiles(url);
		} catch (URISyntaxException e) {
			Logger.getLogger(this.getClass().getName()).throwing(
                    this.getClass().getName(), "load", e);
		}
		return kb;
	}

	private void loadFiles(URL url) throws URISyntaxException {
		File path = new File(url.toURI().resolve(""));
		Collection<File> files = new LinkedList<File>();
		collectFiles(path, files);
		//MMExtensionsDataManager.getInstance().addFiles(files);
		new UploadAction(files, false).actionPerformed(null);
	}
	

	private void collectFiles(File path, Collection<File> result) {
		File[] files = path.listFiles();
		if (files == null) return;
		for (File file : files) {
			if (file.isDirectory()) {
				collectFiles(file, result); 
			}
			else {
				result.add(file);
			}
		}
	}

	public String getId() {
		return MMEXTENSIONS;
	}

	public String getDefaultStorageLocation() {
		return dir;
	}
	
	public Collection<PersistentObjectDescriptor> saveAll(KnowledgeBase kb) {
		Collection<PersistentObjectDescriptor> result = new ArrayList<PersistentObjectDescriptor>();
		for (File file : MMExtensionsDataManager.getInstance().getFiles()) {
			try {
				String name = URLEncoder.encode(file.getName(), "UTF-8");
				result.add(new PersistentObjectDescriptor(dir + name, new FileInputStream(file)));				
			} catch (Exception e) {
				Logger.getLogger(this.getClass().getName()).throwing(
	                    this.getClass().getName(), "saveAll", e);
			}
		}
		return result;
	}

	public Document save(KnowledgeBase kb) {
		return null;
	}

}
