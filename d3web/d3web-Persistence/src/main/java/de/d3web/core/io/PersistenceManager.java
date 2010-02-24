/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *                    denkbares GmbH
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.KnowledgeBase;
import de.d3web.core.Resource;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.progress.CombinedProgressListener;
import de.d3web.core.io.progress.DummyProgressListener;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.terminology.info.DCElement;
import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;

/**
 * A singleton to save or load knowledge bases.
 * @author Markus Friedrich (denkbares GmbH)
 */
public class PersistenceManager {
	
	public static final String MULTIMEDIA_PATH_PREFIX = "multimedia/";
	
	public static final String EXTENDED_PLUGIN_ID = "KnowledgePersistenceExtensionPoints";
	public static final String EXTENDED_POINT_READER = "KnowledgeReader";
	public static final String EXTENDED_POINT_WRITER = "KnowledgeWriter";
	public static final String EXTENDED_POINT_FRAGMENT = "FragmentHandler";
	
	private static PersistenceManager instance;
	
	private Extension[] readerPlugins;
	private Extension[] writerPlugins;
	private Extension[] fragmentPlugins;
	
	/**
	 * Private constructor
	 * For public access getInstance should be used
	 */
	private PersistenceManager() {
		updatePlugins();
	}

	private void updatePlugins() {
		PluginManager manager = PluginManager.getInstance();
		readerPlugins = manager.getExtensions(EXTENDED_PLUGIN_ID, EXTENDED_POINT_READER);
		writerPlugins = manager.getExtensions(EXTENDED_PLUGIN_ID, EXTENDED_POINT_WRITER);
		fragmentPlugins = manager.getExtensions(EXTENDED_PLUGIN_ID, EXTENDED_POINT_FRAGMENT);
	}
	
	/**
	 * Method to access the singleton of the PersistanceManager.
	 * If none has been created yet, the instance is initialized.
	 * @return the Singleton
	 */
	public static PersistenceManager getInstance() {
		if (instance==null) {
			instance = new PersistenceManager();
		}
		return instance;
	}

