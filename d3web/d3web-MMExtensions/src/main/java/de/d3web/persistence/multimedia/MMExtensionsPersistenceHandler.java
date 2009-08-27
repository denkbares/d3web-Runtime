package de.d3web.persistence.multimedia;

import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
		if(path == null) return;
		File[] fileArray = path.listFiles();
		if(fileArray == null) return;
		Collection<File> files = Arrays.asList(fileArray);
		new UploadAction(files, false).actionPerformed(null);
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
