package de.d3web.io.tests;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.XMLTestCase;

import de.d3web.core.KnowledgeBase;
import de.d3web.core.io.PersistenceManager;
import de.d3web.io.tests.utils.Butil;
import de.d3web.io.tests.utils.JarExtractor;
import de.d3web.io.tests.utils.PersistenceHelper;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Saves a jar-File to a KnowledgeBase and reloads it.
 * Original and Reloaded should be similar.
 * 
 * @author Johannes Dienst
 *
 */
public class PersistenceTest extends XMLTestCase {
	
	String _originalFolder = "src/test/resources/kbs/original/";
	String _reloadedFolder = "target/reloadedKBs/";
	ArrayList<String> _excludedFolders;
	ArrayList<String> _excludedFileTypes;
	
    public PersistenceTest(String name) throws Exception{
        super(name);
        _excludedFolders = new ArrayList<String>();
        _excludedFolders.add("CVS");
        _excludedFolders.add(".svn");
        _excludedFolders.add("META-INF");
        _excludedFolders.add("CRS-INF");
        _excludedFolders.add("KB-INF");
        _excludedFileTypes = new ArrayList<String>();
        _excludedFileTypes.add(".MF");
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
     * Diffs all files from originalFolder with the reloaded ones.
     * 1. File-Existing-Test
     * 2. File-Content-Test
     * 
     * @throws Exception
     */
    public void testForEquality() throws Exception {
   
    	// Test if Errors occurred
    	PersistenceHelper h = new PersistenceHelper(_excludedFileTypes, _excludedFolders);
    	h.testFileExisting(_originalFolder, _reloadedFolder);
    	ArrayList<String[]> err =  h.getErrors();
    	StringBuffer message = new StringBuffer("Missing Files:\n\r");
    	for (String[] m : err) {
    		message.append(m[0] + " missing in " + m[1] + " or in reloaded-Folder"+ "\n\r");
    	}
    	assertEquals(message.toString(),0, err.size());
    	
    	// Test if files are similar
    	ArrayList<File[]> pairs = h.getPairs();
    	Diff diff;
    	DetailedDiff df;
    	BufferedReader org;
    	BufferedReader rel;
    	int actual = 0;
    	message = new StringBuffer("Differences found (Without Properties):\n\r");
    	for (File[] p : pairs) {
    		message = new StringBuffer("Differences found in "+p[0].getName()+" (Without Properties):\n\r");
			org = new BufferedReader(new InputStreamReader(new FileInputStream(p[0])));
			rel = new BufferedReader(new InputStreamReader(new FileInputStream(p[1])));
    		
    		if (p[0].getName().endsWith(".xml")) {
    	    	diff = new Diff(org, rel);
    	    	df = new DetailedDiff(diff);
    	    	actual = df.getAllDifferences().size();
    	    	if (actual <=  1)
    	    		actual = 0;
    	    	   	    	
    	    	for (Object f : df.getAllDifferences())
    	    		message.append(((Difference)f).toString() + "\n\r");
    	    	
    	    	assertEquals(message.toString(), 0, actual);
    	    	
    	    	
    		} else {
    			assertEquals("Difference in File: " + p[0].getPath() , Butil.readString(new FileInputStream(p[0])), Butil.readString(new FileInputStream(p[1])));
    		}
    	}
    }
}
