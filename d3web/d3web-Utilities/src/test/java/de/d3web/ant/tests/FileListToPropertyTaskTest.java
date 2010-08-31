/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

/*
 * Created on 17.05.2004
 */
package de.d3web.ant.tests;

import org.junit.Test;

import de.d3web.ant.tasks.FileListToPropertyTask;

/**
 * CleanEmptyDirsTaskTest (in ) de.d3web.ant.tests d3web-Utilities
 * 
 * @author hoernlein
 * @date 17.05.2004
 */
public class FileListToPropertyTaskTest {

	@Test
	public void test() {

		FileListToPropertyTask fl2pt = new FileListToPropertyTask();
		fl2pt.setDir("D:\\_eclipseexport\\d3webTrain_build_20040910_1104_fullserver\\tomcat\\webapps\\d3webTrain\\WEB-INF\\classes");
		fl2pt.setMatch("users.*?xml");
		fl2pt.setFile("D:\\_eclipseexport\\d3webTrain_build_20040910_1104_fullserver\\tomcat\\webapps\\d3webTrain\\WEB-INF\\classes\\defaultUserAccessManager.properties");
		fl2pt.setName("userXMLFile");
		fl2pt.execute();

	}
}
