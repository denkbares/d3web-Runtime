/*
 * Created on 20.06.2003
 */
package de.d3web.ant.tasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * @author hoernlein
 */
public class FileListToPropertyTask extends Task {
	
    private String dir;
    private String match;
    private String file;
    private String name;

	public void setFile(String file) { this.file = file; }
	public void setMatch(String match) { this.match = match; }
	public void setDir(String dir) { this.dir = dir; }
	public void setName(String name) { this.name = name; }
	
	public void execute() throws BuildException {
	    
	    String filesS = "";

	    File theDir = new File(dir);
        if (!theDir.exists() || !theDir.isDirectory())
            throw new BuildException(dir + " is not a existing directory!");
        
        File[] files = theDir.listFiles();
        for (int i = 0; i < files.length; i++)
            if (files[i].isFile() && files[i].getName().matches(match))
                filesS += "," + files[i].getName();
        
        if (!"".equals(filesS))
            filesS = filesS.substring(1);
        
        File theFile = new File(file);
        if (theFile.exists() && theFile.isDirectory())
            throw new BuildException(file + " is a existing directory!");
        
        try {
            
            if (!theFile.exists())
                theFile.createNewFile();

            String content = "";
            BufferedReader bin = new BufferedReader(new FileReader(theFile));
            String line = bin.readLine();
            while (line != null) {
                boolean add = true;
                if (line.indexOf("=") != -1) {
		            String start = line.substring(0, line.indexOf("="));
		            if (start.trim().equals(name))
		                add = false;
                }
	            if (add)
                    content += line + "\n";
                line = bin.readLine();
            }
            bin.close();
            
            content += "\n" + name + " = " + filesS + "\n";

            theFile.delete();
            theFile.createNewFile();
            BufferedWriter bout = new BufferedWriter(new FileWriter(theFile));
            bout.write(content);
            bout.flush();
            bout.close();
            
    	    System.out.println("set property " + name + " in " + file + " to " + filesS);
            
        } catch (IOException ex) {
            throw new BuildException("IOException while working with " + file + "!");
        }
	    
	}

}
