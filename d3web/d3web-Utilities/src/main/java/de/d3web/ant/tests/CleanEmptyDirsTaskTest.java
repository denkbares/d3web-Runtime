/*
 * Created on 17.05.2004
 */
package de.d3web.ant.tests;

import junit.framework.TestCase;
import de.d3web.ant.tasks.CleanEmptyDirsTask;

/**
 * CleanEmptyDirsTaskTest (in )
 * de.d3web.ant.tests
 * d3web-Utilities
 * @author hoernlein
 * @date 17.05.2004
 */
public class CleanEmptyDirsTaskTest extends TestCase {
    
    public void test() {
	    
	    CleanEmptyDirsTask cedt = new CleanEmptyDirsTask();
	    cedt.setDir("D:\\_eclipseexport\\d3webTrain_build_20040517_1354_fullserver\\tomcat\\webapps\\d3webTrain\\WEB-INF\\classes");
	    cedt.execute();
	
    }
}
