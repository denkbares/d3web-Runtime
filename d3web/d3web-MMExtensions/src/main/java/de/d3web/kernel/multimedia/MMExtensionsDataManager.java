package de.d3web.kernel.multimedia;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class MMExtensionsDataManager  {

	public static MMExtensionsDataManager instance = new MMExtensionsDataManager();
	private MMExtensionsDataManager() {
	}
	public static MMExtensionsDataManager getInstance() {
		return instance;
	}

	private Collection<MMExtensionsListener> listeners = new ArrayList<MMExtensionsListener>();
	
	private Set<File> files = new HashSet<File>(); 
	
	public void addFiles(Collection<File> newFiles) {
		files.addAll(newFiles);
		fireFilesAdded(newFiles);
	}
	
	public void setFiles(Collection<File> newFiles) {
		files.clear();
		addFiles(newFiles);
	}
	
	public void removeFiles(Collection<File> oldFiles) {
		files.removeAll(oldFiles);
		fireFilesRemoved(oldFiles);
	}
	
	public Collection<File> getFiles() {
		return files;
	}
	
	public void clear() {
		files.clear();
		fireFilesRemoved(files);
	}
	
	
	public void fireFilesAdded(Collection<File> files) {
		for (MMExtensionsListener listener : listeners) {
			listener.filesAdded(files);
		}
	}
	
	public void fireFilesRemoved(Collection<File> files) {
		for (MMExtensionsListener listener : listeners) {
			listener.filesRemoved(files);
		}
	}
	
	
	public void addListener(MMExtensionsListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(MMExtensionsListener listener) {
		listeners.remove(listener);
	}
	
}
