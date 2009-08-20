package de.d3web.persistence.utilities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * A class to update a jar-File. Before the output-stream is created, the old jar-File at the same
 * position will be read completely and saved in an internal data structure (maybe very 
 * memory-expensive). When the output-stream will be closed, all jar-Entries of the jar-File
 * that has been read, will be added to the new jar-File, but only if they haven't been replaced before.
 * @author gbuscher
 */
public class JARUpdater extends JARWriter {
	
	/**
	 * key: JarEntry
	 * value: List of JarEntryData
	 */
	private Hashtable entryContentTable = new Hashtable();
	
	/**
	 * Manifest of the jar-File to update.
	 */
	private Manifest oldManifest;

	/**
	 * @param myStorageURL URL of the jar-File to update
	 */
	public JARUpdater(URL myStorageURL) {
		super(myStorageURL);
	}
	
	/**
	 * @param myStorageUrl URL
	 * @return InputStream to the given storageURL
	 * @throws IOException
	 */
	private InputStream getUrlInputStream(URL storageUrl) throws IOException {
		return storageUrl.openConnection().getInputStream();	
	}
	
	/**
	 * This inner class is to store information of a read JarEntry.
	 */
	private class JarEntryData {
		int read;
		byte[] buf;
		
		private JarEntryData(int read, byte[] buf) {
			this.read = read;
			this.buf = buf;
		}
	}
	
	
	/**
	 * Reads the jar-File at "storageURL". Every read JarEntry is stored in "entryContentTable"
	 * as key. The values of the table are Lists of JarEntryData-Objects.
	 * In addition, the manifest of the read jar-File is stored in "oldManifest".
	 * @param storageURL URL of the jar-File
	 * @throws IOException
	 */
	private void readOldJarFile(URL storageURL) throws IOException {
		JarInputStream jarIn = new JarInputStream(getUrlInputStream(storageURL));
		JarEntry entry;
		while ((entry = jarIn.getNextJarEntry()) != null) {
			List contentList = new LinkedList();
			byte[] buf = new byte[4096];
			int read = jarIn.read(buf);
			while (read != -1) { 
				contentList.add(new JarEntryData(read, buf));
				buf = new byte[4096];
				read = jarIn.read(buf);
			}
			if (!entry.isDirectory()) {
				entryContentTable.put(entry, contentList); 
			}
		}
		
		oldManifest = jarIn.getManifest();
		
		jarIn.close();
	}
	
	/**
	 * Lazy instantiation of jarOutputStream with given manifest.
	 */
	protected JarOutputStream getJarOutputStream()
		throws FileNotFoundException, IOException {
		if (jarOut == null) {
			readOldJarFile(getStorageURL());
			
			if (getManifest() != null) {
				jarOut =
					new JarOutputStream(
						getUrlOutputStream(getStorageURL()),
						getManifest());
			} else {
				if (oldManifest == null) {
					oldManifest = new Manifest();
				}
				jarOut = new JarOutputStream(getUrlOutputStream(getStorageURL()));
			}
			
			
		}
		return jarOut;
	}
	
	/**
	 * Adds all entries that have not been replaced by new ones and 
	 * closes the stream when finished.
	 */
	public void close() throws IOException {		
		if (jarOut != null) {
			writeAllRemainingEntries();
		}
		super.close();
	}
	
	/**
	 * Adds all entries of the jar-File to update to the new jar-File, if they haven't been
	 * written, yet.
	 */
	private void writeAllRemainingEntries() {
		Enumeration entryEnum = entryContentTable.keys();
		while (entryEnum.hasMoreElements()) {
			JarEntry entry = (JarEntry) entryEnum.nextElement();
			try {	
				JarEntry newEntry = new JarEntry(entry.getName());
				newEntry.setComment(entry.getComment());
				newEntry.setTime(entry.getTime());
				jarOut.putNextEntry(newEntry);
				List readList = (List) entryContentTable.get(entry);
				Iterator readIter = readList.iterator();
				while (readIter.hasNext()) {
					JarEntryData readData = (JarEntryData) readIter.next();
					jarOut.write(readData.buf, 0, readData.read);
				}
				jarOut.flush();
				jarOut.closeEntry();
				
			} catch (Exception ex) {
				// the entry has already been written, so do not overwrite by old one
			}
		}
	}

}
