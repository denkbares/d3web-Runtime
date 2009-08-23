/*
 * Created on 17.05.2004
 */
package de.d3web.ant.tasks;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * CleanEmptyDirsTask (in )
 * de.d3web.ant.tasks
 * d3web-Utilities
 * @author hoernlein
 * @date 17.05.2004
 */
public class CleanEmptyDirsTask extends Task {

    private String dir;

    public void setDir(String dir) { this.dir = dir; }
    
    public void execute() throws BuildException {
        System.out.println("deleting empty dirs in " + dir);

        File theDir = new File(dir);
        if (!theDir.exists() || !theDir.isDirectory())
            throw new BuildException(dir + " is not a existing directory!");

        int d = 0;
        
        File[] contents = theDir.listFiles();
        for (int i = 0; i < contents.length; i++)
            d = deleteEmptyDirs(contents[i], d);

        System.out.println("deleted " + d + " dir" + (d == 1 ? "" : "s"));
    }

    /**
     * @param file
     * @param i
     * @return int
     */
    private int deleteEmptyDirs(File file, int d) {
        
        if (!file.isDirectory())
            return d;
        else {
            File[] contents = file.listFiles();
            for (int i = 0; i < contents.length; i++)
                d = deleteEmptyDirs(contents[i], d);
            
            if (file.listFiles().length == 0) {
                file.delete();
                d++;
            }
            
            return d;
        }
    }
    
}
