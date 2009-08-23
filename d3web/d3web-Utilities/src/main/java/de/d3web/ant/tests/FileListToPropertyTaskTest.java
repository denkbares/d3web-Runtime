/*
 * Created on 17.05.2004
 */
package de.d3web.ant.tests;

import junit.framework.TestCase;
import de.d3web.ant.tasks.FileListToPropertyTask;

/**
 * CleanEmptyDirsTaskTest (in )
 * de.d3web.ant.tests
 * d3web-Utilities
 * @author hoernlein
 * @date 17.05.2004
 */
public class FileListToPropertyTaskTest extends TestCase {
    
    public void test() {
	    
	    FileListToPropertyTask fl2pt = new FileListToPropertyTask();
	    fl2pt.setDir("D:\\_eclipseexport\\d3webTrain_build_20040910_1104_fullserver\\tomcat\\webapps\\d3webTrain\\WEB-INF\\classes");
	    fl2pt.setMatch("users.*?xml");
	    fl2pt.setFile("D:\\_eclipseexport\\d3webTrain_build_20040910_1104_fullserver\\tomcat\\webapps\\d3webTrain\\WEB-INF\\classes\\defaultUserAccessManager.properties");
	    fl2pt.setName("userXMLFile");
	    fl2pt.execute();
	
    }
}
