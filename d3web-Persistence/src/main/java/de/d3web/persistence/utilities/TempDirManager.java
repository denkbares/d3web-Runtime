package de.d3web.persistence.utilities;

import java.io.File;
import java.io.FilenameFilter;
import java.rmi.server.UID;
import java.util.logging.Logger;



/**
 * manages everything, that is needed for the temporary directory
 * 
 * @author pkluegl 
 */
public class TempDirManager {
    
	private static final File TEMP_DIR = new File((System.getProperty("java.io.tmpdir")));
	
	private static final String D3WEB = "_d3web_";
	
	private File D3WEB_TEMP_DIR;
	
	private static final long TIME_DIFF = 1000*60*60; // = 1 hour
		
	private class TempFilenameFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return name.startsWith(D3WEB);
		}
	};
	
	private static TempDirManager _instance = new TempDirManager();
	private TempDirManager() {
	};
	public static TempDirManager getInstance() {
		return _instance;
	}

	private void deleteTempDir(File tempDir) {
		boolean deleted = true;
		File[] files = tempDir.listFiles();
		if(files == null) return;
		for (File file : files) {
			if (file.isDirectory()) {
				deleteTempDir(file);
			} else {
				if (!file.delete()) {
					file.deleteOnExit();
					deleted = false;
				}
			}
		}
		if (!tempDir.delete()) {
			tempDir.deleteOnExit();
			deleted = false;
		}
		if (!deleted) {
			Logger.getLogger(this.getClass().getName()).warning("Error while deleting temporary directory");
		}
		
	}
	
	
	synchronized public void deleteTempDir() {
		if (D3WEB_TEMP_DIR != null && D3WEB_TEMP_DIR.exists())
			deleteTempDir(D3WEB_TEMP_DIR);
	}

	public void deleteOldTempDirs() {
		File[] files = TEMP_DIR.listFiles(new TempFilenameFilter());
		if(files == null) return;
		for (File dir : files) {
			if((System.currentTimeMillis() - dir.lastModified()) > TIME_DIFF) {
				deleteTempDir(dir);
			}
		}
	}
	
	
	public void newTempDir() {
		D3WEB_TEMP_DIR = getNewTempDir();
	}
	
	public File getTempDir() {
		if(D3WEB_TEMP_DIR == null) {
			newTempDir();
		}
		if(!D3WEB_TEMP_DIR.exists()) {
			D3WEB_TEMP_DIR.mkdir();
		}
		return D3WEB_TEMP_DIR;
	}

	private File getNewTempDir() {
		return new File(TEMP_DIR, D3WEB + new UID().toString().replaceAll("\\W", "_"));
	}
	
}