	/**
	 * Loads a knowledgebase from a zip file and informes a listener about the progress
	 * @param file zip file (usually a jar file)
	 * @param listener listener which should be informed 
	 * @return a KnowledgeBase containing the knowledge from the zip file
	 * @throws IOException if an error occurs, an IO Exception is thrown
	 */
	public KnowledgeBase load(File file, ProgressListener listener) throws IOException {
		updatePlugins();
		ZipFile zipfile = new ZipFile(file);
		try {
			KnowledgeBase kb = new KnowledgeBase();
			Enumeration<? extends ZipEntry> entries = zipfile.entries();
			long size = 0;
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (!entry.isDirectory()) {
					size+=entry.getSize();
				}
			}
			CombinedProgressListener cpl = new CombinedProgressListener(size, listener);
			entries = zipfile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.isDirectory()) {
					continue;
				}
				cpl.next(entry.getSize());
				boolean parsed = false;
				String name = entry.getName();
				for (Extension plugin: readerPlugins) {
					//checks if this entry can be parsed with this plugin
					boolean canparse = false;
					String filepattern = plugin.getParameter("filepattern");
					String filename = plugin.getParameter("filename");
					if (filepattern != null) {
						if (name.matches(filepattern)) {
							canparse = true;
						}
					} else if (filename != null) {
						if (name.equals(filename)) {
							canparse = true;
						}
					}
					if (canparse) {
						KnowledgeReader reader = (KnowledgeReader) plugin.getSingleton();
						reader.read(kb, zipfile.getInputStream(entry), cpl);
						parsed = true;
						//parse each entry only once
						break;
					} 
				}
				if (!parsed) {
					if (name.startsWith(MULTIMEDIA_PATH_PREFIX)) {
						JarBinaryRessource jarBinaryRessource = new JarBinaryRessource(entry, file);
						kb.addResouce(jarBinaryRessource);
					} else if (notNeeded(entry)) {
						//nothing to to, files were necessary for previous versions of persistence
					} else {
						Logger.getLogger("Persistence").warning("No parser for entry "+name+
								" available. This file will be lost when saving the KnowledgeBase.");
					}
				}
			}
			return kb;
		} finally {
			zipfile.close();
		}
	}

	private boolean notNeeded(ZipEntry entry) {
		String name = entry.getName();
		return name.equalsIgnoreCase("KB-INF/Index.xml")
				||name.equalsIgnoreCase("CRS-INF/Index.xml")
				||name.equals("META-INF/MANIFEST.MF");
	}
	
	/**
	 * Loads a knowledgebase from a zip file
	 * @param file zip file (usually a jar file)
	 * @return a KnowledgeBase containing the knowledge from the zip file
	 * @throws IOException if an error occurs, an IO Exception is thrown
	 */
	public KnowledgeBase load(File file) throws IOException {
		return load(file, new DummyProgressListener());
	}
	
	/**
	 * Saves the knowledge base to a file
	 * During this process, a temporary file is created. If the process is successful,
	 * the temporary file replaces the input file. 
	 * @param kb Knowledge Base to be saved
	 * @param file File in which the knowledge base should be stored
	 * @param listener listener which should be informed
	 * @throws IOException if an error occurs, an IO Exception is thrown
	 */
	public void save(KnowledgeBase kb, File file, ProgressListener listener) throws IOException {
		updatePlugins();
		Manifest manifest = new Manifest();
		Attributes mainAttributes = manifest.getMainAttributes();
		mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "2.0");
		mainAttributes.put(new Attributes.Name("Date"),new Date().toString());
		mainAttributes.put(new Attributes.Name("Name"), kb.getDCMarkup().getContent(DCElement.TITLE));
		mainAttributes.put(new Attributes.Name("ID"), kb.getId());
		mainAttributes.put(new Attributes.Name("Author"), kb.getDCMarkup().getContent(DCElement.CREATOR));
		mainAttributes.put(new Attributes.Name("User"), System.getProperty ("user.name"));
		File tempfile = new File(file.getCanonicalPath()+".temp");
		
		JarOutputStream jarOutputStream = new JarOutputStream(
				new FileOutputStream(tempfile), manifest);
		int size = 0;
		for (Extension plugin : writerPlugins) {
			KnowledgeWriter writer = (KnowledgeWriter) plugin.getSingleton();
			size += writer.getEstimatedSize(kb);
		}
		size += kb.getResources().size();
		CombinedProgressListener cpl = new CombinedProgressListener(size, listener);
		try {
			
			for (Extension plugin : writerPlugins) {
				String filename = plugin.getParameter("filename");
				if (filename == null) {
					throw new IOException("No filename defined in plugin.xml");
				}
				ZipEntry entry = new ZipEntry(filename);
				jarOutputStream.putNextEntry(entry);
				KnowledgeWriter writer = (KnowledgeWriter) plugin.getSingleton();
				cpl.next(writer.getEstimatedSize(kb));
				writer.write(kb, jarOutputStream, cpl);
			}
			cpl.next(kb.getResources().size());
			int i = 0;
			for (Resource ressource: kb.getResources()) {
				ZipEntry entry = new ZipEntry(MULTIMEDIA_PATH_PREFIX+ressource.getPathName());
				jarOutputStream.putNextEntry(entry);
				InputStream inputStream = ressource.getInputStream();
				try {
					Util.stream(inputStream, jarOutputStream);
				}
				finally {
					inputStream.close();
				}
				i++;
				float percent = i / (float) kb.getResources().size();
				cpl.updateProgress(percent, "Saving binary ressources");
			}
		} 
		finally {
			jarOutputStream.close();
		}
		File bakfile = new File(URLDecoder.decode(file.getCanonicalPath()+".bak", "UTF-8"));
		//delete old backup file
		bakfile.delete();
		//backup original file, if it exists
		if (file.exists()&&!file.renameTo(bakfile)) throw new IOException("Cannot override existing knowledge base file");
		//override original file
		if (!tempfile.renameTo(file)) {
			//if not successful, restore backup and delete created output file
			if (bakfile.exists()) bakfile.renameTo(file);
			tempfile.delete();
			throw new IOException("Cannot rename temporary file");	
		}
		//if successful backup is not needed any more
		bakfile.delete(); 
	}

	/**
	 * This method is used to create an xml element for an object using the fragment handler with the highest priority who can create the element.
	 * @param object Inputobject
	 * @param doc Document in which the element should be created
	 * @return The element representing the input object
	 * @throws NoSuchFragmentHandlerException
	 * @throws IOException if an error occurs, an IO Exception is thrown
	 */
	public Element writeFragment(Object object, Document doc) throws NoSuchFragmentHandlerException, IOException {
		for (Extension plugin: fragmentPlugins) {
			FragmentHandler handler = (FragmentHandler) plugin.getSingleton();
			if (handler.canWrite(object)) {
				Element element = handler.write(object, doc);
				return element;
			}
		}
		throw new NoSuchFragmentHandlerException("No fragment handler found for: "+object);
	}
	
	/**
	 * Reads an xml element an creates its corresponding object. For this operation, the fragment handler with the highest priority, who can handle the element, is used.
	 * @param child xml Element
	 * @param kb The knowledge base is used to get instances of the objects linked to the created object
	 * @return the created object
	 * @throws NoSuchFragmentHandlerException
	 * @throws IOException if an error occurs, an IO Exception is thrown
	 */
	public Object readFragment(Element child, KnowledgeBase kb) throws NoSuchFragmentHandlerException, IOException {
		for (Extension plugin: fragmentPlugins) {
			FragmentHandler handler = (FragmentHandler) plugin.getSingleton();
			if (handler.canRead(child)) {
				return handler.read(kb, child);
			}
		}
		throw new NoSuchFragmentHandlerException("No fragment handler found for: "+child);
	}

	/**
	 * Saves the knowledge base to a file
	 * During this process, a temporary file is created. If the process is successful,
	 * the temporary file replaces the input file. 
	 * @param kb Knowledge Base to be saved
	 * @param file File in which the knowledge base should be stored
	 * @throws IOException if an error occurs, an IO Exception is thrown
	 */
	public void save(KnowledgeBase kb, File file) throws IOException {
		save(kb, file, new DummyProgressListener());
	}
}
